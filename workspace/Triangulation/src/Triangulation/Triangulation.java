/*
 * Copyright (c) 01.2018
 */

package Triangulation;

import java.util.HashMap;
import Triangulation.Mj_Polygon;
import Triangulation.Decompositor;
import Triangulation.Mj_List;
import Triangulation.Mj_ListVertex;
import Triangulation.Mj_Vertex;
import Triangulation.Mj_Point;
import Triangulation.Decompositor.Scaning;

/**
 * ����� ��� ������������ ����� �� ����� ��������� (������� ��������, ���������� ���������� �������).
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Triangulation {
	
	// ��� ����������� ��������, ����� ������ ���� ���������� �����, ���� �� ��� ������� �� ��� ��������
	public static double delta = 0.1; 
	// ������������ ���������� �� ���������� delta � �������� ��� ������������
	public static HashMap<Double, Double> org_coords = new HashMap<Double, Double>();
	
	private Decompositor decompositor = new Decompositor();
	private Triangulator triangulator = new Triangulator();
	private Mj_Polygon outer_polygon; // ������� ������� �� ������������ �� ���������� ��������
	private Mj_List<Mj_Polygon> monotone_polygons; // ������ ���������� ��������� ����� ������������ �������� ��������
	private Mj_List<Mj_Polygon> triangles = new Mj_List<Mj_Polygon>(); // ������ ������������� ����� ������������
		
	/**
	 * ������� �������� �� ���� ������ ����� � ������������ � ������� (x, y), �� ������� ������� �������.
	 * ����� ������� �������� �� ���� ������ �������� � ���� ������� �������� ����� � ������������ � 
	 * ������� (x, y) ���������, ������� ������������� ������ ��������, ��������� � ������ ���������.
	 * � �������� ������� ��������� ����� �������� �������� NULL. ����� ������� ��������� �������� ������� 
	 * �� ���������� �������(�) � ���������� ��� (��) � ���� ������� �������� ����� � ������������ � 
	 * ������� (x, y).
	 * 
	 * @param outer_polygon ������ ����� � ������������ � ������� (x, y), �� ������� ������� �������
	 * @param inner_polygons ������ �������� ����� � ������������ � ������� (x, y) ���������, ������� 
	 * ������������� ������ ��������, ��������� � ������ ���������
	 * @return ������ �������� ����� � ������������ � ������� (x, y) ���������� ��������� ���� null
	 */
	
	public double[][] getMonotonePolygons(double[] outer, 
			                              double[][] inners) {
		Mj_List<Mj_Polygon> inner_polys_list = new Mj_List<Mj_Polygon>(); // ���������� ��������, ���� ��� ����
		
		// ��������� �� ��������. ���� �� ������ ���-�� ���������, �� 
		// ������� �� ������� ���������.
		if(outer.length % 2 == 0) {
			outer_polygon = makePolygon(outer);
			
			// ��������� ������� ���������� ���������
			if(inners != null) {				
				// ��������� ������ ���������� ������� �� �������� � ��������� ������ ���������� ���������
				for(int i = 0; i < inners.length; i++) {
					if(inners[i].length % 2 == 0)
						inner_polys_list.append(makePolygon(inners[i]));
				}
				
				// ����������� �� ���������� ���������
				removeInnerPolygons(inner_polys_list);
			} else {
				// ���������, ��� ���������� ����� �� ������� � ������� �������� �� ������� �������
				boolean outer_CW = getDirectionOfPolygonVertexes(outer_polygon); 
				
				// ���� ����� ������ �������� ������ ������� �������, �� ������ ��� �� ��������
				if(!outer_CW) {
					outer_polygon.changeCircumventPoints();
					outer_CW = true; 
				}
			}
		}
		
		// ������������ �������� �� ���������� �����
		monotone_polygons = decompositor.regularize(outer_polygon);
		
		// ������������ ���������� ���������
		return triangulation();
	}
	
	/**
	 * ��������� ������������ ���������� ��������� �� ������ � ���������� 
	 * ������ ������������� � ���� ������� �������� � ������������. ����������
	 * ������������� ��������� � ����: array_1 = {x1,y1, x2,y2, x3,y3}.
	 * 
	 * 
	 * @return ������ ������������� � ���� ������� �������� � ������������
	 */
	
	private double[][] triangulation() {
		if(monotone_polygons != null && monotone_polygons.length() > 0) {
			monotone_polygons.first();
		
			for(int i = 0; i < monotone_polygons.length(); i++) {
				triangles.append(triangulator.triangulateMonotonePolygon(monotone_polygons.val()));
				monotone_polygons.next();
			}
		
			// ���������� ��������� ������������ (�����������) ���������� ������,
			// ������� �������� ��� ���������� � ��������
			decompositor.returnOriginalView(triangles);
		
			return getArraysCoords();
		} else
			return null;
	}
	
	/**
	 * ����������� ���������� ������ ������������� � ������ ��������.
	 * 
	 * @return ������ �������� � ������������
	 */
	
	private double[][] getArraysCoords() {
		double[][] arraysCoords = new double[triangles.length()][];
		
		triangles.first();
		
		for(int i = 0; i < triangles.length(); i++) {
			Mj_Polygon p = triangles.val();
			
			double[] tmp = new double[p.size * 2];
			
			for(int j = 0; j < p.size * 2; j+=2) {
				arraysCoords[i] = tmp;
				
				tmp[j] = p.point().x;
				tmp[j + 1] = p.point().y;	
				
				p.advance();
			}
			
			triangles.next();
		}
		
		triangles.delete_list();
		
		return arraysCoords;
	}
	
	/**
	 * ���� ������� �������� � ���� ���������� ��������, �� ��� ������� �����������
	 * �� ���. � ����� ���������� ������� ��� ���������� ���������. ����� ��������� 
	 * ����������� � ���, ��� ����� ������ ����� �������� �������� � ���� ����� ������� 
	 * ����� ������-������ ����������� ��������. ����� �������, �� ��������� ����������� 
	 * ������ �������� ��������, ���������� � ������������ ������ ����������� ��������, 
	 * �������� ����������� ��������� �����, ���� ����, ������ ����������� ������ �����������
	 * ��������. ����� ������ ����������� �� ���� ������ � �������� ������ ������� �������.
	 * 
	 * @param inner_polys_list ���������� ��������
	 */
	
	private void removeInnerPolygons(Mj_List<Mj_Polygon> inner_polys_list) {	
		int size = inner_polys_list.length(); // ������ ������ ���������� ���������
		
		for(int i = 0; i < size; i++) {
			inner_polys_list.first(); // ��������� �� ������ ������� � ������
			
			// ������� ���������� ������� �������� ������ ������������� � ��������
			inner_polys_list.val(getPolygonWithMinDistance(inner_polys_list));
			// ���������� ��� �������� � ���� ������� � ������� �� ������ ���������� ��������� 
			// �������, ������� ���������� � �������
			unionPolygons(inner_polys_list.remove());
		}
	}
	
	/**
	 * ���������� ������� ������� � ����������. � ����� ���������� ���� ������� �������.
	 * 
	 * @param outer_polygon ������� �������
	 * @param inner_polygon ���������� �������
	 */ 
	
	private void unionPolygons(Mj_Polygon inner_polygon) {
		// ����������� ������ ������ �� ������� ��������
		boolean outer_CW = getDirectionOfPolygonVertexes(outer_polygon); 
		// ����������� ������ ������ �� ���������� ��������
		boolean inner_CW = getDirectionOfPolygonVertexes(inner_polygon);
		
		// ���� ����� ������ �������� ������ ������� �������, �� ������ ��� �� ��������
		if(!outer_CW) {
			outer_polygon.changeCircumventPoints();
			outer_CW = true; 
		}
		
		// ���� ����������� ������ ������ ��������� ���������, �� ������ ����������� ������
		// � ����������� �������� �� ���������������
		if(outer_CW == inner_CW) {
			inner_polygon.changeCircumventPoints();
		}
		
		// ��������� ������� ������� �� ���� ������ (������������ ����� �������� � �����������
		// ���������), ��� ���� ������� �� ���������. � �����, ���������� ������� ���������� 
		// ������ ��������.
		outer_polygon.getVertex().split_triangle(inner_polygon.getVertex(), outer_CW);
		
		// �������� ������ ��������
		outer_polygon.resize();
	}
	
	/**
	 * ���������� ����������� ������ �� ����� � ��������� ����� � ��������.
	 * 
	 * @param polygon �������
	 * @return TRUE, ���� ����� �������������� �� ������� �������, ����� - FALSE 
	 */
	
	private boolean getDirectionOfPolygonVertexes(Mj_Polygon polygon) {
		// ���������, ��� ���������� ����� �� ������� � ������� �������� �� ������� �������
		boolean CW = true; 
		
		// ��������� ������� ��������
		double S = polygon.getAreaOfPolygon();
		
		// ���� ������� �������� ������ ����, �� ����� ������ ����������� ������ ������� �������
		if(S < 0)
			CW = false;
		
		return CW;
	}
	
	/**
	 * ������� ���������� �������, ������� ����� ����� ���������� � �����-�� ������� �������� ��������.
	 * 
	 * @param outer_polygon ������� �������
	 * @param inner_polys_list ������ ���������� ���������
	 * @return ���������� �������, ������� ����� ����� ���������� � �����-�� ������� �������� ��������
	 */
	
	private Mj_Polygon getPolygonWithMinDistance(Mj_List<Mj_Polygon> inner_polys_list) {
		double minDistance = 0; // ����������� ���������� ����� ����� �������	
		int min_count = -1; // ������ ������������� ����������� �������� � ������ ���������� ��������� 
		Mj_Vertex minVertex = null; // ������� ����������� �������� ����� ���� ������������� � �������� ��������
		
		// ��������� ������� �������� ����� �� �����
		Mj_Vertex[] outer_schedule = decompositor.buildSchedule(outer_polygon, Scaning.LEFT_TO_RIGHT);
		Mj_Polygon inner = null; // ������ ���������� ������� ������		
		
		for(int i = 0; i < outer_schedule.length; i++) {
			inner = inner_polys_list.first();
			
			for(int j = 0; j < inner_polys_list.length(); j++) {
				// ��������� ������� �������� ����� �� �����
				Mj_Vertex[] inner_schedule = decompositor.buildSchedule(inner, Scaning.LEFT_TO_RIGHT);
				
				for(int k = 0; k < inner_schedule.length; k++) {
					double min = getDistanceBetweenVertexes(outer_schedule[i], inner_schedule[k]);
					
					if(i == 0 && j == 0 && k == 0) {
						minDistance = min;
						min_count = j;
						minVertex = inner_schedule[k];
						outer_polygon.setVertex(outer_schedule[i]);
					} else {
						if(min < minDistance) {
							minDistance = min;
							min_count = j;
							minVertex = inner_schedule[k];
							outer_polygon.setVertex(outer_schedule[i]);
						}
					}
				}
				
				inner = inner_polys_list.next();
			}
		}
		
		inner_polys_list.first(); // ���������� ���� ������ �� ������� ����� ������
		
		// ���� ������ ���������� �������
		for(int i = 0; i < min_count; i++) {
			inner_polys_list.next();
		}
		
		// ���� ������� �� ���������� �������� ������������ � �������� ��������
		for(int j = 0; j < inner_polys_list.val().size; j++) {
			if(inner_polys_list.val().getVertex().equalsVertex(minVertex)) {
				inner_polys_list.val().setVertex(minVertex);
				break;
			}
			
			inner_polys_list.val().advance();
		}
		
		return inner_polys_list.val();
	}
	
	/**
	 * ��������� ���������� ����� ����� ��������� �� �� �����������.
	 * 
	 * @param v1 ������ �������
	 * @param v2 ������ �������
	 * @return ���������� ����� ����� ���������
	 */
	
	private double getDistanceBetweenVertexes(Mj_Vertex v1, Mj_Vertex v2) {
		return Math.sqrt(Math.pow((v2.point().x - v1.point().x), 2) + Math.pow((v2.point().y - v1.point().y), 2));
	}
	
	/**
	 * ������� ������� �� ��������� ������� ���������.
	 * 
	 * @param array ������ ���������
	 * @return ������� ���� null
	 */
	
	private Mj_Polygon makePolygon(double[] array) {
		if(array.length % 2 == 0) {
			Mj_Point[] p_outer = getPointsArray(array);
			Mj_Vertex[] v_array = getVertexesArray(p_outer);
		
			Mj_ListVertex v_list = new Mj_ListVertex();
			v_list = v_list.arrayToList(v_array, v_array.length);
		
			return new Mj_Polygon(v_list.first());
		}
		
		return null;
	}
	
	/**
	 * ������� ������ ����� �� ��������� ������ ���������.
	 * 
	 * @param array ������ ���������
	 * @return ������ �����
	 */
	
	private Mj_Point[] getPointsArray(double[] array) {
		Mj_Point[] points = new Mj_Point[array.length / 2];
		
		for(int i = 0, j = 0; i < points.length; i++, j += 2)
			points[i] = new Mj_Point(array[j], array[j + 1]);
		
		return points;
	}
	
	/**
	 * ������� ������ ������ �� ��������� ������ �����.
	 * 
	 * @param array ������ �����
	 * @return ������ ������
	 */
	
	private Mj_Vertex[] getVertexesArray(Mj_Point[] array) {
		Mj_Vertex[] vertexes = new Mj_Vertex[array.length];
		
		for(int i = 0; i < array.length; i++)
			vertexes[i] = new Mj_Vertex(array[i]);
		
		return vertexes;
	}
}

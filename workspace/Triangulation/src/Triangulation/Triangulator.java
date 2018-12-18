/*
 * Copyright (c) 01.2018
 */

package Triangulation;

import java.util.LinkedList;

import Triangulation.Decompositor.Scaning;
import Triangulation.Mj_Point.Point_position;
import Triangulation.Mj_Vertex.Rotation;

/**
 * ����� ��� ������������ ���������� ���������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Triangulator {

	/**
	 * ������������� �������� ���������� �������. ��������������, ��� ��� ������������, 
	 * ����� ����� ������� �������� �������� ��� ������� ��������. 
	 *  
	 * 
	 * @param p ��������������� �������
	 * @return ������ �������������, �������������� ��������� ������������ ��������
	 */
	
	// �������������� ������� �������� � ������� ��� ������ �������� ��������.
	enum Chain { UPPER,   // �������
		         LOWER }; // ������
		         
	private Mj_List<Mj_Polygon> triangles = new Mj_List<Mj_Polygon>(); // ������ ������������� ����� ������������
	
	private Mj_Vertex v, // �������������� �������
                      vu, // ��������� �������������� ������� � ������� ������� ��������
                      vl; // ��������� �������������� ������� � ������ ������� ��������
	
	/**
	 * ��������� ������������ ����������� ��������.
	 * 
	 * @param p ������� ��� ������������
	 * @return ������ ������������� ����� ������������ ��������
	 */
	
	public Mj_List<Mj_Polygon> triangulateMonotonePolygon(Mj_Polygon p) {
		// ���� ������, ������� ���� ���������, �� ��� �� ���������� ��������� (��������, ���������� �� 
		// ��� ������������, ��������� � ���� ��������)
		LinkedList<Mj_Vertex> s = new LinkedList<>(); 		
		
		p.leastVertex(p, Scaning.LEFT_TO_RIGHT); 
		
		v = vu = vl = p.getVertex();
		
		s.push(v);
		
		Chain chain = advancePtr(vu, vl, v); // ������� ��� ������ ������� ��������
		
		s.push(v);
		
		while(true) {
			chain = advancePtr(vu, vl, v);
			
			if(adjacent(v, s.getFirst()) && !adjacent(v, s.getLast())) {
				Point_position side = (chain == Chain.UPPER) ? Point_position.LEFT : Point_position.RIGHT;
				Mj_Vertex a = s.getFirst();
				Mj_Vertex b = nextToFirst(s);
				
				while((s.size() > 1) && (b.point().classify(v.point(), a.point()) == side)) {
					if(chain == Chain.UPPER) {
						p.setVertex(b);
						triangles.append(p.split(v));
					} else {
						p.setVertex(v);
						triangles.append(p.split(b));
					}
					
					s.pop();
					a = b;
					b = nextToFirst(s);
				}
				
				s.push(v);
			} else if(!adjacent(v, s.getFirst())) {
				Mj_Polygon q;
				Mj_Vertex t = s.pop();
				
				if(chain == Chain.UPPER) {
					p.setVertex(t);
					q = p.split(v);
				} else {
					p.setVertex(v);
					q = p.split(t);
					q.advance(Rotation.CLOCKWISE);
				}
				
				triangulateFanPolygon(q, triangles);
				
				while(!s.isEmpty())
					s.pop();
				
				s.push(t);
				s.push(v);
			} else {
				p.setVertex(v);
				triangulateFanPolygon(p, triangles);
				
				return triangles;
			}
		}
	}
	
	/**
	 * ���������� ��������� ������� ����� �� ������, ���� NULL, ���� ��� ���.
	 * 
	 * @param s ����
	 * @return ��������� ������� ����� �� ������, ���� NULL, ���� ��� ���
	 */
	
	private Mj_Vertex nextToFirst(LinkedList<Mj_Vertex> s) {
		if(s.size() <= 1)
			return null;
		else
			return s.get(s.indexOf(s.getFirst()) + 1);
	}
	
	/**
	 * ���������� ��������� ���� ������ ��������.
	 * 
	 * @param v �������
	 * @param w �������
	 * @return TRUE, ���� ��� �������� �������, �������� ���������, ����� - FALSE
	 */
	
	private boolean adjacent(Mj_Vertex v, Mj_Vertex w) {
		return ((w.equalsVertex(v.cw())) || (w.equalsVertex(v.ccw())));
	}
	
	/**
	 * ������������� � ����� �� ���� ������� ������ v ��������� ������������� ��������.
	 * 
	 * @param vu ��������� �������������� ������� � ������� ������� ��������
	 * @param vl ��������� �������������� ������� � ������ ������� ��������
	 * @param v �������������� �������
	 * @return �������������� ������� �������� � ������� ��� ������ �������� ��������
	 */
	
	private Chain advancePtr(Mj_Vertex vu, Mj_Vertex vl, Mj_Vertex v) {
		Mj_Vertex vun = vu.cw();
		Mj_Vertex vln = vl.ccw();
		
		if(vun.point().less(vln.point())) {
			this.v = this.vu = vun;
			
			return Chain.UPPER;
		} else {
			this.v = this.vl = vln;
			
			return Chain.LOWER;
		}
	}
	
	/**
	 * ������������ ��������� n-�������� p �� n - 2 ������������� � ��������� �� � ������ 
	 * �������������.
	 * 
	 * @param p �������
	 * @param triangles ������ �������������
	 */
	
	private void triangulateFanPolygon(Mj_Polygon p, Mj_List<Mj_Polygon> triangles) {
		Mj_Vertex w = p.getVertex().cw().cw();
		int size = p.size;
		
		for(int i = 3; i < size; i++) {
			triangles.append(p.split(w));
			w = w.cw();
		}
		
		triangles.append(p);
	}
}

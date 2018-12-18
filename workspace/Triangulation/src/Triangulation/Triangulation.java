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
 * Класс для триангуляции любых по форме полигонов (включая полигоны, содержащие внутренние области).
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Triangulation {
	
	// При расщеплении полигона, чтобы небыло двух одинаковых ребер, одно из них смещаем на эту величину
	public static double delta = 0.1; 
	// Оригинальные координаты до добавления delta и поворота при декомпозиции
	public static HashMap<Double, Double> org_coords = new HashMap<Double, Double>();
	
	private Decompositor decompositor = new Decompositor();
	private Triangulator triangulator = new Triangulator();
	private Mj_Polygon outer_polygon; // Внешний полигон до декомпозиции на монотонные полигоны
	private Mj_List<Mj_Polygon> monotone_polygons; // Список монотонных полигонов после декомпозиции внешнего полигона
	private Mj_List<Mj_Polygon> triangles = new Mj_List<Mj_Polygon>(); // Список треугольников после триангуляции
		
	/**
	 * Функция получает на вход массив точек с координатами в формате (x, y), из которых состоит полигон.
	 * Также функция получает на вход второй аргумент в виде массива массивов точек с координатами в 
	 * формате (x, y) полигонов, которые располагаются внутри полигона, заданного в первом аргументе.
	 * В качестве второго аргумента можно задавать значение NULL. Затем функция разбивает исходный полигон 
	 * на монотонный полигон(ы) и возвращает его (их) в виде массива массивов точек с координатами в 
	 * формате (x, y).
	 * 
	 * @param outer_polygon массив точек с координатами в формате (x, y), из которых состоит полигон
	 * @param inner_polygons массив массивов точек с координатами в формате (x, y) полигонов, которые 
	 * располагаются внутри полигона, заданного в первом аргументе
	 * @return массив массивов точек с координатами в формате (x, y) монотонных полигонов либо null
	 */
	
	public double[][] getMonotonePolygons(double[] outer, 
			                              double[][] inners) {
		Mj_List<Mj_Polygon> inner_polys_list = new Mj_List<Mj_Polygon>(); // Внутренние полигоны, если они есть
		
		// Проверяем на четность. Если не четное кол-во координат, то 
		// полигон не удастся построить.
		if(outer.length % 2 == 0) {
			outer_polygon = makePolygon(outer);
			
			// Проверяем наличие внутренних полигонов
			if(inners != null) {				
				// Проверяем каждый внутренний полигон на четность и заполняем список внутренних полигонов
				for(int i = 0; i < inners.length; i++) {
					if(inners[i].length % 2 == 0)
						inner_polys_list.append(makePolygon(inners[i]));
				}
				
				// Избавляемся от внутренних полигонов
				removeInnerPolygons(inner_polys_list);
			} else {
				// Принимаем, что изначально обход от вершины к вершине полигона по часовой стрелке
				boolean outer_CW = getDirectionOfPolygonVertexes(outer_polygon); 
				
				// Если обход вершин полигона против часовой стрелки, то меняем его на обратный
				if(!outer_CW) {
					outer_polygon.changeCircumventPoints();
					outer_CW = true; 
				}
			}
		}
		
		// Декомпозиция полигона на монотонные части
		monotone_polygons = decompositor.regularize(outer_polygon);
		
		// Триангуляция монотонных полигонов
		return triangulation();
	}
	
	/**
	 * Выполняет триангуляцию монотонных полигонов из списка и возвращает 
	 * список треугольников в виде массива массивов с координатами. Координаты
	 * треугольников храняться в виде: array_1 = {x1,y1, x2,y2, x3,y3}.
	 * 
	 * 
	 * @return список треугольников в виде массива массивов с координатами
	 */
	
	private double[][] triangulation() {
		if(monotone_polygons != null && monotone_polygons.length() > 0) {
			monotone_polygons.first();
		
			for(int i = 0; i < monotone_polygons.length(); i++) {
				triangles.append(triangulator.triangulateMonotonePolygon(monotone_polygons.val()));
				monotone_polygons.next();
			}
		
			// Возвращает полигонам оригинальные (изначальные) координаты вершин,
			// которые менялись при приращении и повороте
			decompositor.returnOriginalView(triangles);
		
			return getArraysCoords();
		} else
			return null;
	}
	
	/**
	 * Преобразует координаты вершин треугольников в массив массивов.
	 * 
	 * @return массив массивов с координатами
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
	 * Если полигон содержит в себе внутренние полигоны, то эта функция избавляется
	 * от них. В итоге получаются полигон без внутренних полигонов. Смысл алгоритма 
	 * заключается в том, что берем каждую точку внешнего полигона и ищем самую ближнею 
	 * точку какого-нибудь внутреннего полигона. Когда находим, то проверяем направление 
	 * обхода внешнего полигона, сравниваем с направлением обхода внутреннего полигона, 
	 * которому принадлежит найденная точка, если надо, меняем направление обхода внутреннего
	 * полигона. Затем делаем расщепление по двум точкам и остается только внешний полигон.
	 * 
	 * @param inner_polys_list внутренние полигоны
	 */
	
	private void removeInnerPolygons(Mj_List<Mj_Polygon> inner_polys_list) {	
		int size = inner_polys_list.length(); // Размер списка внутренних полигонов
		
		for(int i = 0; i < size; i++) {
			inner_polys_list.first(); // Переходим на первый полигон в списке
			
			// Находим внутренний полигон наиболее близко расположенный к внешнему
			inner_polys_list.val(getPolygonWithMinDistance(inner_polys_list));
			// Объединяем два полигона в один внешний и удаляем из списка внутренних полигонов 
			// полигон, который объединили с внешним
			unionPolygons(inner_polys_list.remove());
		}
	}
	
	/**
	 * Объединяет внешний полигон с внутренним. В итоге получается один внешний полигон.
	 * 
	 * @param outer_polygon внешний полигон
	 * @param inner_polygon внутренний полигон
	 */ 
	
	private void unionPolygons(Mj_Polygon inner_polygon) {
		// Направление обхода вершин во внешнем полигоне
		boolean outer_CW = getDirectionOfPolygonVertexes(outer_polygon); 
		// Направление обхода вершин во внутреннем полигоне
		boolean inner_CW = getDirectionOfPolygonVertexes(inner_polygon);
		
		// Если обход вершин полигона против часовой стрелки, то меняем его на обратный
		if(!outer_CW) {
			outer_polygon.changeCircumventPoints();
			outer_CW = true; 
		}
		
		// Если направления обхода вершин полигонов совпадают, то меняем направление обхода
		// у внутреннего полигона на противоположное
		if(outer_CW == inner_CW) {
			inner_polygon.changeCircumventPoints();
		}
		
		// Разрезает внешний полигон по двум точкам (наиближайшие точки внешнего и внутреннего
		// полигонов), при этом создает их дубликаты. В итоге, внутренний полигон становится 
		// частью внешнего.
		outer_polygon.getVertex().split_triangle(inner_polygon.getVertex(), outer_CW);
		
		// Изменяем размер полигона
		outer_polygon.resize();
	}
	
	/**
	 * Определяет направление обхода от точки к следующей точки в полигоне.
	 * 
	 * @param polygon полигон
	 * @return TRUE, если обход осуществляется по часовой стрелке, иначе - FALSE 
	 */
	
	private boolean getDirectionOfPolygonVertexes(Mj_Polygon polygon) {
		// Принимаем, что изначально обход от вершины к вершине полигона по часовой стрелке
		boolean CW = true; 
		
		// Вычисляем площадь полигона
		double S = polygon.getAreaOfPolygon();
		
		// Если площадь полигона меньше нуля, то обход вершин совершается против часовой стрелки
		if(S < 0)
			CW = false;
		
		return CW;
	}
	
	/**
	 * Находит внутренний полигон, который ближе всего расположен к какой-то вершине внешнего полигона.
	 * 
	 * @param outer_polygon внешний полигон
	 * @param inner_polys_list список внутренних полигонов
	 * @return внутренний полигон, который ближе всего расположен к какой-то вершине внешнего полигона
	 */
	
	private Mj_Polygon getPolygonWithMinDistance(Mj_List<Mj_Polygon> inner_polys_list) {
		double minDistance = 0; // Минимальное расстояние между двумя точками	
		int min_count = -1; // Индекс наиближайшего внутреннего полигона в списке внутренних полигонов 
		Mj_Vertex minVertex = null; // Вершина внутреннего полигона ближе всех расположенная к внешнему полигону
		
		// Сортируем вершины полигона слева на право
		Mj_Vertex[] outer_schedule = decompositor.buildSchedule(outer_polygon, Scaning.LEFT_TO_RIGHT);
		Mj_Polygon inner = null; // Первый внутренний полигон списка		
		
		for(int i = 0; i < outer_schedule.length; i++) {
			inner = inner_polys_list.first();
			
			for(int j = 0; j < inner_polys_list.length(); j++) {
				// Сортируем вершины полигона слева на право
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
		
		inner_polys_list.first(); // Перемещаем окно списка на первого члена списка
		
		// Ищем нужный внутренний полигон
		for(int i = 0; i < min_count; i++) {
			inner_polys_list.next();
		}
		
		// Ищем вершину во внутреннем полигоне наиближайшую к внешнему полигону
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
	 * Вычисляет расстояние между двумя вершинами по их координатам.
	 * 
	 * @param v1 первая вершина
	 * @param v2 вторая вершина
	 * @return расстояние между двумя вершинами
	 */
	
	private double getDistanceBetweenVertexes(Mj_Vertex v1, Mj_Vertex v2) {
		return Math.sqrt(Math.pow((v2.point().x - v1.point().x), 2) + Math.pow((v2.point().y - v1.point().y), 2));
	}
	
	/**
	 * Создает полигон по заданному массиву координат.
	 * 
	 * @param array массив координат
	 * @return полигон либо null
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
	 * Создает массив точек из заданноко списка координат.
	 * 
	 * @param array массив координат
	 * @return массив точек
	 */
	
	private Mj_Point[] getPointsArray(double[] array) {
		Mj_Point[] points = new Mj_Point[array.length / 2];
		
		for(int i = 0, j = 0; i < points.length; i++, j += 2)
			points[i] = new Mj_Point(array[j], array[j + 1]);
		
		return points;
	}
	
	/**
	 * Создает массив вершин из заданноко списка точек.
	 * 
	 * @param array массив точек
	 * @return массив вершин
	 */
	
	private Mj_Vertex[] getVertexesArray(Mj_Point[] array) {
		Mj_Vertex[] vertexes = new Mj_Vertex[array.length];
		
		for(int i = 0; i < array.length; i++)
			vertexes[i] = new Mj_Vertex(array[i]);
		
		return vertexes;
	}
}

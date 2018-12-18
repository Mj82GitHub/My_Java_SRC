/*
 * Copyright (c) 09.2017
 */

package Triangulation;

/** 
 * Класс вершины полигона.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Mj_Vertex extends Node {
	
	private Mj_Point point; // Координаты вершины
	
	// Перемещение к следующей точке
	enum Rotation { CLOCKWISE,           // По часовой стрелке
		            COUNTER_CLOCKWISE }; // Против часовой стрелки
	
	public Mj_Vertex() {
   		super();
   		
   		point = new Mj_Point();
	}
		            
	public Mj_Vertex(double x, double y) {
		super();
		
		point = new Mj_Point(x, y);
	}
	
	public Mj_Vertex(Mj_Point p) {
		super();
		
		point = p;
	}
	
	public Mj_Vertex(Mj_Vertex v) {
		super();
		
		point = new Mj_Point();
		point.x = v.point.x;
		point.y = v.point.y;
		
		next = v.next;
		prev = v.prev;
	}
		
	/**
	 * Возвращает следующую вершину полигона.
	 * 
	 * @return следующая вершина полигона
	 */
	
	public Mj_Vertex cw() {
		return (Mj_Vertex) next;
	}
	
	/**
	 * Возвращает предыдущую вершину полигона.
	 * 
	 * @return предыдущая вершина полигона
	 */
	
	public Mj_Vertex ccw() {
		return (Mj_Vertex) prev;
	}
	
	/**
	 * Определяет как расположена соседняя вершина по отношению к текущей.
	 * 
	 * @param rotation значение перечисления {CLOCKWISE, COUNTER_CLOCKWISE}
	 * @return возвращает одно из значений перечисления
	 */
	
	public Mj_Vertex neighbor(Rotation rotation) {
		return ((rotation == Rotation.CLOCKWISE) ? cw() : ccw());
	}
	
	/**
	 * Возвращает точку на плоскости, в которой находится текущая вершина.
	 * 
	 * @return точка на плоскости
	 */
	
	public Mj_Point point() {
		return point;
	}
	
	/**
	 * Включает вершину сразу же после текущей вершины.
	 * 
	 * @param v вставляемая вершина
	 * @return вставляемая вершина
	 */
	
	public Mj_Vertex insert(Mj_Vertex v) {
		return (Mj_Vertex) ((Node) this).insert(v);
	}
	
	/**
	 * Удаляет текущую вершину из данного связаного списка.
	 * 
	 * @return удаленная вершина
	 */
	
	public Mj_Vertex remove() {
		return (Mj_Vertex) ((Node) this).n_remove();
	}
	
	/**
	 * Используется для присоединения к текущей вершине заданной аргументом выршины. 
	 * 
	 * @param node присоединяемая к текущей вершина
	 */
	
	public void splice(Mj_Vertex b) {
		((Node) this).splice(b);
	}
	
	/**
	 * Разделяет полигон вдоль хорды, соединяющей текущую вершину (пусть, вершина A) с вершиной B. 
	 * 
	 * @param b вершина хорды
	 * @return вершину, являющуюся дубликатом вершины B.
	 */
	
	public Mj_Vertex split(Mj_Vertex b) {
		// Заносим bp перед вершиной b
		Mj_Vertex bp = b.ccw().insert(new Mj_Vertex(new Mj_Point(b.point().x, b.point().y)));
		// Заносим ap после текущей вершины
		insert(new Mj_Vertex(new Mj_Point(point().x, point().y)));
		splice(bp);
		
		return bp;
	}
	
	/**
	 * Разделяет полигон вдоль хорды, соединяющей текущую вершину (пусть, вершина A) с вершиной B. 
	 * Специальная функция для триангуляции полигонов.
	 * 
	 * @param b вершина хорды
	 * @param outer_CW признак того, что боход точек внешнего полина по часовой стрелке
	 * @return вершину, являющуюся дубликатом вершины B.
	 */
	
	public Mj_Vertex split_triangle(Mj_Vertex b, boolean outer_CW) {
		// Заносим bp перед вершиной b
		Mj_Vertex bp = b.ccw().insert(new Mj_Vertex(new Mj_Point(b.point().x, b.point().y)));
		// Заносим ap после текущей вершины
		insert(new Mj_Vertex(new Mj_Point(point().x, point().y)));
		splice(bp);

		// Вычисляем угол вектора к оси X (против часовой стрелки)
		Edge edge = new Edge(bp.point, ((Mj_Vertex) bp.next).point);
		double angle = Math.round((edge.dest.subtraction(edge.org)).polarAngle());
		
//		System.out.println("Angle: " + angle + " [" + bp.point.x + ", " + bp.point.y + " - " + ((Mj_Vertex) bp.next).point.x + ", " + ((Mj_Vertex) bp.next).point.y + "]");
		
		// Изменяем одинаковые координаты ребра при разрезании полигона
		if(outer_CW) {
			if(angle == 0 || (angle >= 315 && angle <= 360)) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				bp.point.y += Triangulation.delta;
				((Mj_Vertex) bp.next).point.y += Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
			} else if(angle >= 45 && angle <= 135) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				bp.point.x -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.x -= Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
			} else if(angle > 135 && angle <= 180) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				bp.point.y -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.y -= Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
			} else if(angle >= 225 && angle <= 270) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				bp.point.x += Triangulation.delta;
				((Mj_Vertex) bp.next).point.x += Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
			}
		} else {
			if(angle == 0 || (angle >= 315 && angle <= 360)) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				bp.point.y -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.y -= Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
			} else if(angle >= 45 && angle <= 135) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				bp.point.x += Triangulation.delta;
				((Mj_Vertex) bp.next).point.x += Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
			} else if(angle > 135 && angle <= 180) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				bp.point.y += Triangulation.delta;
				((Mj_Vertex) bp.next).point.y += Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
			} else if(angle >= 225 && angle <= 270) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				bp.point.x -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.x -= Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
			}
		}
		
		return bp;
	}
	
	/**
	 * Определяет эквивалентность двух вершин.
	 *  
	 * @param p вершина для сравнения
	 * @return возвращает TRUE, если вершины эквивалентны (равны), иначе FALSE
	 */
	
	public boolean equalsVertex(Mj_Vertex v) {
		if((point.x == v.point.x) && (point.y == v.point.y))
			return true;
		else
			return false;
		
	}
}

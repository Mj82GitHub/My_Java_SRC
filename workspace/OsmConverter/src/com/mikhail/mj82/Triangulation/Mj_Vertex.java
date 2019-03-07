/*
 * Copyright (c) 09.2017
 */

package com.mikhail.mj82.Triangulation;

/** 
 * Класс вершины полигона.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Mj_Vertex extends Mj_Node {
	
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
		return (Mj_Vertex) ((Mj_Node) this).insert(v);
	}
	
	/**
	 * Удаляет текущую вершину из данного связаного списка.
	 * 
	 * @return удаленная вершина
	 */
	
	public Mj_Vertex remove() {
		return (Mj_Vertex) ((Mj_Node) this).n_remove();
	}
	
	/**
	 * Используется для присоединения к текущей вершине заданной аргументом выршины. 
	 * 
	 * @param node присоединяемая к текущей вершина
	 */
	
	public void splice(Mj_Vertex b) {
		((Mj_Node) this).splice(b);
	}
	
	/**
	 * Объелиняет два полигона по одному ребру. Задаются два ребра двух полигонов
	 * с обходом в одну и ту же сторону, и по этим ребрам происходит слияние
	 * двух полигонов в один.
	 * 
	 * 
	 * @param bp вершина ребра оъединяемого полигона
	 */
	
	public void union(Mj_Vertex bp) {
		Mj_Vertex tmp = (Mj_Vertex) bp.next();
		((Mj_Vertex) next()).splice(bp);
		splice(tmp);
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
	
	public Mj_Vertex split_and_offset_triangle(Mj_Vertex b, boolean outer_CW, Mj_Polygon outer_polygon) {
		// Заносим bp перед вершиной b
		Mj_Vertex bp = b.ccw().insert(new Mj_Vertex(new Mj_Point(b.point().x, b.point().y)));
		// Заносим ap после текущей вершины
		insert(new Mj_Vertex(new Mj_Point(point().x, point().y)));
		splice(bp);
		
		Mj_Vertex a = this; // Точка внешнего полигона
		Mj_Vertex ap = (Mj_Vertex) bp.next(); // Точка внешнего полигона после рассечения
		
		Mj_Vertex a_prev = (Mj_Vertex) ((Mj_Vertex) b.prev()).prev(); // Перед точкой a
		Mj_Vertex ap_next = (Mj_Vertex) ((Mj_Vertex) bp.next()).next(); // После точки ap
		
		Mj_Vertex b_next = (Mj_Vertex) b.next(); // После точки b
		Mj_Vertex bp_prev = (Mj_Vertex) bp.prev(); // Перед точкой bp
		
		// Длины ребер до и после точек рассечения
		double l_a = (a_prev.point().subtraction(a.point())).length();
		double l_b = (b_next.point().subtraction(b.point())).length();
		double l_ap = (ap_next.point().subtraction(ap.point())).length();
		double l_bp = (bp_prev.point().subtraction(bp.point())).length();	
		
		// Объект для изменения координат вершин в месте рассечения
		VertexOffseter vOff = new VertexOffseter();
		
		// Сохраняем точки разреза внешнего полигона
		if(l_a >= l_ap && l_b >= l_bp) {
			// Сохраняем оригинальные точки разреза, чтобы
			// по ним больше не делали еще один разрез
			Triangulation.useOuterVertexes.add(new Mj_Vertex(a));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(b));
			
			// Смещаем координаты вершин в месте рассечения полигона так, чтобы небыло самопересечений
			vOff.offset(a, b, outer_polygon, Triangulation.delta);
			
			// Сохраняем измененные точки разреза, чтобы
			// по ним больше не делали еще один разрез
			Triangulation.useOuterVertexes.add(new Mj_Vertex(a));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(b));
		} else if(l_a >= l_ap && l_b < l_bp) {
			// Сохраняем оригинальные точки разреза, чтобы
			// по ним больше не делали еще один разрез
			Triangulation.useOuterVertexes.add(new Mj_Vertex(a));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
			
			// Смещаем координаты вершин в месте рассечения полигона так, чтобы небыло самопересечений
			vOff.offset(a, bp, outer_polygon, Triangulation.delta);
			
			// Сохраняем измененные точки разреза, чтобы
			// по ним больше не делали еще один разрез
			Triangulation.useOuterVertexes.add(new Mj_Vertex(a));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
		} else if(l_a < l_ap && l_b >= l_bp) {
			// Сохраняем оригинальные точки разреза, чтобы
			// по ним больше не делали еще один разрез
			Triangulation.useOuterVertexes.add(new Mj_Vertex(ap));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(b));
			
			// Смещаем координаты вершин в месте рассечения полигона так, чтобы небыло самопересечений
			vOff.offset(ap, b, outer_polygon, Triangulation.delta);
			
			// Сохраняем измененные точки разреза, чтобы
			// по ним больше не делали еще один разрез
			Triangulation.useOuterVertexes.add(new Mj_Vertex(ap));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(b));
		} else if(l_a < l_ap && l_b < l_bp) {
			// Сохраняем оригинальные точки разреза, чтобы
			// по ним больше не делали еще один разрез
			Triangulation.useOuterVertexes.add(new Mj_Vertex(ap));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
			
			// Смещаем координаты вершин в месте рассечения полигона так, чтобы небыло самопересечений
			vOff.offset(ap, bp, outer_polygon, Triangulation.delta);
			
			// Сохраняем измененные точки разреза, чтобы
			// по ним больше не делали еще один разрез
			Triangulation.useOuterVertexes.add(new Mj_Vertex(ap));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
		}
				
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
	
	public Mj_Vertex split_triangle(Mj_Vertex b, boolean outer_CW, Mj_Polygon outer_polygon) {
		// Заносим bp перед вершиной b
		Mj_Vertex bp = b.ccw().insert(new Mj_Vertex(new Mj_Point(b.point().x, b.point().y)));
		// Заносим ap после текущей вершины
		insert(new Mj_Vertex(new Mj_Point(point().x, point().y)));
		splice(bp);

		// Вычисляем угол вектора к оси X (против часовой стрелки)
		Edge edge = new Edge(bp.point, ((Mj_Vertex) bp.next).point);
		double angle = Math.round((edge.dest.subtraction(edge.org)).polarAngle());
		
//		System.out.println("Angle: " + angle + " [" + bp.point.x + ", " + bp.point.y + " - " + ((Mj_Vertex) bp.next).point.x + ", " + ((Mj_Vertex) bp.next).point.y + "]");
		
		// Изменяем приращения одинаковых точек ребра при разрезании полигона
		if(!outer_CW) {
			if(angle == 0 || (angle >= 315 && angle <= 360)) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// Сохраняем оригинальные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// Изменяем точки разреза
				bp.point.y += Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y += Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				// Сохраняем соответствие оригинальных координат и смещенных
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// Сохраняем измененные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle > 0 && angle < 45) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// Сохраняем оригинальные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// Изменяем точки разреза
				bp.point.y += Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y += Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// Сохраняем измененные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle >= 45 && angle <= 135) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				// Сохраняем оригинальные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// Изменяем точки разреза
				bp.point.x -= Triangulation.delta;
				bp.point.x = Decompositor.round7(bp.point.x);
				
				((Mj_Vertex) bp.next).point.x -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.x = Decompositor.round7(((Mj_Vertex) bp.next).point.x);
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
				
				// Сохраняем измененные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle > 135 && angle <= 180) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// Сохраняем оригинальные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// Изменяем точки разреза
				bp.point.y += Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y += Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// Сохраняем измененные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle >180 && angle < 225) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// Сохраняем оригинальные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// Изменяем точки разреза
				bp.point.y -= Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// Сохраняем измененные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle >= 225 && angle <= 270) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				// Сохраняем оригинальные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// Изменяем точки разреза
				bp.point.x += Triangulation.delta;
				bp.point.x = Decompositor.round7(bp.point.x);
				
				((Mj_Vertex) bp.next).point.x += Triangulation.delta;
				((Mj_Vertex) bp.next).point.x = Decompositor.round7(((Mj_Vertex) bp.next).point.x);
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
				
				// Сохраняем измененные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle > 270 && angle < 315) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				// Сохраняем оригинальные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// Изменяем точки разреза
				bp.point.x += Triangulation.delta;
				bp.point.x = Decompositor.round7(bp.point.x);
				
				((Mj_Vertex) bp.next).point.x += Triangulation.delta;
				((Mj_Vertex) bp.next).point.x = Decompositor.round7(((Mj_Vertex) bp.next).point.x);
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
				
				// Сохраняем измененные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			}
		} else {
			if(angle == 0 || (angle >= 315 && angle <= 360)) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// Сохраняем оригинальные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// Изменяем точки разреза
				bp.point.y -= Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// Сохраняем измененные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle > 0 && angle < 45) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// Сохраняем оригинальные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// Изменяем точки разреза
				bp.point.y -= Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// Сохраняем измененные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle >= 45 && angle <= 135) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				// Сохраняем оригинальные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// Изменяем точки разреза
				bp.point.x += Triangulation.delta;
				bp.point.x = Decompositor.round7(bp.point.x);
				
				((Mj_Vertex) bp.next).point.x += Triangulation.delta;
				((Mj_Vertex) bp.next).point.x = Decompositor.round7(((Mj_Vertex) bp.next).point.x);
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
				
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
								
				// Изменяем точки разреза
				bp.point.y -= Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// Сохраняем измененные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle > 135 && angle <= 180) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// Сохраняем оригинальные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// Изменяем точки разреза
				bp.point.y += Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y += Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				// Изменяем точки разреза
				bp.point.x += Triangulation.delta;
				bp.point.x = Decompositor.round7(bp.point.x);
				
				((Mj_Vertex) bp.next).point.x += Triangulation.delta;
				((Mj_Vertex) bp.next).point.x = Decompositor.round7(((Mj_Vertex) bp.next).point.x);
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
				
				// Сохраняем измененные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle >180 && angle < 225) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// Сохраняем оригинальные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// Изменяем точки разреза
				bp.point.y += Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y += Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// Сохраняем измененные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle >= 225 && angle <= 270) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				// Сохраняем оригинальные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// Изменяем точки разреза
				bp.point.x -= Triangulation.delta;
				bp.point.x = Decompositor.round7(bp.point.x);
				
				((Mj_Vertex) bp.next).point.x -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.x = Decompositor.round7(((Mj_Vertex) bp.next).point.x);
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
				
				// Сохраняем измененные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle > 270 && angle < 315) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				// Сохраняем оригинальные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// Изменяем точки разреза
				bp.point.x -= Triangulation.delta;
				bp.point.x = Decompositor.round7(bp.point.x);
				
				((Mj_Vertex) bp.next).point.x -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.x = Decompositor.round7(((Mj_Vertex) bp.next).point.x);
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
				
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
								
				// Изменяем точки разреза
				bp.point.y -= Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// Сохраняем измененные точки разреза, чтобы
				// по ним больше не делали еще один разрез
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			}
		}
		
		return bp;
	}
	
	/**
	 * Определяет эквивалентность двух вершин.
	 *  
	 * @param v вершина для сравнения
	 * @return возвращает TRUE, если вершины эквивалентны (равны), иначе FALSE
	 */
	
	public boolean equalsVertex(Mj_Vertex v) {
		if((point.x == v.point.x) && (point.y == v.point.y) && next == v.next && prev == v.prev)
			return true;
		else
			return false;
		
	}
	
	/**
	 * Определяет эквивалентность координат двух вершин.
	 *  
	 * @param v вершина для сравнения
	 * @return возвращает TRUE, если вершины эквивалентны (равны), иначе FALSE
	 */
	
	public boolean equalsCoordsVertex(Mj_Vertex v) {
		if(v != null) {
			if((point.x == v.point.x) && (point.y == v.point.y))
				return true;
			else
				return false;
		}
		
		return false;
	}
}

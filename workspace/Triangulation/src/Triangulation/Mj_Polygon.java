/*
 * Copyright (c) 09.2017
 */

package Triangulation;

import Triangulation.Decompositor.Scaning;
import Triangulation.Mj_Point.Point_position;
import Triangulation.Mj_Vertex.Rotation;

/**
 * Класс полигона.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Mj_Polygon {

	private Mj_Vertex v; // Текущая вершина
	int size; // Размер полигона
	
	/**
	 * Изменяет значение переменной size.
	 */
	
	public Mj_Polygon() {
		v = null;
		size = 0;
	}
	
	public Mj_Polygon(Mj_Polygon p) {
		size = p.size;
		
		if(size == 0)
			v = null;
		else {
			v = new Mj_Vertex(p.point());
			
			for(int i = 1; i < size; i++) {
				p.advance(Rotation.CLOCKWISE);
				v = v.insert(new Mj_Vertex(p.point()));
			}
			
			p.advance(Rotation.CLOCKWISE);
			v = v.cw();
		}			
	}
	
	public Mj_Polygon(Mj_Vertex v) {
		this.v = v; // Первая вершина из списка вершин
		
		resize();
	}
	
	/**
	 * Изменяет значение размера полигона.
	 */
	
	public void resize() {
		if(v == null) 
			size = 0;
		else {
			Mj_Vertex v = this.v.cw();
			
			for(size = 1; !(v.equalsVertex(this.v) && v.equals(this.v)); ++size, v = v.cw()) {}
		}
	}
	
	/**
	 * Удаляет полигон, но прежде освобождает все вершины.
	 */
	
	public void delete_polygon() {
		if(v != null) {
			Mj_Vertex w = v.cw();
			
			while(v != w) {		
				w = v.cw();
			}
			
			v = null;
		}
		
//		delete();
	}
	
	/**
	 * Уничтожает системой ссылку на объект.
	 */
	
	private void delete() {
		try {
			this.finalize(); // Освобождаем ресурсы системы
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Возвращает текущую вершину данного полигона.
	 * 
	 * @return текущая вершина данного полигона
	 */
	
	public Mj_Vertex getVertex() {
		return v;
	}
	
	/**
	 * Возвращает размер полигона (кол-во вершин, из которых состоит полигон).
	 * @return
	 */
	
	public int size() {
		return size;
	}
	
	/**
	 * Возвращает точку на плоскости, которая соответствует текущей вершине.
	 * 
	 * @return точка на плоскости
	 */
	
	public Mj_Point point() {
		return v.point();
	}
	
	/**
	 * Возвращает текущее ребро (начинается в текущей вершине и заканчивается в 
	 * следующей после нее вершине).
	 * 
	 * @return текущее ребро
	 */
	
	public Edge edge() {
		return new Edge(point(), v.cw().point());
	}
	
	/**
	 * Возвращает следующую вершину полигона.
	 * 
	 * @return следующая вершина полигона
	 */
	
	public Mj_Vertex cw() {
		return v.cw();
	}
	
	/**
	 * Возвращает предыдущую вершину полигона.
	 * 
	 * @return предыдущая вершина полигона
	 */
	
	public Mj_Vertex ccw() {
		return v.ccw();
	}
	
	/**
	 * Определяет как расположена соседняя вершина по отношению к текущей.
	 * 
	 * @param rotation значение перечисления {CLOCKWISE, COUNTER_CLOCKWISE}
	 * @return возвращает одно из значений перечисления
	 */
	
	public Mj_Vertex neighbor(Rotation rotation) {
		return v.neighbor(rotation);
	}
	
	/**
	 * Перемещается на следующую или предыдущую вершину полигона, в зависимости от
	 * заданного аргумента.
	 * 
	 * @param rotation значение перечисления {CLOCKWISE, COUNTER_CLOCKWISE}
	 * @return вершину полигона, в зависимости от заданного аргумента
	 */
	
	public Mj_Vertex advance(Rotation rotation) {
		return v = v.neighbor(rotation);
	}
	
	/**
	 * Перемещается на следующую вершину полигона.
	 * 
	 * @return следующую вершину полигона
	 */
	
	public Mj_Vertex advance() {
		return v = v.neighbor(Rotation.CLOCKWISE);
	}
	
	/**
	 * Возврвщает вершину, указанную в качестве аргумента.
	 * 
	 * @param v указанную в качестве аргумента вершина
	 * @return вершина, указанная в качестве аргумента
	 */
	
	public Mj_Vertex setVertex(Mj_Vertex v) {
		return this.v = v; 
	}
	
	/**
	 * Вносит новую вершину после текущей и делает ее текущей вершиной полигона.
	 * 
	 * @param p новая вершина
	 * @return новую текущую вершину полигона
	 */
	
	public Mj_Vertex insert(Mj_Point p) {
		if(size++ == 0)
			v = new Mj_Vertex(p);
		else
			v = v.insert(new Mj_Vertex(p));
		
		return v;
	}
	
	/**
	 * Удаляет текущую вершину. 
	 */
	
	public void remove() {
		Mj_Vertex v = this.v;
		
		this.v = (--size == 0) ? null : this.v.ccw();
		v.remove();
	}
	
	/**
	 * Возвращает указатель на новый полигон. 
	 * 
	 * @param v вершина хорды
	 * @return указатель на новый полигон
	 */
	
	public Mj_Polygon split(Mj_Vertex b) {
		Mj_Vertex bp = v.split(b);
		resize();
		
		return new Mj_Polygon(bp);
	}
	
	/**
	 * Обсчитывает точку s и полигон p и возвращает значение TRUE только в том случае,
	 * если точка лежит внутри полигона p (в том числе и на его границе).
	 * 
	 * @param s точка
	 * @param p полигон
	 * @return TRUE, если точка лежит внутри полигона, иначе - FALSE
	 */
	
	public boolean pointToConvexPolygon(Mj_Point s, Mj_Polygon p) {
		if(p.size() == 1)
			return (s == p.point());
		
		if(p.size() == 2) {
			Point_position c = s.classify(p.edge());
			
			return ((c == Point_position.BETWEEN) || (c == Point_position.ORIGIN) || (c == Point_position.DESTINATION));
		}
		
		Mj_Vertex org = p.getVertex();
		
		for(int i = 0; i < p.size(); i++, p.advance(Rotation.CLOCKWISE)) {
			if(s.classify(p.edge()) == Point_position.LEFT) {
				p.setVertex(org);
				
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Производит поиск наименьшей вершины в полигоне. Здесь подразумевается вершина,
	 * которая меньше всех остальных при линейном упорядочевании точек, задаваемом
	 * функцией сравнения.
	 * 
	 * @param p полигон
	 * @param cmp ф-ция сравнения
	 * @return наименьшая вершина в полигоне
	 */
	
	public Mj_Vertex leastVertex(Mj_Polygon p, Scaning cmp) {
		Mj_Vertex bestV = p.getVertex();
		p.advance(Rotation.CLOCKWISE);
		
		for(int i = 1; i < p.size; p.advance(Rotation.CLOCKWISE), i++) {
			switch(cmp) {
			case LEFT_TO_RIGHT:
				if(CompareFunc.leftToRightCmp(p.getVertex(), bestV) < 0)
					bestV = p.getVertex();
				
				p.setVertex(bestV);
				
				return bestV;
			case RIGHT_TO_LEFT:
				if(CompareFunc.rightToLeftCmp(p.getVertex(), bestV) < 0)
					bestV = p.getVertex();
				
				p.setVertex(bestV);
				
				return bestV;
			}
		}
		
		return null;
	}
	
	/**
	 * Вычисляет площадь полигона.
	 * Для расчетов считается, что последняя вершина полигона совмещена с первой.
	 * A = 1/2 * SUM(i=1; i=N-1) (XiYi+1 - Xi+1Yi) + XnY1 - X1Yn
	 * 
	 * @param polygon полигон
	 * @return площадь полигона
	 */
	
	public double getAreaOfPolygon() {
		double x1_xN = v.point().x; // Координата Х первой/последней вершины полигона
		double y1_yN = v.point().y; // Координата Y первой/последней вершины полигона
		
		double sum = 0;
		Mj_Vertex v = this.v;
		
		for(int i = 0; i < size; i++) {
			double a = ((Mj_Vertex) v).point().x * ((Mj_Vertex) v.next()).point().y;
			double b = ((Mj_Vertex) v.next()).point().x * ((Mj_Vertex) v).point().y;
			
			sum += (a - b) + (x1_xN * y1_yN) - (x1_xN * y1_yN);
			
			v = (Mj_Vertex) v.next();
		}
		
		return sum / 2;
	}
	
	/**
	 * Меняет напавление обхода вершин полигона на противоположное.
	 * 
	 * @return полигон с измененным направлением обхода вершин
	 */
	
	public Mj_Polygon changeCircumventPoints() {
		for(int i = 0; i < size; i++) {
			Node c = v.next;
			
			v.next = v.prev;
			v.prev = c;
			
			v = v.ccw();
		}
		
		return this;
	}
}

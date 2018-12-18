/*
 * Copyright (c) 09.2017
 */

package Insertion;

import Insertion.Point.Point_position;
import Insertion.Vertex.Rotation;

/**
 * Класс полигона.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Polygon {

	private Vertex v; // Текущая вершина
	int size; // Размер полигона
	
	/**
	 * Изменяет значение переменной size.
	 */
	
	public Polygon() {
		v = null;
		size = 0;
	}
	
	public Polygon(Polygon p) {
		size = p.size;
		
		if(size == 0)
			v = null;
		else {
			v = new Vertex(p.point());
			
			for(int i = 1; i < size; i++) {
				p.advance(Rotation.CLOCKWISE);
				v = v.insert(new Vertex(p.point()));
			}
			
			p.advance(Rotation.CLOCKWISE);
			v = v.cw();
		}			
	}
	
	public Polygon(Vertex v) {
		this.v = v; // Первая вершина из списка вершин
		
		resize();
	}
	
	/**
	 * Изменяет значение размера полигона.
	 */
	
	private void resize() {
		if(v == null) 
			size = 0;
		else {
			Vertex v = this.v.cw();
			
			for(size = 1; !(v.equalsVertex(this.v)); ++size, v = v.cw()) {}
		}
	}
	
	/**
	 * Удаляет полигон, но прежде освобождает все вершины.
	 */
	
	public void delete_polygon() {
		if(v != null) {
			Vertex w = v.cw();
			
			while(!v.equalsVertex(w)) {
				Vertex tmp = w.remove();				
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
	
	public Vertex getVertex() {
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
	
	public Point point() {
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
	
	public Vertex cw() {
		return v.cw();
	}
	
	/**
	 * Возвращает предыдущую вершину полигона.
	 * 
	 * @return предыдущая вершина полигона
	 */
	
	public Vertex ccw() {
		return v.ccw();
	}
	
	/**
	 * Определяет как расположена соседняя вершина по отношению к текущей.
	 * 
	 * @param rotation значение перечисления {CLOCKWISE, COUNTER_CLOCKWISE}
	 * @return возвращает одно из значений перечисления
	 */
	
	public Vertex neighbor(Rotation rotation) {
		return v.neighbor(rotation);
	}
	
	/**
	 * Перемещается на следующую или предыдущую вершину полигона, в зависимости от
	 * заданного аргумента.
	 * 
	 * @param rotation значение перечисления {CLOCKWISE, COUNTER_CLOCKWISE}
	 * @return вершину полигона, в зависимости от заданного аргумента
	 */
	
	public Vertex advance(Rotation rotation) {
		return v = v.neighbor(rotation);
	}
	
	/**
	 * Возврвщает вершину, указанную в качестве аргумента.
	 * 
	 * @param v указанную в качестве аргумента вершина
	 * @return вершина, указанная в качестве аргумента
	 */
	
	public Vertex setVertex(Vertex v) {
		return this.v = v; 
	}
	
	/**
	 * Вносит новую вершину после текущей и делает ее текущей вершиной полигона.
	 * 
	 * @param p новая вершина
	 * @return новую текущую вершину полигона
	 */
	
	public Vertex insert(Point p) {
		if(size++ == 0)
			v = new Vertex(p);
		else
			v = v.insert(new Vertex(p));
		
		return v;
	}
	
	/**
	 * Удаляет текущую вершину. 
	 */
	
	public void remove() {
		Vertex v = this.v;
		
		this.v = (--size == 0) ? null : this.v.ccw();
		v.remove();
	}
	
	/**
	 * Возвращает указатель на новый полигон. 
	 * 
	 * @param v вершина хорды
	 * @return указатель на новый полигон
	 */
	
	public Polygon split(Vertex b) {
		Vertex bp = v.split(b);
		resize();
		
		return new Polygon(bp);
	}
	
	/**
	 * Обсчитывает точку s и полигон p и возвращает значение TRUE только в том случае,
	 * если точка лежит внутри полигона p (в том числе и на его границе).
	 * 
	 * @param s точка
	 * @param p полигон
	 * @return TRUE, если точка лежит внутри полигона, иначе - FALSE
	 */
	
	public boolean pointToConvexPolygon(Point s, Polygon p) {
		if(p.size() == 1)
			return (s.equalsPoints(p.point()));
		
		if(p.size() == 2) {
			Point_position c = s.classify(p.edge());
			
			return ((c == Point_position.BETWEEN) || (c == Point_position.ORIGIN) || (c == Point_position.DESTINATION));
		}
		
		Vertex org = p.getVertex();
		
		for(int i = 0; i < p.size(); i++, p.advance(Rotation.CLOCKWISE)) {
			if(s.classify(p.edge()) == Point_position.LEFT) {
				p.setVertex(org);
				
				return false;
			}
		}
		
		return true;
	}
}

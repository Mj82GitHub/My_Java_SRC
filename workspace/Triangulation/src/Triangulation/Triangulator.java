/*
 * Copyright (c) 01.2018
 */

package Triangulation;

import java.util.LinkedList;

import Triangulation.Decompositor.Scaning;
import Triangulation.Mj_Point.Point_position;
import Triangulation.Mj_Vertex.Rotation;

/**
 * Класс для триангуляции монотонных полигонов.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Triangulator {

	/**
	 * Триангулирует заданный монотонный полигон. Предполагается, что при триангуляции, 
	 * самая левая вершина полигона является его текущей вершиной. 
	 *  
	 * 
	 * @param p триангулируемый полигон
	 * @return список треугольников, представляющих результат триангуляции полигона
	 */
	
	// Принадлежность вершины полигона к верхней или нижней цепочкам полигона.
	enum Chain { UPPER,   // Верхняя
		         LOWER }; // Нижняя
		         
	private Mj_List<Mj_Polygon> triangles = new Mj_List<Mj_Polygon>(); // Список треугольников после триангуляции
	
	private Mj_Vertex v, // Обрабатываемая вершина
                      vu, // Последняя обрабатываемая вершина в верхней цепочке полигона
                      vl; // Последняя обрабатываемая вершина в нижней цепочке полигона
	
	/**
	 * Выполняет триангуляцию монотонного полигона.
	 * 
	 * @param p полигон для триангуляции
	 * @return список треугольников после триангуляции полигона
	 */
	
	public Mj_List<Mj_Polygon> triangulateMonotonePolygon(Mj_Polygon p) {
		// Стек вершин, которые были проверены, но еще не обработаны полностью (возможно, обнаружены не 
		// все треугольники, связанные с этой вершиной)
		LinkedList<Mj_Vertex> s = new LinkedList<>(); 		
		
		p.leastVertex(p, Scaning.LEFT_TO_RIGHT); 
		
		v = vu = vl = p.getVertex();
		
		s.push(v);
		
		Chain chain = advancePtr(vu, vl, v); // Верхняя или нижняя цепочка полигона
		
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
	 * Возвращает следующий элемент стека за первым, либо NULL, если его нет.
	 * 
	 * @param s стек
	 * @return следующий элемент стека за первым, либо NULL, если его нет
	 */
	
	private Mj_Vertex nextToFirst(LinkedList<Mj_Vertex> s) {
		if(s.size() <= 1)
			return null;
		else
			return s.get(s.indexOf(s.getFirst()) + 1);
	}
	
	/**
	 * Определяет соседство двух вершин полигона.
	 * 
	 * @param v вершина
	 * @param w вершина
	 * @return TRUE, если две заданные вершины, являются соседними, иначе - FALSE
	 */
	
	private boolean adjacent(Mj_Vertex v, Mj_Vertex w) {
		return ((w.equalsVertex(v.cw())) || (w.equalsVertex(v.ccw())));
	}
	
	/**
	 * Устанавливает к какой из двух цепочек вершин v относится произведенное действие.
	 * 
	 * @param vu последняя обрабатываемая вершина в верхней цепочке полигона
	 * @param vl последняя обрабатываемая вершина в нижней цепочке полигона
	 * @param v обрабатываемая вершина
	 * @return принадлежность вершины полигона к верхней или нижней цепочкам полигона
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
	 * Деструктивно разбивает n-угольник p на n - 2 треугольников и добавляет их в список 
	 * треугольников.
	 * 
	 * @param p полигон
	 * @param triangles список треугольников
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

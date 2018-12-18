/*
 * Copyright (c) 09.2017
 */

package Insertion;

/** 
 *  ласс вершины полигона.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Vertex extends Node {
	
	private Point point; //  оординаты вершины
	
	// ѕеремещение к следующей точке
	enum Rotation { CLOCKWISE,           // ѕо часовой стрелке
		            COUNTER_CLOCKWISE }; // ѕротив часовой стрелки
	
	public Vertex() {
   		super();
   		
   		point = new Point();
	}
		            
	public Vertex(double x, double y) {
		super();
		
		point = new Point(x, y);
	}
	
	public Vertex(Point p) {
		super();
		
		point = p;
	}
	
	public Vertex(Vertex v) {
		super();
		
		point = new Point();
		point.x = v.point.x;
		point.y = v.point.y;
		
		next = v.next;
		prev = v.prev;
	}
		
	/**
	 * ¬озвращает следующую вершину полигона.
	 * 
	 * @return следующа€ вершина полигона
	 */
	
	public Vertex cw() {		
		return (Vertex) next;
	}
	
	/**
	 * ¬озвращает предыдущую вершину полигона.
	 * 
	 * @return предыдуща€ вершина полигона
	 */
	
	public Vertex ccw() {		
		return (Vertex) prev;
	}
	
	/**
	 * ќпредел€ет как расположена соседн€€ вершина по отношению к текущей.
	 * 
	 * @param rotation значение перечислени€ {CLOCKWISE, COUNTER_CLOCKWISE}
	 * @return возвращает одно из значений перечислени€
	 */
	
	public Vertex neighbor(Rotation rotation) {
		return ((rotation == Rotation.CLOCKWISE) ? cw() : ccw());
	}
	
	/**
	 * ¬озвращает точку на плоскости, в которой находитс€ текуща€ вершина.
	 * 
	 * @return точка на плоскости
	 */
	
	public Point point() {
		return point;
	}
	
	/**
	 * ¬ключает вершину сразу же после текущей вершины.
	 * 
	 * @param v вставл€ема€ вершина
	 * @return вставл€ема€ вершина
	 */
	
	public Vertex insert(Vertex v) {
		return (Vertex) ((Node) this).insert(v);
	}
	
	/**
	 * ”дал€ет текущую вершину из данного св€заного списка.
	 * 
	 * @return удаленна€ вершина
	 */
	
	public Vertex remove() {
		return (Vertex) ((Node) this).n_remove();
	}
	
	/**
	 * »спользуетс€ дл€ присоединени€ к текущей вершине заданной аргументом выршины. 
	 * 
	 * @param node присоедин€ема€ к текущей вершина
	 */
	
	public void splice(Vertex b) {
		((Node) this).splice(b);
	}
	
	/**
	 * –аздел€ет полигон вдоль хорды, соедин€ющей текущую вершину (пусть, вершина A) с вершиной B. 
	 * 
	 * @param b вершина хорды
	 * @return вершину, €вл€ющуюс€ дубликатом вершины B.
	 */
	
	public Vertex split(Vertex b) {
		// «аносим bp перед вершиной b
		Vertex bp = b.ccw().insert(new Vertex(b.point()));
		// «аносим ap после текущей аершины
		insert(new Vertex(point()));
		splice(bp);
		
		return bp;
	}
	
	/**
	 * ќпредел€ет эквивалентность двух вершин.
	 *  
	 * @param p вершина дл€ сравнени€
	 * @return возвращает TRUE, если вершины эквивалентны (равны), иначе FALSE
	 */
	
	public boolean equalsVertex(Vertex v) {
		if((point.x == v.point.x) && (point.y == v.point.y))
			return true;
		else
			return false;
		
	}
}

/*
 * Copyright (c) 09.2017
 */

package com.mikhail.mj82.Triangulation;

import com.mikhail.mj82.Triangulation.Mj_Vertex.Rotation;

/**
 * Класс активных ребер полигона при сканировании сканирующей линией.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class ActiveEdge extends ActiveElement {

	public Mj_Vertex v; // Концевая вершина текущего ребра
	// Целевая вершина пары ребер, состоящая из текущего ребра и активного ребра,
	// расположенного непосредственно над ним
	public Mj_Vertex w; 
	// Используется для отслеживания связи от текущего ребра к ребру, которое
	// может пересекать текущее ребро за сканирующей линией
	public Rotation rotation;
	
	public ActiveEdge(Mj_Vertex v, Rotation r) {
		super(Active_element_type.ACTIVE_EDGE);
		
		rotation = r;
		this.v = v;
		this.w = v;
	}
	
	public ActiveEdge(Mj_Vertex v, Rotation r, Mj_Vertex w) {
		super(Active_element_type.ACTIVE_EDGE);
		
		rotation = r;
		this.v = v;
		this.w = w;
	}
	
	public Edge edge() {
		return new Edge(v.point(), v.cw().point());
	}
	
	public double getY() {
		return edge().getY(Decompositor.curx);
	}
	
	public double slope() {
		return edge().slope();
	}
}

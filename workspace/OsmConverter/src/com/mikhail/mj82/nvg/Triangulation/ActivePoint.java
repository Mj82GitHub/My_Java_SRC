/*
 * Copyright (c) 09.2017
 */

package com.mikhail.mj82.nvg.Triangulation;

import com.mikhail.mj82.nvg.Triangulation.Mj_Vertex.Rotation;

/**
 * ����� �������� ����� �������� ��� ������������ ����������� ������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class ActivePoint  extends ActiveEdge {

	public Mj_Point p;
	
	public ActivePoint(Mj_Point p) {
		super(new Mj_Vertex(p), Rotation.CLOCKWISE);
		
		type = Active_element_type.ACTIVE_POINT;
		this.p = p;
	}
	
	public double getY() {
		return p.y;
	}
}

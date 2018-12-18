/*
 * Copyright (c) 09.2017
 */

package Triangulation;

import Triangulation.Mj_Vertex.Rotation;

/**
 * ����� �������� ����� �������� ��� ������������ ����������� ������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class ActiveEdge extends ActiveElement {

	public Mj_Vertex v; // �������� ������� �������� �����
	// ������� ������� ���� �����, ��������� �� �������� ����� � ��������� �����,
	// �������������� ��������������� ��� ���
	public Mj_Vertex w; 
	// ������������ ��� ������������ ����� �� �������� ����� � �����, �������
	// ����� ���������� ������� ����� �� ����������� ������
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

/*
 * Copyright (c) 09.2017
 */

package mj82.Triangulation;

import mj82.Triangulation.Mj_Point.Point_position;

/**
 * ����� ����������� ��� ������������� ���� ���� ������ �����. 
 * 
 *@author Mikhail Kushnerov (mj82) *
 */

public class Edge {
	
	public static double t; // �������������� ��� ���������� ����� �����������
	
	public Mj_Point org; // ����� ������ �����
	public Mj_Point dest;// ����� ����� �����
	
	// �������� ���� ����������� �����
	enum Intersect { COLLINEAR,       // ����������� (����� �� ����� ������)
		             PARALLEL,        // �����������
		             SKEW,            // ���������
		             SKEW_CROSS,      // ��������� � ������������
		             SKEW_NO_CROSS }; // ���������, �� ��� �����������
	
	public Edge() {
		org = new Mj_Point(0, 0);
		dest = new Mj_Point(1, 0);
	}
	
	public Edge(Mj_Point org, Mj_Point dest) {
		this.org = org;
		this.dest = dest;
	}
	
	/**
	 * ��������� ������� (��������) ����� �� 90 �������� � ���������� �� ������� �������
	 * ������ ��� ������� �����.
	 * 
	 * @return ���������� �����
	 */
	
	public Edge rot() {
		Mj_Point m = (org.sum(dest)).multiplication(0.5);
		Mj_Point v = dest.subtraction(org);
		Mj_Point n = new Mj_Point(v.y, -v.x);
		
		org = m.subtraction(n.multiplication(0.5));
		dest = m.sum(n.multiplication(0.5));
		
		return this;
	}
	
	/**
	 * ��������� ����������� �������� ����� �� ��������.
	 * 
	 * @return ������������ �����
	 */
	
	public Edge flip() {
		return rot().rot();
	}
	
	/**
	 * ���������� ����� ����������� ���� �����.����������� �������� t � 
	 * ��������������� ��������� ��� ���� �����.
	 * 
	 * P(t) = a + t * (b - a) - ��������������� ����� �����.
	 * 
	 * @param t ��������, ������������� � ��������� �����
	 * @return ����� �����������
	 */
	
	public Mj_Point point(double t) {
		Edge.t = t;
		return org.sum(dest.subtraction(org).multiplication(Edge.t));
	}
	
	/**
	 * ���������� �������� ���� ����������� ���� �����.
	 * 
	 * @param e ������ �����
	 * @param t ��������, ������������� � ��������� �����
	 * @return �������� ���� ����������� ���� �����
	 */
	
	public Intersect intersect(Edge e, double t) {		
		Mj_Point a = org;
		Mj_Point b = dest;
		Mj_Point c = e.org;
		Mj_Point d = e.dest;
		Mj_Point n = new Mj_Point(d.subtraction(c).y, c.subtraction(d).x);
		double denom = dotProduct(n, b.subtraction(a));
		
		if(denom == 0.0) {
			Point_position aclass = org.classify(e);
			
			if((aclass == Point_position.LEFT) || (aclass == Point_position.RIGHT))
				return Intersect.PARALLEL;
			else
				return Intersect.COLLINEAR;
		} 
		
		double num = dotProduct(n, a.subtraction(c));
		
		Edge.t = -num / denom;
		
		return Intersect.SKEW;
	}
	
	/**
	 * ���������� ��������������� ���� �����.
	 * 
	 * @param e ����� ��� ���������
	 * @return ���������� TRUE, ���� ����� ������������ (�����), ����� FALSE
	 */
	
	public boolean equalsEdges(Edge e) {
		if(org.equalsPoints(e.org) && dest.equalsPoints(e.dest))
			return true;
		else
			return false;
	}
	
	/**
	 * ���������� ���������� ������������ ���� ��������.
	 * 
	 * @param p ������
	 * @param q ������
	 * @return ��������� ���������� ������������ ���� ��������
	 */
	
	private double dotProduct(Mj_Point p, Mj_Point q) {
		return (p.x * q.x + p.y * q.y);
	}
	
	/**
	 * ���������� �������� SKEW_CROSS, ���� � ������ ���� ������� ������� ������ �����
	 * ���������� ������� ������ ����� e. ���� ������� ������ ����� ������������, �� 
	 * ������������ �������� ��������� t ����� ����� ������� ������ �����, ���������������
	 * ����� �����������. � ��������� ������ �-��� ���������� ���� �� ��������� ���������� 
	 * ��������: COLLINEAR, PARALLEL ��� SKEW_NO_CROSS.
	 * 	
	 * @param e ������ �����
	 * @param t ��������, ������������� � ��������� �����
	 * @return �������� ���� ����������� ���� �����
	 */
	
	public Intersect cross(Edge e, double t) {		
		Intersect crossType = e.intersect(this, Edge.t);
		
		if((crossType == Intersect.COLLINEAR) || (crossType == Intersect.PARALLEL))
			return crossType;
		
		if((Edge.t < 0.0) || (Edge.t > 1.0))
			return Intersect.SKEW_NO_CROSS;
		
		intersect(e, Edge.t);
		
		if((0.0 <= Edge.t) && (Edge.t <= 1.0))
			return Intersect.SKEW_CROSS;
		else
			return Intersect.SKEW_NO_CROSS;
	}
	
	/**
	 * ���������� ������������ ����� ��� ���.
	 * 
	 * @return ���������� TRUE, ���� ����� ������������, ����� - FALSE
	 */
	
	public boolean isVertical() {
		return (org.x == dest.x);
	}
	
	/**
	 * ���������� �������� ������� �������� �����.
	 * 
	 * @return ���������� �������� ������� �������� ����� ��� �������� 
	 * Double.MAX_VALUE, ���� ������� ����� �����������.
	 */
	
	public double slope() {
		if(org.x != dest.x)
			return (dest.y - org.y) / (dest.x - org.x);
		
		return Double.MAX_VALUE;
	}
	
	/**
	 * �������� �������� x � �-��� ���������� �������� y, ��������������� �����
	 * (x, y) �� ������� ����������� ������ �����. �-��� ��������� ������ � ��� ������, 
	 * ���� ������� ����� �� �����������.
	 * 
	 * @param x ���������� ����� �� ��� X
	 * @return ���������� �������� y, ��������������� �����
	 * (x, y) �� ������� ����������� ������ �����
	 */
	
	public double getY(double x) {
		return slope() * (x - org.x) + org.y;
	}
	
	/**
	 * ������ ��������� � �������� ����� ����� �������.
	 * 
	 * @return �������������� �����
	 */
	
	public Edge changeCircumventPoints() {
		Mj_Point tmp_org = org;
		Mj_Point tmp_dest = dest;
		
		org = tmp_dest;
		dest = tmp_org;
		
		return this;
	}
}

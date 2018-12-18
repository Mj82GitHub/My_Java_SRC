/*
 * Copyright (c) 09.2017
 */

package Insertion;

import Insertion.Point.Point_position;

/**
 * ����� ����������� ��� ������������� ���� ���� ������ �����. 
 * 
 *@author Mikhail Kushnerov (mj82) *
 */

public class Edge {
	
	public Point org; // ����� ������ �����
	public Point dest;// ����� ����� �����
	
	// �������� ���� ����������� �����
	enum Intersect { COLLINEAR,       // ����������� (����� �� ����� ������)
		             PARALLEL,        // �����������
		             SKEW,            // ���������
		             SKEW_CROSS,      // ��������� � ������������
		             SKEW_NO_CROSS }; // ���������, �� ��� �����������
	
	public Edge() {
		org = new Point(0, 0);
		dest = new Point(1, 0);
	}
	
	public Edge(Point org, Point dest) {
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
		Point m = (org.sum(dest)).multiplication(0.5);
		Point v = dest.subtraction(org);
		Point n = new Point(v.y, -v.x);
		
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
	
	public Point point(double t) {
		return org.sum(dest.subtraction(org).multiplication(t));
	}
	
	/**
	 * ���������� �������� ���� ����������� ���� �����.
	 * 
	 * @param e ������ �����
	 * @param t ��������, ������������� � ��������� �����
	 * @return �������� ���� ����������� ���� �����
	 */
	
	public Intersect intersect(Edge e, double t) {
		Point a = org;
		Point b = dest;
		Point c = e.org;
		Point d = e.dest;
		Point n = new Point(d.subtraction(c).y, c.subtraction(d).x);
		double denom = dotProduct(n, b.subtraction(a));
		
		if(denom == 0.0) {
			Point_position aclass = org.classify(e);
			
			if((aclass == Point_position.LEFT) || (aclass == Point_position.RIGHT))
				return Intersect.PARALLEL;
		} else
			return Intersect.COLLINEAR;
		
		double num = dotProduct(n, a.subtraction(c));
		
		t = -num / denom;
		
		return Intersect.SKEW;
	}
	
	/**
	 * ���������� ���������� ������������ ���� ��������.
	 * 
	 * @param p ������
	 * @param q ������
	 * @return ��������� ���������� ������������ ���� ��������
	 */
	
	private double dotProduct(Point p, Point q) {
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
		double s = 0;
		Intersect crossType = e.intersect(this, s);
		
		if((crossType == Intersect.COLLINEAR) || (crossType == Intersect.PARALLEL))
			return crossType;
		
		if((s < 0.0) || (s > 1.0))
			return Intersect.SKEW_NO_CROSS;
		
		intersect(e, t);
		
		if((0.0 <= t) || (t <= 1.0))
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
}

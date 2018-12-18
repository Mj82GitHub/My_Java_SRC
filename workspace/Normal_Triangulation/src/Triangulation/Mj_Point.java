/*
 * Copyright (c) 09.2017
 */

package Triangulation;

/**
 * ����� ������� �����.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Mj_Point {

	public double x; // ���������� ����� �� ��� �
	public double y; // ���������� ����� �� ��� Y
		
	// ������������ ��������� ������� ����� �� ��������� � ������� ������ �����
	enum Point_position { LEFT,          // �����
		                  RIGHT,         // ������
		                  BEYOND,        // �������
		                  BEHIND,        // ������
		                  BETWEEN,       // �����
		                  ORIGIN,        // ������
		                  DESTINATION }; // �����
	
	public Mj_Point() {
		x = 0;
		y = 0;
	}
	
	public Mj_Point(Mj_Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	public Mj_Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * ����� ���� ��������.
	 * 
	 * @param p ���������
	 * @return ��������� �������� ���� ��������
	 */
	
	public Mj_Point sum(Mj_Point p) {
		return new Mj_Point(this.x + p.x, this.y + p.y);
	}
	
	/**
	 * �������� ���� ��������.
	 * 
	 * @param p ����������
	 * @return ��������� ��������� ���� ��������
	 */
	
	public Mj_Point subtraction(Mj_Point p) {
		return new Mj_Point(this.x - p.x, this.y - p.y);
	}
	
	/**
	 * ��������� ��������� ������� �� �����.
	 * 
	 * @param s ��������� (������)
	 * @param p ��������
	 * @return ��������� ���������� ��������� ������� �� �����
	 */
	
	public Mj_Point multiplication(double s, Mj_Point p) {
		return new Mj_Point(s * p.x, s * p.y);
	}
	
	/**
	 * ��������� ��������� ������� �� �����.
	 * 
	 * @param s ��������� (������)
	 * @return ��������� ���������� ��������� ������� �� �����
	 */
	
	public Mj_Point multiplication(double s) {
		return new Mj_Point(s * this.x, s * this.y);
	}
	
	/**
	 * ���������� ���������� X ������� �����, ���� index = 0, ��� ���������� Y, 
	 * ���� index = 1.
	 *  
	 * @param index ������ ����������
	 * @return ���������� ������� �����
	 */
	
	public double get(int index) {
		return (index == 0 ? x : y);
	}
	
	/**
	 * ���������� ��������������� ���� �����.
	 *  
	 * @param p ����� ��� ���������
	 * @return ���������� TRUE, ���� ����� ������������ (�����), ����� FALSE
	 */
	
	public boolean equalsPoints(Mj_Point p) {
		if((this.x == p.x) && (this.y == p.y))
			return true;
		else
			return false;
		
	}
	
	/**
	 * ���������� ������������������ ������� ���������, ����� ���������, ��� this.p > p,
	 * ���� this.p.x > p.x, ���� this.p.x = p.x � this.p.y > p.y.
	 * 
	 * @param p ����� ��� ���������
	 * @return ���������� TRUE, ���� ����� ������ ��������, ����� FALSE
	 */
	
	public boolean more(Mj_Point p) {
		return ((this.x > p.x) || ((this.x == p.x) && (this.y > p.y)));
	}
	
	/**
	 * ���������� ������������������ ������� ���������, ����� ���������, ��� this.p < p,
	 * ���� this.p.x < p.x, ���� this.p.x = p.x � this.p.y < p.y.
	 * 
	 * @param p ����� ��� ���������
	 * @return  ���������� TRUE, ���� ����� ������ ��������, ����� FALSE
	 */
	
	public boolean less(Mj_Point p) {
		return ((this.x < p.x) || ((this.x == p.x) && (this.y < p.y)));
	}
	
	/**
	 * ����������� ��������� ������� ����� ������������ ������� ������, 
	 * ��������� ����� �������.
	 * 
	 * @param p0 ��������� ����� �������
	 * @param p1 �������� ����� �������
	 * @return ���������� �������� ���� ������������, ����������� �� ��������� ������� �����
	 */
	
	public Point_position classify(Mj_Point p0, Mj_Point p1) {
		Mj_Point p2 = this; // ������� �����
		Mj_Point a = p1.subtraction(p0); // ������ �� �������� ������
		Mj_Point b = p2.subtraction(p0); // ������ �� ������� ����� � ��������� ����� �������
		
		double sa = a.x * b.y - b.x * a.y; // ���������� ���������� ������������
		
		if(sa > 0.0) // �������
			return Point_position.LEFT;
		
		if(sa < 0.0) // �������
			return Point_position.RIGHT;
		
		if((a.x * b.x < 0.0) || (a.y * b.y < 0.0)) // �������
			return Point_position.BEHIND;
		
		if(a.length() < b.length()) // �������
			return Point_position.BEYOND;
		
		if(p0.equalsPoints(p2))
			return Point_position.ORIGIN;
		
		if(p1.equalsPoints(p2))
			return Point_position.DESTINATION;
		
		return Point_position.BETWEEN;
	}
	
	/**
	 * ����������� ��������� ������� ����� ������������ ������� ������, 
	 * ��������� ������, ���������� �� ���� �����.
	 * 
	 * @param e �����
	 * @return ���������� �������� ���� ������������, ����������� �� ��������� ������� �����
	 */
	
	public Point_position classify(Edge e) {
		return classify(e.org, e.dest);
	}
	
	/**
	 * ���������� �������� ��������� ���� (����, ���������� ����� �������� � �������� ����
	 *  � ������������� � ����������� �������� ������ ������� �������).
	 * 
	 * @return �������� ��������� ����
	 */
	
	public double polarAngle() {
		double theta = Math.atan(this.y / this.x); // � ��������
		theta *= 360 / (2 * 3.1415926); // ������� � �������
		
		if((this.x == 0.0) && (this.y == 0.0))
			return -1;
		
		if(this.x == 0.0)
			return ((this.y > 0.0) ? 90 : 270);
		
		if(this.x > 0.0) // 1 � 4 ���������
			return ((this.y >= 0.0) ? theta : 360 + theta);
		else // 2 � 3 ���������
			return (180 + theta);
	}
	
	/**
	 * ���������� ����� �������� �������.
	 * 
	 * @return ����� �������� �������
	 */
	
	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}
	
	/**
	 * ���������� �������� ���������� (�� ������) �� ������� ����� �� �����.
	 * 
	 * @param e �����
	 * @return �������� ���������� (�� ������) �� ������� ����� �� �����
	 */
	
	public double distance(Edge e) {
		Edge ab = e;
		ab.flip().rot(); // ������� ab �� 90 �������� ������ ������� �������
		
		Mj_Point n = (ab.dest.subtraction(ab.org)); // n - ������, ���������������� ����� e
		n = n.multiplication((n.length() * 1.0)); // ������������ ������� n
		
		Edge f = new Edge(this, this.sum(n)); // ����� f = n ��������������� �� ������� �����
		
		// t = ���������� �� ������ ����� ������� f �� �����, � ������ ����� f ���������� ����� e
		double t = 0;
		f.intersect(e, t);
		
		return t;
	}
}

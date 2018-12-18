/*
 * Copyright (c) 09.2017
 */

package Insertion;

/** 
 * ����� ������� ��������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Vertex extends Node {
	
	private Point point; // ���������� �������
	
	// ����������� � ��������� �����
	enum Rotation { CLOCKWISE,           // �� ������� �������
		            COUNTER_CLOCKWISE }; // ������ ������� �������
	
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
	 * ���������� ��������� ������� ��������.
	 * 
	 * @return ��������� ������� ��������
	 */
	
	public Vertex cw() {		
		return (Vertex) next;
	}
	
	/**
	 * ���������� ���������� ������� ��������.
	 * 
	 * @return ���������� ������� ��������
	 */
	
	public Vertex ccw() {		
		return (Vertex) prev;
	}
	
	/**
	 * ���������� ��� ����������� �������� ������� �� ��������� � �������.
	 * 
	 * @param rotation �������� ������������ {CLOCKWISE, COUNTER_CLOCKWISE}
	 * @return ���������� ���� �� �������� ������������
	 */
	
	public Vertex neighbor(Rotation rotation) {
		return ((rotation == Rotation.CLOCKWISE) ? cw() : ccw());
	}
	
	/**
	 * ���������� ����� �� ���������, � ������� ��������� ������� �������.
	 * 
	 * @return ����� �� ���������
	 */
	
	public Point point() {
		return point;
	}
	
	/**
	 * �������� ������� ����� �� ����� ������� �������.
	 * 
	 * @param v ����������� �������
	 * @return ����������� �������
	 */
	
	public Vertex insert(Vertex v) {
		return (Vertex) ((Node) this).insert(v);
	}
	
	/**
	 * ������� ������� ������� �� ������� ��������� ������.
	 * 
	 * @return ��������� �������
	 */
	
	public Vertex remove() {
		return (Vertex) ((Node) this).n_remove();
	}
	
	/**
	 * ������������ ��� ������������� � ������� ������� �������� ���������� �������. 
	 * 
	 * @param node �������������� � ������� �������
	 */
	
	public void splice(Vertex b) {
		((Node) this).splice(b);
	}
	
	/**
	 * ��������� ������� ����� �����, ����������� ������� ������� (�����, ������� A) � �������� B. 
	 * 
	 * @param b ������� �����
	 * @return �������, ���������� ���������� ������� B.
	 */
	
	public Vertex split(Vertex b) {
		// ������� bp ����� �������� b
		Vertex bp = b.ccw().insert(new Vertex(b.point()));
		// ������� ap ����� ������� �������
		insert(new Vertex(point()));
		splice(bp);
		
		return bp;
	}
	
	/**
	 * ���������� ��������������� ���� ������.
	 *  
	 * @param p ������� ��� ���������
	 * @return ���������� TRUE, ���� ������� ������������ (�����), ����� FALSE
	 */
	
	public boolean equalsVertex(Vertex v) {
		if((point.x == v.point.x) && (point.y == v.point.y))
			return true;
		else
			return false;
		
	}
}

/*
 * Copyright (c) 09.2017
 */

package Insertion;

import Insertion.Point.Point_position;
import Insertion.Vertex.Rotation;

/**
 * ����� ��������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Polygon {

	private Vertex v; // ������� �������
	int size; // ������ ��������
	
	/**
	 * �������� �������� ���������� size.
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
		this.v = v; // ������ ������� �� ������ ������
		
		resize();
	}
	
	/**
	 * �������� �������� ������� ��������.
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
	 * ������� �������, �� ������ ����������� ��� �������.
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
	 * ���������� �������� ������ �� ������.
	 */
	
	private void delete() {
		try {
			this.finalize(); // ����������� ������� �������
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������� ������� ������� ������� ��������.
	 * 
	 * @return ������� ������� ������� ��������
	 */
	
	public Vertex getVertex() {
		return v;
	}
	
	/**
	 * ���������� ������ �������� (���-�� ������, �� ������� ������� �������).
	 * @return
	 */
	
	public int size() {
		return size;
	}
	
	/**
	 * ���������� ����� �� ���������, ������� ������������� ������� �������.
	 * 
	 * @return ����� �� ���������
	 */
	
	public Point point() {
		return v.point();
	}
	
	/**
	 * ���������� ������� ����� (���������� � ������� ������� � ������������� � 
	 * ��������� ����� ��� �������).
	 * 
	 * @return ������� �����
	 */
	
	public Edge edge() {
		return new Edge(point(), v.cw().point());
	}
	
	/**
	 * ���������� ��������� ������� ��������.
	 * 
	 * @return ��������� ������� ��������
	 */
	
	public Vertex cw() {
		return v.cw();
	}
	
	/**
	 * ���������� ���������� ������� ��������.
	 * 
	 * @return ���������� ������� ��������
	 */
	
	public Vertex ccw() {
		return v.ccw();
	}
	
	/**
	 * ���������� ��� ����������� �������� ������� �� ��������� � �������.
	 * 
	 * @param rotation �������� ������������ {CLOCKWISE, COUNTER_CLOCKWISE}
	 * @return ���������� ���� �� �������� ������������
	 */
	
	public Vertex neighbor(Rotation rotation) {
		return v.neighbor(rotation);
	}
	
	/**
	 * ������������ �� ��������� ��� ���������� ������� ��������, � ����������� ��
	 * ��������� ���������.
	 * 
	 * @param rotation �������� ������������ {CLOCKWISE, COUNTER_CLOCKWISE}
	 * @return ������� ��������, � ����������� �� ��������� ���������
	 */
	
	public Vertex advance(Rotation rotation) {
		return v = v.neighbor(rotation);
	}
	
	/**
	 * ���������� �������, ��������� � �������� ���������.
	 * 
	 * @param v ��������� � �������� ��������� �������
	 * @return �������, ��������� � �������� ���������
	 */
	
	public Vertex setVertex(Vertex v) {
		return this.v = v; 
	}
	
	/**
	 * ������ ����� ������� ����� ������� � ������ �� ������� �������� ��������.
	 * 
	 * @param p ����� �������
	 * @return ����� ������� ������� ��������
	 */
	
	public Vertex insert(Point p) {
		if(size++ == 0)
			v = new Vertex(p);
		else
			v = v.insert(new Vertex(p));
		
		return v;
	}
	
	/**
	 * ������� ������� �������. 
	 */
	
	public void remove() {
		Vertex v = this.v;
		
		this.v = (--size == 0) ? null : this.v.ccw();
		v.remove();
	}
	
	/**
	 * ���������� ��������� �� ����� �������. 
	 * 
	 * @param v ������� �����
	 * @return ��������� �� ����� �������
	 */
	
	public Polygon split(Vertex b) {
		Vertex bp = v.split(b);
		resize();
		
		return new Polygon(bp);
	}
	
	/**
	 * ����������� ����� s � ������� p � ���������� �������� TRUE ������ � ��� ������,
	 * ���� ����� ����� ������ �������� p (� ��� ����� � �� ��� �������).
	 * 
	 * @param s �����
	 * @param p �������
	 * @return TRUE, ���� ����� ����� ������ ��������, ����� - FALSE
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

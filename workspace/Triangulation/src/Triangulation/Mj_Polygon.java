/*
 * Copyright (c) 09.2017
 */

package Triangulation;

import Triangulation.Decompositor.Scaning;
import Triangulation.Mj_Point.Point_position;
import Triangulation.Mj_Vertex.Rotation;

/**
 * ����� ��������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Mj_Polygon {

	private Mj_Vertex v; // ������� �������
	int size; // ������ ��������
	
	/**
	 * �������� �������� ���������� size.
	 */
	
	public Mj_Polygon() {
		v = null;
		size = 0;
	}
	
	public Mj_Polygon(Mj_Polygon p) {
		size = p.size;
		
		if(size == 0)
			v = null;
		else {
			v = new Mj_Vertex(p.point());
			
			for(int i = 1; i < size; i++) {
				p.advance(Rotation.CLOCKWISE);
				v = v.insert(new Mj_Vertex(p.point()));
			}
			
			p.advance(Rotation.CLOCKWISE);
			v = v.cw();
		}			
	}
	
	public Mj_Polygon(Mj_Vertex v) {
		this.v = v; // ������ ������� �� ������ ������
		
		resize();
	}
	
	/**
	 * �������� �������� ������� ��������.
	 */
	
	public void resize() {
		if(v == null) 
			size = 0;
		else {
			Mj_Vertex v = this.v.cw();
			
			for(size = 1; !(v.equalsVertex(this.v) && v.equals(this.v)); ++size, v = v.cw()) {}
		}
	}
	
	/**
	 * ������� �������, �� ������ ����������� ��� �������.
	 */
	
	public void delete_polygon() {
		if(v != null) {
			Mj_Vertex w = v.cw();
			
			while(v != w) {		
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
	
	public Mj_Vertex getVertex() {
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
	
	public Mj_Point point() {
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
	
	public Mj_Vertex cw() {
		return v.cw();
	}
	
	/**
	 * ���������� ���������� ������� ��������.
	 * 
	 * @return ���������� ������� ��������
	 */
	
	public Mj_Vertex ccw() {
		return v.ccw();
	}
	
	/**
	 * ���������� ��� ����������� �������� ������� �� ��������� � �������.
	 * 
	 * @param rotation �������� ������������ {CLOCKWISE, COUNTER_CLOCKWISE}
	 * @return ���������� ���� �� �������� ������������
	 */
	
	public Mj_Vertex neighbor(Rotation rotation) {
		return v.neighbor(rotation);
	}
	
	/**
	 * ������������ �� ��������� ��� ���������� ������� ��������, � ����������� ��
	 * ��������� ���������.
	 * 
	 * @param rotation �������� ������������ {CLOCKWISE, COUNTER_CLOCKWISE}
	 * @return ������� ��������, � ����������� �� ��������� ���������
	 */
	
	public Mj_Vertex advance(Rotation rotation) {
		return v = v.neighbor(rotation);
	}
	
	/**
	 * ������������ �� ��������� ������� ��������.
	 * 
	 * @return ��������� ������� ��������
	 */
	
	public Mj_Vertex advance() {
		return v = v.neighbor(Rotation.CLOCKWISE);
	}
	
	/**
	 * ���������� �������, ��������� � �������� ���������.
	 * 
	 * @param v ��������� � �������� ��������� �������
	 * @return �������, ��������� � �������� ���������
	 */
	
	public Mj_Vertex setVertex(Mj_Vertex v) {
		return this.v = v; 
	}
	
	/**
	 * ������ ����� ������� ����� ������� � ������ �� ������� �������� ��������.
	 * 
	 * @param p ����� �������
	 * @return ����� ������� ������� ��������
	 */
	
	public Mj_Vertex insert(Mj_Point p) {
		if(size++ == 0)
			v = new Mj_Vertex(p);
		else
			v = v.insert(new Mj_Vertex(p));
		
		return v;
	}
	
	/**
	 * ������� ������� �������. 
	 */
	
	public void remove() {
		Mj_Vertex v = this.v;
		
		this.v = (--size == 0) ? null : this.v.ccw();
		v.remove();
	}
	
	/**
	 * ���������� ��������� �� ����� �������. 
	 * 
	 * @param v ������� �����
	 * @return ��������� �� ����� �������
	 */
	
	public Mj_Polygon split(Mj_Vertex b) {
		Mj_Vertex bp = v.split(b);
		resize();
		
		return new Mj_Polygon(bp);
	}
	
	/**
	 * ����������� ����� s � ������� p � ���������� �������� TRUE ������ � ��� ������,
	 * ���� ����� ����� ������ �������� p (� ��� ����� � �� ��� �������).
	 * 
	 * @param s �����
	 * @param p �������
	 * @return TRUE, ���� ����� ����� ������ ��������, ����� - FALSE
	 */
	
	public boolean pointToConvexPolygon(Mj_Point s, Mj_Polygon p) {
		if(p.size() == 1)
			return (s == p.point());
		
		if(p.size() == 2) {
			Point_position c = s.classify(p.edge());
			
			return ((c == Point_position.BETWEEN) || (c == Point_position.ORIGIN) || (c == Point_position.DESTINATION));
		}
		
		Mj_Vertex org = p.getVertex();
		
		for(int i = 0; i < p.size(); i++, p.advance(Rotation.CLOCKWISE)) {
			if(s.classify(p.edge()) == Point_position.LEFT) {
				p.setVertex(org);
				
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * ���������� ����� ���������� ������� � ��������. ����� ��������������� �������,
	 * ������� ������ ���� ��������� ��� �������� �������������� �����, ����������
	 * �������� ���������.
	 * 
	 * @param p �������
	 * @param cmp �-��� ���������
	 * @return ���������� ������� � ��������
	 */
	
	public Mj_Vertex leastVertex(Mj_Polygon p, Scaning cmp) {
		Mj_Vertex bestV = p.getVertex();
		p.advance(Rotation.CLOCKWISE);
		
		for(int i = 1; i < p.size; p.advance(Rotation.CLOCKWISE), i++) {
			switch(cmp) {
			case LEFT_TO_RIGHT:
				if(CompareFunc.leftToRightCmp(p.getVertex(), bestV) < 0)
					bestV = p.getVertex();
				
				p.setVertex(bestV);
				
				return bestV;
			case RIGHT_TO_LEFT:
				if(CompareFunc.rightToLeftCmp(p.getVertex(), bestV) < 0)
					bestV = p.getVertex();
				
				p.setVertex(bestV);
				
				return bestV;
			}
		}
		
		return null;
	}
	
	/**
	 * ��������� ������� ��������.
	 * ��� �������� ���������, ��� ��������� ������� �������� ��������� � ������.
	 * A = 1/2 * SUM(i=1; i=N-1) (XiYi+1 - Xi+1Yi) + XnY1 - X1Yn
	 * 
	 * @param polygon �������
	 * @return ������� ��������
	 */
	
	public double getAreaOfPolygon() {
		double x1_xN = v.point().x; // ���������� � ������/��������� ������� ��������
		double y1_yN = v.point().y; // ���������� Y ������/��������� ������� ��������
		
		double sum = 0;
		Mj_Vertex v = this.v;
		
		for(int i = 0; i < size; i++) {
			double a = ((Mj_Vertex) v).point().x * ((Mj_Vertex) v.next()).point().y;
			double b = ((Mj_Vertex) v.next()).point().x * ((Mj_Vertex) v).point().y;
			
			sum += (a - b) + (x1_xN * y1_yN) - (x1_xN * y1_yN);
			
			v = (Mj_Vertex) v.next();
		}
		
		return sum / 2;
	}
	
	/**
	 * ������ ���������� ������ ������ �������� �� ���������������.
	 * 
	 * @return ������� � ���������� ������������ ������ ������
	 */
	
	public Mj_Polygon changeCircumventPoints() {
		for(int i = 0; i < size; i++) {
			Node c = v.next;
			
			v.next = v.prev;
			v.prev = c;
			
			v = v.ccw();
		}
		
		return this;
	}
}

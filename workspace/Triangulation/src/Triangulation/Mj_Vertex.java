/*
 * Copyright (c) 09.2017
 */

package Triangulation;

/** 
 * ����� ������� ��������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Mj_Vertex extends Node {
	
	private Mj_Point point; // ���������� �������
	
	// ����������� � ��������� �����
	enum Rotation { CLOCKWISE,           // �� ������� �������
		            COUNTER_CLOCKWISE }; // ������ ������� �������
	
	public Mj_Vertex() {
   		super();
   		
   		point = new Mj_Point();
	}
		            
	public Mj_Vertex(double x, double y) {
		super();
		
		point = new Mj_Point(x, y);
	}
	
	public Mj_Vertex(Mj_Point p) {
		super();
		
		point = p;
	}
	
	public Mj_Vertex(Mj_Vertex v) {
		super();
		
		point = new Mj_Point();
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
	
	public Mj_Vertex cw() {
		return (Mj_Vertex) next;
	}
	
	/**
	 * ���������� ���������� ������� ��������.
	 * 
	 * @return ���������� ������� ��������
	 */
	
	public Mj_Vertex ccw() {
		return (Mj_Vertex) prev;
	}
	
	/**
	 * ���������� ��� ����������� �������� ������� �� ��������� � �������.
	 * 
	 * @param rotation �������� ������������ {CLOCKWISE, COUNTER_CLOCKWISE}
	 * @return ���������� ���� �� �������� ������������
	 */
	
	public Mj_Vertex neighbor(Rotation rotation) {
		return ((rotation == Rotation.CLOCKWISE) ? cw() : ccw());
	}
	
	/**
	 * ���������� ����� �� ���������, � ������� ��������� ������� �������.
	 * 
	 * @return ����� �� ���������
	 */
	
	public Mj_Point point() {
		return point;
	}
	
	/**
	 * �������� ������� ����� �� ����� ������� �������.
	 * 
	 * @param v ����������� �������
	 * @return ����������� �������
	 */
	
	public Mj_Vertex insert(Mj_Vertex v) {
		return (Mj_Vertex) ((Node) this).insert(v);
	}
	
	/**
	 * ������� ������� ������� �� ������� ��������� ������.
	 * 
	 * @return ��������� �������
	 */
	
	public Mj_Vertex remove() {
		return (Mj_Vertex) ((Node) this).n_remove();
	}
	
	/**
	 * ������������ ��� ������������� � ������� ������� �������� ���������� �������. 
	 * 
	 * @param node �������������� � ������� �������
	 */
	
	public void splice(Mj_Vertex b) {
		((Node) this).splice(b);
	}
	
	/**
	 * ��������� ������� ����� �����, ����������� ������� ������� (�����, ������� A) � �������� B. 
	 * 
	 * @param b ������� �����
	 * @return �������, ���������� ���������� ������� B.
	 */
	
	public Mj_Vertex split(Mj_Vertex b) {
		// ������� bp ����� �������� b
		Mj_Vertex bp = b.ccw().insert(new Mj_Vertex(new Mj_Point(b.point().x, b.point().y)));
		// ������� ap ����� ������� �������
		insert(new Mj_Vertex(new Mj_Point(point().x, point().y)));
		splice(bp);
		
		return bp;
	}
	
	/**
	 * ��������� ������� ����� �����, ����������� ������� ������� (�����, ������� A) � �������� B. 
	 * ����������� ������� ��� ������������ ���������.
	 * 
	 * @param b ������� �����
	 * @param outer_CW ������� ����, ��� ����� ����� �������� ������ �� ������� �������
	 * @return �������, ���������� ���������� ������� B.
	 */
	
	public Mj_Vertex split_triangle(Mj_Vertex b, boolean outer_CW) {
		// ������� bp ����� �������� b
		Mj_Vertex bp = b.ccw().insert(new Mj_Vertex(new Mj_Point(b.point().x, b.point().y)));
		// ������� ap ����� ������� �������
		insert(new Mj_Vertex(new Mj_Point(point().x, point().y)));
		splice(bp);

		// ��������� ���� ������� � ��� X (������ ������� �������)
		Edge edge = new Edge(bp.point, ((Mj_Vertex) bp.next).point);
		double angle = Math.round((edge.dest.subtraction(edge.org)).polarAngle());
		
//		System.out.println("Angle: " + angle + " [" + bp.point.x + ", " + bp.point.y + " - " + ((Mj_Vertex) bp.next).point.x + ", " + ((Mj_Vertex) bp.next).point.y + "]");
		
		// �������� ���������� ���������� ����� ��� ���������� ��������
		if(outer_CW) {
			if(angle == 0 || (angle >= 315 && angle <= 360)) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				bp.point.y += Triangulation.delta;
				((Mj_Vertex) bp.next).point.y += Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
			} else if(angle >= 45 && angle <= 135) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				bp.point.x -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.x -= Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
			} else if(angle > 135 && angle <= 180) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				bp.point.y -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.y -= Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
			} else if(angle >= 225 && angle <= 270) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				bp.point.x += Triangulation.delta;
				((Mj_Vertex) bp.next).point.x += Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
			}
		} else {
			if(angle == 0 || (angle >= 315 && angle <= 360)) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				bp.point.y -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.y -= Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
			} else if(angle >= 45 && angle <= 135) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				bp.point.x += Triangulation.delta;
				((Mj_Vertex) bp.next).point.x += Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
			} else if(angle > 135 && angle <= 180) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				bp.point.y += Triangulation.delta;
				((Mj_Vertex) bp.next).point.y += Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
			} else if(angle >= 225 && angle <= 270) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				bp.point.x -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.x -= Triangulation.delta;
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
			}
		}
		
		return bp;
	}
	
	/**
	 * ���������� ��������������� ���� ������.
	 *  
	 * @param p ������� ��� ���������
	 * @return ���������� TRUE, ���� ������� ������������ (�����), ����� FALSE
	 */
	
	public boolean equalsVertex(Mj_Vertex v) {
		if((point.x == v.point.x) && (point.y == v.point.y))
			return true;
		else
			return false;
		
	}
}

/*
 * Copyright (c) 09.2017
 */

package Triangulation;

/** 
 * ����� ������� ��������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Mj_Vertex extends Mj_Node {
	
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
		return (Mj_Vertex) ((Mj_Node) this).insert(v);
	}
	
	/**
	 * ������� ������� ������� �� ������� ��������� ������.
	 * 
	 * @return ��������� �������
	 */
	
	public Mj_Vertex remove() {
		return (Mj_Vertex) ((Mj_Node) this).n_remove();
	}
	
	/**
	 * ������������ ��� ������������� � ������� ������� �������� ���������� �������. 
	 * 
	 * @param b �������������� � ������� �������
	 */
	
	public void splice(Mj_Vertex b) {
		((Mj_Node) this).splice(b);
	}
	
	/**
	 * ���������� ��� �������� �� ������ �����. �������� ��� ����� ���� ���������
	 * � ������� � ���� � �� �� �������, � �� ���� ������ ���������� �������
	 * ���� ��������� � ����.
	 * 
	 * 
	 * @param bp ������� ����� ������������ ��������
	 */
	
	public void union(Mj_Vertex bp) {
		Mj_Vertex tmp = (Mj_Vertex) bp.next();
		((Mj_Vertex) next()).splice(bp);
		splice(tmp);
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
	
	public Mj_Vertex split_and_offset_triangle(Mj_Vertex b, boolean outer_CW, Mj_Polygon outer_polygon) {
		// ������� bp ����� �������� b
		Mj_Vertex bp = b.ccw().insert(new Mj_Vertex(new Mj_Point(b.point().x, b.point().y)));
		// ������� ap ����� ������� �������
		insert(new Mj_Vertex(new Mj_Point(point().x, point().y)));
		splice(bp);
		
		Mj_Vertex a = this; // ����� �������� ��������
		Mj_Vertex ap = (Mj_Vertex) bp.next(); // ����� �������� �������� ����� ����������
		
		Mj_Vertex a_prev = (Mj_Vertex) ((Mj_Vertex) b.prev()).prev(); // ����� ������ a
		Mj_Vertex ap_next = (Mj_Vertex) ((Mj_Vertex) bp.next()).next(); // ����� ����� ap
		
		Mj_Vertex b_next = (Mj_Vertex) b.next(); // ����� ����� b
		Mj_Vertex bp_prev = (Mj_Vertex) bp.prev(); // ����� ������ bp
		
		// ����� ����� �� � ����� ����� ����������
		double l_a = (a_prev.point().subtraction(a.point())).length();
		double l_b = (b_next.point().subtraction(b.point())).length();
		double l_ap = (ap_next.point().subtraction(ap.point())).length();
		double l_bp = (bp_prev.point().subtraction(bp.point())).length();	
		
		// ������ ��� ��������� ��������� ������ � ����� ����������
		VertexOffseter vOff = new VertexOffseter();
		
		// ��������� ����� ������� �������� ��������
		if(l_a >= l_ap && l_b >= l_bp) {
			// ��������� ������������ ����� �������, �����
			// �� ��� ������ �� ������ ��� ���� ������
			Triangulation.useOuterVertexes.add(new Mj_Vertex(a));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(b));
			
			// ������� ���������� ������ � ����� ���������� �������� ���, ����� ������ ���������������
			vOff.offset(a, b, outer_polygon, Triangulation.delta);
			
			// ��������� ���������� ����� �������, �����
			// �� ��� ������ �� ������ ��� ���� ������
			Triangulation.useOuterVertexes.add(new Mj_Vertex(a));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(b));
		} else if(l_a >= l_ap && l_b < l_bp) {
			// ��������� ������������ ����� �������, �����
			// �� ��� ������ �� ������ ��� ���� ������
			Triangulation.useOuterVertexes.add(new Mj_Vertex(a));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
			
			// ������� ���������� ������ � ����� ���������� �������� ���, ����� ������ ���������������
			vOff.offset(a, bp, outer_polygon, Triangulation.delta);
			
			// ��������� ���������� ����� �������, �����
			// �� ��� ������ �� ������ ��� ���� ������
			Triangulation.useOuterVertexes.add(new Mj_Vertex(a));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
		} else if(l_a < l_ap && l_b >= l_bp) {
			// ��������� ������������ ����� �������, �����
			// �� ��� ������ �� ������ ��� ���� ������
			Triangulation.useOuterVertexes.add(new Mj_Vertex(ap));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(b));
			
			// ������� ���������� ������ � ����� ���������� �������� ���, ����� ������ ���������������
			vOff.offset(ap, b, outer_polygon, Triangulation.delta);
			
			// ��������� ���������� ����� �������, �����
			// �� ��� ������ �� ������ ��� ���� ������
			Triangulation.useOuterVertexes.add(new Mj_Vertex(ap));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(b));
		} else if(l_a < l_ap && l_b < l_bp) {
			// ��������� ������������ ����� �������, �����
			// �� ��� ������ �� ������ ��� ���� ������
			Triangulation.useOuterVertexes.add(new Mj_Vertex(ap));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
			
			// ������� ���������� ������ � ����� ���������� �������� ���, ����� ������ ���������������
			vOff.offset(ap, bp, outer_polygon, Triangulation.delta);
			
			// ��������� ���������� ����� �������, �����
			// �� ��� ������ �� ������ ��� ���� ������
			Triangulation.useOuterVertexes.add(new Mj_Vertex(ap));
			Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
		}
				
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
	
	public Mj_Vertex split_triangle(Mj_Vertex b, boolean outer_CW, Mj_Polygon outer_polygon) {
		// ������� bp ����� �������� b
		Mj_Vertex bp = b.ccw().insert(new Mj_Vertex(new Mj_Point(b.point().x, b.point().y)));
		// ������� ap ����� ������� �������
		insert(new Mj_Vertex(new Mj_Point(point().x, point().y)));
		splice(bp);

		// ��������� ���� ������� � ��� X (������ ������� �������)
		Edge edge = new Edge(bp.point, ((Mj_Vertex) bp.next).point);
		double angle = Math.round((edge.dest.subtraction(edge.org)).polarAngle());
		
//		System.out.println("Angle: " + angle + " [" + bp.point.x + ", " + bp.point.y + " - " + ((Mj_Vertex) bp.next).point.x + ", " + ((Mj_Vertex) bp.next).point.y + "]");
		
		// �������� ���������� ���������� ����� ����� ��� ���������� ��������
/*		if(!outer_CW) {
			if(angle == 0 || (angle >= 315 && angle <= 360)) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// ��������� ������������ ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// �������� ����� �������
				bp.point.y += Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y += Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				// ��������� ������������ ������������ ��������� � ���������
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// ��������� ���������� ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle > 0 && angle < 45) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// ��������� ������������ ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// �������� ����� �������
				bp.point.y += Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y += Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// ��������� ���������� ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle >= 45 && angle <= 135) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				// ��������� ������������ ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// �������� ����� �������
				bp.point.x -= Triangulation.delta;
				bp.point.x = Decompositor.round7(bp.point.x);
				
				((Mj_Vertex) bp.next).point.x -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.x = Decompositor.round7(((Mj_Vertex) bp.next).point.x);
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
				
				// ��������� ���������� ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle > 135 && angle <= 180) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// ��������� ������������ ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// �������� ����� �������
				bp.point.y += Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y += Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// ��������� ���������� ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle >180 && angle < 225) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// ��������� ������������ ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// �������� ����� �������
				bp.point.y -= Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// ��������� ���������� ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle >= 225 && angle <= 270) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				// ��������� ������������ ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// �������� ����� �������
				bp.point.x += Triangulation.delta;
				bp.point.x = Decompositor.round7(bp.point.x);
				
				((Mj_Vertex) bp.next).point.x += Triangulation.delta;
				((Mj_Vertex) bp.next).point.x = Decompositor.round7(((Mj_Vertex) bp.next).point.x);
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
				
				// ��������� ���������� ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle > 270 && angle < 315) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				// ��������� ������������ ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// �������� ����� �������
				bp.point.x += Triangulation.delta;
				bp.point.x = Decompositor.round7(bp.point.x);
				
				((Mj_Vertex) bp.next).point.x += Triangulation.delta;
				((Mj_Vertex) bp.next).point.x = Decompositor.round7(((Mj_Vertex) bp.next).point.x);
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
				
				// ��������� ���������� ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			}
		} else {
			if(angle == 0 || (angle >= 315 && angle <= 360)) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// ��������� ������������ ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// �������� ����� �������
				bp.point.y -= Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// ��������� ���������� ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle > 0 && angle < 45) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// ��������� ������������ ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// �������� ����� �������
				bp.point.y -= Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// ��������� ���������� ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle >= 45 && angle <= 135) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				// ��������� ������������ ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// �������� ����� �������
				bp.point.x += Triangulation.delta;
				bp.point.x = Decompositor.round7(bp.point.x);
				
				((Mj_Vertex) bp.next).point.x += Triangulation.delta;
				((Mj_Vertex) bp.next).point.x = Decompositor.round7(((Mj_Vertex) bp.next).point.x);
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
				
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
								
				// �������� ����� �������
				bp.point.y -= Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// ��������� ���������� ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle > 135 && angle <= 180) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// ��������� ������������ ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// �������� ����� �������
				bp.point.y += Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y += Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				// �������� ����� �������
				bp.point.x += Triangulation.delta;
				bp.point.x = Decompositor.round7(bp.point.x);
				
				((Mj_Vertex) bp.next).point.x += Triangulation.delta;
				((Mj_Vertex) bp.next).point.x = Decompositor.round7(((Mj_Vertex) bp.next).point.x);
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
				
				// ��������� ���������� ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle >180 && angle < 225) {
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
				
				// ��������� ������������ ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// �������� ����� �������
				bp.point.y += Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y += Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// ��������� ���������� ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle >= 225 && angle <= 270) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				// ��������� ������������ ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// �������� ����� �������
				bp.point.x -= Triangulation.delta;
				bp.point.x = Decompositor.round7(bp.point.x);
				
				((Mj_Vertex) bp.next).point.x -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.x = Decompositor.round7(((Mj_Vertex) bp.next).point.x);
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
				
				// ��������� ���������� ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			} else if(angle > 270 && angle < 315) {
				double xb = bp.point.x;
				double xbn = ((Mj_Vertex) bp.next).point.x;
				
				// ��������� ������������ ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
				
				// �������� ����� �������
				bp.point.x -= Triangulation.delta;
				bp.point.x = Decompositor.round7(bp.point.x);
				
				((Mj_Vertex) bp.next).point.x -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.x = Decompositor.round7(((Mj_Vertex) bp.next).point.x);
				
				Triangulation.org_coords.put(bp.point.x, xb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.x, xbn);
				
				double yb = bp.point.y;
				double ybn = ((Mj_Vertex) bp.next).point.y;
								
				// �������� ����� �������
				bp.point.y -= Triangulation.delta;
				bp.point.y = Decompositor.round7(bp.point.y);
				
				((Mj_Vertex) bp.next).point.y -= Triangulation.delta;
				((Mj_Vertex) bp.next).point.y = Decompositor.round7(((Mj_Vertex) bp.next).point.y);
				
				Triangulation.org_coords.put(bp.point.y, yb);
				Triangulation.org_coords.put(((Mj_Vertex) bp.next).point.y, ybn);
				
				// ��������� ���������� ����� �������, �����
				// �� ��� ������ �� ������ ��� ���� ������
				Triangulation.useOuterVertexes.add(new Mj_Vertex(bp));
				Triangulation.useOuterVertexes.add(new Mj_Vertex((Mj_Vertex) bp.next));
			}
		}*/
		
		return bp;
	}
	
	/**
	 * ���������� ��������������� ���� ������.
	 *  
	 * @param v ������� ��� ���������
	 * @return ���������� TRUE, ���� ������� ������������ (�����), ����� FALSE
	 */
	
	public boolean equalsVertex(Mj_Vertex v) {
		if((point.x == v.point.x) && (point.y == v.point.y) && next == v.next && prev == v.prev)
			return true;
		else
			return false;
		
	}
	
	/**
	 * ���������� ��������������� ��������� ���� ������.
	 *  
	 * @param v ������� ��� ���������
	 * @return ���������� TRUE, ���� ������� ������������ (�����), ����� FALSE
	 */
	
	public boolean equalsCoordsVertex(Mj_Vertex v) {
		if(v != null) {
			if((point.x == v.point.x) && (point.y == v.point.y))
				return true;
			else
				return false;
		}
		
		return false;
	}
}

/*
 * Copyright (c) 09.2017
 */

package mj82.Triangulation;

import java.util.ArrayList;

import mj82.Triangulation.ActiveElement.Active_element_type;
import mj82.Triangulation.Mj_Point.Point_position;
import mj82.Triangulation.Mj_Vertex.Rotation;

/**
 * ����� ��� ������������ ��������� �� ���������� �����.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Decompositor {
	
	private double[] angs; // ���� ����� �������� ������������ ��� �
	private double min = Double.MIN_VALUE; // ����������� ���� � ��� � ������ ������� �������
		
//	private boolean isRotateLeftRight; // ������� ����, ��� ������� ���������� ������������ ��� ������������ ����� �� �����
//	private boolean isRotateRightLeft; // ������� ����, ��� ������� ���������� ������������ ��� ������������ ������ �� ����
	public static double centerX = 0; // ����� �� ��� � ��������������� ��������
	public static double centerY = 0; // ����� �� ��� Y ��������������� ��������
//	private VertexHashMap old_coords_left_right; // ������������ ���������� �� �������� ��� ������������ ����� �� �����
//	private VertexHashMap old_coords_right_left; // ������������ ���������� �� �������� ��� ������������ ������ �� ����
	
	public static Scaning sweepdirection; // ������� ����������� ������������
	public static double curx; // ������� ������� ����������� �����
	public static Current_transition_type curtype; // ������� ��� ��������
	// ������� ��� ��������
	enum Current_transition_type { START_TYPE, // ��������� �������
		                           BEND_TYPE,  // ������� ��������
		                           END_TYPE }; // �������� �������

	// ����������� ����������� ����������� ������ ��������
	enum Scaning { LEFT_TO_RIGHT,   // �����-�������
		           RIGHT_TO_LEFT }; // ������-������
		
	/**
	 * ��������� �������� ������� �� ����������. 
	 * 
	 * @param p �������
	 * @return ������ ���������� ���������
	 */
		           
	public Mj_List<Mj_Polygon> regularize(Mj_Polygon p) {
		VertexHashMap old_coords_left_right = null; // ������������ ���������� �� �������� ��� ������������ ����� �� �����
		VertexHashMap old_coords_right_left = null; // ������������ ���������� �� �������� ��� ������������ ������ �� ����
		boolean isRotateLeftRight; // ������� ����, ��� ������� ���������� ������������ ��� ������������ ����� �� �����
		boolean isRotateRightLeft; // ������� ����, ��� ������� ���������� ������������ ��� ������������ ������ �� ����
		
		isRotateLeftRight = false;
		Mj_Vertex[] schedule = buildSchedule(p, Scaning.LEFT_TO_RIGHT);
		int vert_edges = getAngels(schedule, Scaning.LEFT_TO_RIGHT);
//		getBound(schedule);
		
		if(vert_edges > 0) {
			isRotateLeftRight = true;
			old_coords_left_right = rotate(schedule, min);
		}
		
		// ���� 1
		Mj_List<Mj_Polygon> polys1 = new Mj_List<Mj_Polygon>();
		semiregularize(p, Scaning.LEFT_TO_RIGHT, polys1);
		
		// ���� 2
		Mj_List<Mj_Polygon> polys2 = new Mj_List<Mj_Polygon>();
		polys1.last();
		
//		System.out.println("///////////////////////");
		
		while(!polys1.isHead()) {
			Mj_Polygon q = polys1.remove();
			
			isRotateRightLeft = false;
			Mj_Vertex[] schedule2 = buildSchedule(q, Scaning.RIGHT_TO_LEFT);
			int vert_edges2 = getAngels(schedule2, Scaning.RIGHT_TO_LEFT);
			
			if(vert_edges2 > 0) {
				isRotateRightLeft = true;
				old_coords_right_left = rotate(schedule2, min);
			}
			
			semiregularize(q, Scaning.RIGHT_TO_LEFT, polys2);
			
			if(isRotateRightLeft) {
				returnOriginalViewAfterRotate(old_coords_right_left, polys2.val());
				old_coords_right_left = null;
			}
		}
		
		if(polys2 != null && polys2.length() > 0) {
			polys2.first();
		
			for(int i = 0; i < polys2.length(); i++) {
				// ���������� ��������� ������������ (�����������) ���������� ������,
				// ������� �������� ��� ��������					
				if(isRotateLeftRight)
					returnOriginalViewAfterRotate(old_coords_left_right, polys2.val());
				
				polys2.next();
			}
		}
		
		return polys2;
	}
	
	/**
	 * ��������� ��� ������ �������� ������������ � ���� �������.
	 * 
	 * @param p �������
	 * @param direction ����������� ������������ ��������
	 * @param polys ������ ������������ ���������� ���������
	 */
	
	public void semiregularize(Mj_Polygon p, Scaning direction, Mj_List<Mj_Polygon> polys) {		
		sweepdirection = direction;
		Scaning cmp;
		// ��� �������� ����� ���������� ������������ ��������
		Mj_Polygon [] splitPolygons = new Mj_Polygon[2];
		// ������ ���������, ������� ��������� ��� ����������
		Mj_List<Mj_Polygon> polys3 = new Mj_List<Mj_Polygon>();
		// ������ ���������, � ������� ���� ������������ �����, ����� ����������
		// ��������� ��������
		Mj_List<Mj_Polygon> polys4 = new Mj_List<Mj_Polygon>();
		
		if(sweepdirection == Scaning.LEFT_TO_RIGHT)
			cmp = Scaning.LEFT_TO_RIGHT;
		else
			cmp = Scaning.RIGHT_TO_LEFT;
		
		Mj_Vertex[] schedule = buildSchedule(p, cmp);
		RandomizedSearchTree<ActiveElement> sweepline = buildSweepline();
		
		int countVertexes = 0; // ������� ���������� ������ � ��������

		for(countVertexes = 0; countVertexes < p.size; countVertexes++) {
			Mj_Vertex v = schedule[countVertexes];
			
			curx = v.point().x;
			
			switch(curtype = typeEvent(v, cmp)) {
			case START_TYPE:
				splitPolygons = startTransition(v, sweepline, splitPolygons, polys3);
				
				if(splitPolygons[0] != null && splitPolygons[1] != null) {
					// ���� ������������ �����
					Mj_Vertex[] schedule_sp1 = buildSchedule(splitPolygons[0], cmp);
					Mj_Vertex[] schedule_sp2 = buildSchedule(splitPolygons[1], cmp);					
					int vert_edges_sp1 = getAngels(schedule_sp1, null);
					int vert_edges_sp2 = getAngels(schedule_sp2, null);
					
					// ���� � ����� �������������� ��������� ���� ������������ �����,
					// �� ���������� ����
					if(vert_edges_sp1 > 0 && vert_edges_sp2 > 0) {
						polys4.append(splitPolygons[0]);
						polys4.append(splitPolygons[1]);
						countVertexes = Integer.MAX_VALUE - 1;
						continue;
					} else if(vert_edges_sp1 > 0 && vert_edges_sp2 == 0) { // ���� � ������ �������� ���� ������������ �����
						polys4.append(splitPolygons[0]);
						p = splitPolygons[1];
					} else if(vert_edges_sp1 == 0 && vert_edges_sp2 > 0) { // ���� �� ������ �������� ���� ������������ �����
						polys4.append(splitPolygons[1]);
						p = splitPolygons[0];
					} else { // � ����� ��������� ��� ������������ �����
						if(splitPolygons[0].size >= splitPolygons[1].size) {
							p = splitPolygons[0];
							polys3.append(splitPolygons[1]);
						} else {
							p = splitPolygons[1];
							polys3.append(splitPolygons[0]);
						}
					}
					
					countVertexes = -1;		
					schedule = buildSchedule(p, cmp);
					sweepline = buildSweepline();
					splitPolygons[0] = null;
					splitPolygons[1] = null;
				}
				break;
			case BEND_TYPE:
				bendTransition(v, sweepline);
				break;
			case END_TYPE:
				endTransition(v, sweepline, polys);				
				break;
			}
		}
		
		p.setVertex(null);
		p.size = 0;
		
		// ��������� ������ ���������� ��������� ����������� ��� ���������
		if(polys3.length() != 0) {
			polys3.last();
			
			while(!polys3.isHead()) {
				Mj_Polygon q = polys3.remove();
				semiregularize(q, cmp, polys);
			}
		}
		
		// ��������� ������ ���������� ��������� � ������������� ������� ����������� ��� ���������
		if(polys4.length() != 0) {
			polys4.last();
					
			while(!polys4.isHead()) {
				Mj_Polygon q = polys4.remove();
				polys.append(regularize(q));
			}
		}
	}
	
	/**
	 * ��������� ������� � ������� ���������� ���������� X.
	 * 
	 * @param p �������
	 * @param cmp ������������� ����������� ������������ �������� ��� X (����� ��� ������)
	 * @return ������ ��������������� ������
	 */
	
	public Mj_Vertex[] buildSchedule(Mj_Polygon p, Scaning cmp) {
		Mj_Vertex[] schedule = new Mj_Vertex[p.size];
		
		for(int i = 0; i < p.size; i++, p.advance(Rotation.CLOCKWISE)) {
			schedule[i] = p.getVertex();
		}
		
		insertionSort(schedule, p.size, cmp);
		
		return schedule;
	}
	
	/**
	 * ��������� ������� � ������� ���������� ���������� X. ������� �������� �������
	 * ������ ��������� ��������, ����� ��� ������������� � ��������� �����������
	 * �����.
	 * 
	 * @param p �������
	 * @param cmp ������������� ����������� ��� X (����� ��� ������)
	 * @return ������ ��������������� ������
	 */
	
	public Mj_Vertex[] buildCopySchedule(Mj_Polygon p, Scaning cmp) {
		Mj_Vertex[] schedule = new Mj_Vertex[p.size];
		
		for(int i = 0; i < p.size; i++, p.advance(Rotation.CLOCKWISE)) {
			schedule[i] = new Mj_Vertex(p.getVertex());
		}
		
		insertionSort(schedule, p.size, cmp);
		
		return schedule;
	}
	
	/**
	 * ��������� �������� � ������� �� �������� � ��������.
	 * 
	 * @param a ������
	 * @param n ���-�� ��������� ������� ��� ����������
	 */
	
	public void insertionSort(Mj_Vertex[] a, int n, Scaning cmp) {
		for(int i = 0; i < n - 1; i++) {
			int min = i;
			
			for(int j = i + 1; j < n; j++) {
				switch(cmp) {
				case LEFT_TO_RIGHT:
					if(CompareFunc.leftToRightCmp(a[j], a[min]) < 0)
						min = j;
					break;
				case RIGHT_TO_LEFT:
					if(CompareFunc.rightToLeftCmp(a[j], a[min]) < 0)
						min = j;
					break;
				}
			}
			
			a = swap(a, i, min);
		}
	}
	
	/**
	 * ��������� �������� ������ �������� � ������� ������ ��� �������� �� ���� ��������.
	 * 
	 * @param a ������ ������
	 * @param i ������ ������
	 * @param min ������ ������
	 * @return ���������� ������ ������
	 */
	
	public Mj_Vertex[] swap(Mj_Vertex[] a, int i, int min) {
		Mj_Vertex v = a[i];
		
		a[i] = a[min];
		a[min] = v;
		
		return a;
	}
	
	/**
	 * ���������� ��� ������� (���������, ��������, ��������) ��� ����������� ��������.
	 * 	
	 * @param v �������
	 * @param cmp ����������� ������������
	 * @return ��� ������� ��� ����������� ��������
	 */
	
	public Current_transition_type typeEvent(Mj_Vertex v, Scaning cmp) {
		int a = 0;
		int b = 0;
		
		switch(cmp) {
		case LEFT_TO_RIGHT:
			a = CompareFunc.leftToRightCmp(v.cw(), v);
			b = CompareFunc.leftToRightCmp(v.ccw(), v);
			break;
		case RIGHT_TO_LEFT:
			a = CompareFunc.rightToLeftCmp(v.cw(), v);
			b = CompareFunc.rightToLeftCmp(v.ccw(), v);
			break;
		}
		
		if((a <= 0) && (b <= 0))
			return Current_transition_type.END_TYPE;
		else if((a > 0) && (b > 0))
			return Current_transition_type.START_TYPE;
		else
			return Current_transition_type.BEND_TYPE;
	}
	
	/**
	 * ������� ��������� ����������� �����.
	 * 
	 * @return ����������� �����
	 */
	
	public RandomizedSearchTree<ActiveElement> buildSweepline() {
		RandomizedSearchTree<ActiveElement> sweepline = new RandomizedSearchTree<ActiveElement>(Decompositor::activeElementCmp);
		sweepline.insert(new ActivePoint(new Mj_Point(0.0, -Double.MAX_VALUE)));
		
		return sweepline;
	}
	
	/**
	 * ���������� ��� �������� ��������. ������� ���������� �� ������ ��������� Y
	 * �� ����� ����������� �� ����������� ������. ���� ��� ������������ � ����� � ��� �� �����,
	 * �� ���������, ��� �������� ����� ����������� ��� �������� ������. ���� ��� �������� 
	 * �������� ��������� �������, �� ��� �����������, ����� �� ��� ����� ���� �������,
	 * ������������ �� ��������������� ��������� �������.
	 * 
	 * @param a ������ �������� �������
	 * @param b ������ �������� �������
	 * @return 0 - ���� �������� �����, 1 - ���� ������ �������� ������� ������ ������� ��������� ��������, 
	 * -1 - ���� ������ �������� ������� ������ ������� ��������� ��������
	 */
	
	static int activeElementCmp(ActiveElement a, ActiveElement b) {	
		double ya = a.getY();
		double yb = b.getY();
		
		ya = round8(ya);
		yb = round8(yb);			
		
		if(ya < yb)
			return -1;
		else if(ya > yb)
			return 1;
				
		if((a.type == Active_element_type.ACTIVE_POINT) && (b.type == Active_element_type.ACTIVE_POINT))
			return 0;
		else if(a.type == Active_element_type.ACTIVE_POINT)
			return -1; 
		else if(b.type == Active_element_type.ACTIVE_POINT)
			return 1;
		
		int rval = 1;
		
		if((sweepdirection == Scaning.LEFT_TO_RIGHT && curtype == Current_transition_type.START_TYPE) || 
		   (sweepdirection == Scaning.RIGHT_TO_LEFT && curtype == Current_transition_type.END_TYPE))
			rval = -1;
		
		double ma = a.slope();
		double mb = b.slope();
		
		if(ma < mb)
			return rval;
		else if(ma > mb)
			return -rval;
		
		return 0;
	}
	
	/**
	 * ������������ ������� ��� ���������� ����������� ����� "��������� �������".
	 * 
	 * @param v �������
	 * @param sweepline ����������� �����
	 * @param splitPolygons ��� �������� ����� ���������� ������������ ��������
	 * @param polys3 ������ ��� �������������� ��������� ��� ��������� ����������� ��������
	 * @return ��� �������� ����� ���������� ������������ ��������
	 */
	
	public Mj_Polygon [] startTransition(Mj_Vertex v, RandomizedSearchTree<ActiveElement> sweepline,
		 	                             Mj_Polygon [] splitPolygons, Mj_List<Mj_Polygon> polys3) {
		
		ActivePoint ve = new ActivePoint(v.point());
		ActiveEdge a = (ActiveEdge) sweepline.locate(ve);
//		a.type = Active_element_type.ACTIVE_EDGE;
		
		Mj_Vertex w = a.w;
		
		if(!isConvex(v)) {
/*			if(splitPolygons[0] != null && splitPolygons[1] != null) {
				
				ArrayList<Mj_Vertex> sp1 = splitPolygons[0].getArrayListOfVertex(splitPolygons[0]);
				ArrayList<Mj_Vertex> sp2 = splitPolygons[1].getArrayListOfVertex(splitPolygons[1]);
				
				if(sp1.contains(v))
					polys3.append(splitPolygons[1]);
				else if(sp2.contains(v))
					polys3.append(splitPolygons[0]);
					
			}*/
			
			Mj_Vertex wp = v.split(w);
							
			splitPolygons[0] = new Mj_Polygon(v);
			splitPolygons[1] = new Mj_Polygon(wp);
			
			sweepline.insert(new ActiveEdge(wp.cw(), Rotation.CLOCKWISE, wp.cw()));
			sweepline.insert(new ActiveEdge(v.ccw(), Rotation.COUNTER_CLOCKWISE, v));
			
			a.w = (sweepdirection == Scaning.LEFT_TO_RIGHT) ? wp.ccw() : v;
		} else {
			sweepline.insert(new ActiveEdge(v.ccw(), Rotation.COUNTER_CLOCKWISE, v));
			sweepline.insert(new ActiveEdge(v, Rotation.CLOCKWISE, v));
			
			a.w = v;
		}
		
		return splitPolygons;
	}
	
	/**
	 * �������� ������� �������� �� ����������.
	 * 
	 * @param v ������� ��������
	 * @return TRUE, ���� ������� ��������, ����� - FALSE (��������)
	 */
	
	public boolean isConvex(Mj_Vertex v) {
		Mj_Vertex u = v.ccw();
		Mj_Vertex w = v.cw();
		
		Point_position c = w.point().classify(u.point(), v.point());
		
		return ((c == Point_position.BEYOND) || (c == Point_position.RIGHT));
	}
	
	/**
	 * ������������ ������� ��� ���������� ����������� ����� "������� ��������".
	 * 
	 * @param v �������
	 * @param sweepline ����������� �����
	 */
	
	public void bendTransition(Mj_Vertex v, RandomizedSearchTree<ActiveElement> sweepline) {
		ActivePoint ve = new ActivePoint(v.point());
		ActiveEdge a = (ActiveEdge) sweepline.locate(ve);
		ActiveEdge b = (ActiveEdge) sweepline.next();
		
		a.w = v;
		b.w = v;
		b.v = b.v.neighbor(b.rotation);
	}
	
	/**
	 * ������������ ������� ��� ���������� ����������� ����� "�������� �������".
	 * 
	 * @param v �������
	 * @param sweepline ����������� �����
	 * @param polys �������
	 * @param splitPolygons ��� �������� ����� ���������� ��������� ��������
	 * @return TRUE, ���� ��������� ��������� ����������� �������� �� ��������� ������������ ��������, 
	 * ����� - FALSE
	 */
	 
	public void endTransition(Mj_Vertex v, RandomizedSearchTree<ActiveElement> sweepline, 
			                     Mj_List<Mj_Polygon> polys) {
		ActivePoint ve = new ActivePoint(v.point());
		ActiveElement  a = sweepline.locate(ve);
		ActiveEdge b = (ActiveEdge) sweepline.next();
		ActiveEdge c = (ActiveEdge) sweepline.next();
		
		// ���� ����������� ��������� ����������� �������� �� ��������� ������������ ��������
		boolean isUpdatePoly = false;
		
		if(isConvex(v)) {
			polys.append(new Mj_Polygon(v));
			
			isUpdatePoly = true;
			
			// ������������� ������� � ������ ��������� ������� (�� �����, �� ��� ����� �� ��������)
			Mj_Polygon tmp = polys.last();
//			System.out.println("POLYGON");
//			for(int i = 0; i < tmp.size; i++) {
//				System.out.println("(" + tmp.point().x + " - " + tmp.point().y + ")");
//				tmp.advance();
//			}
		} else
			((ActiveEdge) a).w = v;		
		
		if(!isUpdatePoly) {
			sweepline.remove(b);
			sweepline.remove(c);
		} 
	}
	
	/**
	 * ��������� ���� ������� � ��� X (������ ������� �������)
	 * 
	 * @param edge ������
	 * @return ���� ������� � ��� X (������ ������� �������)
	 */
	
	private double getAngs(Edge edge) {
		Mj_Point sub = edge.dest.subtraction(edge.org);
		double angle = (Math.atan(sub.y / sub.x) * 180) / Math.PI;
		
		return angle;
	}
	
	/**
	 * ����������� ���� � ��� X ������� �� ����� ��������, ������� ���-�� ����� ������
	 * 90 �������� (������������), ������� ����� ��������� ���� � ��� X.
	 * 
	 * @param vrtxs ������ ������ ��������
	 * @param direction ����������� ����������� ����������� ������ ��������
	 * @return ���-�� ������������ �����
	 */ 
	
	public int getAngels(Mj_Vertex[] vrtxs, Scaning direction) {
		angs = new double[vrtxs.length];
		int count = 0;
		boolean isMin = false; 
		
		for(int i = 0; i < vrtxs.length; i++) {
			angs[i] = getAngs(new Edge(vrtxs[i].point(), ((Mj_Vertex) vrtxs[i].next()).point()));
			
//			System.out.println("" + (i + 1) + ": " + angs[i]);			
			
			// ��������� �������� ������������ ���� ����� ������������ ��� �
			if(angs[i] != 0 && !isMin) {
				min = Math.abs(angs[i]);
				isMin = true;
			}
			
			// ���� ����������� ���� ����� ������������ ��� �
			if(Math.abs(angs[i]) < min && Math.abs(angs[i]) != Math.abs(0))
				min = Math.abs(angs[i]);
			
			// ������� ������������ �����
			if(Math.abs(angs[i]) == 90) {
				count++;
				
				// ���� ������������ �����
/*				if(direction != null && direction == Scaning.LEFT_TO_RIGHT) {
					if(!isRotateLeftRight && count >= 1)
						isRotateLeftRight = true;
				}
				
				if(direction != null && direction == Scaning.RIGHT_TO_LEFT) {
					if(!isRotateRightLeft && count >= 1)
						isRotateRightLeft = true;
				}*/				
			}
		}
		
//		System.out.println("����� ������������ �����: " + count + ", Min angle: " + min);
		
		return count;
	}
	
	/**
	 * ������������ ������� �� ������� ������� �� �������� ���� ������������ ����� ������ ���������.
	 * 
	 * @param vrtxs ������ ������ ��������
	 * @param angle ���� �������� (� ��������)
	 * @return ������������ ���������� �� ��������
	 */
	
	public VertexHashMap rotate(Mj_Vertex[] vrtxs, double angle) {		
		return rotate(vrtxs, angle, 0, 0);
	}
	
	/**
	 * ������������ ������� �� ������� ������� �� �������� ���� ������������ �������� �����.
	 * 
	 * @param vrtxs ������ ������ ��������
	 * @param angle ���� �������� (� ��������)
	 * @param x0 �����, ������������ ������� ������������ �������
	 * @return ������������ ���������� �� ��������
	 */
	
	private VertexHashMap rotate(Mj_Vertex[] vrtxs, double angle, double x0, double y0) {
		VertexHashMap old_coords = new VertexHashMap();
		// ��������� ������������ ���������� ������ �������� ����� ��� ���������
		ArrayList<Mj_Vertex> originCoords = new ArrayList<>(vrtxs.length);
		
		for(int i = 0; i < vrtxs.length; i++)
			originCoords.add(new Mj_Vertex(vrtxs[i].point().x, vrtxs[i].point().y));
		
		// ������������ �� ��� ���, ���� �������� ������������ �����
		int vert_edges = 0;
		
		do {
			for(int i = 0; i < vrtxs.length; i++) {
				double x = vrtxs[i].point().x;
				double y = vrtxs[i].point().y;
			
				vrtxs[i].point().x = (x - x0) * Math.cos(((angle * Math.PI) / 180)) - (y - y0) * Math.sin(((angle * Math.PI) / 180));
				vrtxs[i].point().y = (x - x0) * Math.sin(((angle * Math.PI) / 180)) + (y - y0) * Math.cos(((angle * Math.PI) / 180));

				vrtxs[i].point().x = round7(vrtxs[i].point().x);
				vrtxs[i].point().y = round7(vrtxs[i].point().y);
			}
			
			vert_edges = getAngels(vrtxs, null);
			angle += 0.1;
		} while(vert_edges != 0);
		
		// ��������� ������������� ������������ ��������� ������ � ����������� (�����������)
		for(int i = 0; i < vrtxs.length; i++) {			
			old_coords.put(new Mj_Vertex(vrtxs[i]), new Mj_Vertex(originCoords.get(i)));
		}
		
		return old_coords;
	}
	
	/**
	 * ����������� �� ����� ����� �������� ����� ����� �������.
	 * 
	 * @param val �������������� �����
	 * @return ����� � ������ ����� �������� ����� ����� �������
	 */
	
	static double round7(double val) {
		double tmp = val * 10000000;
		tmp = Math.round(tmp);
		tmp = tmp / 10000000;
		
		return tmp;
	}
	
	/**
	 * ����������� �� ����� ����� �������� ����� ����� �������.
	 * 
	 * @param val �������������� �����
	 * @return ����� � ������ ����� �������� ����� ����� �������
	 */
	
	static double round8(double val) {
		double tmp = val * 100000000;
		tmp = Math.round(tmp);
		tmp = tmp / 100000000;
		
		return tmp;
	}
	
	/**
	 * ���������� ��������� ������������ (�����������) ���������� ������,
	 * ������� �������� ��� ��������.
	 * 
	 * @param polys ������ ���������
	 * @param old_coords ������������ ���������� �� ��������
	 * @return ������ ��������� � ������������� ������������ ������
	 */
	
	public Mj_Polygon returnOriginalViewAfterRotate(VertexHashMap old_coords, Mj_Polygon p) {		
		// ����� ��������
		Mj_Vertex[] schedule = buildSchedule(p, Scaning.LEFT_TO_RIGHT);
			
		for(int i = 0; i < schedule.length; i++) {
			Mj_Vertex tmp_v = old_coords.get(schedule[i]);
				
			if(tmp_v != null) {
				schedule[i].point().x = tmp_v.point().x;
				schedule[i].point().y = tmp_v.point().y;
			}
		}
		
		return p;
	}
	
	/**
	 * ���������� ��������� ������������ (�����������) ���������� ������,
	 * ������� �������� ��� ����������.
	 * 
	 * @param polys ������ ���������
	 * @return ������ ��������� � ������������� ������������ ������
	 */
	
	public Mj_Polygon returnOriginalViewAfterSplit(Mj_Polygon p) {		
		// ����� ����������
		if(p != null) {
			Mj_Vertex[] schedule = buildSchedule(p, Scaning.LEFT_TO_RIGHT);
			
//			System.out.println("New polygon # " + ++polygonCounter);
			for(int i = 0; i < schedule.length; i++) {
				try {
					schedule[i].point().x = Triangulation.org_coords.get(schedule[i].point().x);
				} catch(NullPointerException e) {}
				try {
					schedule[i].point().y = Triangulation.org_coords.get(schedule[i].point().y);
				} catch(NullPointerException e) {}
				
//				System.out.println("[" + schedule[i].point().x + ", " + schedule[i].point().y + "]");
			}
		}
		
		return p;
	}
}

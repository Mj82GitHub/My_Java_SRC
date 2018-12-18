/*
 * Copyright (c) 09.2017
 */

package Triangulation;

import java.util.HashMap;

import Triangulation.ActiveElement.Active_element_type;
import Triangulation.Mj_Point.Point_position;
import Triangulation.Mj_Vertex.Rotation;

/**
 * ����� ��� ������������ ��������� �� ���������� �����.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Decompositor {
	
	private double[] angs; // ���� ����� �������� ������������ ��� �
	private double min = Double.MIN_VALUE; // ����������� ���� � ��� � ������ ������� �������
	private double angle = 0.1; // ����, �� ������� �������������� ������� ��� ����������� � ��� ������������ ������
	
	private boolean isRotate = false; // ������� ����, ��� ������� ���������� ������������	
	public static double centerX = 0; // ����� �� ��� � ��������������� ��������
	public static double centerY = 0; // ����� �� ��� Y ��������������� ��������
	
	private HashMap<Double, Double> old_coords = new HashMap<Double, Double>(); // ������������ ���������� �� ��������
	
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
		
		Mj_Vertex[] schedule = buildSchedule(p, Scaning.LEFT_TO_RIGHT);
		getAngels(schedule);
		
		if(isRotate) {
			rotate(schedule, angle);
			getAngels(schedule);
		}
		
		// ���� 1
		Mj_List<Mj_Polygon> polys1 = new Mj_List<Mj_Polygon>();
		semiregularize(p, Scaning.LEFT_TO_RIGHT, polys1);
		
		// ���� 2
		Mj_List<Mj_Polygon> polys2 = new Mj_List<Mj_Polygon>();
		polys1.last();
		
		while(!polys1.isHead()) {
			Mj_Polygon q = polys1.remove();
			semiregularize(q, Scaning.RIGHT_TO_LEFT, polys2);
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
		
		if(sweepdirection == Scaning.LEFT_TO_RIGHT)
			cmp = Scaning.LEFT_TO_RIGHT;
		else
			cmp = Scaning.RIGHT_TO_LEFT;
		
		Mj_Vertex[] schedule = buildSchedule(p, cmp);		
		RandomizedSearchTree<ActiveElement> sweepline = buildSweepline();
		
		for(int i = 0; i < p.size; i++) {
			Mj_Vertex v = schedule[i];
			curx = v.point().x;
			
			switch(curtype = typeEvent(v, cmp)) {
			case START_TYPE:
				startTransition(v, sweepline);
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
	}
	
	/**
	 * ��������� ������� � ������� ���������� ���������� X.
	 * 
	 * @param p �������
	 * @param cmp ������������� ����������� ��� X (����� ��� ������)
	 * @return ������ ��������������� ������
	 */
	
	public Mj_Vertex[] buildSchedule(Mj_Polygon p, Scaning cmp) {
		Mj_Vertex[] schedule = new Mj_Vertex[p.size];
		
		for(int i = 0; i < p.size; i++, p.advance(Rotation.CLOCKWISE)) 
			schedule[i] = p.getVertex();
		
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
		
		if((float) ya < (float) yb)
			return -1;
		else if((float) ya > (float) yb)
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
	 */
	
	public void startTransition(Mj_Vertex v, RandomizedSearchTree<ActiveElement> sweepline) {
		ActivePoint ve = new ActivePoint(v.point());
		ActiveEdge a = (ActiveEdge) sweepline.locate(ve);
//		a.type = Active_element_type.ACTIVE_EDGE;
		
		Mj_Vertex w = a.w;
		
		if(!isConvex(v)) {
			Mj_Vertex wp = v.split(w);
			
			sweepline.insert(new ActiveEdge(wp.cw(), Rotation.CLOCKWISE, wp.cw()));
			sweepline.insert(new ActiveEdge(v.ccw(), Rotation.COUNTER_CLOCKWISE, v));
			
			a.w = (sweepdirection == Scaning.LEFT_TO_RIGHT) ? wp.ccw() : v;
		} else {
			sweepline.insert(new ActiveEdge(v.ccw(), Rotation.COUNTER_CLOCKWISE, v));
			sweepline.insert(new ActiveEdge(v, Rotation.CLOCKWISE, v));
			
			a.w = v;
		}
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
	 */
	 
	public void endTransition(Mj_Vertex v, RandomizedSearchTree<ActiveElement> sweepline, Mj_List<Mj_Polygon> polys) {
		ActivePoint ve = new ActivePoint(v.point());
		ActiveElement  a = sweepline.locate(ve);
		ActiveEdge b = (ActiveEdge) sweepline.next();
		ActiveEdge c = (ActiveEdge) sweepline.next();
		
		if(isConvex(v))
			polys.append(new Mj_Polygon(v));
		else
			((ActiveEdge) a).w = v;		
		
		sweepline.remove(b);
		sweepline.remove(c);
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
	 * 90 �������� (������������), ������� ����� ��������� ���� � ��� X, ����������� �����������
	 * ����� ��������������� ������� ��������������.
	 * 
	 * @param vrtxs ������ ������ ��������
	 */ 
	
	private void getAngels(Mj_Vertex[] vrtxs) {
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
				
				// ���� ������������ ����� � �� ����
				if(!isRotate && count > 1)
					isRotate = true;
			}
		}
		
		System.out.println("����� ������������ �����: " + count + ", Min angle: " + min);
	}
	
	/**
	 * ������������ ������� �� ������� ������� �� �������� ���� ������������ ����� ������ ���������.
	 * 
	 * @param vrtxs ������ ������ ��������
	 * @param angle ���� �������� (� ��������)
	 */
	
	private void rotate(Mj_Vertex[] vrtxs, double angle) {		
		rotate(vrtxs, angle, 0, 0);
	}
	
	/**
	 * ������������ ������� �� ������� ������� �� �������� ���� ������������ �������� �����.
	 * 
	 * @param vrtxs ������ ������ ��������
	 * @param angle ���� �������� (� ��������)
	 * @param x0 �����, ������������ ������� ������������ �������
	 */
	
	private void rotate(Mj_Vertex[] vrtxs, double angle, double x0, double y0) {		
		for(int i = 0; i < vrtxs.length; i++) {
			double x = vrtxs[i].point().x;
			double y = vrtxs[i].point().y;
			
			vrtxs[i].point().x = (x - x0) * Math.cos(((angle * Math.PI) / 180)) - (y - y0) * Math.sin(((angle * Math.PI) / 180));
			vrtxs[i].point().y = (x - x0) * Math.sin(((angle * Math.PI) / 180)) + (y - y0) * Math.cos(((angle * Math.PI) / 180));

			old_coords.put(vrtxs[i].point().x, x);
			old_coords.put(vrtxs[i].point().y, y);
		}
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
	 * ������� �������� ��� ���������� � ��������.
	 * 
	 * @param polys ������ ���������
	 * @return ������ ��������� � ������������� ������������ ������
	 */
	
	public Mj_List<Mj_Polygon> returnOriginalView(Mj_List<Mj_Polygon> polys) {
		Mj_Polygon p = polys.first();
		
		// ����� ��������
		while(!polys.isHead()) {
			Mj_Vertex[] schedule = buildSchedule(p, Scaning.LEFT_TO_RIGHT);
			
			for(int i = 0; i < schedule.length; i++) {
				schedule[i].point().x = old_coords.get(schedule[i].point().x);
				schedule[i].point().y = old_coords.get(schedule[i].point().y);
			}
			
			p = polys.next();
		}
		
		p = polys.first();
		
		int count = 0; // ������� ���������
		// ����� ����������
		while(!polys.isHead()) {
			Mj_Vertex[] schedule = buildSchedule(p, Scaning.LEFT_TO_RIGHT);
			
			System.out.println("New polygon. " + count++);
			for(int i = 0; i < schedule.length; i++) {
				try {
					schedule[i].point().x = Triangulation.org_coords.get(schedule[i].point().x);
				} catch(NullPointerException e) {}
				try {
					schedule[i].point().y = Triangulation.org_coords.get(schedule[i].point().y);
				} catch(NullPointerException e) {}
				
				System.out.println("[" + schedule[i].point().x + ", " + schedule[i].point().y + "]");
			}
			
			p = polys.next();
		}
		
		return polys;
	}	
}

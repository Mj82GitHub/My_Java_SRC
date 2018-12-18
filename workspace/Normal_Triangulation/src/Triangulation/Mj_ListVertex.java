/*
 * Copyright (c) 09.2017
 */

package Triangulation;

/**
 * ����� ����������� ������ ������ �������� - ������������ ����� ��������� ����� ���������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Mj_ListVertex {

	private Mj_Vertex header; // �������� ���� ������
	private Mj_Vertex win; // ������� ���� ������
	private int length; // ������ ������
	
	public Mj_ListVertex() {
		length = 0;
		
		header = new Mj_Vertex();
		win = header;
	}
	
	/**
	 * ��������� ���� ���������� ������.
	 */
	
	public void delete_list() {
		while(length() > 0) {
			first();
			remove();
		}
		
		header.delete_Node();
	}
	
	/**
	 * ������� ����� ������� ����� ��������.
	 * 
	 * @param v ����� �������
	 * @return ��������� �� ����� �������
	 */
	
	public Mj_Vertex insert(Mj_Vertex v) {
		win.insert(new Mj_Vertex(v));
		++length;
		
		return v;
	}
	
	/**
	 * ��������� ����� ������� � ����� ������.
	 * 
	 * @param v ����� �������
	 * @return ��������� �� ����� �������
	 */
	
	public Mj_Vertex append(Mj_Vertex v) {
		Mj_Node tmp = header.prev.insert(new Mj_Vertex(v));	
		
		header.prev = tmp;
		header.next.prev = header.prev;
		header.prev.next = header.next;		
		
		++length;
		
		return v;
	}
	
	/**
	 * ��������� ����� ������ � ����� �������� ������. ������ �������
	 * ������ l ���������� ����� ���������� �������� �������� ������.
	 * 
	 * @param l ����� ������
	 * @return ��������� �� ������� ������
	 */
	
	public Mj_ListVertex append(Mj_ListVertex l) {
		Mj_Vertex a = (Mj_Vertex) header.prev;
		a.splice(l.header);
		
		length += l.length;
		
		l.header.remove();
		l.length = 0;
		l.win = header;
		
		return this;
	}
	
	/**
	 * ��������� ����� ������� � ������ ������.
	 * 
	 * @param v ����� �������
	 * @return ��������� �� ����� �������
	 */
	
	public Mj_Vertex prepend(Mj_Vertex v) {
		header.insert(new Mj_Vertex(v));
		
		header.next.prev = header.prev;
		header.prev.next = header.next;		
		
		++length;
		
		return v;
	}
	
	/**
	 * ������� ������� ������� � ������ ���������� ������� �������.
	 * 
	 * @return ��������� �� ������ ��� ��������� �������
	 */
	
	public Mj_Vertex remove() {
		if(win.equalsVertex(header))
			return null;
		
		Mj_Vertex v = win;
		Mj_Vertex tmp = (Mj_Vertex) win.next;
		
		if(win.equalsVertex((Mj_Vertex) header.prev)) 
			header.prev = win.prev;
		
		if(win.equalsVertex((Mj_Vertex) header.next))
			header.next = win.next;
		
		win = (Mj_Vertex) win.prev;
		((Mj_Vertex) win.next.n_remove()).delete_Node();
		
		win.next = tmp;
		tmp.prev = win;
		
		--length;
		
		return v;
	}
	
	/**
	 * ������ �������� �������� �� �������� ����������. ���� ������� 
	 * ������� �������� ��������, �� ������� �������� �� ����������.
	 * 
	 * @param v �������
	 */
	
	public void val(Mj_Vertex v) {
		if(win != header)
			win = v;
	}
	
	/**
	 * ���������� ������� �������, �� ���� ������� ��������� �������� ��������,
	 * �� ���������� null.
	 * 
	 * @return ������� ������� ��� null
	 */
	
	public Mj_Vertex val() {
		return win;
	}
	
	/**
	 * ���������� � ���������� �������� � ������.
	 * 
	 * @return ��������� ������� ������
	 */
	
	public Mj_Vertex next() {
		win = (Mj_Vertex) win.next;
		
		return win;
	}
	
	/**
	 * ���������� � ����������� �������� � ������.
	 * 
	 * @return ���������� ������� ������
	 */
	
	public Mj_Vertex prev() {
		win = (Mj_Vertex) win.prev;
		
		return win;
	}
	
	/**
	 * ���������� � ������� �������� � ������. �� ����������
	 * ������� ��������, �c�� ������ ������.
	 * 
	 * @return ������ ������� ������
	 */
	
	public Mj_Vertex first() {
		win = (Mj_Vertex) header.next;
		
		return win;
	}
	
	/**
	 * ���������� � ���������� �������� � ������. �� ����������
	 * ������� ��������, �c�� ������ ������.
	 * 
	 * @return ��������� ������� ������
	 */
	
	public Mj_Vertex last() {
		win = (Mj_Vertex) header.prev;
		
		return win;
	}
	
	/**
	 * ���������� �������� ����� ������.
	 * 
	 * @return �������� ����� ������
	 */
	public int length() {
		return length;
	}
	
	/**
	 * ���������� TRUE, ���� ������� ������� �������� ������ � ������.
	 * 
	 * @return TRUE, ���� ������� ������� �������� ������ � ������, ����� - FALSE.
	 */
	
	public boolean isFirst() {
		return ((win == header.next) && (length > 0));
	}
	
	/**
	 * ���������� TRUE, ���� ������� ������� �������� ��������� � ������.
	 * 
	 * @return TRUE, ���� ������� ������� �������� ��������� � ������, ����� - FALSE.
	 */
	
	public boolean isLast() {
		return ((win == header.prev) && (length > 0));
	}
	
	/**
	 * ���������� TRUE, ���� ������� ������� �������� �������� � ������.
	 * 
	 * @return TRUE, ���� ������� ������� �������� �������� � ������, ����� - FALSE.
	 */
	
	public boolean isHead() {
		return (win == header);
	}
	
	/**
	 * ��������� N ��������� ������� � ������.
	 * 
	 * @param a ������ ���������
	 * @param n ���-�� ��������� �������, ����������� � ������
	 * @return ��������� �� ������
	 */
	
	public Mj_ListVertex arrayToList(Mj_Vertex[] a, int n) {
		Mj_ListVertex s = new Mj_ListVertex();
		
		for(int i = 0; i < n; i++)
			s.append(a[i]);
		
		return s;
	}
	
	public Mj_Vertex leastItem(Mj_ListVertex s, CompareFuncImpl<Mj_Vertex> cfi) {
		if(s.length == 0)
			return null;
		
		Mj_Vertex v = s.first();
		
		for(s.next(); !s.isHead(); s.next()) {
			if(cfi.cmp(s.val(), v) < 0)
				v = s.val();
		}
		
		return v;
	}
}

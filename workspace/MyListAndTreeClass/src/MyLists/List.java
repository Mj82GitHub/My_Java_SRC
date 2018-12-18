package MyLists;
/*
 * Copyright (c) 09.2017
 */

/**
 * ����� ����������� ������ - ������������ ����� ��������� ����� ���������.
 * 
 * @author Mikhail Kushnerov (mj82)
 *
 * @param <T> �������� ���� ������
 */

public class List<T> {

	private ListNode<T> header; // �������� ���� ������
	private ListNode<T> win; // ������� ���� ������
	private int length = 0; // ������ ������
	
	public List() {		
		header = new ListNode<T>(null);
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
		
		header.delete_ListNode();
	}
	
	/**
	 * ������� ����� ������� ����� ��������.
	 * 
	 * @param val ����� �������
	 * @return ��������� �� ����� �������
	 */
	
	public T insert(T val) {
		win.insert(new ListNode<T>(val));
		++length;
		
		return val;
	}
	
	/**
	 * ��������� ����� ������� � ����� ������.
	 * 
	 * @param val ����� �������
	 * @return ��������� �� ����� �������
	 */
	
	public T append(T val) {
		header.prev().insert(new ListNode<T>(val));
		++length;
		
		return val;
	}
	
	/**
	 * ��������� ����� ������ � ����� �������� ������. ������ �������
	 * ������ l ���������� ����� ���������� �������� �������� ������.
	 * 
	 * @param l ����� ������
	 * @return ��������� �� ������� ������
	 */
	
	public List<T> append(List<T> l) {
		ListNode<T> a = (ListNode<T>) header.prev;
		a.splice(l.header);
		
		length += l.length;
		
		l.header.n_remove();
		l.length = 0;
		l.win = header;
		
		return this;
	}
	
	/**
	 * ��������� ����� ������� � ������ ������.
	 * 
	 * @param val ����� �������
	 * @return ��������� �� ����� �������
	 */
	
	public T prepend(T val) {
		header.insert(new ListNode<T>(val));
		++length;
		
		return val;
	}
	
	/**
	 * ������� ������� ������� � ������ ���������� ������� �������.
	 * 
	 * @return ��������� �� ������ ��� ��������� �������
	 */
	
	public T remove() {
		if(win == header)
			return null;
		
		T val = win.val;
		
		win = (ListNode<T>) win.prev();
		((ListNode<T>) win.next).n_remove().delete_Node();
		
		--length;
		
		return val;
	}
	
	/**
	 * ������ �������� �������� �� �������� ����������. ���� ������� 
	 * ������� �������� ��������, �� ������� �������� �� ����������.
	 * 
	 * @param val �������
	 */
	
	public void val(T v) {
		if(win != header)
			win.val = v;
	}
	
	/**
	 * ���������� ������� �������, �� ���� ������� ��������� �������� ��������,
	 * �� ���������� null.
	 * 
	 * @return ������� ������� ��� null
	 */
	
	public T val() {
		return win.val;
	}
	
	/**
	 * ���������� � ���������� �������� � ������.
	 * 
	 * @return ��������� ������� ������
	 */
	
	public T next() {
		win = (ListNode<T>) win.next;
		
		return win.val;
	}
	
	/**
	 * ���������� � ����������� �������� � ������.
	 * 
	 * @return ���������� ������� ������
	 */
	
	public T prev() {
		win = (ListNode<T>) win.prev;
		
		return win.val;
	}
	
	/**
	 * ���������� � ������� �������� � ������. �� ����������
	 * ������� ��������, �c�� ������ ������.
	 * 
	 * @return ������ ������� ������
	 */
	
	public T first() {
		win = (ListNode<T>) header.next;
		
		return win.val;
	}
	
	/**
	 * ���������� � ���������� �������� � ������. �� ����������
	 * ������� ��������, �c�� ������ ������.
	 * 
	 * @return ��������� ������� ������
	 */
	
	public T last() {
		win = (ListNode<T>) header.prev;
		
		return win.val;
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
	
	public List<T> arrayToList(T[] a, int n) {
		List<T> s = new List<T>();
		
		for(int i = 0; i < n; i++)
			s.append(a[i]);
		
		return s;
	}
/*	
	public T leastItem(List<T> s, CompareFuncImpl<T> cfi) {
		if(s.length == 0)
			return null;
		
		T v = s.first();
		
		for(s.next(); !s.isHead(); s.next()) {
			if(cfi.cmp(s.val(), v) < 0)
				v = s.val();
		}
		
		return v;
	}
*/
}

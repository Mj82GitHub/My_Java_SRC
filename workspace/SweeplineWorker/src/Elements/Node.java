package Elements;
/*
 * Copyright (c) 09.2017
 */



public class Node {
	
	protected Node next; // ����� � ������������ ����
	protected Node prev; // ����� � ��������������� ����

	public Node() {
		next = this;
		prev = this;
	}
	
	/**
	 * �������� ���� ����� �� ����� �������� ����.
	 * 
	 * @param b ����������� ����
	 * @return ����������� ����
	 */
	
	public Node insert(Node b) {
		Node c = next;
		
		b.next = c;
		b.prev = this;
		
		next = b;
		c.prev = b;
		
		return b;
	}
	
	/**
	 * ������� ������� ���� �� ������� ��������� ������.
	 * 
	 * @return ��������� ����
	 */
	
	public Node n_remove() {
		prev.next = next;
		next.prev = prev;
		
		next = prev = this;
		
		return this;
	}
	
	/**
	 * ������������ ��� ������������� � �������� ���� ��������� ���������� ����. 
	 * 
	 * @param b �������������� � �������� ����
	 */
	
	public void splice(Node b) {
		Node a = this;
		Node an = a.next;
		Node bn = b.next;
		
		a.next = bn;
		b.next = an;
		an.prev = b;
		bn.prev = a;
	}
	
	/**
	 * ���������� ���������� ����.
	 * 
	 * @return ���������� ����
	 */
	
	public Node prev() {
		return prev;
	}
	
	/**
	 * ���������� ����������� ����.
	 * 
	 * @return ����������� ����
	 */
	
	public Node next() {
		return next;
	}
	
	/**
	 * ���������� �������� ������ �� ������.
	 */
	
	public void delete_Node() {
		try {
			this.finalize(); // ����������� ������� �������
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

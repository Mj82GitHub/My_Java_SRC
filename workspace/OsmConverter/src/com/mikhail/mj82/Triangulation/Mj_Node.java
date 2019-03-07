package com.mikhail.mj82.Triangulation;
/*
 * Copyright (c) 09.2017
 */

public class Mj_Node {
	
	protected Mj_Node next; // ����� � ������������ ����
	protected Mj_Node prev; // ����� � ��������������� ����

	public Mj_Node() {
		next = this;
		prev = this;
	}
	
	/**
	 * �������� ���� ����� �� ����� �������� ����.
	 * 
	 * @param b ����������� ����
	 * @return ����������� ����
	 */
	
	public Mj_Node insert(Mj_Node b) {
		Mj_Node c = next;
		
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
	
	public Mj_Node n_remove() {
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
	
	public void splice(Mj_Node b) {
		Mj_Node a = this;
		Mj_Node an = a.next;
		Mj_Node bn = b.next;
		
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
	
	public Mj_Node prev() {
		return prev;
	}
	
	/**
	 * ���������� ����������� ����.
	 * 
	 * @return ����������� ����
	 */
	
	public Mj_Node next() {
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

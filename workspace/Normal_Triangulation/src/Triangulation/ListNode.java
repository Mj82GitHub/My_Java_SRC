/*
 * Copyright (c) 09.2017
 */

package Triangulation;

/**
 * ����� ����������� ���� ��������� ������.
 * 
 * @author Mikhail Kushnerov (mj82)
 *
 * @param <T> �������� ���� ������
 */

public class ListNode<T> extends Mj_Node {

	public T val; // ��������� �� ����������� ������� ���� ������
	
	public ListNode() {	
		val = null;
		
		next = this;
		prev = this;
	}
	
	public ListNode(T val) {	
		this.val = val;
		
		next = this;
		prev = this;
	}
}

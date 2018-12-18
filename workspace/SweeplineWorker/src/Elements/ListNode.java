package Elements;
/*
 * Copyright (c) 09.2017
 */



/**
 * ����� ����������� ���� ��������� ������.
 * 
 * @author Mikhail Kushnerov (mj82)
 *
 * @param <T> �������� ���� ������
 */

public class ListNode<T> extends Node {

	public T val; // ��������� �� ����������� ������� ���� ������
	
	public ListNode(T val) {	
		super();
		
		this.val = val;
	}
	
	/**
	 * ���������� �������� ������ �� ������.
	 */
	
	public void delete_ListNode() {
		try {
			this.finalize(); // ����������� ������� �������
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
}

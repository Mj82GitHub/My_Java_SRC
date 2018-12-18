package mj82.Triangulation;
/*
 * Copyright (c) 10.2017
 */

/**
 * ���������� ����� ����� ���������� ������ ������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class BraidedNode<T> extends TreeNode<T> {
	
	public BraidedNode<T> next;
	public BraidedNode<T> prev;

	public BraidedNode(T val) {
		super(val);
		
		next = this;
		prev = this;
	}
	
	public BraidedNode<T> rchild() {
		return (BraidedNode<T>) rchild;
	}
	
	public BraidedNode<T> lchild() {
		return (BraidedNode<T>) lchild;
	}
	
	/**
	 * �������� ���� ����� �� ����� �������� ����.
	 * 
	 * @param b ����������� ����
	 * @return ����������� ����
	 */
	
	public BraidedNode<T> insert(BraidedNode<T> b) {
		BraidedNode<T> c = next;
		
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
	
	public BraidedNode<T> remove() {
		prev.next = next;
		next.prev = prev;
		
		next = prev = this;
		
		return this;
	}
	
	/**
	 * ������������ ��� ������������� � �������� ���� ��������� ���������� ����. 
	 * 
	 * @param node �������������� � �������� ����
	 */
	
	public void splice(BraidedNode<T> b) {
		BraidedNode<T> a = this;
		BraidedNode<T> an = a.next;
		BraidedNode<T> bn = b.next;
		
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
	
	public BraidedNode<T> prev() {
		return prev;
	}
	
	/**
	 * ���������� ����������� ����.
	 * 
	 * @return ����������� ����
	 */
	
	public BraidedNode<T> next() {
		return next;
	}
	
	/**
	 * ���������� �������� ������ �� ������.
	 */
	
	public void delete_BraidedNode() {
		try {
			this.finalize(); // ����������� ������� �������
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

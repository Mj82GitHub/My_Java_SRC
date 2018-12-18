package mj82.Triangulation;
/*
 * Copyright (c) 09.2017
 */

import java.util.Random;

/**
 * ���������� ����� ����� � ������ ���������� ������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class RandomizedNode<T> extends BraidedNode<T> {

	private Random rand;
	
	protected RandomizedNode<T> parent;
	protected double priority;
	
	public RandomizedNode(T v) {
		super(v);
		
		rand = new Random(-1); // ���� ������ -1, �� ������ ��� ������������ ����� ���� �����, ����� ��� ������ ������� ��������� ������������ ���������� �����
		
		priority = rand.nextDouble();
		parent = null;
	}
	
	public RandomizedNode(T v, int seed) {
		super(v);
		
		if(seed != -1) {
			rand = new Random(seed);
		} else
			rand = new Random(-1);
		
		priority = rand.nextDouble();
		parent = null;
	}
	
	/**
	 * ���������� ����� �������� ���� � ��� ����� ��������.
	 * 
	 * @Return ���������� ������ �������
	 */
	
	public RandomizedNode<T> lchild() {
		return (RandomizedNode<T>) lchild;
	}
	
	/**
	 * ���������� ����� �������� ���� � ��� ������ ��������.
	 * 
	 * @Return ���������� ������� �������
	 */
	
	public RandomizedNode<T> rchild() {
		return (RandomizedNode<T>) rchild;
	}
	
	/**
	 * ���������� ����� �������� ���� � ��� ����������� �����.
	 * 
	 * @Return ���������� ����������� ����
	 */
	
	public RandomizedNode<T> next() {
		return (RandomizedNode<T>) next;
	}
	
	/**
	 * ���������� ����� �������� ���� � ��� ���������� �����.
	 * 
	 * @Return ���������� ���������� ����
	 */
	
	public RandomizedNode<T> prev() {
		return (RandomizedNode<T>) prev;
	}
	
	/**
	 * ���������� ����� �������� ���� � ��� ��������.
	 * 
	 * @Return ���������� �������
	 */
	
	public RandomizedNode<T> parent() {
		return (RandomizedNode<T>) parent;
	}
	
	/**
	 * ���������� ��������� �������� ����.
	 * 
	 * @return ��������� �������� ����
	 */
	
	public double priority() {
		return priority;
	}
	
	/**
	 * ��������� ������ ������� ��� �������� ����.
	 */
	
	protected void rotateRight() {
		RandomizedNode<T> y = this;
		RandomizedNode<T> x = y.lchild();
		RandomizedNode<T> p = y.parent();
		
		y.lchild = x.rchild();
		
		if(y.lchild() != null)
			y.lchild().parent = y;
		
		if(p.rchild() == y)
			p.rchild = x;
		else
			p.lchild = x;
		
		x.parent = p;
		x.rchild = y;
		y.parent = x;
	}
	
	/**
	 * ��������� ����� ������� ��� �������� ����.
	 */
	
	protected void rotateLeft() {
		RandomizedNode<T> x = this;
		RandomizedNode<T> y = x.rchild();
		RandomizedNode<T> p = x.parent();
		
		x.rchild = y.lchild();
		
		if(x.rchild() != null)
			x.rchild().parent = x;
		
		if(p.lchild() == x)
			p.lchild = y;
		else
			p.rchild = y;
		
		y.parent = p;
		y.lchild = x;
		x.parent = y;
	}
	
	/**
	 * ��������� ������� ���� ����� �� ����������� � ����� ����� ������������ 
	 * ������� �� ��� ���, ���� ��������� �������� ���� �� ������ ������ ��� ������,
	 * ��� � ��� ������.
	 */
	
	protected RandomizedNode<T> bubbleUp() {
		RandomizedNode<T> p = parent();
		
		if(priority() < p.priority()) {
			if(p.lchild() == this)
				p.rotateRight();
			else
				p.rotateLeft();
			
			p = p.bubbleUp();
		}
		
		return p;
	}
	
	/**
	 * ��������� ������� ���� ���� �� ����������� � ������� ����� ������ ����� ������������ 
	 * ������� �� ��� ���, ���� ��������� �������� ���� �� ������ ������� ��� ������
	 * ����������� ����� ��� ��������.
	 */
	
	protected RandomizedNode<T> bubbleDown() {		
		float lcPriority = (lchild() != null) ? (float) lchild().priority() : 2.0f;
		float rcPriority = (rchild() != null) ? (float) rchild().priority() : 2.0f;
		float minPriority = (lcPriority < rcPriority) ? lcPriority : rcPriority;
		
		if(priority() <= minPriority)
			return this; // �������
		
		if(lcPriority < rcPriority)
			rotateRight();
		else
			rotateLeft();
		
		bubbleDown(); // �������
		
		return this; // �������
	}	
	
	/**
	 * ���������� �������� ������ �� ������.
	 */
	
	public void delete_RandomizedNode() {
		try {
			this.delete_BraidedNode();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

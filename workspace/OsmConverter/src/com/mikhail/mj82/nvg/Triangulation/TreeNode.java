package com.mikhail.mj82.nvg.Triangulation;
/*
 * Copyright (c) 09.2017
 */

/**
 * ���������� ����� ����� ��������� ������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class TreeNode<T> {

	protected TreeNode<T> lchild; // ����� ������� ����
	protected TreeNode<T> rchild; // ������ ������� ����
	
	protected T val; // ������� ������ ����
	
	public TreeNode(T v) {		
		val = v;
		lchild = null;
		rchild = null;
	}
	
	/**
	 * ������� ��� ������� �������� ����, ���� ��� ����������.
	 */
	
	public void delete_TreeNode() {
		try {
			if(lchild != null)
				lchild.finalize();
			
			if(rchild != null)
				rchild.finalize();
			
			this.finalize();
		
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

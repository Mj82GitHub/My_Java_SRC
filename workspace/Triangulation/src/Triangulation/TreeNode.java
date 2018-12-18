/*
 * Copyright (c) 09.2017
 */

package Triangulation;

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

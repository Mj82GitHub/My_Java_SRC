package MyLists;
/*
 * Copyright (c) 09.2017
 */

/**
 * ���������� ����� ������ ���������� ������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class RandomizedSearchTree<T> {

	private RandomizedNode<T> root; // �������� ����
	private RandomizedNode<T> win; // ���� (������� ����)
	private CompareFuncImpl<T> cfi; // �-��� ���������
	
	public RandomizedSearchTree(CompareFuncImpl<T> cfi) {
		this.cfi = cfi;

		win = root = new RandomizedNode<T>(null);
		
		root.priority = -1.0;
	}
	
	public RandomizedSearchTree(CompareFuncImpl<T> cfi, int seed) {
		this.cfi = cfi;

		win = root = new RandomizedNode<T>(null, seed);
		
		root.priority = -1.0;
	}
	
	/**
	 * ���������� �������� ������ �� ������.
	 */
	
	public void delete_RandomizedSearchTree() {
		root.delete_RandomizedNode();
	}
	
	/**
	 * ������ ��������� ������� ������ �������.
	 * 
	 * @return ������ �� ����� ������� �������.
	 */
	
	public T next() {
		win = win.next();
		
		return win.val;
	}
	
	/**
	 * ������ ���������� ������� ������ �������.
	 * 
	 * @return ������ �� ����� ������� �������.
	 */
	
	public T prev() {
		win = win.prev();
		
		return win.val;
	}
	
	/**
	 * ��������� ������������ ����� ������ ������.
	 * 
	 * @param vfi �-���������� ��������� ��� �-��� ������ � ����� ������ ������
	 */
	
	public void inorder(VisitFuncImpl<T> vfi) {
		RandomizedNode<T> n = root.next();
		
		while(n != root) {
			vfi.visit(n.val);
			n = n.next();
		}
	}
	
	/**
	 * ���������� ������� �������.
	 * 
	 * @return ������� �������
	 */
	
	public T val() {
		return win.val;
	}
	
	public boolean isFirst() {
		return (win == root.next()) && (root != root.next());
	}
	
	public boolean isLast() {
		return (win == root.prev()) && (root != root.next());
	}
	
	public boolean isHead() {
		return (win == root);
	}
	
	public boolean isEmpty() {
		return (root == root.next());
	}
	
	public T find(T val) {
		RandomizedNode<T> n = root.rchild();
		
		while(n != null) {
			int result = cfi.cmp(val, n.val);
			
			if(result < 0)
				n = n.lchild();
			else if(result > 0)
				n = n.rchild();
			else {
				win = n;
				
				return n.val;
			}
		}
		
		return null;
	}
	
	public T findMin() {
		win = root.next();
		
		return win.val;
	}
	
	/**
	 * ��������� �������� ������ ����������� �������� � ������.
	 * 
	 * @param val �������� �-���
	 * @return ���������� ������� � ������, ������� �� ������, ��� �������� val.
	 * ���� � ������ ������������ ������� �� ��������� val, �� ���������� �������� val.
	 * ���� ������ �������� ���, �� ������������ �������� ������, ��� val, ����
	 * �� � ������ � ������ �������� ���, �� ������������ null.
	 */
	
	public T locate(T val) {
		RandomizedNode<T> b = root;
		RandomizedNode<T> n = root.rchild();
		
		while(n != null) {
			int result = cfi.cmp(val, n.val);
			
			if(result < 0)
				n = n.lchild();
			else if(result > 0) {
				b = n;
				n = n.rchild();
			} else {
				win = n;
				
				return win.val;
			}
		}
		
		win = b;
		
		return win.val;
	}
	
	/**
	 * ������� ������� � ������� ������ ���������� ������.
	 * 
	 * @param val �������, ������� ������� � ������ ������
	 * @return ��������� �� �������� �������
	 */
	
	public T insert(T val) {
		int result = 1; 
		RandomizedNode<T> p = root;
		RandomizedNode<T> n = root.rchild();
		
		while(n != null) {
			p = n;
			result = cfi.cmp(val, p.val);
			
			if(result < 0)
				n = p.lchild();
			else if(result > 0)
				n = p.rchild();
			else
				return null;
		}
		
		win = new RandomizedNode<T>(val);
		win.parent = p;
		
		if(result < 0) {
			p.lchild = win;
			p.prev().insert(win);
		} else {
			p.rchild = win;
			p.insert(win);
		}
		
		T tmp_val = win.val; // ���������� ������� �������� �� ������ ������� � ������
		
		win = win.bubbleUp();
		
		find(tmp_val); // ���������� ���� �� ������� �������
		
		return val;
	}
	
	/**
	 * ������� ������� ������� � ������ � ������ ������� ���������� �������.
	 */
	
	public void remove() {
		if(win != root)
			remove(win);
	}
	
	/**
	 * ������� �������� ���������� ������� � ������. ���� �������� ������� ��������� � 
	 * �������, �� ������� ���������� ���������� ������� ������, �����, ������� �������
	 * �� ����������.
	 * 
	 * @param val ��������� �������
	 * @return ������ �� ��������� �� ������ �������
	 */
	
	public T remove(T val) {
		T v = find(val);
		
		if(v != null) {
			remove();
			return v;
		}
		
		return null;
	}
	
	/**
	 * ������� ���������� ������� � ������.
	 * 
	 * @return ���������� ������� � ������
	 */
	
	public T removeMin() {
		T val = root.next().val;
		
		if(root != root.next())
			remove(root.next());
		
		return val;
	}
	
	/**
	 * ������� ���� �� ���������� ������.
	 * 
	 * @param n ��������� ����
	 */
	
	private void remove(RandomizedNode<T> n) {
		n.priority = 1.5;
		n = n.bubbleDown();
		
		RandomizedNode<T> p = n.parent();
		
		if(p.lchild() == n)
			p.lchild = null;
		else
			p.rchild = null;
		
		if(win == n)
			win = n.prev();
		
		n.remove();
	}
}

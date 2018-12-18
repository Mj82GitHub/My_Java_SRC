package MyLists;
/*
 * Copyright (c) 10.2017
 */

/**
 * Обобщенный класс двоичного дерева поиска.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class SearchTree<T> {

	private TreeNode<T> root;
	private CompareFuncImpl<T> cfi; // Ф-ция сравнения
	
	public SearchTree(CompareFuncImpl<T> cfi) {
		this.cfi = cfi;
		root = null;
	}
	
	public boolean isEmpty() {
		return (root == null);
	}
	
	public T find(T val) {
		TreeNode<T> n = root;
		
		while(n != null) {
			int result = cfi.cmp(val, n.val);
			
			if(result < 0)
				n = n.lchild;
			else if(result > 0)
				n = n.rchild;
			else
				return n.val;
		}
		
		return null;
	}
	
	public T findMin() {
		TreeNode<T> n = findMin(root);
		
		return (n != null ? n.val : null);
	}
	
	public void inorder(VisitFuncImpl<T> vfi) {
		inorder(root, vfi);
	}
	
	public void insert(T val) {
		if(root == null) {
			root = new TreeNode<T>(val);
			
			return;
		} else {
			int result = 0;
			
			TreeNode<T> p;
			TreeNode<T> n;
			
			p = n = root;
			
			while(n != null) {
				p = n;
				
				result = cfi.cmp(val, p.val);
				
				if(result < 0)
					n = p.lchild;
				else if(result > 0)
					n = p.rchild;
				else
					return;
			}
			
			if(result < 0)
				p.lchild = new TreeNode<T>(val);
			else
				p.rchild = new TreeNode<T>(val);
		}
	}
	
	public void remove(T val) {
		root = remove(val, root);
	}
	
	public T removeMin() {
		T v = findMin();
		remove(v);
		
		return v;
	}
	
	public T[] heapSort(T[] s, int n, CompareFuncImpl<T> cfi) {
		SearchTree<T> t = new SearchTree<T>(cfi);
		
		for(int i = 0; i < n; i++)
			t.insert(s[i]);
		
		for(int i = 0; i < n; i++)
			s[i] = t.removeMin();
		
		return s;
	}
	
	private TreeNode<T> findMin(TreeNode<T> n) {
		if(n == null)
			return null;
		
		while(n.lchild != null) 
			n = n.lchild;
		
		return n;
	}
	
	private TreeNode<T> remove(T val, TreeNode<T> n) {
		if(n == null)
			return n;
		
		int result = cfi.cmp(val, n.val);
		
		if(result < 0)
			n.lchild = remove(val, n.lchild);
		else if(result > 0)
			n.rchild = remove(val, n.rchild);
		else {
			if(n.lchild == null) {
				TreeNode<T> old = n;
				n = old.rchild;
				old.delete_TreeNode();
				
				return n;
			} else if(n.rchild == null) {
				TreeNode<T> old = n;
				n = old.lchild;
				old.delete_TreeNode();
				
				return n;
			} else {
				TreeNode<T> m = findMin(n.rchild);
				n.val = m.val;
				n.rchild = remove(m.val, n.rchild);
				
				return n;
			}
		}
		
		return n;
	}
	
	private void inorder(TreeNode<T> n, VisitFuncImpl<T> vfi) {
		if(n != null) {
			inorder(n.lchild, vfi);
			vfi.visit(n.val);
			inorder(n.rchild, vfi);
		}
	}
}

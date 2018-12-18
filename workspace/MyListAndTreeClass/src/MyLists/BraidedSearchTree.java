package MyLists;
/*
 * Copyright (c) 10.2017
 */

/**
 * Обобщенный класс связанного дерева поиска.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class BraidedSearchTree<T> {

	private BraidedNode<T> root;
	private BraidedNode<T> win;
	private CompareFuncImpl<T> cfi; // Ф-ция сравнения
	
	public BraidedSearchTree(CompareFuncImpl<T> cfi) {
		this.cfi = cfi;
		
		win = root = new BraidedNode<T>(null);
	}
	
	public void delete_BraidedSearchTree() {
		root.delete_BraidedNode();
	}
	
	public T next() {
		win = win.next();
		
		return win.val;
	}
	
	public T prev() {
		win = win.prev();
		
		return win.val;
	}
	
	public void inorder(VisitFuncImpl<T> vfi) {
		BraidedNode<T> n = root.next();
		
		while(n != root) {
			vfi.visit(n.val);
			n = n.next();
		}
	}
	
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
		BraidedNode<T> n = root.rchild();
		
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
	
	public T insert(T val) {
		int result = 1;
		BraidedNode<T> p = root;
		BraidedNode<T> n = root.rchild();
		
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
		
		win = new BraidedNode<T>(val);
		
		if(result < 0) {
			p.lchild = win;
			p.prev().insert(win);
		} else {
			p.rchild = win;
			p.insert(win);
		}
		
		return val;
	}
	
	public void remove() {
		if(win != root)
			root.rchild = remove(win.val, root.rchild);
	}
	
	public T removeMin() {
		T val = root.next().val;
		
		if(root != root.next())
			remove(val, root.rchild);
		
		return val;
	}
	
	private TreeNode<T> remove(T val, TreeNode<T> n) {
		int result = cfi.cmp(val, n.val);
		
		if(result < 0)
			n.lchild = remove(val, n.lchild);
		else if(result > 0)
			n.rchild = remove(val, n.rchild);
		else {
			if(n.lchild == null) {
				BraidedNode<T> old = (BraidedNode<T>) n;
				
				if(win == old)
					win = old.prev();
				
				n = old.rchild();
				old.remove();
				old.delete_BraidedNode();
			} else if(n.rchild == null) {
				BraidedNode<T> old = (BraidedNode<T>) n;
				
				if(win == old)
					win = old.prev();
				
				n = old.lchild();
				old.remove();
				old.delete_BraidedNode();
			} else {
				BraidedNode<T> m = ((BraidedNode<T>) n).next();
				n.val = m.val;
				n.rchild = remove(m.val, n.rchild);
			}
		}
		
		return n;
	}
}

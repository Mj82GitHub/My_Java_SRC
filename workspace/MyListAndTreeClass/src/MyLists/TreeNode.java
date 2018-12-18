package MyLists;
/*
 * Copyright (c) 09.2017
 */

/**
 * Обобщенный класс узлов двоичного дерева.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class TreeNode<T> {

	protected TreeNode<T> lchild; // Левый потомок узла
	protected TreeNode<T> rchild; // Правый потомок узла
	
	protected T val; // Элемент данных узла
	
	public TreeNode(T v) {		
		val = v;
		lchild = null;
		rchild = null;
	}
	
	/**
	 * Удаляет оба потомка текущего узла, если они существуют.
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

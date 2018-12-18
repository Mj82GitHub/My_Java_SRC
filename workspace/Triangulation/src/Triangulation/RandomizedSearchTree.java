/*
 * Copyright (c) 09.2017
 */

package Triangulation;

/**
 * Обобщенный класс дерева случайного поиска.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class RandomizedSearchTree<T> {

	private RandomizedNode<T> root; // Головной узел
	private RandomizedNode<T> win; // Окно (текущий узел)
	private CompareFuncImpl<T> cfi; // Ф-ция сравнения
	
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
	 * Уничтожает системой ссылку на объект.
	 */
	
	public void delete_RandomizedSearchTree() {
		root.delete_RandomizedNode();
	}
	
	/**
	 * Делает следующий элемент дерева текущим.
	 * 
	 * @return ссылка на новый текущий элемент.
	 */
	
	public T next() {
		win = win.next();
		
		return win.val;
	}
	
	/**
	 * Делает предыдущий элемент дерева текущим.
	 * 
	 * @return ссылка на новый текущий элемент.
	 */
	
	public T prev() {
		win = win.prev();
		
		return win.val;
	}
	
	/**
	 * Выполняет симметричный обход дерева поиска.
	 * 
	 * @param vfi ф-циональный интерфейс для ф-ции работы с узлом дерева поиска
	 */
	
	public void inorder(VisitFuncImpl<T> vfi) {
		RandomizedNode<T> n = root.next();
		
		while(n != root) {
			vfi.visit(n.val);
			n = n.next();
		}
	}
	
	/**
	 * Возвращает текущий элемент.
	 * 
	 * @return текущий элемент
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
	 * Выполняет операцию поиска наибольшего элемента в дереве.
	 * 
	 * @param val аргумент ф-ции
	 * @return наибольший элемент в дереве, который не больше, чем значение val.
	 * Если в дереве присутствует элемент со значением val, то возвращается значение val. 
	 * Если такого значения нет, то возвращается значение меньше, чем val, если
	 * же в дереве и такого значения нет, то возвращается null.
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
	 * Заносит элемент в текущее дерево случайного поиска.
	 * 
	 * @param val элемент, который заносит в дерево поиска
	 * @return указатель на вносимый элемент
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
		
		T tmp_val = win.val; // Запоминаем текущее значение на случай ротаций в дереве
		
		win = win.bubbleUp();
		
		find(tmp_val); // Возвращаем окно на текущую позицию
		
		return val;
	}
	
	/**
	 * Удаляет текущий элемент в дереве и делает текущим предыдущий элемент.
	 */
	
	public void remove() {
		if(win != root)
			remove(win);
	}
	
	/**
	 * Удаляет заданный аргументом элемент в дереве. Если заданный элемент совпадает с 
	 * текущим, то текущим становится предыдущий элемент дерева, иначе, текущий элемент
	 * не изменяется.
	 * 
	 * @param val удаляемый элемент
	 * @return ссылка на удаляемый из дерева элемент
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
	 * Удаляет наименьший элемент в дереве.
	 * 
	 * @return наименьший элемент в дереве
	 */
	
	public T removeMin() {
		T val = root.next().val;
		
		if(root != root.next())
			remove(root.next());
		
		return val;
	}
	
	/**
	 * Удаляет узел из поискового дерева.
	 * 
	 * @param n удаляемый узел
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
		n.delete_RandomizedNode();
	}
}

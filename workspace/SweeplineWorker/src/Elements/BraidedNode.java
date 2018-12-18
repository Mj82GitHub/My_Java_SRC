package Elements;
/*
 * Copyright (c) 10.2017
 */

/**
 * Обобщенный класс узлов связанного дерева поиска.
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
	 * Включает узел сразу же после текущего узла.
	 * 
	 * @param b вставляемый узел
	 * @return вставляемый узел
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
	 * Удаляет текущий узел из данного связаного списка.
	 * 
	 * @return удаленный узел
	 */
	
	public BraidedNode<T> remove() {
		prev.next = next;
		next.prev = prev;
		
		next = prev = this;
		
		return this;
	}
	
	/**
	 * Используется для присоединения к текущему узлу заданного аргументом узла. 
	 * 
	 * @param node присоединяемый к текущему узел
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
	 * Возвращает предыдущий узел.
	 * 
	 * @return предыдущий узел
	 */
	
	public BraidedNode<T> prev() {
		return prev;
	}
	
	/**
	 * Возвращает последующий узел.
	 * 
	 * @return последующий узел
	 */
	
	public BraidedNode<T> next() {
		return next;
	}
	
	/**
	 * Уничтожает системой ссылку на объект.
	 */
	
	public void delete_BraidedNode() {
		try {
			this.finalize(); // Освобождаем ресурсы системы
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

/*
 * Copyright (c) 09.2017
 */

package Insertion;

public class Node {
	
	protected Node next; // Связь к последующему узлу
	protected Node prev; // Связь к предшествующему узлу

	public Node() {
		next = this;
		prev = this;
	}
	/**
	 * Включает узел сразу же после текущего узла.
	 * 
	 * @param node вставляемый узел
	 * @return вставляемый узел
	 */
	
	public Node insert(Node b) {
		Node c = next;
		
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
	
	public Node n_remove() {
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
	
	public void splice(Node b) {
		Node a = this;
		Node an = a.next;
		Node bn = b.next;
		
		a.next = bn;
		b.next = an;
		an.prev = b;
		bn.prev = a;
	}
	
	/**
	 * Уничтожает системой ссылку на объект.
	 */
	
	public void delete_Node() {
		try {
			this.finalize(); // Освобождаем ресурсы системы
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Возвращает предыдущий узел.
	 * 
	 * @return предыдущий узел
	 */
	
	public Node prev() {
		return prev;
	}
	
	/**
	 * Возвращает последующий узел.
	 * 
	 * @return последующий узел
	 */
	
	public Node next() {
		return next;
	}
}

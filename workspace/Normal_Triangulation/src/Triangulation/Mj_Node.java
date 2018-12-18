package Triangulation;
/*
 * Copyright (c) 09.2017
 */

public class Mj_Node {
	
	protected Mj_Node next; // Связь к последующему узлу
	protected Mj_Node prev; // Связь к предшествующему узлу

	public Mj_Node() {
		next = this;
		prev = this;
	}
	
	/**
	 * Включает узел сразу же после текущего узла.
	 * 
	 * @param b вставляемый узел
	 * @return вставляемый узел
	 */
	
	public Mj_Node insert(Mj_Node b) {
		Mj_Node c = next;
		
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
	
	public Mj_Node n_remove() {
		prev.next = next;
		next.prev = prev;
		
		next = prev = this;
		
		return this;
	}
	
	/**
	 * Используется для присоединения к текущему узлу заданного аргументом узла. 
	 * 
	 * @param b присоединяемый к текущему узел
	 */
	
	public void splice(Mj_Node b) {
		Mj_Node a = this;
		Mj_Node an = a.next;
		Mj_Node bn = b.next;
		
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
	
	public Mj_Node prev() {
		return prev;
	}
	
	/**
	 * Возвращает последующий узел.
	 * 
	 * @return последующий узел
	 */
	
	public Mj_Node next() {
		return next;
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
}

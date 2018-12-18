package MyLists;
/*
 * Copyright (c) 09.2017
 */



/**
 *  ласс реализующий узел св€заного списка.
 * 
 * @author Mikhail Kushnerov (mj82)
 *
 * @param <T> параметр типа класса
 */

public class ListNode<T> extends Node {

	public T val; // ”казывает на фактический элемент узла списка
	
	public ListNode(T val) {	
		super();
		
		this.val = val;
	}
	
	/**
	 * ”ничтожает системой ссылку на объект.
	 */
	
	public void delete_ListNode() {
		try {
			this.finalize(); // ќсвобождаем ресурсы системы
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
}

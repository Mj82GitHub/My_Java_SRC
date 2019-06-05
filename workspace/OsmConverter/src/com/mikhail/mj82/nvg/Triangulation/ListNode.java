/*
 * Copyright (c) 09.2017
 */

package com.mikhail.mj82.nvg.Triangulation;

/**
 * Класс реализующий узел связаного списка.
 * 
 * @author Mikhail Kushnerov (mj82)
 *
 * @param <T> параметр типа класса
 */

public class ListNode<T> extends Mj_Node {

	public T val; // Указывает на фактический элемент узла списка
	
	public ListNode() {	
		val = null;
		
		next = this;
		prev = this;
	}
	
	public ListNode(T val) {	
		this.val = val;
		
		next = this;
		prev = this;
	}
}

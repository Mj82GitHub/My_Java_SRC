/*
 * Copyright (c) 09.2017
 */

package com.mikhail.mj82.Triangulation;

/**
 * Класс реализующий список вершин полигона - упорядоченый набор конечного числа элементов.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Mj_ListVertex {

	private Mj_Vertex header; // Головной узел списка
	private Mj_Vertex win; // Текущий узел списка
	private int length; // Размер списка
	
	public Mj_ListVertex() {
		length = 0;
		
		header = new Mj_Vertex();
		win = header;
	}
	
	/**
	 * Разрушает узлы связанного списка.
	 */
	
	public void delete_list() {
		while(length() > 0) {
			first();
			remove();
		}
		
		header.delete_Node();
	}
	
	/**
	 * Заносит новый элемент после текущего.
	 * 
	 * @param v новый элемент
	 * @return указатель на новый элемент
	 */
	
	public Mj_Vertex insert(Mj_Vertex v) {
		win.insert(new Mj_Vertex(v));
		++length;
		
		return v;
	}
	
	/**
	 * Вставляет новый элемент в конец списка.
	 * 
	 * @param v новый элемент
	 * @return указатель на новый элемент
	 */
	
	public Mj_Vertex append(Mj_Vertex v) {
		Mj_Node tmp = header.prev.insert(new Mj_Vertex(v));	
		
		header.prev = tmp;
		header.next.prev = header.prev;
		header.prev.next = header.next;		
		
		++length;
		
		return v;
	}
	
	/**
	 * Вставляет новый список в конец текущего списка. Первый элемент
	 * списка l помещается после последнего элемента текущего списка.
	 * 
	 * @param l новый список
	 * @return указатель на текущий список
	 */
	
	public Mj_ListVertex append(Mj_ListVertex l) {
		Mj_Vertex a = (Mj_Vertex) header.prev;
		a.splice(l.header);
		
		length += l.length;
		
		l.header.remove();
		l.length = 0;
		l.win = header;
		
		return this;
	}
	
	/**
	 * Вставляет новый элемент в начало списка.
	 * 
	 * @param v новый элемент
	 * @return указатель на новый элемент
	 */
	
	public Mj_Vertex prepend(Mj_Vertex v) {
		header.insert(new Mj_Vertex(v));
		
		header.next.prev = header.prev;
		header.prev.next = header.next;		
		
		++length;
		
		return v;
	}
	
	/**
	 * Удаляет текущий элемент и делает предыдущий элемент текущим.
	 * 
	 * @return указатель на только что удаленный элемент
	 */
	
	public Mj_Vertex remove() {
		if(win.equalsVertex(header))
			return null;
		
		Mj_Vertex v = win;
		Mj_Vertex tmp = (Mj_Vertex) win.next;
		
		if(win.equalsVertex((Mj_Vertex) header.prev)) 
			header.prev = win.prev;
		
		if(win.equalsVertex((Mj_Vertex) header.next))
			header.next = win.next;
		
		win = (Mj_Vertex) win.prev;
		((Mj_Vertex) win.next.n_remove()).delete_Node();
		
		win.next = tmp;
		tmp.prev = win;
		
		--length;
		
		return v;
	}
	
	/**
	 * Замена текущего элемента на заданный аргументом. Если текущий 
	 * элемент является головным, то никаких действий не происходит.
	 * 
	 * @param v элемент
	 */
	
	public void val(Mj_Vertex v) {
		if(win != header)
			win = v;
	}
	
	/**
	 * Возвращает текущий элемент, но если текущим элементом является головной,
	 * то возвращает null.
	 * 
	 * @return текущий элемент или null
	 */
	
	public Mj_Vertex val() {
		return win;
	}
	
	/**
	 * Перемещает к следующему элементу в списке.
	 * 
	 * @return следующий элемент списка
	 */
	
	public Mj_Vertex next() {
		win = (Mj_Vertex) win.next;
		
		return win;
	}
	
	/**
	 * Перемещает к предыдущему элементу в списке.
	 * 
	 * @return предыдущий элемент списка
	 */
	
	public Mj_Vertex prev() {
		win = (Mj_Vertex) win.prev;
		
		return win;
	}
	
	/**
	 * Перемещает к первому элементу в списке. Не производит
	 * никаких действий, еcли список пустой.
	 * 
	 * @return первый элемент списка
	 */
	
	public Mj_Vertex first() {
		win = (Mj_Vertex) header.next;
		
		return win;
	}
	
	/**
	 * Перемещает к последнему элементу в списке. Не производит
	 * никаких действий, еcли список пустой.
	 * 
	 * @return последний элемент списка
	 */
	
	public Mj_Vertex last() {
		win = (Mj_Vertex) header.prev;
		
		return win;
	}
	
	/**
	 * Возвращает значение длины списка.
	 * 
	 * @return значение длины списка
	 */
	public int length() {
		return length;
	}
	
	/**
	 * Возвращает TRUE, если текущий элемент является первым в списке.
	 * 
	 * @return TRUE, если текущий элемент является первым в списке, иначе - FALSE.
	 */
	
	public boolean isFirst() {
		return ((win == header.next) && (length > 0));
	}
	
	/**
	 * Возвращает TRUE, если текущий элемент является последним в списке.
	 * 
	 * @return TRUE, если текущий элемент является последним в списке, иначе - FALSE.
	 */
	
	public boolean isLast() {
		return ((win == header.prev) && (length > 0));
	}
	
	/**
	 * Возвращает TRUE, если текущий элемент является головным в списке.
	 * 
	 * @return TRUE, если текущий элемент является головным в списке, иначе - FALSE.
	 */
	
	public boolean isHead() {
		return (win == header);
	}
	
	/**
	 * Загружает N элементов массива в список.
	 * 
	 * @param a массив элементов
	 * @param n кол-во элементов массива, загружаемых в список
	 * @return указатель на список
	 */
	
	public Mj_ListVertex arrayToList(Mj_Vertex[] a, int n) {
		Mj_ListVertex s = new Mj_ListVertex();
		
		for(int i = 0; i < n; i++)
			s.append(a[i]);
		
		return s;
	}
	
	public Mj_Vertex leastItem(Mj_ListVertex s, CompareFuncImpl<Mj_Vertex> cfi) {
		if(s.length == 0)
			return null;
		
		Mj_Vertex v = s.first();
		
		for(s.next(); !s.isHead(); s.next()) {
			if(cfi.cmp(s.val(), v) < 0)
				v = s.val();
		}
		
		return v;
	}
}

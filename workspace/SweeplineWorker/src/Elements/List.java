package Elements;
/*
 * Copyright (c) 09.2017
 */

/**
 * Класс реализующий список - упорядоченый набор конечного числа элементов.
 * 
 * @author Mikhail Kushnerov (mj82)
 *
 * @param <T> параметр типа класса
 */

public class List<T> {

	private ListNode<T> header; // Головной узел списка
	private ListNode<T> win; // Текущий узел списка
	private int length = 0; // Размер списка
	
	public List() {		
		header = new ListNode<T>(null);
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
		
		header.delete_ListNode();
	}
	
	/**
	 * Заносит новый элемент после текущего.
	 * 
	 * @param val новый элемент
	 * @return указатель на новый элемент
	 */
	
	public T insert(T val) {
		win.insert(new ListNode<T>(val));
		++length;
		
		return val;
	}
	
	/**
	 * Вставляет новый элемент в конец списка.
	 * 
	 * @param val новый элемент
	 * @return указатель на новый элемент
	 */
	
	public T append(T val) {
		header.prev().insert(new ListNode<T>(val));
		++length;
		
		return val;
	}
	
	/**
	 * Вставляет новый список в конец текущего списка. Первый элемент
	 * списка l помещается после последнего элемента текущего списка.
	 * 
	 * @param l новый список
	 * @return указатель на текущий список
	 */
	
	public List<T> append(List<T> l) {
		ListNode<T> a = (ListNode<T>) header.prev;
		a.splice(l.header);
		
		length += l.length;
		
		l.header.n_remove();
		l.length = 0;
		l.win = header;
		
		return this;
	}
	
	/**
	 * Вставляет новый элемент в начало списка.
	 * 
	 * @param val новый элемент
	 * @return указатель на новый элемент
	 */
	
	public T prepend(T val) {
		header.insert(new ListNode<T>(val));
		++length;
		
		return val;
	}
	
	/**
	 * Удаляет текущий элемент и делает предыдущий элемент текущим.
	 * 
	 * @return указатель на только что удаленный элемент
	 */
	
	public T remove() {
		if(win == header)
			return null;
		
		T val = win.val;
		
		win = (ListNode<T>) win.prev();
		((ListNode<T>) win.next).n_remove().delete_Node();
		
		--length;
		
		return val;
	}
	
	/**
	 * Замена текущего элемента на заданный аргументом. Если текущий 
	 * элемент является головным, то никаких действий не происходит.
	 * 
	 * @param val элемент
	 */
	
	public void val(T v) {
		if(win != header)
			win.val = v;
	}
	
	/**
	 * Возвращает текущий элемент, но если текущим элементом является головной,
	 * то возвращает null.
	 * 
	 * @return текущий элемент или null
	 */
	
	public T val() {
		return win.val;
	}
	
	/**
	 * Перемещает к следующему элементу в списке.
	 * 
	 * @return следующий элемент списка
	 */
	
	public T next() {
		win = (ListNode<T>) win.next;
		
		return win.val;
	}
	
	/**
	 * Перемещает к предыдущему элементу в списке.
	 * 
	 * @return предыдущий элемент списка
	 */
	
	public T prev() {
		win = (ListNode<T>) win.prev;
		
		return win.val;
	}
	
	/**
	 * Перемещает к первому элементу в списке. Не производит
	 * никаких действий, еcли список пустой.
	 * 
	 * @return первый элемент списка
	 */
	
	public T first() {
		win = (ListNode<T>) header.next;
		
		return win.val;
	}
	
	/**
	 * Перемещает к последнему элементу в списке. Не производит
	 * никаких действий, еcли список пустой.
	 * 
	 * @return последний элемент списка
	 */
	
	public T last() {
		win = (ListNode<T>) header.prev;
		
		return win.val;
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
	
	public List<T> arrayToList(T[] a, int n) {
		List<T> s = new List<T>();
		
		for(int i = 0; i < n; i++)
			s.append(a[i]);
		
		return s;
	}
/*	
	public T leastItem(List<T> s, CompareFuncImpl<T> cfi) {
		if(s.length == 0)
			return null;
		
		T v = s.first();
		
		for(s.next(); !s.isHead(); s.next()) {
			if(cfi.cmp(s.val(), v) < 0)
				v = s.val();
		}
		
		return v;
	}
*/
}

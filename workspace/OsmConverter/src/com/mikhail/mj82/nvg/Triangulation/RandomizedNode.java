package com.mikhail.mj82.nvg.Triangulation;
/*
 * Copyright (c) 09.2017
 */

import java.util.Random;

/**
 * Обобщенный класс узлов в дереве случайного поиска.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class RandomizedNode<T> extends BraidedNode<T> {

	private Random rand;
	
	protected RandomizedNode<T> parent;
	protected double priority;
	
	public RandomizedNode(T v) {
		super(v);
		
		rand = new Random(-1); // Если убрать -1, то каждый раз генерируется новое случ число, иначе при каждом запуске программы генерируются одинаковые числа
		
		priority = rand.nextDouble();
		parent = null;
	}
	
	public RandomizedNode(T v, int seed) {
		super(v);
		
		if(seed != -1) {
			rand = new Random(seed);
		} else
			rand = new Random(-1);
		
		priority = rand.nextDouble();
		parent = null;
	}
	
	/**
	 * Определяет связь текущего узла с его левым потомком.
	 * 
	 * @Return возвращает левого потомка
	 */
	
	public RandomizedNode<T> lchild() {
		return (RandomizedNode<T>) lchild;
	}
	
	/**
	 * Определяет связь текущего узла с его правым потомком.
	 * 
	 * @Return возвращает правого потомка
	 */
	
	public RandomizedNode<T> rchild() {
		return (RandomizedNode<T>) rchild;
	}
	
	/**
	 * Определяет связь текущего узла с его последующим узлом.
	 * 
	 * @Return возвращает последующий узел
	 */
	
	public RandomizedNode<T> next() {
		return (RandomizedNode<T>) next;
	}
	
	/**
	 * Определяет связь текущего узла с его предыдущим узлом.
	 * 
	 * @Return возвращает предыдущий узел
	 */
	
	public RandomizedNode<T> prev() {
		return (RandomizedNode<T>) prev;
	}
	
	/**
	 * Определяет связь текущего узла с его потомком.
	 * 
	 * @Return возвращает потомка
	 */
	
	public RandomizedNode<T> parent() {
		return (RandomizedNode<T>) parent;
	}
	
	/**
	 * Возвращает приоритет текущего узла.
	 * 
	 * @return приоритет текущего узла
	 */
	
	public double priority() {
		return priority;
	}
	
	/**
	 * Выполняет правую ротацию для текущего узла.
	 */
	
	protected void rotateRight() {
		RandomizedNode<T> y = this;
		RandomizedNode<T> x = y.lchild();
		RandomizedNode<T> p = y.parent();
		
		y.lchild = x.rchild();
		
		if(y.lchild() != null)
			y.lchild().parent = y;
		
		if(p.rchild() == y)
			p.rchild = x;
		else
			p.lchild = x;
		
		x.parent = p;
		x.rchild = y;
		y.parent = x;
	}
	
	/**
	 * Выполняет левую ротацию для текущего узла.
	 */
	
	protected void rotateLeft() {
		RandomizedNode<T> x = this;
		RandomizedNode<T> y = x.rchild();
		RandomizedNode<T> p = x.parent();
		
		x.rchild = y.lchild();
		
		if(x.rchild() != null)
			x.rchild().parent = x;
		
		if(p.lchild() == x)
			p.lchild = y;
		else
			p.rchild = y;
		
		y.parent = p;
		y.lchild = x;
		x.parent = y;
	}
	
	/**
	 * Переносит текущий узел вверх по направлению к корню путем многократных 
	 * ротаций до тех пор, пока приоритет текущего узла не станет больше или равным,
	 * чем у его предка.
	 */
	
	protected RandomizedNode<T> bubbleUp() {
		RandomizedNode<T> p = parent();
		
		if(priority() < p.priority()) {
			if(p.lchild() == this)
				p.rotateRight();
			else
				p.rotateLeft();
			
			p = p.bubbleUp();
		}
		
		return p;
	}
	
	/**
	 * Переносит текущий узел вниз по направлению к внешним узлам дерева путем многократных 
	 * ротаций до тех пор, пока приоритет текущего узла не станет меньшим или равным
	 * приоритетам обеих его потомков.
	 */
	
	protected RandomizedNode<T> bubbleDown() {		
		float lcPriority = (lchild() != null) ? (float) lchild().priority() : 2.0f;
		float rcPriority = (rchild() != null) ? (float) rchild().priority() : 2.0f;
		float minPriority = (lcPriority < rcPriority) ? lcPriority : rcPriority;
		
		if(priority() <= minPriority)
			return this; // Изменил
		
		if(lcPriority < rcPriority)
			rotateRight();
		else
			rotateLeft();
		
		bubbleDown(); // Изменил
		
		return this; // Изменил
	}	
	
	/**
	 * Уничтожает системой ссылку на объект.
	 */
	
	public void delete_RandomizedNode() {
		try {
			this.delete_BraidedNode();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

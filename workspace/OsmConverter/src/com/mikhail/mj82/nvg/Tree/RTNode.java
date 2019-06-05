/*
 * Copyright (c) 08.2016
 */

package com.mikhail.mj82.nvg.Tree;

import java.io.Serializable;

import com.mikhail.mj82.nvg.Geom.JRect;

/**
 * Класс узла дерева.
 *
 * @author Mikhail Kushnerov (mj82)
 */

public class RTNode implements Serializable {
	
	private static final long serialVersionUID = -3465536469559896588L;

	public final int M = 16; // Максимальное кол-во дочерних элементов в узле
	
	private JRect MBR; // Ограничивающий прямоугольник узла
	// Индекс в массиве узлов дерева, указывающий на узел-родитель
	private int parent;
	// Массив индексов дочерних узлов в массиве узлов дерева
	private int[] children;
	// Массив с листами в дереве (массив объектов дерева).
	private GPSObject[] objects; 
	private boolean isLeaf; // Свойство показывающее является ли этот узел конечным (листом)
	private int level; // Уровень узла в дереве (0 = лист).
	
	public RTNode() {
		parent = -10;
		children = new int[0];
		objects = new GPSObject[0];
		MBR = new JRect();
	}
	
	public RTNode(RTNode node) {
		parent = -10;
		copy(node);
	}

	/**
	 * Копирует узел.
	 *
	 * @param node узел, копию которого делаем
     */

	public void copy(RTNode node) {		
		objects = new GPSObject[node.getObjects().length];
		children = new int[node.getChildren().length];
		
		if(objects.length > 0) {
			for(int i = 0; i < node.getObjects().length; i++) {
				
				objects[i] = new GPSObject();
				objects[i].setSeek(node.getObject(i).getSeek());
				objects[i].setMbr(node.getObject(i).getMbr());
			}
			
			isLeaf = true;
		} else {
			for(int i = 0; i < node.getChildren().length; i++) {
				children[i] = node.getChildren()[i];
			}
			
			isLeaf = false;
		}
		
		MBR.set(node.getMbr());
		parent = node.getParent();
		level = node.getLevel();
	}

	/**
	 * Очищает массив листьев дерева (объектов дерева).
	 */

	public void clearObjects() {
		objects = new GPSObject[0];
	}

	/**
	 * Очищает массив дочерних узлов дерева.
	 */

	public void clearChildren() {
		children = new int[0];
	}

	/**
	 * Возвращает ограничивающий прямоугольник узла.
	 *
	 * @return ограничивающий прямоугольник узла
     */

	public JRect getMbr() {
		return MBR;
	}

	/**
	 * Задает ограничивающий прямоугольник узла.
	 *
	 * @param mbr ограничивающий прямоугольник узла
     */

	public void setMbr(JRect mbr) {
		this.MBR.left = mbr.left;
		this.MBR.top = mbr.top;
		this.MBR.right = mbr.right;
		this.MBR.bottom = mbr.bottom;
	}

	/**
	 * Возвращает свойство, указывающее является ли узел в дереве конечным (листом).
	 *
	 * @return если true - узел является листом дерева, иначе - false
     */

	public boolean isLeaf() {
		return isLeaf;
	}

	/**
	 *Устанавливает свойство, указывающее является ли узел в дереве конечным (листом).
	 *
	 * @param isLeaf если true - узел является листом дерева, иначе - false
     */

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	/**
	 * Возвращает массив индексов дочерних узлов в массиве узлов дерева.
	 *
	 * @return массив индексов дочерних узлов в массиве узлов дерева
     */

	public int[] getChildren() {
		return children;
	}

	/**
	 * Возвращает индекс узла из массива индексов дочерних узлов в массиве узлов дерева.
	 *
	 * @param index индекс в массиве индексов дочерних узлов в массиве узлов дерева
	 * @return индекс узла из массива индексов дочерних узлов в массиве узлов дерева
     */

	public int getChild(int index) {
			return children[index];
	}

	/**
	 * Вставляет индекс узла в массив индексов дочерних узлов  в массиве узлов дерева.
	 *
	 * @param index индекс в массиве индексов дочерних узлов в массиве узлов дерева
	 * @param nodeId идентификатор узла
     */

	public void setChild(int index, int nodeId) {
		if(children.length > index && children.length != 0) {
			children[index] = nodeId;
			isLeaf = false;
		} else {
			if(index >= 0 && index < M) {
				int[] tmp = children;
				int size = children.length; 
				
				children = new int[index + 1];
			    
				if(tmp.length != 0)
				   	System.arraycopy(tmp, 0, children, 0, size);
				    
				children[children.length - 1] = nodeId;
				isLeaf = false;
			}
		}
	}

	/**
	 * Возвращает массив с листьями дерева (с объектами дерева).
	 *
	 * @return массив с листьями дерева (с объектами дерева)
     */

	public GPSObject[] getObjects() {
		return objects;
	}

	/**
	 * Возврвщает лист (объект) из массива листьев (объектов).
	 *
	 * @param index индекс в массиве листьев (объектов)
	 * @return лист (объект) из массива листьев (объектов)
     */

	public GPSObject getObject(int index) {
			return objects[index];
	}

	/**
	 * Вставляет лист (объект) в массив листьев (объектов).
	 *
	 * @param index индекс в массиве листьев (объектов)
	 * @param obj лист (объект) из массива листьев (объектов)
     */

	public void setObject(int index, GPSObject obj) {
		if(objects.length > index && objects.length != 0) {
			objects[index] = obj;
			isLeaf = true;
		} else {
			if(index >= 0 && index < M) {
				GPSObject[] tmp = objects;
				int size = objects.length; 
				
				objects = new GPSObject[index + 1];
			    
				if(tmp.length != 0)
				   	System.arraycopy(tmp, 0, objects, 0, size);
				    
				objects[objects.length - 1] = obj;
				isLeaf = true;
			}
		}
	}
	
	/**
	 * Заменяет массив объектов новым массивом
	 * @param objs новый массив объектов
	 */
	public void setNewObjects(GPSObject[] objs) {
		objects = objs;
	}

	/**
	 * Возвращает индекс в массиве узлов дерева, указывающий на узел-родитель.
	 *
	 * @return индекс в массиве узлов дерева, указывающий на узел-родитель
     */

	public int getParent() {
		return parent;
	}

	/**
	 * Устанавливает индекс в массиве узлов дерева, указывающий на узел-родитель.
	 *
	 * @param parentId индекс в массиве узлов дерева, указывающий на узел-родитель
     */

	public void setParent(int parentId) {
		if(parentId >= 0)
			parent = parentId;
	}

	/**
	 * Возвращает уровень узла в дереве (0 = лист).
	 *
	 * @return уровень узла в дереве
     */

	public int getLevel() {
		return level;
	}

	/**
	 * Устанавливает уровень узла в дереве (0 = лист).
	 *
	 * @param level уровень узла в дереве
     */

	public void setLevel(int level) {
		if(level >= 0)
			this.level = level;
	}

	/**
	 * Определяет пересекаются ли две области (прямоугольника).
	 *
	 * @param mbr1 прямоугольник 1
	 * @param mbr2 прямоугольник 2
     * @return если true - то прямоугольники пересекаются друг с другом, иначе - false
     */

	public boolean isIntersected(JRect mbr1, JRect mbr2) {
		if(mbr1.left <= mbr2.right && mbr1.top <= mbr2.bottom) {
			if(mbr1.right >= mbr2.left && mbr1.bottom >= mbr2.top)
				return true;
		}
		
		return false;
	}

	/**
	 * Определяет пересекается ли ограничивающий прямоугольник этого узла (MBR) с
	 * заданным прямоугольником (mbr).
	 *
	 * @param mbr заданный прямоугольник
	 * @return если true - то прямоугольники пересекаются друг с другом, иначе - false
     */

	public boolean isIntersected(JRect mbr) {
		if(MBR.left <= mbr.right && MBR.top <= mbr.bottom) {
			if(MBR.right >= mbr.left && MBR.bottom >= mbr.top)
				return true;
		}
		
		return false;
	}

	/**
	 * Возвращает значение площади пересечения ограничивающего прямоугольника этого узла (MBR)
	 * с заданной областью (прямоугольником mbr_ovrl).
	 *
	 * @param mbr_ovrl заданный прямоугольник
	 * @return значение площади пересечения ограничивающего прямоугольника этого узла (MBR)
	 * с заданной областью (прямоугольником mbr_ovrl)
     */
	public double Overlap(JRect mbr_ovrl) {
		double x;
		double y;
		
		x = Math.min(mbr_ovrl.right, MBR.right) - Math.max(mbr_ovrl.left, MBR.left);
		
		if(x <= 0) 
			return 0;
		
		y = Math.min(mbr_ovrl.bottom, MBR.bottom) - Math.max(mbr_ovrl.top, MBR.top);
		
		if(y <= 0) 
			return 0;
		
		return x * y;
	}

	/**
	 * Возвращает площадь ограничивающего этот узел прямоугольника (MBR).
	 *
	 * @return площадь ограничивающего этот узел прямоугольника (MBR)
     */

	public double area() {
		return (MBR.right - MBR.left) * (MBR.bottom - MBR.top);
	}

	/**
	 * Возвращает площадь заданного ограничивающего прямоугольника (mbr).
	 *
	 * @param mbr заданный прямоугольник
	 * @return площадь заданного ограничивающего прямоугольника (mbr)
     */

	public double area(JRect mbr) {
		return (mbr.right - mbr.left) * (mbr.bottom - mbr.top);
	}

	/**
	 * Возвращает периметр ограничивающего этот узел прямоугольника (MBR).
	 *
	 * @return периметр ограничивающего этот узел прямоугольника (MBR)
     */

	public double margin() {
		return ((MBR.right - MBR.left) + (MBR.bottom - MBR.top)) * 2;
	}
}

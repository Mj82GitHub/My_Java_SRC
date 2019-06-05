/*
 * Copyright (c) 08.2016
 */

package com.mikhail.mj82.nvg.Tree;

import java.io.Serializable;

import com.mikhail.mj82.nvg.Geom.JRect;

/**
 * Класс листа в графе поиска. Содержит значение смещения (положения) точки в файле карты,
 * а также значения широты и долготы этой точки.
 *
 * @author Mikhail Kushnerov (mj82)
 */

public class GPSObject implements Serializable {

	private static final long serialVersionUID = -4980313183782655160L;
	
	private long seek; // Смещение в файле карты
	
	/*
	Ограничивающий объект прямоугольник. В данном случае объект у нас - это точка на карте с
	координатами широты и долготы, поэтому в прямоугольнике задаем координаты только верхнего
	левого угла, а ширина и высота прямоугольника равны нулю.
	 */
	
	private JRect mbr;
	
	public GPSObject() {
		seek = 0;
		mbr = new JRect();
	}
	
	public GPSObject(long seek, JRect mbr) {
		this.seek = seek;
		this.mbr = mbr;
	}
	
	/**
	 * Возвращает смещение точки в файле карты.
	 * 
	 * @return смещение в файле
	 */
	
	public long getSeek() {
		return seek;
	}
	
	/**
	 * Задает смещение точки в файле карты.
	 * 
	 * @param seek смещение в файле
	 */
	
	public void setSeek(long seek) {
		this.seek = seek;
	}
	
	/**
	 * Возвращает ограничивающий прямоугольник.
	 * 
	 * @return ограничивающий прямоугольник
	 */
	
	public JRect getMbr() {
		return mbr;
	}
		
	/**
	 * Задает новый ограничивающий прямоугольник.
	 * 
	 * @param mbr ограничивающий прямоугольник
	 */
	
	public void setMbr(JRect mbr) {
		this.mbr.left = mbr.left;
		this.mbr.top = mbr.top;
		this.mbr.right = mbr.right;
		this.mbr.bottom = mbr.bottom;
	}
	
	/**
	 * Сравнивает объекты.
	 * 
	 * @param obj сравниваемый объект
	 * @return TRUE, если объекты равны, иначе - FALSE
	 */
	public boolean compareObject(GPSObject obj) {
		return (seek == obj.getSeek() && 
				mbr.left == obj.getMbr().left &&
				mbr.top == obj.getMbr().top &&
				mbr.right == obj.getMbr().right &&
				mbr.bottom == obj.getMbr().bottom);
	}
}

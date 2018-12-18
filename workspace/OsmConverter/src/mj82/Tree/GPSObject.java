/*
 * Copyright (c) 08.2016
 */

package mj82.Tree;

import java.io.Serializable;

import mj82.Geom.JRect;

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
}

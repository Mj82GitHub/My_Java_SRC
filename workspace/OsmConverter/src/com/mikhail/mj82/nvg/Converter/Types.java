/*
 * Copyright (c) 12.2016
 */

package com.mikhail.mj82.nvg.Converter;

/**
 * Интерфейс для доступа к данным по типам объектов на карте.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public interface Types {

	/**
	 * Возвращает тип объекта на карте в виде двухбайтового кода.
	 * 
	 * @param k тип объекта на карте
	 * @param v подтип типа объекта на карте
	 * @return тип объекта на карте в виде двухбайтового кода
	 */
	
	public short getTypeOfObject(String k, String v);
	
	/**
	 * Возвращает тип объекта на карте в виде текстовой строки.
	 * 
	 * @param v подтип типа объекта на карте
	 * @return тип объекта на карте в виде текстовой строки
	 */
	
	public String getTypeOfObject(short v);
}

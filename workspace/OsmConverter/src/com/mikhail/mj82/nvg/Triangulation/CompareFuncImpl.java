/*
 * Copyright (c) 09.2017
 */

package com.mikhail.mj82.nvg.Triangulation;

/**
 * Обобщенный функциональный интерфейс для ф-ций сравнения двух элементов.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public interface CompareFuncImpl<T> {
		
	public int cmp(T val_1, T val_2);
}

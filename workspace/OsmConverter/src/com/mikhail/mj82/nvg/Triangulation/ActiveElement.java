/*
 * Copyright (c) 09.2017
 */

package com.mikhail.mj82.nvg.Triangulation;

/**
 * Класс 
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class ActiveElement implements ActiveElementFunctions{
	
	// Тип структуры сканирующей линии
	enum Active_element_type { ACTIVE_POINT, // Точка
		                       ACTIVE_EDGE }; // Ребро

	public Active_element_type type; // ACTIVE_EDGE (ребро) или ACTIVE_POINT (точка)
	
	public ActiveElement(Active_element_type type) {
		this.type = type;
	}	
}

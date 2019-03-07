/*
 * Copyright (c) 09.2017
 */

package com.mikhail.mj82.Triangulation;

/**
 * »нтерфейс в помощь классу ActiveElement
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public interface ActiveElementFunctions {

	/**
	 * ¬озвращает координату Y точки пересечени€ текущего ребра
	 * со сканирующей линией.
	 * 
	 * @return координату Y точки пересечени€ текущего ребра
	 * со сканирующей линией
	 */
	
	default double getY() {
		return 0.0;
	}
	
	/**
	 * ¬озвращает текущее ребро.
	 * 
	 * @return текущее ребро
	 */
	
	default Edge edge() {
		return new Edge();
	}
	
	/**
	 * ¬озвращает наклон текущего ребра.
	 * 
	 * @return наклон текущего ребра
	 */
	
	default double slope() {
		return 0.0;
	}
}

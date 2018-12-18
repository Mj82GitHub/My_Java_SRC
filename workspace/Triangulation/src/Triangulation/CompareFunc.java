/*
 * Copyright (c) 09.2017
 */

package Triangulation;

/**
 * Класс содержит функции, выполняющие сравнение вершин относительно оси X,
 * а также в зависимости от направления, с которого начинается сравнение (справа или слева).
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class CompareFunc {

	/**
	 * Сравнивает две вершины (их координаты).
	 * 
	 * @param v первая вершина
	 * @param w вторая вершина
	 * @return 0 - если вершины равны, 1 - если первая вершина больше второй, 
	 * -1 - если первая вершина меньше второй
	 */
	
	static int cmp(Mj_Vertex v, Mj_Vertex w) {
		if(v.point().less(w.point()))
			return -1;
		else if(v.point().more(w.point()))
			return 1;
		else
			return 0;
	}
	
	/**
	 * Сравнивает две вершины (их координаты). Учитывает направление
	 * оси X, меньшее значенее справа.
	 * 
	 * @param v первая вершина
	 * @param w вторая вершина
	 * @return 0 - если вершины равны, -1 - если первая вершина больше второй, 
	 * 1 - если первая вершина меньше второй
	 */
	
	static int rightToLeftCmp(Mj_Vertex v, Mj_Vertex w) {
		int result = cmp(v, w);
		
		if(result == -1)
			result = 1;
		else if(result == 1)
			result = -1;
		
		return result;
	}
	
	/**
	 * Сравнивает две вершины (их координаты). Учитывает направление
	 * оси X, меньшее значенее слева.
	 * 
	 * @param v первая вершина
	 * @param w вторая вершина
	 * @return 0 - если вершины равны, 1 - если первая вершина больше второй, 
	 * -1 - если первая вершина меньше второй
	 */
	
	static int leftToRightCmp(Mj_Vertex v, Mj_Vertex w) {
		return cmp(v, w);
	}
}

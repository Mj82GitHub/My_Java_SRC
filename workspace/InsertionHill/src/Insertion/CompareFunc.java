package Insertion;

public class CompareFunc {
	
	/**
	 * Сравнивает две вершины (их координаты).
	 * 
	 * @param v первая вершина
	 * @param w вторая вершина
	 * @return 0 - если вершины равны, 1 - если первая вершина больше второй, 
	 * -1 - если первая вершина меньше второй
	 */
	
	static int cmp(Point v, Point w) {
		if(v.less(w))
			return -1;
		else if(v.more(w))
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
	
	static int rightToLeftCmp(Point v, Point w) {
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
	
	static int leftToRightCmp(Point v, Point w) {
		return cmp(v, w);
	}
}

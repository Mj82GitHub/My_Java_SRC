package MyLists;



public class CompareFunc {

	/**
	 * Сравнивает два ребра.
	 * 
	 * @param a первое ребро
	 * @param b второе ребро
	 * @return 0 - если ребра равны, 1 - если первое ребро больше второго, 
	 * -1 - если первое ребро меньше второго
	 */
	static int edgeCmp2(Edge a, Edge b) {
		double ya = a.getY(MyList.curx);
		double yb = b.getY(MyList.curx);
		
		if(ya < yb)
			return -1;
		else if(ya > yb)
			return 1;
		
		double ma = a.slope();
		double mb = b.slope();
		
		if(ma > mb)
			return -1;
		else if(ma < mb)
			return 1;
				
		return 0;
	}
	
	/**
	 * Сравнивает два целочисленных числа.
	 * 
	 * @param a первое число
	 * @param b второе число
	 * @return 0 - если числа равны, 1 - если первое число больше второго, 
	 * -1 - если первое число меньше второго
	 */
	
	static int cmp(int a, int b) {
		if(a < b)
			return -1;
		else if(a > b)
			return 1;
		else
			return 0;
	}
	
	/**
	 * Сравнивает две вершины (их координаты).
	 * 
	 * @param v первая вершина
	 * @param w вторая вершина
	 * @return 0 - если вершины равны, 1 - если первая вершина больше второй, 
	 * -1 - если первая вершина меньше второй
	 */
/*	
	static int cmp(Vertex v, Vertex w) {
		if(v.point().less(w.point()))
			return -1;
		else if(v.point().more(w.point()))
			return 1;
		else
			return 0;
	}
*/	
	/**
	 * Сравнивает две вершины (их координаты). Учитывает направление
	 * оси X, меньшее значенее справа.
	 * 
	 * @param v первая вершина
	 * @param w вторая вершина
	 * @return 0 - если вершины равны, -1 - если первая вершина больше второй, 
	 * 1 - если первая вершина меньше второй
	 */
/*	
	static int rightToLeftCmp(Vertex v, Vertex w) {
		int result = cmp(v, w);
		
		if(result == -1)
			result = 1;
		else if(result == 1)
			result = -1;
		
		return result;
	}
*/	
	/**
	 * Сравнивает две вершины (их координаты). Учитывает направление
	 * оси X, меньшее значенее слева.
	 * 
	 * @param v первая вершина
	 * @param w вторая вершина
	 * @return 0 - если вершины равны, 1 - если первая вершина больше второй, 
	 * -1 - если первая вершина меньше второй
	 */
/*	
	static int leftToRightCmp(Vertex v, Vertex w) {
		return cmp(v, w);
	}
*/
}

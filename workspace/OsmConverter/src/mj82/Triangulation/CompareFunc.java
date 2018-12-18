package mj82.Triangulation;

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
		if(v.equalsVertex(w)) {
			// Вычисляем угол вектора к оси X (против часовой стрелки)
			Edge edge_v = new Edge(v.point(), ((Mj_Vertex) v.next()).point());
			Edge edge_w = new Edge(w.point(), ((Mj_Vertex) w.next()).point());
			
			// Вычичляем полярные углы ребер
			double angle_a = Math.round((edge_v.dest.subtraction(edge_v.org)).polarAngle());
			double angle_b = Math.round((edge_w.dest.subtraction(edge_w.org)).polarAngle());
				
			if(angle_a == angle_b)
				return 0;
			else if((angle_a > 0 && angle_a <= 180))
				return 1;
			else if((angle_a > 180 && angle_a <= 360))
				return -1;
		} else {
			if(v.point().less(w.point()))
				return -1;
			else if(v.point().more(w.point()))
				return 1;
			else 
				return 0;
		}
		
		return 0;
	}
	
	/**
	 * Сравнивает две вершины (их координаты). Учитывает направление
	 * оси X, меньшее значенее справа.
	 * 
	 * @param v первая вершина
	 * @param w вторая вершина
	 * @return 0 - если вершины равны, -1 - если первая вершина меньше второй, 
	 * 1 - если первая вершина больше второй
	 */
	
	static int rightToLeftCmp(Mj_Vertex v, Mj_Vertex w) {
		int result = cmp(v, w);		
		
		if(v.point().x == w.point().x && v.point().y < w.point().y)
			return -1;
		else if(v.point().x == w.point().x && v.point().y > w.point().y)
			return 1;
		else if(result == -1)
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

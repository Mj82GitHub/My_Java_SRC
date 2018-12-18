/*
 * Copyright (c) 05.2018
 */ 

package mj82.Triangulation;

import mj82.Triangulation.Edge.Intersect;

/**
 * Класс для изменения координат вершин в месте рассечения после 
 * объединения внешнего полигона с внутренним.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class VertexOffseter {

	/**
	 * Изменяет координаты заданных вершин полигона так, чтобы после этого 
	 * в полигоне не существовало самопересечения сторон.
	 * 
	 * @param v1 вершина полигона
	 * @param v2 вершина полигона
	 * @param delta смещение точек относительно друг друга
	 * @param outer_polygon полигон
	 */
	
	public void offset(Mj_Vertex v1, Mj_Vertex v2, Mj_Polygon outer_polygon, double delta) {
		if(delta <= 0)
			delta = 0.0000001;
		
		boolean isCross = true; // Считаем, что пересечение есть!
		
		double x1 = v1.point().x;
		double y1 = v1.point().y;
		
		double x2 = v2.point().x;
		double y2 = v2.point().y;
		
		// Счетчик для перебора возможных комбинаций смещений координат вершин
		int count = 1;
		
		while(isCross) {
			switch(count) {
			case 1:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				
				break;
			case 2:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				
				break;
			case 3:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				
				break;
			case 4:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				
				break;
			case 5:
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 6:
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 7:
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 8:
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 9:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				
				break;
			case 10:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				
				break;
			case 11:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				
				break;
			case 12:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				
				break;
			case 13:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				
				break;
			case 14:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				
				break;
			case 15:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				
				break;
			case 16:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				
				break;
			case 17:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 18:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 19:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 20:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 21:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 22:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 23:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 24:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 25:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 26:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 27:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 28:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 29:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 30:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 31:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 32:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 33:
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 34:
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 35:
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 36:
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 37:
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 38:
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 39:
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 40:
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 41:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 42:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 43:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 44:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 45:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 46:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 47:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 48:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y += delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 49:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 50:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 51:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 52:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y += delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 53:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 54:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x += delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 55:
				v1.point().x += delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			case 56:
				v1.point().x -= delta;
				v1.point().x = Decompositor.round7(v1.point().x);
				v1.point().y -= delta;
				v1.point().y = Decompositor.round7(v1.point().y);
				
				v2.point().x -= delta;
				v2.point().x = Decompositor.round7(v2.point().x);
				v2.point().y -= delta;
				v2.point().y = Decompositor.round7(v2.point().y);
				
				break;
			default:
				System.out.println("НЕ УДАЛОСЬ ИЗМЕНИТЬ КООРДИНАТЫ ВЕРШИН ПОЛИГОНА.");
				return;
			}
			
			// Изменяем размер полигона
			outer_polygon.resize();
			
			isCross = self_test(outer_polygon);
			
			if(isCross) {
				v1.point().x = x1;
				v1.point().y = y1;
				
				v2.point().x = x2;
				v2.point().y = y2;
			}
			
			count++;
		}
		
		// Сохраняем соответствие оригинальных координат и смещенных
		Triangulation.org_coords.put(v1.point().x, x1);
		Triangulation.org_coords.put(v1.point().y, y1);
		Triangulation.org_coords.put(v2.point().x, x2);
		Triangulation.org_coords.put(v2.point().y, y2);
	}
	
	/**
	 * Определяет пересечение несмежных сторон полигона. 
	 * 
	 * @param P тестируемый полигон
	 * @return TRUE, если пересечения присутствуют, иначе - FALSE
	 */
	
	private boolean self_test(Mj_Polygon P) {	
		Mj_Vertex first = P.getVertex();		
		
		for(int i = 0; i <= P.size - 1; i++) {
			Edge a = new Edge(first.point(), ((Mj_Vertex) first.next()).point());
			Mj_Vertex third = first.cw().cw();
			
			for(int j = 1; j < P.size - 2; j++) {
				Edge b = new Edge(third.point(), ((Mj_Vertex) third.next()).point());
			
				Intersect inter = a.cross(b, 0.0);
					
				if(inter == Intersect.SKEW_CROSS)
					return true;
				
				third = third.cw();
			}
			
			first = first.cw();
		}
		
		return false;
	}
}

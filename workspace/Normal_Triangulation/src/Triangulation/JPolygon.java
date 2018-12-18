/*
 * Copyright (c) 02.2017
 */

package Triangulation;

import java.util.Arrays;

/**
 * Класс предназначен для создания многоугольников (полигонов).
 * 
 * @author Mikhail Kushnerov (mj82)
 *
 */

public class JPolygon {

	// Общее количество точек в полигоне. Это значение представляет собой число, из которых
	// действительно состоит полигон и может быть меньше, чем количество задаваемых точек
	// в конструкторе. Это значение может быть NULL.
	public int n_points;
	
	// Массив Х координат
	public double [] x_points;
	
	// Массив Y координат
	public double [] y_points;
	
	// Граници этого полигона. Это значение может быть NULL.
	private JRect bounds;
	
	// Размер массивов X и Y координат поумолчанию.
	private final int MIN_LENGTH = 4;
	
	/**
	 * Создает пустой полигон.
	 */
	
	public JPolygon() {
		x_points = new double[MIN_LENGTH];
		y_points = new double [MIN_LENGTH];
	}
	
	/**
	 * Создает полигон с заданными параметрами.
	 * 
	 * @param x_points - количество точек для построения полигона
	 * @param y_points - массив X координат
	 * @param n_points - массив Y координат
	 */
	
	public JPolygon(double [] x_points, double [] y_points, int n_points) {
		if(n_points > x_points.length || n_points > y_points.length)
			throw new IndexOutOfBoundsException("n_points > x_points.length || n_points > y_points.length");
		
		if(n_points < 0)
			throw new NegativeArraySizeException("n_points < 0");
		
		this.n_points = n_points;
		this.x_points = Arrays.copyOf(x_points, n_points);
		this.y_points = Arrays.copyOf(y_points, n_points);
	}
	
	/**
	 * Сбрасывает все параметры и создает пустой полигон.
	 */
	
	public void reset() {
		n_points = 0;
		bounds = null;
	}
	
	/**
	 * Аннулирует (сбрасывает) данные о границах полигона.
	 */
	
	public void invalidate() {
		bounds = null;
	}
	
	/**
	 * Перемещает вершины полигона на dX вдоль оси X и на dY вдоль оси Y.
	 * 
	 * @param dX - расстояние, на которое перемещаются вершины полигона вдоль оси X
	 * @param dY - расстояние, на которое перемещаются вершины полигона вдоль оси Y
	 */
	
	public void translate(double dX, double dY) {
		for(int i = 0; i < n_points; i++) {
			x_points[i] += dX;
			y_points[i] += dY;
		}
		
		if(bounds != null)
			bounds.translate(dX, dY);
	}
	
	/**
	 * Вычисляет границы прямоугольника ограничивающего полигон.
	 * 
	 * @param x_points - количество точек для построения полигона
	 * @param y_points - массив X координат
	 * @param n_points - массив Y координат
	 */
	
	private void calculateBounds(double [] x_points, double [] y_points, int n_points) {
		double boundsMinX = Double.MAX_VALUE;
		double boundsMinY = Double.MAX_VALUE;
		double boundsMaxX = Double.MIN_VALUE;
		double boundsMaxY = Double.MIN_VALUE;
		
		for(int i = 0; i < n_points; i++) {
			double x = x_points[i];
			boundsMinX = Math.min(boundsMinX, x);
			boundsMaxX = Math.max(boundsMaxX, x);
			
			double y = y_points[i];
			boundsMinY = Math.min(boundsMinY, y);
			boundsMaxY = Math.max(boundsMaxY, y);
		}
		
		bounds = new JRect(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY);
	}
	
	/**
	 * Обновляет границы ограничивающего полигон прямоугольника.
	 * 
	 * @param x - добавленная к полигону координата по оси X
	 * @param y - добавленная к полигону координата по оси Y
	 */
	
	private void updateBounds(double x, double y) {
		if(x < bounds.left) {
			bounds.width = bounds.width + (bounds.left - x);
			bounds.left = x;
		} else {
			bounds.width = Math.max(bounds.width, x - bounds.left);
		}
		
		if(y < bounds.top) {
			bounds.height = bounds.height + (bounds.top - y);
			bounds.top = y;
		} else {
			bounds.height = Math.max(bounds.height, y - bounds.top);
		}
	}
	
	/**
	 * Добавляет точку к полигону.
	 * 
	 * @param x - координата по оси X
	 * @param y - координата по оси Y
	 */
	
	public void addPoint(double x, double y) {
		if(n_points >= x_points.length || n_points >= y_points.length) {
			int newLength = n_points * 2;
			
			if(newLength < MIN_LENGTH) {
				newLength = MIN_LENGTH;
			} else if((newLength & (newLength - 1)) != 0) {
				newLength = Integer.highestOneBit(newLength);
			}
			
			x_points = Arrays.copyOf(x_points, newLength);
			y_points = Arrays.copyOf(y_points, newLength);
		}
		
		x_points[n_points] = x;
		y_points[n_points] = y;
		n_points++;
		
		if(bounds != null)
			updateBounds(x, y);
	}
	
	/**
	 * Возвращает ограничивающий полигон прямоугольник.
	 * 
	 * @return ограничивающий полигон прямоугольник
	 */
	
	public JRect getBounds() {
		if(n_points == 0)
			return new JRect();
		
		if(bounds == null)
			calculateBounds(x_points, y_points, n_points);
		
		return bounds.getBounds();
	}
	
	/**
	 *  Определяет находится ли точка с заданными координатами внутри нашего прямоугольника.
	 * 
	 * @param x - координата по оси X
	 * @param y - координата по оси Y
	 * @return true, если точка внутри нашего прямоугольника, иначе - false
	 */
	
	public boolean contains(double x, double y) {
		if(n_points <= 2 || !getBounds().contains(x, y)) {
			return false;
		}
		
		int hits = 0;
		double last_x = x_points[n_points - 1];
		double last_y = y_points[n_points - 1];
		double cur_x, cur_y;
		
		for(int i = 0; i < n_points; last_x = cur_x, last_y = cur_y, i++) {
			cur_x = x_points[i];
			cur_y = y_points[i];
			
			if(cur_y == last_y) {
				continue;
			}
			
			double left_x;
			
			if(cur_x < last_x) {
				if(x >= last_x) {
					continue;
				}
				
				left_x = cur_x;
			} else {
				if(x >= cur_x) {
					continue;
				}
				
				left_x = last_x;
			}
			
			double test1, test2;
			
			if(cur_y < last_y) {
				if(y < cur_y || y >= last_y) {
					continue;
				}
				
				if(x < left_x) {
					hits++;
					continue;
				}
				
				test1 = x - cur_x;
				test2 = y - cur_y;
			} else {
				if(y < last_y || y >= cur_y) {
					continue;
				}
				
				if(x < left_x) {
					hits++;
					continue;
				}
				
				test1 = x - last_x;
				test2 = y - last_y;
			}
			
			if(test1 < (test2 / (last_y - cur_y) * (last_x - cur_x))) {
				hits++;
			}
		}
		
		return ((hits & 1) != 0);
	}

	/**
	 * Определяет находится ли полигон polygon внутри нашего полигона.
	 * 
	 * @param polygon - заданный полигон
	 * @return true, если полигон polygon внутри нашего полигона, иначе - false
	 */
	
	public boolean contains(JPolygon polygon) {
		JRect rect = polygon.getBounds();
		
		return this.getBounds().contains(rect);
	}
}

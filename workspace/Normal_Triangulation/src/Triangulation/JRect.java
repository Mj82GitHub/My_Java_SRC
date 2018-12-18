/*
 * Copyright (c) 08.2016
 */

package Triangulation;

import java.io.Serializable;

/**
 * Класс предназначен для создания прямоугольника.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class JRect implements Serializable {
	
	private static final long serialVersionUID = -2855079158194121381L;
	
	public double left; // Координата верхнего левого угла прямоугольника по оси X
	public double top; // Координата верхнего левого угла прямоугольника по оси Y
	public double right; // Координата нижнего правого угла прямоугольника по оси X
	public double bottom; // Координата нижнего правого угла прямоугольника по оси Y
	public double width; // Ширина прямоугольника
	public double height; // Высота прямоугольника
	
/**
 * Создает новый прямоугольник. Все координаты равны нулю.
 */
	
	public JRect() {
		left = 0;
		top = 0;
		right = 0;
		bottom = 0;
		
		width = this.right - this.left;
		height = this.bottom - this.top;
	}

	/**
	 * Создает новый точечный прямоугольник. Координаты нижнего правого угла равны
	 * координатам верхнего левого угла.
	 */

	public JRect(double left, double top) {
		this.left = left;
		this.top = top;
		this.right = left;
		this.bottom = top;
		
		width = this.right - this.left;
		height = this.bottom - this.top;
	}

/**
 * Создает новый точечный прямоугольник. Для корректного создания прямоугольника
 * необходимо, чтобы left <= right, top <= bottom.
 * 
 * @param left координата верхнего левого угла прямоугольника по оси X
 * @param top координата верхнего левого угла прямоугольника по оси Y
 * @param right координата нижнего правого угла прямоугольника по оси X
 * @param bottom координата нижнего правого угла прямоугольника по оси Y
 */
	
	public JRect(double left, double top, double right, double bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		
		width = this.right - this.left;
		height = this.bottom - this.top;
	}
	
/**
 * Возвращает горизонтальный цент прямоугольника.
 */
	
	public final double centerX() {
		return ((double)right - (double)left) / 2.0;
	}
		
/**
 * Возвращает вертикальный цент прямоугольника.
 */
	
	public final double centerY() {
		return ((double)bottom - (double)top) / 2.0;
	}
	
/**
 * Возвращает true, если точка с координатой (x, y) находится внутри прямоугольника,
 * т.е. left <= x < right и top <= y < bottom. Пустой прямоугольник не может содержать
 * внутри себя точку (x, y).
 * 
 * @param x X координата точки проверяемой на нахождение в прямоугольнике
 * @param y Y координата точки проверяемой на нахождение в прямоугольнике
 */
	
	public boolean contains(double x, double y) {
		return (x >= left && x <= right && y >= top && y <= bottom) ? true : false; 
	}
	
	/**
	 * Возвращает true, если принимаемые четыре координаты прямоугольника находятся внутри
	 * или на тех же местах этого прямоугольника. Пустой прямоугольник не может содержать внутри
	 * себя другой прямоугольник.
	 * 
	 * @param left - левая сторона проверяемого прямоугольника
	 * @param top - верхняя сторона проверяемого прямоугольника
	 * @param right - правая сторона проверяемого прямоугольника
	 * @param bottom - нижняя сторона проверяемого прямоугольника
	 */
	
	public boolean contains(double left, double top, double right, double bottom) {
		       // Сперва проверяем на пустоту прямоугольника
		return this.left < this.right && this.top < this.bottom 
			   // Теперь проверяем на принадлежность к прямоугольнику
			   && this.left <= left && this.top <= top
			   && this.right >= right && this.bottom >= bottom;
	}
	
	/**
	 * Возвращает true, если принимаемые прямоугольник находятся внутри
	 * или на тех же местах этого прямоугольника. Пустой прямоугольник не может содержать внутри
	 * себя другой прямоугольник.
	 * 
	 * @param rect - проверяемый прямоугольник
	 */
	
	public boolean contains(JRect rect) {
		return this.left < this.right && this.top < this.bottom 
			   && this.left <= rect.left && this.top <= rect.top
			   && this.right >= rect.right && this.bottom >= rect.bottom;
	}
	
/**
 * Возвращает высоту прямоугольника. Если top >= bottom, то результат может быть
 * отрицательным.
 */
	
	public final double height() {
		return bottom - top;
	}
	
/**
 * Возвращает true, если прямоугольник пустой (left >= right или top >= bottom).
 */
	
	public final boolean isEmpty() {
		return (left >= right || top >= bottom) ? true : false;
	}

/**
 * Задает координатам прямоугольника новые значения.
 * 
 * @param left координата верхнего левого угла прямоугольника по оси X
 * @param top координата верхнего левого угла прямоугольника по оси Y
 * @param right координата нижнего правого угла прямоугольника по оси X
 * @param bottom координата нижнего правого угла прямоугольника по оси Y
 */
	
	public void set(double left, double top, double right, double bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		
		width = this.right - this.left;
		height = this.bottom - this.top;
	}
	
/**
 * Задает координатам прямоугольника новые значения, копируя координаты прямоугольника src.
 * 
 * @param src прямоугольник, координаты которого копируем
 */
	
	public void set(JRect src) {
		this.set(src.left, src.top, src.right, src.bottom);
	}
	
/**
 * Устанавливает все координаты прямоугольника равными нулю.
 */
	
	public void setEmpty() {
		this.left = 0;
		this.top = 0;
		this.right = 0;
		this.bottom = 0;
		
		width = this.right - this.left;
		height = this.bottom - this.top;
	}
	
/**
 * Возвращает текстовое описание прямоугольника.
 */
	
	@Override
	public String toString() {
		return "[" + left + ", " + top + " - " + right + ", " + bottom + "]";
	}
		
/**
 * Возвращает ширину прямоугольника, если left >= right, то результат может быть
 * отрицательным.
 */
	
	public final double width() {
		return right - left;
	}
	
	/**
	 * Перемещает вершины прямоугольника на dX вдоль оси X и на dY вдоль оси Y.
	 * 
	 * @param dX - расстояние, на которое перемещаются вершины прямоугольника вдоль оси X
	 * @param dY - расстояние, на которое перемещаются вершины прямоугольника вдоль оси Y
	 */
	
	public void translate(double dX, double dY) {
		double old_left = this.left;
		double new_left = old_left + dX;
		
		if(dX < 0) {
			if(new_left > old_left) {
				if(width >= 0) {
					width += new_left - Double.MIN_VALUE;
				}
				
				new_left = Double.MIN_VALUE;
			}
		} else {
			if(new_left < old_left) {
				if(width >= 0) {
					width += new_left - Double.MAX_VALUE;
					
					if(width < 0) {
						width = Double.MAX_VALUE;
					}
				}
				
				new_left = Double.MAX_VALUE;
			}
		}
		
		this.left = new_left;
		
		double old_top = this.top;
		double new_top = old_top + dY;
		
		if(dY < 0) {
			if(new_top > old_top) {
				if(height >= 0) {
					height += new_top - Double.MIN_VALUE; 
				}
				
				new_top = Double.MIN_VALUE;
			}
		} else {
			if(new_top < old_top) {
				if(height >= 0) {
					height += new_top - Double.MAX_VALUE;
					
					if(height < 0) {
						height = Double.MAX_VALUE;
					}
					
					new_top = Double.MAX_VALUE;
				}
			}
		}
		
		this.top = new_top;
	}
		
	public boolean intersects(double x, double y, double w, double h) {
		if(isEmpty() || w <= 0 || h <= 0) {
			return false;
		}
		
		double x0 = left;
		double y0 = top;
		
		return (x + w > x0 && y + h > y0 && x < x0 + width && y < y0 + height);
	}
	
	/**
	 * Возвращает копию прямоугольника.
	 * 
	 * @return копия прямоугольника
	 */
	
	public JRect getBounds() {
		return new JRect(left, top, right, bottom);
	}
}
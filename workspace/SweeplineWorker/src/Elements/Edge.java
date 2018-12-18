/*
 * Copyright (c) 09.2017
 */

package Elements;
import Elements.Point.Point_position;

/**
 * Класс применяется для представления всех форм прямых линий. 
 * 
 *@author Mikhail Kushnerov (mj82) *
 */

public class Edge {
	
	public static double t; // Рассчитывается при вычислении точки пересечения
	
	public Point org; // Точка начала ребра
	public Point dest;// Точка конца ребра
	
	// Значение типа пересечения линий
	enum Intersect { COLLINEAR,       // Коллинеарны (лежат на одной прямой)
		             PARALLEL,        // Параллельны
		             SKEW,            // Наклонены
		             SKEW_CROSS,      // Наклонены и пересекаются
		             SKEW_NO_CROSS }; // Наклонены, но без пересечения
	
	public Edge() {
		org = new Point(0, 0);
		dest = new Point(1, 0);
	}
	
	public Edge(Point org, Point dest) {
		this.org = org;
		this.dest = dest;
	}
	
	/**
	 * Реализует поворот (вращение) ребра на 90 градусов в напрвлении по часовой стрелке
	 * вокруг его средней точки.
	 * 
	 * @return повернутое ребро
	 */
	
	public Edge rot() {
		Point m = (org.sum(dest)).multiplication(0.5);
		Point v = dest.subtraction(org);
		Point n = new Point(v.y, -v.x);
		
		org = m.subtraction(n.multiplication(0.5));
		dest = m.sum(n.multiplication(0.5));
		
		return this;
	}
	
	/**
	 * Изменение направления текущкго ребра на обратное.
	 * 
	 * @return перевернутое ребро
	 */
	
	public Edge flip() {
		return rot().rot();
	}
	
	/**
	 * Определяет точку пересечения двух линий.Подставляет параметр t в 
	 * параметрическое уравнение для этой линии.
	 * 
	 * P(t) = a + t * (b - a) - параметрическая форма линии.
	 * 
	 * @param t параметр, подставляемый в уравнение линии
	 * @return точка пересечения
	 */
	
	public Point point(double t) {
		Edge.t = t;
		return org.sum(dest.subtraction(org).multiplication(Edge.t));
	}
	
	/**
	 * Возвращает значение типа пересечения двух линий.
	 * 
	 * @param e прямая линия
	 * @param t параметр, подставляемый в уравнение линии
	 * @return значение типа пересечения двух линий
	 */
	
	public Intersect intersect(Edge e, double t) {		
		Point a = org;
		Point b = dest;
		Point c = e.org;
		Point d = e.dest;
		Point n = new Point(d.subtraction(c).y, c.subtraction(d).x);
		double denom = dotProduct(n, b.subtraction(a));
		
		if(denom == 0.0) {
			Point_position aclass = org.classify(e);
			
			if((aclass == Point_position.LEFT) || (aclass == Point_position.RIGHT))
				return Intersect.PARALLEL;
			else
				return Intersect.COLLINEAR;
		} 
		
		double num = dotProduct(n, a.subtraction(c));
		
		Edge.t = -num / denom;
		
		return Intersect.SKEW;
	}
	
	/**
	 * Реализация скалярного произведения двух векторов.
	 * 
	 * @param p вектор
	 * @param q вектор
	 * @return результат скалярного произведения двух векторов
	 */
	
	private double dotProduct(Point p, Point q) {
		return (p.x * q.x + p.y * q.y);
	}
	
	/**
	 * Возвращает значение SKEW_CROSS, если и только если текущий отрезок прямой линии
	 * пересекает отрезок прямой линии e. Если отрезки прямой линии пересекаются, то 
	 * возвращается значение параметры t вдоль этого отрезка прямой линии, соответствующее
	 * точке пересечения. В противном случае ф-ция возвращает одно из следующих подходящих 
	 * значений: COLLINEAR, PARALLEL или SKEW_NO_CROSS.
	 * 	
	 * @param e прямая линия
	 * @param t параметр, подставляемый в уравнение линии
	 * @return значение типа пересечения двух линий
	 */
	
	public Intersect cross(Edge e, double t) {		
		Intersect crossType = e.intersect(this, Edge.t);
		
		if((crossType == Intersect.COLLINEAR) || (crossType == Intersect.PARALLEL))
			return crossType;
		
		if((Edge.t < 0.0) || (Edge.t > 1.0))
			return Intersect.SKEW_NO_CROSS;
		
		intersect(e, Edge.t);
		
		if((0.0 <= Edge.t) && (Edge.t <= 1.0))
			return Intersect.SKEW_CROSS;
		else
			return Intersect.SKEW_NO_CROSS;
	}
	
	/**
	 * Определяет вертикальное ребро или нет.
	 * 
	 * @return возвращает TRUE, если ребро вертикальное, иначе - FALSE
	 */
	
	public boolean isVertical() {
		return (org.x == dest.x);
	}
	
	/**
	 * Определяет величину наклона текущего ребра.
	 * 
	 * @return возвращает величину наклона текущего ребра или значение 
	 * Double.MAX_VALUE, если текущее ребро вертикально.
	 */
	
	public double slope() {
		if(org.x != dest.x)
			return (dest.y - org.y) / (dest.x - org.x);
		
		return Double.MAX_VALUE;
	}
	
	/**
	 * Задается значение x и ф-ция возвращает значение y, соответствующее точке
	 * (x, y) на текущей бесконечной прямой линии. Ф-ция действует только в том случае, 
	 * если текущее ребро не вертикально.
	 * 
	 * @param x координата точки по оси X
	 * @return возвращает значение y, соответствующее точке
	 * (x, y) на текущей бесконечной прямой линии
	 */
	
	public double getY(double x) {
		return slope() * (x - org.x) + org.y;
	}
}

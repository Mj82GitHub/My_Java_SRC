package MyLists;


/**
 * Класс объекта точки.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Point {

	public double x; // Координата точки по оси Х
	public double y; // Координата точки по оси Y
	
	// Перечисление положения текущей точки по отношению к отрезку прямой линии
	enum Point_position { LEFT,          // Слева
		                  RIGHT,         // Справа
		                  BEYOND,        // Впереди
		                  BEHIND,        // Позади
		                  BETWEEN,       // Между
		                  ORIGIN,        // Начало
		                  DESTINATION }; // Конец
	
	public Point() {
		x = 0;
		y = 0;
	}
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Сумма двух векторов.
	 * 
	 * @param p слагаемое
	 * @return результат сложения двух векторов
	 */
	
	public Point sum(Point p) {
		return new Point(this.x + p.x, this.y + p.y);
	}
	
	/**
	 * Разность двух векторов.
	 * 
	 * @param p вычитаемое
	 * @return результат вычитания двух векторов
	 */
	
	public Point subtraction(Point p) {
		return new Point(this.x - p.x, this.y - p.y);
	}
	
	/**
	 * Скалярное умножение вектора на число.
	 * 
	 * @param s множитель (скаляр)
	 * @param p множимое
	 * @return результат скалярного умножения вектора на число
	 */
	
	public Point multiplication(double s, Point p) {
		return new Point(s * p.x, s * p.y);
	}
	
	/**
	 * Скалярное умножение вектора на число.
	 * 
	 * @param s множитель (скаляр)
	 * @return результат скалярного умножения вектора на число
	 */
	
	public Point multiplication(double s) {
		return new Point(s * this.x, s * this.y);
	}
	
	/**
	 * Возвращает координату X текущей точки, если index = 0, или координату Y, 
	 * если index = 1.
	 *  
	 * @param index индекс координаты
	 * @return координата текущей точки
	 */
	
	public double get(int index) {
		return (index == 0 ? x : y);
	}
	
	/**
	 * Определяет эквивалентность двух точек.
	 *  
	 * @param p точка для сравнения
	 * @return возвращает TRUE, если точки эквивалентны (равны), иначе FALSE
	 */
	
	public boolean equalsPoints(Point p) {
		if((this.x == p.x) && (this.y == p.y))
			return true;
		else
			return false;
		
	}
	
	/**
	 * Определяет лексикографический порядок отношения, когда считается, что this.p > p,
	 * если this.p.x > p.x, либо this.p.x = p.x и this.p.y > p.y.
	 * 
	 * @param p точка для сравнения
	 * @return возвращает TRUE, если точка больше заданной, иначе FALSE
	 */
	
	public boolean more(Point p) {
		return ((this.x > p.x) || ((this.x == p.x) && (this.y > p.y)));
	}
	
	/**
	 * Определяет лексикографический порядок отношения, когда считается, что this.p < p,
	 * если this.p.x < p.x, либо this.p.x = p.x и this.p.y < p.y.
	 * 
	 * @param p точка для сравнения
	 * @return  возвращает TRUE, если точка меньше заданной, иначе FALSE
	 */
	
	public boolean less(Point p) {
		return ((this.x < p.x) || ((this.x == p.x) && (this.y < p.y)));
	}
	
	/**
	 * Определение положения текущей точки относительно отрезка прямой, 
	 * заданного двумя точками.
	 * 
	 * @param p0 начальная точка отрезка
	 * @param p1 конечная точка отрезка
	 * @return возвращает значение типа перечисления, указывающее на положение текущей точки
	 */
	
	public Point_position classify(Point p0, Point p1) {
		Point p2 = this; // Текущая точка
		Point a = p1.subtraction(p0); // Вектор по заданным точкам
		Point b = p2.subtraction(p0); // Вектор по текущей точке и начальной точки отрезка
		
		double sa = a.x * b.y - b.x * a.y; // Определяем ориентацию треугольника
		
		if(sa > 0.0)
			return Point_position.LEFT;
		
		if(sa < 0.0)
			return Point_position.RIGHT;
		
		if((a.x * b.x < 0.0) || (a.y * b.y < 0.0))
			return Point_position.BEHIND;
		
		if(a.length() < b.length())
			return Point_position.BEYOND;
		
		if(p0.equalsPoints(p2))
			return Point_position.ORIGIN;
		
		if(p1.equalsPoints(p2))
			return Point_position.DESTINATION;
		
		return Point_position.BETWEEN;
	}
	
	/**
	 * Определение положения текущей точки относительно отрезка прямой, 
	 * заданного ребром, состоящего из двух точек.
	 * 
	 * @param e ребро
	 * @return возвращает значение типа перечисления, указывающее на положение текущей точки
	 */
	
	public Point_position classify(Edge e) {
		return classify(e.org, e.dest);
	}
	
	/**
	 * Возвращает значение полярного угла (угол, образуемый между вектором и полярной осью
	 *  и отсчитываемый в направлении вращения против часовой стрелки).
	 * 
	 * @return значение полярного угла
	 */
	
	public double polarAngle() {
		double theta = Math.atan(this.x / this.y); // В радианах
		theta *= 360 / (2 * 3.1415926); // Перевод в градусы
		
		if((this.x == 0.0) && (this.y == 0.0))
			return -1;
		
		if(this.x == 0.0)
			return ((this.y > 0.0) ? 90 : 270);
		
		if(this.x > 0.0) // 1 и 4 квадранты
			return ((this.y >= 0.0) ? theta : 360 + theta);
		else // 2 и 3 квадранты
			return (180 + theta);
	}
	
	/**
	 * Возвращает длину текущего вектора.
	 * 
	 * @return длина текущего вектора
	 */
	
	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}
	
	/**
	 * Возвращает значение расстояния (со знаком) от текущей точки до ребра.
	 * 
	 * @param e ребро
	 * @return значение расстояния (со знаком) от текущей точки до ребра
	 */
	
	public double distance(Edge e) {
		Edge ab = e;
		ab.flip().rot(); // Поворот ab на 90 градусов против часовой стредки
		
		Point n = (ab.dest.subtraction(ab.org)); // n - вектор, перпендикулярный ребру e
		n = n.multiplication((n.length() * 1.0)); // Нормализация вектора n
		
		Edge f = new Edge(this, this.sum(n)); // Ребро f = n позиционируется на текущей точке
		
		// t = расстоянию со знаком вдоль вектора f до точки, в котрой ребро f пересекает ребро e
		double t = 0;
		f.intersect(e, t);
		
		return t;
	}
}

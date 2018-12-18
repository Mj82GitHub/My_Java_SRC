package Insertion;

import Insertion.Point.Point_position;
import Insertion.Vertex.Rotation;

public class MyInsertionHill implements  CompareFuncImpl<Point> {
	
	private double[] array = new double[] { 100, 300, // Добавляемая точка
			                                300, 150, // Остальное - точки полигона
			                                400, 50,
			                                500, 50,
			                                600, 200,
			                                600, 300,
			                                450, 450,
			                                350, 400,
			                                250, 250};
	// Направление прохождения сканирующей линией полигона
	enum Scaning { LEFT_TO_RIGHT,   // Слева-направо
		           RIGHT_TO_LEFT }; // Справа-налево
	
	private Point[] s = new Point[array.length / 2]; // Массив точек
	private Polygon polygon;
	
	static public Point somePoint;
		
	public MyInsertionHill() {
		insertPoints();
	
		// ПЕРВЫЙ СПОСОБ
//		polygon = insertionHill(s, s.length);
		// ВТОРОЙ СПОСОБ
		polygon = insertionHill2(s, s.length);
		
		printPolygonPoints();
	}
	
	/**
	 * Возвращает текущую оболочку для массива s из n точек.
	 * 
	 * @param s массив точек
	 * @param n кол-во точек из массива для обработки ф-цией
	 * @return текущую оболочку для массива s из n точек
	 */
	
	public Polygon insertionHill(Point[] s, int n) {
		Polygon p = new Polygon();
		p.insert(s[0]);
		
		for(int i = 1; i < n; i++) {
			if(pointToConvexPolygon(s[i], p))
				continue;
			
			somePoint = s[i];
			
			leastVertex(p, this);
			supportingLine(s[i], p, Point_position.LEFT);
			
			Vertex l = p.getVertex();
			
			supportingLine(s[i], p, Point_position.RIGHT);
			p.split(l).delete_polygon();
			p.insert(s[i]);
		}
		
		return p;
	}
	
	/**
	 * Возвращает текущую оболочку для массива pts из n точек.
	 * 
	 * @param pts массив точек
	 * @param n кол-во точек из массива для обработки ф-цией
	 * @return текущую оболочку для массива pts из n точек
	 */
	
	public Polygon insertionHill2(Point[] pts, int n) {
		Point[] s = new Point[n];
		
		for(int i = 0; i < n; i++ )
			s[i] = pts[i];
		
		insertionSort(s, n, Scaning.LEFT_TO_RIGHT);
		
		Polygon p = new Polygon();
		p.insert(new Vertex(s[0]).point());
		
		for(int i = 1; i < n; i++) {
			if(s[i].equalsPoints(s[i - 1]))
				continue;
			
			supportingLine(s[i], p, Point_position.LEFT);
			
			Vertex l = p.getVertex();
			
			supportingLine(s[i], p, Point_position.RIGHT);
			p.split(l).delete_polygon();
			p.insert(new Vertex(s[i]).point());
		}
		
		return p;
	}
	
	/**
	 * Сортирует элементы в массиве от меньшего к большему.
	 * 
	 * @param a массив
	 * @param n кол-во элементов массива для сортировки
	 */
	
	public void insertionSort(Point[] a, int n, Scaning cmp) {
		for(int i = 0; i < n - 1; i++) {
			int min = i;
			
			for(int j = i + 1; j < n; j++) {
				switch(cmp) {
				case LEFT_TO_RIGHT:
					if(CompareFunc.leftToRightCmp(a[j], a[min]) < 0)
						min = j;
					break;
				case RIGHT_TO_LEFT:
					if(CompareFunc.rightToLeftCmp(a[j], a[min]) < 0)
						min = j;
					break;
				}
			}
			
			a = swap(a, i, min);
		}
	}	

	/**
	 * Выполняет взаимную замену значений в массиве вершин для указаных ей двух индексов.
	 * 
	 * @param a массив вершин
	 * @param i первый индекс
	 * @param min второй индекс
	 * @return измененный массив вершин
	 */
	
	public Point[] swap(Point[] a, int i, int min) {
		Point v = a[i];
		
		a[i] = a[min];
		a[min] = v;
		
		return a;
	}
	
	/**
	 * Наименьшую вершину полигона делает текущей.
	 * 
	 * @param p полигон
	 * @param direction направление прохождения сканирующей линией полигона
	 * @return указатель на наименьшую вершину в полигоне
	 */
	
	public Vertex leastVertex(Polygon p,  CompareFuncImpl<Point> cfi) {
		Vertex bestV = p.getVertex();
		
		p.advance(Rotation.CLOCKWISE);
		
		for(int i = 1; i < p.size(); p.advance(Rotation.CLOCKWISE), i++) {
			if(cfi.cmp(p.getVertex().point(), bestV.point()) < 0)
				bestV = p.getVertex();
		}
		
		p.setVertex(bestV);
		
		return bestV;
	}

	/**
	 * Перемещает окно полигона на вершину, которую она находит ( r или l).
	 * См. понятие ближней и дальней цепочек вершин.
	 * 
	 * @param s точка вне полигона
	 * @param p полигон
	 * @param side тип рересечения
	 */
	
	public void supportingLine(Point s, Polygon p, Point_position side) {
		Rotation rotation = (side == Point_position.LEFT) ? Rotation.CLOCKWISE : Rotation.COUNTER_CLOCKWISE;
		
		Vertex a = p.getVertex();
		Vertex b = p.neighbor(rotation);
		Point_position c = b.point().classify(s, a.point());
		
		while((c == side) || (c == Point_position.BEYOND) || (c == Point_position.BETWEEN)) {
			p.advance(rotation);
			a = p.getVertex();
			b = p.neighbor(rotation);
			c = b.point().classify(s, a.point());
		}
	}
	
	private void insertPoints() {
		for(int i = 0, j = 0; i < array.length; i+=2, j++)
			s[j] = new Point(array[i], array[i + 1]);
	}

	/**
	 * Сравнивает две точки и выбирает, какая из них ближе к точку somePoint.
	 * 
	 * @param val_1 первая точка
	 * @param val_2 вторая точка
	 * @return 0 - если вершины равны, 1 - если первая вершина больше второй, 
	 * -1 - если первая вершина меньше второй
	 */
	
	@Override
	public int cmp(Point val_1, Point val_2) {		
		double distA = (somePoint.subtraction(val_1).length());
		double distB = (somePoint.subtraction(val_2).length());
		
		if(distA < distB)
			return -1;
		else if(distA > distB)
			return 1;
		
		return 0;
	}
	
	/**
	 * Обсчитывает точку s и полигон p и возвращает значение TRUE только в том случае,
	 * если точка лежит внутри полигона p (в том числе и на его границе).
	 * 
	 * @param s точка
	 * @param p полигон
	 * @return TRUE, если точка лежит внутри полигона, иначе - FALSE
	 */
	
	public boolean pointToConvexPolygon(Point s, Polygon p) {
		if(p.size() == 1)
			return (s.equalsPoints(p.point()));
		
		if(p.size() == 2) {
			Point_position c = s.classify(p.edge());
			
			return ((c == Point_position.BETWEEN) || (c == Point_position.ORIGIN) || (c == Point_position.DESTINATION));
		}
		
		Vertex org = p.getVertex();
		
		for(int i = 0; i < p.size(); i++, p.advance(Rotation.CLOCKWISE)) {
			if(s.classify(p.edge()) == Point_position.LEFT) {
				p.setVertex(org);
				
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Выводит точки получившегося полигона.
	 */
	
	private void printPolygonPoints() {
		Point p = polygon.getVertex().point();
		
		System.out.println("[ 1 ]: " + p.x + ", "+ p.y);
		
		int count = 2;
		
		while(!polygon.cw().point().equalsPoints(p)) {
			Point tmp = polygon.getVertex().cw().point();
			polygon.setVertex(polygon.getVertex().cw());
			
			System.out.println("[ " + count + " ]: " + tmp.x + ", " + tmp.y);
			
			count++;
		}
	}
	
	public static void main(String[] args) {
		new MyInsertionHill();
	}
}

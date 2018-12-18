/**
 * Класс предназначен для создания прямоугольника. 
 * 
 * @author Mikhail Kushnerov
 */

public class JRect {
	
	final String LIB_LOG = "libLogs"; // Для отладки
	
	int left;
	int top;
	int right;
	int bottom;
	
/**
 * Создает новый прямоугольник. Все координаты равны нулю.
 */
	
	public JRect() {
		left = 0;
		top = 0;
		right = 0;
		bottom = 0;
	}
	
	public JRect(int left, int top) {
		this.left = left;
		this.top = top;
		this.right = left;
		this.bottom = top;
	}

/**
 * Создает новый прямоугольник. Для корректного задания прямоугольника
 * неоходимо чтобы left <= right, top <= bottom.
 * 
 * @param left - Х координата левой стороны прямоугольника
 * @param top - Y координата верхней стороны прямоугольника
 * @param right - Х координата правой стороны прямоугольника
 * @param bottom - Y координата нижней стороны прямоугольника
 */
	
	public JRect(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
/**
 * Возвращает горизонтальный центр прямоугольника.
 */
	
	public final float centerX() {
		return ((float)right - (float)left) / 2.0f;
	}
		
/**
 * Возвращает вертикальный центр прямоугольника.
 */
	
	public final float centerY() {
		return ((float)bottom - (float)top) / 2.0f;
	}
	
/**
 * Возвращает true, если точка с координатой (x, y) находится внутри прямоугольника, 
 * т.е. left <= x < right и top <= y < bottom. Пустой прямоугольник не может содержать 
 * внутри себя точку (x, y).
 * 
 * @param x - X координата точки проверяемой на нахождение в прямоугольнике
 * @param y - Y координата точки проверяемой на нахождение в прямоугольнике
 */
	
	public boolean contains(int x, int y) {
		return (x >= left && x <= right && y >= top && y <= bottom) ? true : false; 
	}
	
/**
 * Возвращает высоту прямоугольника. Если top >= bottom, результат может быть
 * отрицательным.
 */
	
	public final int height() {
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
 * @param left - Х координата левой стороны прямоугольника
 * @param top - Y координата верхней стороны прямоугольника
 * @param right - Х координата правой стороны прямоугольника
 * @param bottom - Y координата нижней стороны прямоугольника
 */
	
	public void set(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
/**
 * Задает координатам прямоугольника новые значения, копируя координаты прямоугольника src.
 * 
 * @param src - прямоугольник, координаты которого копируем
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
	}
	
/**
 * Возвращает стандартное текстовое описание корневого прямоугольника.
 */
	
	@Override
	public String toString() {
		return "[" + left + ", " + top + " - " + right + ", " + bottom + "]";
	}
		
/**
 * Возвращает ширину прямоугольника. Если left >= right, результат может быть
 * отрицательным.
 */
	
	public final int width() {
		return right - left;
	}
}
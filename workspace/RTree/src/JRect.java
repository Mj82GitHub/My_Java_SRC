/**
 * ����� ������������ ��� �������� ��������������. 
 * 
 * @author Mikhail Kushnerov
 */

public class JRect {
	
	final String LIB_LOG = "libLogs"; // ��� �������
	
	int left;
	int top;
	int right;
	int bottom;
	
/**
 * ������� ����� �������������. ��� ���������� ����� ����.
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
 * ������� ����� �������������. ��� ����������� ������� ��������������
 * ��������� ����� left <= right, top <= bottom.
 * 
 * @param left - � ���������� ����� ������� ��������������
 * @param top - Y ���������� ������� ������� ��������������
 * @param right - � ���������� ������ ������� ��������������
 * @param bottom - Y ���������� ������ ������� ��������������
 */
	
	public JRect(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
/**
 * ���������� �������������� ����� ��������������.
 */
	
	public final float centerX() {
		return ((float)right - (float)left) / 2.0f;
	}
		
/**
 * ���������� ������������ ����� ��������������.
 */
	
	public final float centerY() {
		return ((float)bottom - (float)top) / 2.0f;
	}
	
/**
 * ���������� true, ���� ����� � ����������� (x, y) ��������� ������ ��������������, 
 * �.�. left <= x < right � top <= y < bottom. ������ ������������� �� ����� ��������� 
 * ������ ���� ����� (x, y).
 * 
 * @param x - X ���������� ����� ����������� �� ���������� � ��������������
 * @param y - Y ���������� ����� ����������� �� ���������� � ��������������
 */
	
	public boolean contains(int x, int y) {
		return (x >= left && x <= right && y >= top && y <= bottom) ? true : false; 
	}
	
/**
 * ���������� ������ ��������������. ���� top >= bottom, ��������� ����� ����
 * �������������.
 */
	
	public final int height() {
		return bottom - top;
	}
	
/**
 * ���������� true, ���� ������������� ������ (left >= right ��� top >= bottom).
 */
	
	public final boolean isEmpty() {
		return (left >= right || top >= bottom) ? true : false;
	}

/**
 * ������ ����������� �������������� ����� ��������.
 * 
 * @param left - � ���������� ����� ������� ��������������
 * @param top - Y ���������� ������� ������� ��������������
 * @param right - � ���������� ������ ������� ��������������
 * @param bottom - Y ���������� ������ ������� ��������������
 */
	
	public void set(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
/**
 * ������ ����������� �������������� ����� ��������, ������� ���������� �������������� src.
 * 
 * @param src - �������������, ���������� �������� ��������
 */
	
	public void set(JRect src) {
		this.set(src.left, src.top, src.right, src.bottom);
	}
	
/**
 * ������������� ��� ���������� �������������� ������� ����.
 */
	
	public void setEmpty() {
		this.left = 0;
		this.top = 0;
		this.right = 0;
		this.bottom = 0;
	}
	
/**
 * ���������� ����������� ��������� �������� ��������� ��������������.
 */
	
	@Override
	public String toString() {
		return "[" + left + ", " + top + " - " + right + ", " + bottom + "]";
	}
		
/**
 * ���������� ������ ��������������. ���� left >= right, ��������� ����� ����
 * �������������.
 */
	
	public final int width() {
		return right - left;
	}
}
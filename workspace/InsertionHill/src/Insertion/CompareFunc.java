package Insertion;

public class CompareFunc {
	
	/**
	 * ���������� ��� ������� (�� ����������).
	 * 
	 * @param v ������ �������
	 * @param w ������ �������
	 * @return 0 - ���� ������� �����, 1 - ���� ������ ������� ������ ������, 
	 * -1 - ���� ������ ������� ������ ������
	 */
	
	static int cmp(Point v, Point w) {
		if(v.less(w))
			return -1;
		else if(v.more(w))
			return 1;
		else
			return 0;
	}
	
	/**
	 * ���������� ��� ������� (�� ����������). ��������� �����������
	 * ��� X, ������� �������� ������.
	 * 
	 * @param v ������ �������
	 * @param w ������ �������
	 * @return 0 - ���� ������� �����, -1 - ���� ������ ������� ������ ������, 
	 * 1 - ���� ������ ������� ������ ������
	 */
	
	static int rightToLeftCmp(Point v, Point w) {
		int result = cmp(v, w);
		
		if(result == -1)
			result = 1;
		else if(result == 1)
			result = -1;
		
		return result;
	}
	
	/**
	 * ���������� ��� ������� (�� ����������). ��������� �����������
	 * ��� X, ������� �������� �����.
	 * 
	 * @param v ������ �������
	 * @param w ������ �������
	 * @return 0 - ���� ������� �����, 1 - ���� ������ ������� ������ ������, 
	 * -1 - ���� ������ ������� ������ ������
	 */
	
	static int leftToRightCmp(Point v, Point w) {
		return cmp(v, w);
	}
}

/*
 * Copyright (c) 09.2017
 */

package Triangulation;

/**
 * ����� �������� �������, ����������� ��������� ������ ������������ ��� X,
 * � ����� � ����������� �� �����������, � �������� ���������� ��������� (������ ��� �����).
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class CompareFunc {

	/**
	 * ���������� ��� ������� (�� ����������).
	 * 
	 * @param v ������ �������
	 * @param w ������ �������
	 * @return 0 - ���� ������� �����, 1 - ���� ������ ������� ������ ������, 
	 * -1 - ���� ������ ������� ������ ������
	 */
	
	static int cmp(Mj_Vertex v, Mj_Vertex w) {
		if(v.point().less(w.point()))
			return -1;
		else if(v.point().more(w.point()))
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
	
	static int rightToLeftCmp(Mj_Vertex v, Mj_Vertex w) {
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
	
	static int leftToRightCmp(Mj_Vertex v, Mj_Vertex w) {
		return cmp(v, w);
	}
}

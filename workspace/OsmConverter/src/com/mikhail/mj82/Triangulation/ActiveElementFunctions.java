/*
 * Copyright (c) 09.2017
 */

package com.mikhail.mj82.Triangulation;

/**
 * ��������� � ������ ������ ActiveElement
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public interface ActiveElementFunctions {

	/**
	 * ���������� ���������� Y ����� ����������� �������� �����
	 * �� ����������� ������.
	 * 
	 * @return ���������� Y ����� ����������� �������� �����
	 * �� ����������� ������
	 */
	
	default double getY() {
		return 0.0;
	}
	
	/**
	 * ���������� ������� �����.
	 * 
	 * @return ������� �����
	 */
	
	default Edge edge() {
		return new Edge();
	}
	
	/**
	 * ���������� ������ �������� �����.
	 * 
	 * @return ������ �������� �����
	 */
	
	default double slope() {
		return 0.0;
	}
}

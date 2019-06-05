/*
 * Copyright (c) 12.2016
 */

package com.mikhail.mj82.nvg.Converter;

/**
 * ��������� ��� ������� � ������ �� ����� �������� �� �����.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public interface Types {

	/**
	 * ���������� ��� ������� �� ����� � ���� ������������� ����.
	 * 
	 * @param k ��� ������� �� �����
	 * @param v ������ ���� ������� �� �����
	 * @return ��� ������� �� ����� � ���� ������������� ����
	 */
	
	public short getTypeOfObject(String k, String v);
	
	/**
	 * ���������� ��� ������� �� ����� � ���� ��������� ������.
	 * 
	 * @param v ������ ���� ������� �� �����
	 * @return ��� ������� �� ����� � ���� ��������� ������
	 */
	
	public String getTypeOfObject(short v);
}

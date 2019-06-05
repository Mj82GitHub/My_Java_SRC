/*
 * Copyright (c) 09.2017
 */

package com.mikhail.mj82.nvg.Triangulation;

/**
 * ����� 
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class ActiveElement implements ActiveElementFunctions{
	
	// ��� ��������� ����������� �����
	enum Active_element_type { ACTIVE_POINT, // �����
		                       ACTIVE_EDGE }; // �����

	public Active_element_type type; // ACTIVE_EDGE (�����) ��� ACTIVE_POINT (�����)
	
	public ActiveElement(Active_element_type type) {
		this.type = type;
	}	
}

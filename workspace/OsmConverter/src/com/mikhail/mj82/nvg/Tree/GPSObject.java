/*
 * Copyright (c) 08.2016
 */

package com.mikhail.mj82.nvg.Tree;

import java.io.Serializable;

import com.mikhail.mj82.nvg.Geom.JRect;

/**
 * ����� ����� � ����� ������. �������� �������� �������� (���������) ����� � ����� �����,
 * � ����� �������� ������ � ������� ���� �����.
 *
 * @author Mikhail Kushnerov (mj82)
 */

public class GPSObject implements Serializable {

	private static final long serialVersionUID = -4980313183782655160L;
	
	private long seek; // �������� � ����� �����
	
	/*
	�������������� ������ �������������. � ������ ������ ������ � ��� - ��� ����� �� ����� �
	������������ ������ � �������, ������� � �������������� ������ ���������� ������ ��������
	������ ����, � ������ � ������ �������������� ����� ����.
	 */
	
	private JRect mbr;
	
	public GPSObject() {
		seek = 0;
		mbr = new JRect();
	}
	
	public GPSObject(long seek, JRect mbr) {
		this.seek = seek;
		this.mbr = mbr;
	}
	
	/**
	 * ���������� �������� ����� � ����� �����.
	 * 
	 * @return �������� � �����
	 */
	
	public long getSeek() {
		return seek;
	}
	
	/**
	 * ������ �������� ����� � ����� �����.
	 * 
	 * @param seek �������� � �����
	 */
	
	public void setSeek(long seek) {
		this.seek = seek;
	}
	
	/**
	 * ���������� �������������� �������������.
	 * 
	 * @return �������������� �������������
	 */
	
	public JRect getMbr() {
		return mbr;
	}
		
	/**
	 * ������ ����� �������������� �������������.
	 * 
	 * @param mbr �������������� �������������
	 */
	
	public void setMbr(JRect mbr) {
		this.mbr.left = mbr.left;
		this.mbr.top = mbr.top;
		this.mbr.right = mbr.right;
		this.mbr.bottom = mbr.bottom;
	}
	
	/**
	 * ���������� �������.
	 * 
	 * @param obj ������������ ������
	 * @return TRUE, ���� ������� �����, ����� - FALSE
	 */
	public boolean compareObject(GPSObject obj) {
		return (seek == obj.getSeek() && 
				mbr.left == obj.getMbr().left &&
				mbr.top == obj.getMbr().top &&
				mbr.right == obj.getMbr().right &&
				mbr.bottom == obj.getMbr().bottom);
	}
}

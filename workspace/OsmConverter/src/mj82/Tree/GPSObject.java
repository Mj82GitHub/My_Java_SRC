/*
 * Copyright (c) 08.2016
 */

package mj82.Tree;

import java.io.Serializable;

import mj82.Geom.JRect;

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
}

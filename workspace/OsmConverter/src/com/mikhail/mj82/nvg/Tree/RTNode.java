/*
 * Copyright (c) 08.2016
 */

package com.mikhail.mj82.nvg.Tree;

import java.io.Serializable;

import com.mikhail.mj82.nvg.Geom.JRect;

/**
 * ����� ���� ������.
 *
 * @author Mikhail Kushnerov (mj82)
 */

public class RTNode implements Serializable {
	
	private static final long serialVersionUID = -3465536469559896588L;

	public final int M = 16; // ������������ ���-�� �������� ��������� � ����
	
	private JRect MBR; // �������������� ������������� ����
	// ������ � ������� ����� ������, ����������� �� ����-��������
	private int parent;
	// ������ �������� �������� ����� � ������� ����� ������
	private int[] children;
	// ������ � ������� � ������ (������ �������� ������).
	private GPSObject[] objects; 
	private boolean isLeaf; // �������� ������������ �������� �� ���� ���� �������� (������)
	private int level; // ������� ���� � ������ (0 = ����).
	
	public RTNode() {
		parent = -10;
		children = new int[0];
		objects = new GPSObject[0];
		MBR = new JRect();
	}
	
	public RTNode(RTNode node) {
		parent = -10;
		copy(node);
	}

	/**
	 * �������� ����.
	 *
	 * @param node ����, ����� �������� ������
     */

	public void copy(RTNode node) {		
		objects = new GPSObject[node.getObjects().length];
		children = new int[node.getChildren().length];
		
		if(objects.length > 0) {
			for(int i = 0; i < node.getObjects().length; i++) {
				
				objects[i] = new GPSObject();
				objects[i].setSeek(node.getObject(i).getSeek());
				objects[i].setMbr(node.getObject(i).getMbr());
			}
			
			isLeaf = true;
		} else {
			for(int i = 0; i < node.getChildren().length; i++) {
				children[i] = node.getChildren()[i];
			}
			
			isLeaf = false;
		}
		
		MBR.set(node.getMbr());
		parent = node.getParent();
		level = node.getLevel();
	}

	/**
	 * ������� ������ ������� ������ (�������� ������).
	 */

	public void clearObjects() {
		objects = new GPSObject[0];
	}

	/**
	 * ������� ������ �������� ����� ������.
	 */

	public void clearChildren() {
		children = new int[0];
	}

	/**
	 * ���������� �������������� ������������� ����.
	 *
	 * @return �������������� ������������� ����
     */

	public JRect getMbr() {
		return MBR;
	}

	/**
	 * ������ �������������� ������������� ����.
	 *
	 * @param mbr �������������� ������������� ����
     */

	public void setMbr(JRect mbr) {
		this.MBR.left = mbr.left;
		this.MBR.top = mbr.top;
		this.MBR.right = mbr.right;
		this.MBR.bottom = mbr.bottom;
	}

	/**
	 * ���������� ��������, ����������� �������� �� ���� � ������ �������� (������).
	 *
	 * @return ���� true - ���� �������� ������ ������, ����� - false
     */

	public boolean isLeaf() {
		return isLeaf;
	}

	/**
	 *������������� ��������, ����������� �������� �� ���� � ������ �������� (������).
	 *
	 * @param isLeaf ���� true - ���� �������� ������ ������, ����� - false
     */

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	/**
	 * ���������� ������ �������� �������� ����� � ������� ����� ������.
	 *
	 * @return ������ �������� �������� ����� � ������� ����� ������
     */

	public int[] getChildren() {
		return children;
	}

	/**
	 * ���������� ������ ���� �� ������� �������� �������� ����� � ������� ����� ������.
	 *
	 * @param index ������ � ������� �������� �������� ����� � ������� ����� ������
	 * @return ������ ���� �� ������� �������� �������� ����� � ������� ����� ������
     */

	public int getChild(int index) {
			return children[index];
	}

	/**
	 * ��������� ������ ���� � ������ �������� �������� �����  � ������� ����� ������.
	 *
	 * @param index ������ � ������� �������� �������� ����� � ������� ����� ������
	 * @param nodeId ������������� ����
     */

	public void setChild(int index, int nodeId) {
		if(children.length > index && children.length != 0) {
			children[index] = nodeId;
			isLeaf = false;
		} else {
			if(index >= 0 && index < M) {
				int[] tmp = children;
				int size = children.length; 
				
				children = new int[index + 1];
			    
				if(tmp.length != 0)
				   	System.arraycopy(tmp, 0, children, 0, size);
				    
				children[children.length - 1] = nodeId;
				isLeaf = false;
			}
		}
	}

	/**
	 * ���������� ������ � �������� ������ (� ��������� ������).
	 *
	 * @return ������ � �������� ������ (� ��������� ������)
     */

	public GPSObject[] getObjects() {
		return objects;
	}

	/**
	 * ���������� ���� (������) �� ������� ������� (��������).
	 *
	 * @param index ������ � ������� ������� (��������)
	 * @return ���� (������) �� ������� ������� (��������)
     */

	public GPSObject getObject(int index) {
			return objects[index];
	}

	/**
	 * ��������� ���� (������) � ������ ������� (��������).
	 *
	 * @param index ������ � ������� ������� (��������)
	 * @param obj ���� (������) �� ������� ������� (��������)
     */

	public void setObject(int index, GPSObject obj) {
		if(objects.length > index && objects.length != 0) {
			objects[index] = obj;
			isLeaf = true;
		} else {
			if(index >= 0 && index < M) {
				GPSObject[] tmp = objects;
				int size = objects.length; 
				
				objects = new GPSObject[index + 1];
			    
				if(tmp.length != 0)
				   	System.arraycopy(tmp, 0, objects, 0, size);
				    
				objects[objects.length - 1] = obj;
				isLeaf = true;
			}
		}
	}
	
	/**
	 * �������� ������ �������� ����� ��������
	 * @param objs ����� ������ ��������
	 */
	public void setNewObjects(GPSObject[] objs) {
		objects = objs;
	}

	/**
	 * ���������� ������ � ������� ����� ������, ����������� �� ����-��������.
	 *
	 * @return ������ � ������� ����� ������, ����������� �� ����-��������
     */

	public int getParent() {
		return parent;
	}

	/**
	 * ������������� ������ � ������� ����� ������, ����������� �� ����-��������.
	 *
	 * @param parentId ������ � ������� ����� ������, ����������� �� ����-��������
     */

	public void setParent(int parentId) {
		if(parentId >= 0)
			parent = parentId;
	}

	/**
	 * ���������� ������� ���� � ������ (0 = ����).
	 *
	 * @return ������� ���� � ������
     */

	public int getLevel() {
		return level;
	}

	/**
	 * ������������� ������� ���� � ������ (0 = ����).
	 *
	 * @param level ������� ���� � ������
     */

	public void setLevel(int level) {
		if(level >= 0)
			this.level = level;
	}

	/**
	 * ���������� ������������ �� ��� ������� (��������������).
	 *
	 * @param mbr1 ������������� 1
	 * @param mbr2 ������������� 2
     * @return ���� true - �� �������������� ������������ ���� � ������, ����� - false
     */

	public boolean isIntersected(JRect mbr1, JRect mbr2) {
		if(mbr1.left <= mbr2.right && mbr1.top <= mbr2.bottom) {
			if(mbr1.right >= mbr2.left && mbr1.bottom >= mbr2.top)
				return true;
		}
		
		return false;
	}

	/**
	 * ���������� ������������ �� �������������� ������������� ����� ���� (MBR) �
	 * �������� ��������������� (mbr).
	 *
	 * @param mbr �������� �������������
	 * @return ���� true - �� �������������� ������������ ���� � ������, ����� - false
     */

	public boolean isIntersected(JRect mbr) {
		if(MBR.left <= mbr.right && MBR.top <= mbr.bottom) {
			if(MBR.right >= mbr.left && MBR.bottom >= mbr.top)
				return true;
		}
		
		return false;
	}

	/**
	 * ���������� �������� ������� ����������� ��������������� �������������� ����� ���� (MBR)
	 * � �������� �������� (��������������� mbr_ovrl).
	 *
	 * @param mbr_ovrl �������� �������������
	 * @return �������� ������� ����������� ��������������� �������������� ����� ���� (MBR)
	 * � �������� �������� (��������������� mbr_ovrl)
     */
	public double Overlap(JRect mbr_ovrl) {
		double x;
		double y;
		
		x = Math.min(mbr_ovrl.right, MBR.right) - Math.max(mbr_ovrl.left, MBR.left);
		
		if(x <= 0) 
			return 0;
		
		y = Math.min(mbr_ovrl.bottom, MBR.bottom) - Math.max(mbr_ovrl.top, MBR.top);
		
		if(y <= 0) 
			return 0;
		
		return x * y;
	}

	/**
	 * ���������� ������� ��������������� ���� ���� �������������� (MBR).
	 *
	 * @return ������� ��������������� ���� ���� �������������� (MBR)
     */

	public double area() {
		return (MBR.right - MBR.left) * (MBR.bottom - MBR.top);
	}

	/**
	 * ���������� ������� ��������� ��������������� �������������� (mbr).
	 *
	 * @param mbr �������� �������������
	 * @return ������� ��������� ��������������� �������������� (mbr)
     */

	public double area(JRect mbr) {
		return (mbr.right - mbr.left) * (mbr.bottom - mbr.top);
	}

	/**
	 * ���������� �������� ��������������� ���� ���� �������������� (MBR).
	 *
	 * @return �������� ��������������� ���� ���� �������������� (MBR)
     */

	public double margin() {
		return ((MBR.right - MBR.left) + (MBR.bottom - MBR.top)) * 2;
	}
}

/*
 * Copyright (c) 04.2018
 */

package com.mikhail.mj82.Triangulation;

import java.util.ArrayList;

/**
 * ����� ��� �������� ������ ��������, ��������� �� ���� ������,
 * ���� �� ��������� ��������� �� ������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class VertexHashMap {
	
	private ArrayList<MapElement> map; // 
	
	public VertexHashMap() {
		map = new ArrayList<>(0);
	}

	/**
	 * ������� � ������ ������� ��������, ���� �� ������� �������� ������ � ������.
	 * 
	 * @param key ����
	 * @param value ��������
	 */
	
	public void put(Mj_Vertex key, Mj_Vertex value) {
		map.add(new MapElement(key, value));
	}
	
	/**
	 * ���������� �� ������ ������� �������� �� �������� �������, ����������
	 * ������ � ���.
	 * 
	 * @param key ����
	 * @return ������� �������� �� �������� �������
	 */
	
	public Mj_Vertex get(Mj_Vertex key) {
		for(int i = 0; i < map.size(); i++) {
			if(key.equalsCoordsVertex(map.get(i).getKey(key)))
				return map.get(i).getValue(key);
		}
		
		return null;
	}
}

/**
 * ����� ��� �������� ���� ������ ��������, ���� �� ������� �������� 
 * ���������� �� ������, �.�. �� �������� ��������� ������� key ����� 
 * ������� ���������� ������� value.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

class MapElement {
	
	private Mj_Vertex key; // ����
	private Mj_Vertex value; // ��������, �� ������� ��������� ����
	
	MapElement() {
		key = null;
		value = null;
	}
	
	MapElement(Mj_Vertex key, Mj_Vertex value) {
		this.key = key;
		this.value = value;
	}
	
	/**
	 * ������������� ����� �������� ����� � ��������.
	 * 
	 * @param key ����
	 * @param value ��������
	 */
	
	void set(Mj_Vertex key, Mj_Vertex value) {
		this.key = key;
		this.value = value;
	}
	
	/**
	 * ���������� �������� ����� (�������, ���������������
	 * �������� �������).
	 * 
	 * @return ���� �� �������� �������, ���� ������ ����� ��� ���������� NULL
	 */
	
	Mj_Vertex getKey(Mj_Vertex v) {
		if(v.equalsCoordsVertex(key))
			return key;
		
		return null;
	}
	
	/**
	 * ���������� �������� �� ��������� ����� (�������, ���������������
	 * �������� �������).
	 * 
	 * @return ������� �� ��������� �����, ���� ������ �������� ���, ���������� NULL
	 */
	
	Mj_Vertex getValue(Mj_Vertex key) {
		if(this.key.equalsCoordsVertex(key))
			return value;
		
		return null;
	}
}

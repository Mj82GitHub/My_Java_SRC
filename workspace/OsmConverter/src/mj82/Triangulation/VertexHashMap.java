/*
 * Copyright (c) 04.2018
 */

package mj82.Triangulation;

import java.util.ArrayList;

/**
 * Класс для хранения списка объектов, состоящих из двух вершин,
 * одна из которовых указывает на другую.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class VertexHashMap {
	
	private ArrayList<MapElement> map; // 
	
	public VertexHashMap() {
		map = new ArrayList<>(0);
	}

	/**
	 * Заносит в список вершины полигона, одна из которых является ключом к другой.
	 * 
	 * @param key ключ
	 * @param value значение
	 */
	
	public void put(Mj_Vertex key, Mj_Vertex value) {
		map.add(new MapElement(key, value));
	}
	
	/**
	 * Возвращает из списка вершину полигона по заданной вершине, являющейся
	 * ключом к ней.
	 * 
	 * @param key ключ
	 * @return вершину полигона по заданной вершине
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
 * Класс для хранения двух вершин полигона, одна их которых является 
 * указателем на другую, т.е. по значению координат вершины key можно 
 * вернуть координаты вершины value.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

class MapElement {
	
	private Mj_Vertex key; // Ключ
	private Mj_Vertex value; // Значение, на которое указывает ключ
	
	MapElement() {
		key = null;
		value = null;
	}
	
	MapElement(Mj_Vertex key, Mj_Vertex value) {
		this.key = key;
		this.value = value;
	}
	
	/**
	 * Устанавливает новые значения ключа и значения.
	 * 
	 * @param key ключ
	 * @param value значение
	 */
	
	void set(Mj_Vertex key, Mj_Vertex value) {
		this.key = key;
		this.value = value;
	}
	
	/**
	 * Возвращает значение ключа (вершину, соответствующую
	 * заданной вершине).
	 * 
	 * @return ключ по заданной вершине, если такого ключа нет возвращает NULL
	 */
	
	Mj_Vertex getKey(Mj_Vertex v) {
		if(v.equalsCoordsVertex(key))
			return key;
		
		return null;
	}
	
	/**
	 * Возвращает значение по заданному ключу (вершину, соответствующую
	 * заданной вершине).
	 * 
	 * @return начение по заданному ключу, если такого значения нет, возвращает NULL
	 */
	
	Mj_Vertex getValue(Mj_Vertex key) {
		if(this.key.equalsCoordsVertex(key))
			return value;
		
		return null;
	}
}

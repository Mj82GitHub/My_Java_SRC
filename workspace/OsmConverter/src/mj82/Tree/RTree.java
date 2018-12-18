/*
 * Copyright (c) 08.2016
 */

package mj82.Tree;

import java.io.Serializable;
import java.util.ArrayList;

import mj82.Geom.JRect;

/**
 * Класс дерева поиска.
 *
 * @author Mikhail Kushnerov (mj82)
 */

public class RTree implements Serializable {

	private static final long serialVersionUID = -5444319452261480749L;
	
	public final int M = 16; // Максимальное кол-во объектов в узле
	public final int m = (int)(M * 0.4); // Минимальное кол-во объектов в узле
	// Оси для сортировки
	private final int X = 0;
	private final int Y = 1;
	// Границы для сортировки
	private final int Left = 0;
	private final int Right = 1;

	private RTNode[] nodes; // Массив узлов дерева
	private int root; // Ссылка на положение корневого узла в массиве узлов (в дереве)
	private int height; // Высота дерева
	
	public RTree() {
		nodes = new RTNode[1];
		nodes[0] = new RTNode();
		root = 0;
		nodes[root].setLeaf(true);
	}

	/**
	 * Возвращает индекс корня в массиве узлов дерева.
	 *
	 * @return индекс корня в массиве узлов дерева
     */

	public int getRoot() {
		return root;
	}

	/**
	 * Возвращает массив узлов дерева.
	 *
	 * @return массив узлов дерева
     */

	public RTNode[] getNodes() {
		return nodes;
	}
	
	public int getGPSObjectsSize() {
		int size = 0;
		
		for(int i = 0; i < nodes.length; i++) {
			size += nodes[i].getObjects().length;
		}
		
		return size;
	}

	/**
	 * Возвращает высоту дерева.
	 *
	 * @return высота дерева
     */

	public int getHeight() {
		return height;
	}

	/**
	 * Устанавливает высоту дерева.
	 *
	 * @param height значение высоты дерева
     */

	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Определяет является ли корнем узел с заданным индексом.
	 *
	 * @param nodeId индекс узла, который проверяем
	 * @return еслт true - узел корневой, иначе - false
     */

	public boolean isRoot(int nodeId) {
		if(nodeId == root)
			return true;
		else
			return false;
	}

	/**
	 * Создает новый узел дерева.
	 *
	 * @return индекс созданого узла дерева
     */

	public int newNode() {
		RTNode[] tmp = nodes;
		int size = nodes.length;
		
		nodes = new RTNode[size + 1];
		System.arraycopy(tmp, 0, nodes, 0, size);
		nodes[nodes.length - 1] = new RTNode();
		
		return nodes.length - 1;
	}

	/**
	 * Обновляет ограничивающий прямоугольник узла дерева.
	 *
	 * @param nodeId идентификатор узла дерева, ограничивающий прямоугольник которого обновляем
     */

	public void updateMBR(int nodeId) {
		int id;
		boolean changed = false;
		
		nodes[nodeId].getMbr().set(9999, 9999, 0, 0);
		
		if(nodes[nodeId].isLeaf()) {
			for(int i = 0; i < nodes[nodeId].getObjects().length; i++) {
				if(nodes[nodeId].getObject(i).getMbr().left < nodes[nodeId].getMbr().left) {
					nodes[nodeId].getMbr().left = nodes[nodeId].getObject(i).getMbr().left;
					changed = true;
				}
				
				if(nodes[nodeId].getObject(i).getMbr().top < nodes[nodeId].getMbr().top) {
					nodes[nodeId].getMbr().top = nodes[nodeId].getObject(i).getMbr().top;
					changed = true;
				}
				
				if(nodes[nodeId].getObject(i).getMbr().right > nodes[nodeId].getMbr().right) {
					nodes[nodeId].getMbr().right = nodes[nodeId].getObject(i).getMbr().right;
					changed = true;
				}
				
				if(nodes[nodeId].getObject(i).getMbr().bottom > nodes[nodeId].getMbr().bottom) {
					nodes[nodeId].getMbr().bottom = nodes[nodeId].getObject(i).getMbr().bottom;
					changed = true;
				}
			}
		} else {
			for(int i = 0; i < nodes[nodeId].getChildren().length; i++) {
				id = nodes[nodeId].getChild(i);
				
				if(nodes[id].getMbr().left < nodes[nodeId].getMbr().left) {
					nodes[nodeId].getMbr().left = nodes[id].getMbr().left;
					changed = true;
				}
				
				if(nodes[id].getMbr().top < nodes[nodeId].getMbr().top) {
					nodes[nodeId].getMbr().top = nodes[id].getMbr().top;
					changed = true;
				}
				
				if(nodes[id].getMbr().right > nodes[nodeId].getMbr().right) {
					nodes[nodeId].getMbr().right = nodes[id].getMbr().right;
					changed = true;
				}
				
				if(nodes[id].getMbr().bottom > nodes[nodeId].getMbr().bottom) {
					nodes[nodeId].getMbr().bottom = nodes[id].getMbr().bottom;
					changed = true;
				}
			}
		}
		
		if(changed) {
			if(nodes[nodeId].getParent() >= 0) {
				updateMBR(nodes[nodeId].getParent());
			}
		}
	}

	/**
	 * Обновляет ограничивающий прямоугольник узла дерева.
	 *
	 * @param node узел дерева, ограничивающий прямоугольник которого обновляем
     */

	public void updateMBR(RTNode node) {
		int id;
		boolean changed = false;
		
		node.getMbr().set(9999, 9999, 0, 0);
		
		if(node.isLeaf()) {
			for(int i = 0; i < node.getObjects().length; i++) {
				if(node.getObject(i).getMbr().left < node.getMbr().left) {
					node.getMbr().left = node.getObject(i).getMbr().left;
					changed = true;
				}
				
				if(node.getObject(i).getMbr().top < node.getMbr().top) {
					node.getMbr().top = node.getObject(i).getMbr().top;
					changed = true;
				}
				
				if(node.getObject(i).getMbr().right > node.getMbr().right) {
					node.getMbr().right = node.getObject(i).getMbr().right;
					changed = true;
				}
				
				if(node.getObject(i).getMbr().bottom > node.getMbr().bottom) {
					node.getMbr().bottom = node.getObject(i).getMbr().bottom;
					changed = true;
				}
			}
		} else {
			for(int i = 0; i < node.getChildren().length; i++) {
				id = node.getChild(i);
				
				if(nodes[id].getMbr().left < node.getMbr().left) {
					node.getMbr().left = nodes[id].getMbr().left;
					changed = true;
				}
				
				if(nodes[id].getMbr().top < node.getMbr().top) {
					node.getMbr().top = nodes[id].getMbr().top;
					changed = true;
				}
				
				if(nodes[id].getMbr().right > node.getMbr().right) {
					node.getMbr().right = nodes[id].getMbr().right;
					changed = true;
				}
				
				if(nodes[id].getMbr().bottom > node.getMbr().bottom) {
					node.getMbr().bottom = nodes[id].getMbr().bottom;
					changed = true;
				}
			}
		}
		
		if(changed) {
			if(node.getParent() >= 0) {
				updateMBR(node.getParent());
			}
		}
	}

	/**
	 * Быстрая сортировка для объектов дерева по их ограничивающим прямоугольникам.
	 *
	 * @param list массив объектов для сортировки
	 * @param LO минимальный индекс в массиве объектов с которого начинается сортировка
	 * @param HI максимальный индекс в массиве объектов за которым сортировка не происходит
	 * @param axe ось, по которой происходит сортировка
     * @param bound граница, по которой происходит сортировка
     */

	public void quickSortObj(GPSObject[] list, int LO, int HI, int axe, int bound) {
		int lo = LO;
		int hi = HI;
		GPSObject obj;
		double mid = -1;
		
		switch(bound) {
		case Left:
			switch(axe) {
			case X:
				mid = list[(lo + hi) / 2].getMbr().left;
				break;
			case Y:
				mid = list[(lo + hi) / 2].getMbr().top;
				break;
			}
			break;
		case Right:
			switch(axe) {
			case X:
				mid = list[(lo + hi) / 2].getMbr().right;
				break;
			case Y:
				mid = list[(lo + hi) / 2].getMbr().bottom;
				break;
			}
			break;
		}
		
		do {
			switch(bound) {
			case Left:
				switch(axe) {
				case X:
					while(list[lo].getMbr().left < mid) {
						lo++;
					}
					
					while(list[hi].getMbr().left > mid) {
						hi--;
					}
					break;
				case Y:
					while(list[lo].getMbr().top < mid) {
						lo++;
					}
					
					while(list[hi].getMbr().top > mid) {
						hi--;
					}
					break;
				}
				break;
			case Right:
				switch(axe) {
				case X:
					while(list[lo].getMbr().right < mid) {
						lo++;
					}
					
					while(list[hi].getMbr().right > mid) {
						hi--;
					}
					break;
				case Y:
					while(list[lo].getMbr().bottom < mid) {
						lo++;
					}
					
					while(list[hi].getMbr().bottom > mid) {
						hi--;
					}
					break;
				}
				break;
			}
			
			if(lo <= hi) {
				obj = list[lo];
				
				list[lo] = list[hi];
				list[hi] = obj;
				lo++;
				hi--;
			}
		} while(!(lo > hi));
		
		if(hi > LO) {
			quickSortObj(list, LO, hi, axe, bound);
		}
		
		if(lo < HI) {
			quickSortObj(list, lo, HI, axe, bound);
		}
	}

	/**
	 * Быстрая сортировка для узлов дерева по их ограничивающим прямоугольникам.
	 *
	 * @param list массив узлов для сортировки
	 * @param LO минимальный индекс в массиве узлов с которого начинается сортировка
	 * @param HI максимальный индекс в массиве узлов за которым сортировка не происходит
	 * @param axe ось, по которой происходит сортировка
	 * @param bound граница, по которой происходит сортировка
	 */

	public void quickSortNd(int[] list, int LO, int HI, int axe, int bound) {
		int lo = LO;
		int hi = HI;
		int obj;
		double mid = -1;
		
		switch(bound) {
		case Left:
			switch(axe) {
			case X:
				mid = nodes[list[(lo + hi) / 2]].getMbr().left;
				break;
			case Y:
				mid = nodes[list[(lo + hi) / 2]].getMbr().top;
				break;
			}
			break;
		case Right:
			switch(axe) {
			case X:
				mid = nodes[list[(lo + hi) / 2]].getMbr().right;
				break;
			case Y:
				mid = nodes[list[(lo + hi) / 2]].getMbr().bottom;
				break;
			}
			break;
		}
		
		do {
			switch(bound) {
			case Left:
				switch(axe) {
				case X:
					while(nodes[list[lo]].getMbr().left < mid) {
						lo++;
					}
					
					while(nodes[list[hi]].getMbr().left > mid) {
						hi--;
					}
					break;
				case Y:
					while(nodes[list[lo]].getMbr().top < mid) {
						lo++;
					}
					
					while(nodes[list[hi]].getMbr().top > mid) {
						hi--;
					}
					break;
				}
				break;
			case Right:
				switch(axe) {
				case X:
					while(nodes[list[lo]].getMbr().right < mid) {
						lo++;
					}
					
					while(nodes[list[hi]].getMbr().right > mid) {
						hi--;
					}
					break;
				case Y:
					while(nodes[list[lo]].getMbr().bottom < mid) {
						lo++;
					}
					
					while(nodes[list[hi]].getMbr().bottom > mid) {
						hi--;
					}
					break;
				}
				break;
			}
			
			if(lo <= hi) {
				obj = list[lo];
				
				list[lo] = list[hi];
				list[hi] = obj;
				lo++;
				hi--;
			}
		} while(!(lo > hi));
		
		if(hi > LO) {
			quickSortNd(list, LO, hi, axe, bound);
		}
		
		if(lo < HI) {
			quickSortNd(list, lo, HI, axe, bound);
		}
	}

	/**
	 * Определяет ось, по которой будет происходить деление узла  при переполнении его объектами
	 * (листьями) (в соответствии с алгоритмами R*-Tree).
	 *
	 * @param obj лист дерева (объект дерева)
	 * @param nodeId идентификатор листа дерева (объекта дерева)
     * @return ось, по которой будет происходить деление узла
     */

	public int chooseSplitAxisObj(GPSObject obj, int nodeId) {
		GPSObject[] arrayObj = new GPSObject[M + 1];
		int id;
		int axe = -1;
		RTNode node1;
		RTNode node2;
		double perimeter;
		double perimeterMin;
		
		if(!nodes[nodeId].isLeaf()) {
			return -1;
		}
		
		for(int i = 0; i < nodes[nodeId].getObjects().length; i++) {
			arrayObj[i] = nodes[nodeId].getObject(i);
		}
		
		arrayObj[arrayObj.length - 1] = obj;
		
		node1 = new RTNode();
		node2 = new RTNode();
		
		perimeterMin = 999999;
		
		for(int i = 0; i <= 1; i++) { // Оси
			perimeter = 0;
			
			for(int j = 0; j <= 1; j++) { // Левая и правая границы
				
				quickSortObj(arrayObj, 0, arrayObj.length - 1, i, j);

				// Вачисляем периметры во всех возможных комбинациях
				for(int k = 1; k <= M - (m * 2) + 2; k++) {
					id = 0;
					node1.clearObjects();
					node2.clearObjects();

					// Первому узлу присваиваются первые (MIN_M - 1) + k элементов
					while(id < (m - 1) + k) {
						node1.setObject(id, arrayObj[id]);
						id++;
					}

					// Второму узлу присваиваются остальные элементы
					for(; id < arrayObj.length; id++) {
						node2.setObject(id - ((m - 1) + k), arrayObj[id]);
					}
					
					updateMBR(node1);
					updateMBR(node2);

					perimeter = perimeter + node1.margin() + node2.margin();
				}
			}
			
			if(perimeter <= perimeterMin) {
				perimeterMin = perimeter;
				
				axe = i;
			}
			
			perimeter = 0;
		}
		
		arrayObj = null;
		node1 = null;
		node2 = null;
		
		return axe;
	}

	/**
	 * Определяет ось, по которой будет происходить деление узла при переполнении его узлами
	 * (в соответствии с алгоритмами R*-Tree).
	 *
	 * @param nodeFather узел, который делится
	 * @param nodeChild узел, из-за которого происходит деление узла, в который его поместили
     * @return ось, по которой будет происходить деление узла
     */

	public int chooseSplitAxisNd(int nodeFather, int nodeChild) {
		int[] arrayNode = new int[M + 1];
		int id;
		int axe = -1;
		RTNode node1;
		RTNode node2;
		double perimeter;
		double perimeterMin;
		
		for(int i = 0; i < nodes[nodeFather].getChildren().length; i++) {
			arrayNode[i] = nodes[nodeFather].getChild(i);
		}
		
		arrayNode[arrayNode.length - 1] = nodeChild;
		
		node1 = new RTNode();
		node2 = new RTNode();
		
		perimeterMin = 999999;
		
		for(int i = 0; i <= 1; i++) { // Оси
			perimeter = 0;
			
			for(int j = 0; j <= 1; j++) { // Левая и правая границы
				node1.clearChildren();
				node2.clearChildren();
				
				quickSortNd(arrayNode, 0, arrayNode.length - 1, i, j);

				// Вачисляем периметры во всех возможных комбинациях
				for(int k = 1; k <= M - (m * 2) + 2; k++) {
					id = 0;

					// Первому узлу присваиваются первые (MIN_M - 1) + k элементов
					while(id < (m - 1) + k) {
						node1.setChild(id, arrayNode[id]);
						id++;
					}

					// Второму узлу присваиваются остальные элементы
					for(; id < arrayNode.length - 1; id++) {
						node2.setChild(id - ((m - 1) + k), arrayNode[id]);
					}
					
					updateMBR(node1);
					updateMBR(node2);
					
					perimeter += node1.margin() + node2.margin();
				}
			}
			
			if(perimeter <= perimeterMin) {				
				perimeterMin = perimeter;
				
				axe = i;
			}
			
			perimeter = 0;
		}
		
		arrayNode = null;
		node1 = null;
		node2 = null;
		
		return axe;
	}

	/**
	 * Разделяет узел на два в соответствии с алгоритмами R*-tree (page 325:: The R*-tree: An
	 * Efficient and Robust Access Method for Points and Rectangles+).
	 *
	 * @param nodeId индекс узла для разделения
	 * @param obj объект для вставки
     */

	public void splitNodeRStarObj(int nodeId, GPSObject obj) {
		int axe;
		int parentId;
		int newChildId;
		RTNode node1; 
		RTNode node2; 
		RTNode node1Min; 
		RTNode node2Min;
		GPSObject[] arrayObj = new GPSObject[M + 1];
		// Для рассчета текущей и минимальной площади перекрытия областей
		double areaOverlapMin, areaOverlap;
		// Для рассчета текущей и минимальной площадей областей
		double areaMin, area;
		
		if(!nodes[nodeId].isLeaf()) {
			return;
		}
		
		if(isRoot(nodeId)) {
			// Создаем новый узел в массиве узлов дерева и получаем его идентификатор (индекс)
			parentId = newNode();
			// Присваиваем этот индекс корневому узлу
			nodes[root].setParent(parentId);
			// Присваиваем новому узлу индекс корня как дочернего узла
			nodes[parentId].setChild(0, root);
			// Увеличиваем уровень нового узла на 1
			nodes[parentId].setLevel(nodes[nodes[parentId].getChild(0)].getLevel() + 1);
			root = parentId; // Изменяем индекс корня на индекс нового узла
			height++; // Увеличиваем высоту дерева
		} else {
			parentId = nodes[nodeId].getParent();
		}
		
		for(int i = 0; i < arrayObj.length - 1; i++) {
			arrayObj[i] = nodes[nodeId].getObject(i);
		}
		
		arrayObj[arrayObj.length - 1] = obj;
		
		node1Min = new RTNode();
		node2Min = new RTNode();
		
		node1 = new RTNode();
		node2 = new RTNode();
		
		axe = chooseSplitAxisObj(obj, nodeId);
		
		areaOverlapMin = 9999999;
		areaMin = 9999999;
		
		for(int i = 0; i <= 1; i++) {
			quickSortObj(arrayObj, 0, arrayObj.length - 1, axe, i);
			
			for(int k = m - 1; k <= M - m; k++) {
				node1.clearObjects();
				node2.clearObjects();
				
				int j = 0;

				while(j <= k) {
					node1.setObject(j, arrayObj[j]);
					j++;
				}
				
				for(j = k; j < arrayObj.length - 1; j++) {
					node2.setObject(j - k, arrayObj[j + 1]);
				}
				
				updateMBR(node1);
				updateMBR(node2);
				
				areaOverlap = node1.Overlap(node2.getMbr());
				
				if(areaOverlap < areaOverlapMin) {
					node1Min.copy(node1);
					node2Min.copy(node2);
					areaOverlapMin = areaOverlap;
				} else {
					// Если площади перекрытия одинаковые
					if(areaOverlap == areaOverlapMin) {
						area = node1.area() + node2.area(); // Считаем площади узлов
						
						if(area < areaMin) {
							node1Min.copy(node1);
							node2Min.copy(node2);
							areaMin = area;
						}
					}
				}
			}
		}
		
		node1Min.setLevel(0);
		node2Min.setLevel(0);
		
		nodes[nodeId].copy(node1Min); // Вставляем первый узел на место старого (переполненого) узла
		nodes[nodeId].setParent(parentId);
		
		updateMBR(nodeId);
		
		newChildId = newNode(); // Создаем новый узел в массиве узлов дерева
		nodes[newChildId].copy(node2Min); // Вставляем в только что созданный узел второй узел
		// Присваиваем индекс узла родителя значению (переменной) parent нового узла
		nodes[newChildId].setParent(parentId);
		
		updateMBR(newChildId);
		
		node1 = null;
		node2 = null;
		node1Min = null;
		node2Min = null;

		// Если хватает места для вставки второго узла
		if(nodes[parentId].getChildren().length < M) {
			// Присваиваем индекс нового узла
			nodes[parentId].setChild(nodes[parentId].getChildren().length, newChildId);
			updateMBR(parentId);
		} else { // Если места не хватает
			// Вызываем процедуру деления родительского узла
			splitNodeRStarNd(parentId, newChildId);
		}
	}

	/**
	 * Разделяет узел на два в соответствии с алгоритмами R*-tree (page 325:: The R*-tree: An
	 * Efficient and Robust Access Method for Points and Rectangles+).
	 *
	 * @param splitedNodeId индекс узла для разделения
	 * @param insertedNodeId узел для вставки
     */

	public void splitNodeRStarNd(int splitedNodeId, int insertedNodeId) {
		int axe;
		int parentId;
		int newChildId;
		RTNode node1; 
		RTNode node2; 
		RTNode node1Min; 
		RTNode node2Min;
		int[] arrayNode = new int[M + 1];
		// Для рассчета текущей и минимальной площади перекрытия областей
		double areaOverlapMin, areaOverlap;
		// Для рассчета текущей и минимальной площадей областей
		double areaMin, area;
		
		if(nodes[splitedNodeId].isLeaf()) {
			return;
		}
		
		if(isRoot(splitedNodeId)) {
			// Создаем новый узел в массиве узлов дерева и получаем его идентификатор (индекс)
			parentId = newNode();
			// Присваиваем этот индекс корневому узлу
			nodes[root].setParent(parentId);
			// Присваиваем новому узлу индекс корня как дочернего узла
			nodes[parentId].setChild(0, root);
			// Увеличиваем уровень нового узла на 1
			nodes[parentId].setLevel(nodes[nodes[parentId].getChild(0)].getLevel() + 1);
			root = parentId; // Изменяем индекс корня на индекс нового узла
			height++; // Увеличиваем высоту дерева
		} else {
			parentId = nodes[splitedNodeId].getParent();
		}
		
		for(int i = 0; i < arrayNode.length - 1; i++) {
			arrayNode[i] = nodes[splitedNodeId].getChild(i);
		}
		
		arrayNode[arrayNode.length - 1] = insertedNodeId;
		
		node1Min = new RTNode();
		node2Min = new RTNode();
		
		node1 = new RTNode();
		node2 = new RTNode();
		
		axe = this.chooseSplitAxisNd(splitedNodeId, insertedNodeId);
		
		areaOverlapMin = 9999999;
		areaMin = 9999999;
		
		for(int i = 0; i <= 1; i++) {
			quickSortNd(arrayNode, 0, arrayNode.length - 1, axe, i);
			
			for(int k = m - 1; k <= M - m; k++) {
				node1.clearChildren();
				node2.clearChildren();
				
				int j = 0;
				
				while(j <= k) {
					node1.setChild(j, arrayNode[j]);
					j++;
				}
				
				for(j = k; j < arrayNode.length - 1; j++) {
					node2.setChild(j - k, arrayNode[j + 1]);
				}
				
				updateMBR(node1);
				updateMBR(node2);
				
				areaOverlap = node1.Overlap(node2.getMbr());
				
				if(areaOverlap < areaOverlapMin) {
					node1Min.copy(node1);
					node2Min.copy(node2);
					areaOverlapMin = areaOverlap;
				} else {
					// Если площади перекрытия одинаковые
					if(areaOverlap == areaOverlapMin) {
						area = node1.area() + node2.area(); // Считаем площади узлов
						
						if(area < areaMin) {
							node1Min.copy(node1);
							node2Min.copy(node2);
							areaMin = area;
						}
					}
				}
			}
		}
		
		node1Min.setLevel(nodes[splitedNodeId].getLevel());
		node2Min.setLevel(nodes[splitedNodeId].getLevel());

		// Вставляем первый узел на место старого (переполненого) узла
		nodes[splitedNodeId].copy(node1Min);
		nodes[splitedNodeId].setParent(parentId);
		
		newChildId = newNode(); // Создаем новый узел в массиве узлов дерева
		nodes[newChildId].copy(node2Min); // Вставляем в только что созданный узел второй узел
		// Присваиваем индекс узла родителя значению (переменной) parent нового узла
		nodes[newChildId].setParent(parentId);
		
		node1 = null;
		node2 = null;
		node1Min = null;
		node2Min = null;

		// Присваиваем переменной parent всех дочерних узлов нового узла его индекс
		for(int i = 0; i < nodes[newChildId].getChildren().length; i++) {
			nodes[nodes[newChildId].getChild(i)].setParent(newChildId);
		}

		// Если хватает места для вставки второго узла
		if(nodes[parentId].getChildren().length < M) {
			// Присваиваем индекс нового узла
			nodes[parentId].setChild(nodes[parentId].getChildren().length, newChildId);
			updateMBR(parentId);
		} else { // Если места не хватает
			// Вызываем процедуру деления родительского узла
			this.splitNodeRStarNd(parentId, newChildId);
		}
	}

	/**
	 * Выбирает поддерево узла для вставки объекта (листа дерева).
	 *
	 * @param obj объект дерева (лист дерева)
	 * @param nodeId индекс поддерева узла
     * @return индекс поддерева узла для вставки объекта (листа дерева)
     */
	public int chooseSubtree(GPSObject obj, int nodeId) {
		int idChild;
		// Минимальное увеличение перекрытия узла и объекта
		double minOverlapEnlargement;
		double overlapEnlargement;
		double areaEnlargement;

		/*
		Массив индексов узлов с минимальным расширением перекрытия. Требуется массив потому, что
		вставка объекта может вообще не расширять перекрытие. Для этого занесем все индексы в
		массив и выберем узел с минимальным расширением площади ограничивающего прямоугольника узла
		 */
		int[] idChildOverlap = new int[1];
		
		/*
		Массив индексов узлов с минимальным расширением площади. Требуется массив потому, что
		вставка объекта может вообще не расширять площадь ограничивающего прямоугольника узла.
		Для этого занесем все индексы в массив и выберем узел с минимальной площадью
		ограничивающего прямоугольника узла
		 */
		int[] idChildArea = new int[1];
		
		/*
		Переменная для хранения индекса дочернего узла при поиске узла с наименьшей площадью
		ограничивающего прямоугольника узла (в случае, когда имеется  несколько узлов без
		расширения ограничивающего прямоугольника узла)
		 */
		int idZero;

		// Для рассчета изменения ограничивающего прямоугольника узла
		JRect enlargementMbr = new JRect();
		// Для рассчета увеличения ограничивающего прямоугольника узла по осям x, y и площади
		double dspace;
		
		if(nodes[nodeId].isLeaf()) { // Если узел конечный, то возвращаем его
			return nodeId;
		}

		dspace = 9999999;
		idZero = 0;
		minOverlapEnlargement = 999999;
		
		// Если дочерние узлы являются конечными (листьями)
		if(nodes[nodes[nodeId].getChild(0)].isLeaf()) { 
			// Определяем узел с наименьшим увеличением перекрытия
			for(int i = 0; i < nodes[nodeId].getChildren().length; i++) {
				idChild = nodes[nodeId].getChild(i);
				overlapEnlargement = nodes[idChild].area(obj.getMbr()) - nodes[idChild].Overlap(obj.getMbr());
				
				if(overlapEnlargement <= minOverlapEnlargement) {
					// Если увеличение перекрытия равно предыдущему минимальному
					if(overlapEnlargement == minOverlapEnlargement) {
						int[] tmp = idChildOverlap;
						int size = idChildOverlap.length;
						
						idChildOverlap = new int[size + 1];
						System.arraycopy(tmp, 0, idChildOverlap, 0, size);
						idChildOverlap[idChildOverlap.length - 1] = i;
					} else { // Если увеличение перекрытия строго меньше предыдущего минимального
						minOverlapEnlargement = overlapEnlargement;
						
						// Если до этого не встречались два узла с одинаковым минимальным значением
						if(idChildOverlap.length == 1) { 
							idChildOverlap[0] = i;
						} else {
							idChildOverlap = new int[] {i};
						}
					}
				}
			}
			
			/*
			 Если в массиве всего один элемент, тогда найден элемент с минимальным
			 расширением перекрытия
			  */
			if(idChildOverlap.length == 1) {
				nodeId = nodes[nodeId].getChild(idChildOverlap[0]);
				// Рекурсивно вызываем процедуру выбора поддерева
				nodeId = chooseSubtree(obj, nodeId);
				
				return nodeId;
			} 
		} else { // Если же дочерние узлы не конечные
			idChildOverlap = new int[nodes[nodeId].getChildren().length];
			
			/*
			Скопируем индексы в массив idChild_overlap, так как дальше процедура работает
			с этим массивом (на случай, если дочерние узлы конечные и имеется несколько узлов
			с одинаковым увеличением перекрытия, тогда вidChild_overlap будут индексы на эти узлы)
			 */
			for(int i = 0; i < nodes[nodeId].getChildren().length; i++) {
				idChildOverlap[i] = i;
			}
		}
			
		// Определяем узел с наименьшим увеличением площади
		for(int i = 0; i < idChildOverlap.length; i++) {
			idChild = nodes[nodeId].getChild(idChildOverlap[i]);
			
			enlargementMbr.left = Math.min(obj.getMbr().left, nodes[idChild].getMbr().left);
			enlargementMbr.top = Math.min(obj.getMbr().top, nodes[idChild].getMbr().top);
			enlargementMbr.right = Math.max(obj.getMbr().right, nodes[idChild].getMbr().right);
			enlargementMbr.bottom = Math.max(obj.getMbr().bottom, nodes[idChild].getMbr().bottom);
			
			areaEnlargement = nodes[idChild].area(enlargementMbr) - nodes[idChild].area();
			
			if(areaEnlargement < dspace) {
				// Если увеличение площади равно предыдущему минимальному
				if(areaEnlargement == dspace) {
					int[] tmp = idChildArea;
					int size = idChildArea.length;
					
					idChildArea = new int[size + 1];
					System.arraycopy(tmp, 0, idChildArea, 0, size);
					idChildArea[idChildArea.length - 1] = i;
				} else { // Если увеличение площади строго меньше предыдущего минимального
					dspace = areaEnlargement;
					
					// Если до этого не втречались два узла с одинаковым минимальным значением
					if(idChildArea.length == 1) {
						idChildArea[0] = i;
					} else { // если же встречались, тогда установим длину массива равной 1
						idChildArea = new int[] {i};
					}
				}
			}
		}	
		
		/*
		Если в массиве всего один элемент, тогда найден узел с минимальным расширением
		ограничивающего прямоугольника узла
		 */
		if(idChildArea.length == 1) {
			nodeId = nodes[nodeId].getChild(idChildArea[0]);
			nodeId = chooseSubtree(obj, nodeId); // Рекурсивно вызываем процедуру выбора поддерева
		} else {
			/*
			В противном случае (имеется несколько узлов без расширения ограничивающего
			прямоугольника узла либо с одинаковым расширением) находим узел с минимальной
			площадью ограничивающего прямоугольника узла
			 */
			dspace = 999999;
			
			for(int i = 0; i < idChildArea.length; i++) {
				idChild = nodes[nodeId].getChild(idChildArea[i]);
				
				if(nodes[idChild].area() < dspace) {
					idZero = idChildArea[i];
					dspace = nodes[idChild].area();
				}
			}
			
			nodeId = nodes[nodeId].getChild(idZero);
			nodeId = chooseSubtree(obj, nodeId);
		}
		
		return nodeId;
	}


	/**
	 * Ищет объекты входящие в область заданную прямоугльником.
	 *
	 * @param mbr заданная область
	 * @param nodeId узел дерева, с которого начинается поиск объектов
	 * @param objs массив, в который помещаются найденные объекты
     */

	public void findObjectsInArea(JRect mbr, int nodeId, ArrayList<GPSObject> objs) {		
		if(isRoot(nodeId)) {
			objs.clear();
		}
		
		if(!nodes[nodeId].isLeaf()) {
			for(int i = 0; i < nodes[nodeId].getChildren().length; i++) {
				if(nodes[nodes[nodeId].getChild(i)].isIntersected(mbr)) {
					findObjectsInArea(mbr, nodes[nodeId].getChild(i), objs);
				}
			}
		} else {
			for(int i = 0; i < nodes[nodeId].getObjects().length; i++) {
				if(nodes[nodeId].isIntersected(mbr, nodes[nodeId].getObject(i).getMbr())) {
//					obj.add(nodes[nodeId].getObject(i).getSeek());
					addSort(nodes[nodeId].getObject(i), objs);
				}
			}
		}
	}
	
	/**
	 * Вставляет в список объект поискового дерева используя сортировку (от наибольшей площади до
	 * наименьшей ограничивающего прямоугольника).
	 *
	 * @param obj объект для вставки в список
	 * @param objects отсортированый список объектов
	 */

	private void addSort(GPSObject obj, ArrayList<GPSObject> objects) {
        if(objects.size() > 0) {
            double areaObj = area(obj);

            for(int i = objects.size() - 1; i >= 0; i--) {
                if(areaObj <= area(objects.get(i))) {
                    if( i == objects.size() - 1)
                        objects.add(obj);
                    else
                        objects.add(i + 1, obj);
                    break;
                } else if(areaObj > area(objects.get(i)) && i == 0) {
                    objects.add(i, obj);
                }
            }
        } else {
            objects.add(obj);
        }
	}

    /**
     * Вычисляет площадь ограничивающего прямоугольника объекта карты.
     *
     * @param obj объект карты
     * @return площадь ограничивающего прямоугольника объекта карты
     */

	private double area(GPSObject obj) {
        return (obj.getMbr().right - obj.getMbr().left) * (obj.getMbr().bottom  - obj.getMbr().top);
    }

	/**
	 * Ищет объекты входящие в область заданную прямоугльником.
	 *
	 * @param mbr заданная область
	 * @param obj массив, в который помещаются найденные объекты
     */

	public void findObjectsInArea(JRect mbr, ArrayList<GPSObject> objs) {
		findObjectsInArea(mbr, root, objs);
	}

	/**
	 * Вставляет объект (лист) в дерево.
	 *
	 * @param obj объект (лист), который вставляют в дерево
     */
	public void insertObject(GPSObject obj) {
		int nodeId = root;
		
		nodeId = chooseSubtree(obj, nodeId);
		
		// Если кол-во объектов в узле меньше максимально допустимого
		if(nodes[nodeId].getObjects().length < M) {
			nodes[nodeId].setObject(nodes[nodeId].getObjects().length, obj);
			
			updateMBR(nodeId);
		} else { // Если кол-во объектов в узле достигло максимально допустимого
			splitNodeRStarObj(nodeId, obj); // Делим узел
		}
	}
}

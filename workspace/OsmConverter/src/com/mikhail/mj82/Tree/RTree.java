/*
 * Copyright (c) 08.2016
 */

package com.mikhail.mj82.Tree;

import java.io.Serializable;
import java.util.ArrayList;

import com.mikhail.mj82.Geom.JRect;

/**
 * ����� ������ ������.
 *
 * @author Mikhail Kushnerov (mj82)
 */

public class RTree implements Serializable {

	private static final long serialVersionUID = -5444319452261480749L;
	
	public final int M = 16; // ������������ ���-�� �������� � ����
	public final int m = (int)(M * 0.4); // ����������� ���-�� �������� � ����
	// ��� ��� ����������
	private final int X = 0;
	private final int Y = 1;
	// ������� ��� ����������
	private final int Left = 0;
	private final int Right = 1;

	private RTNode[] nodes; // ������ ����� ������
	private int root; // ������ �� ��������� ��������� ���� � ������� ����� (� ������)
	private int height; // ������ ������
	
	public RTree() {
		nodes = new RTNode[1];
		nodes[0] = new RTNode();
		root = 0;
		nodes[root].setLeaf(true);
	}

	/**
	 * ���������� ������ ����� � ������� ����� ������.
	 *
	 * @return ������ ����� � ������� ����� ������
     */

	public int getRoot() {
		return root;
	}

	/**
	 * ���������� ������ ����� ������.
	 *
	 * @return ������ ����� ������
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
	 * ���������� ������ ������.
	 *
	 * @return ������ ������
     */

	public int getHeight() {
		return height;
	}

	/**
	 * ������������� ������ ������.
	 *
	 * @param height �������� ������ ������
     */

	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * ���������� �������� �� ������ ���� � �������� ��������.
	 *
	 * @param nodeId ������ ����, ������� ���������
	 * @return ���� true - ���� ��������, ����� - false
     */

	public boolean isRoot(int nodeId) {
		if(nodeId == root)
			return true;
		else
			return false;
	}

	/**
	 * ������� ����� ���� ������.
	 *
	 * @return ������ ��������� ���� ������
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
	 * ��������� �������������� ������������� ���� ������.
	 *
	 * @param nodeId ������������� ���� ������, �������������� ������������� �������� ���������
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
	 * ��������� �������������� ������������� ���� ������.
	 *
	 * @param node ���� ������, �������������� ������������� �������� ���������
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
	 * ������� ���������� ��� �������� ������ �� �� �������������� ���������������.
	 *
	 * @param list ������ �������� ��� ����������
	 * @param LO ����������� ������ � ������� �������� � �������� ���������� ����������
	 * @param HI ������������ ������ � ������� �������� �� ������� ���������� �� ����������
	 * @param axe ���, �� ������� ���������� ����������
     * @param bound �������, �� ������� ���������� ����������
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
	 * ������� ���������� ��� ����� ������ �� �� �������������� ���������������.
	 *
	 * @param list ������ ����� ��� ����������
	 * @param LO ����������� ������ � ������� ����� � �������� ���������� ����������
	 * @param HI ������������ ������ � ������� ����� �� ������� ���������� �� ����������
	 * @param axe ���, �� ������� ���������� ����������
	 * @param bound �������, �� ������� ���������� ����������
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
	 * ���������� ���, �� ������� ����� ����������� ������� ����  ��� ������������ ��� ���������
	 * (��������) (� ������������ � ����������� R*-Tree).
	 *
	 * @param obj ���� ������ (������ ������)
	 * @param nodeId ������������� ����� ������ (������� ������)
     * @return ���, �� ������� ����� ����������� ������� ����
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
		
		for(int i = 0; i <= 1; i++) { // ���
			perimeter = 0;
			
			for(int j = 0; j <= 1; j++) { // ����� � ������ �������
				
				quickSortObj(arrayObj, 0, arrayObj.length - 1, i, j);

				// ��������� ��������� �� ���� ��������� �����������
				for(int k = 1; k <= M - (m * 2) + 2; k++) {
					id = 0;
					node1.clearObjects();
					node2.clearObjects();

					// ������� ���� ������������� ������ (MIN_M - 1) + k ���������
					while(id < (m - 1) + k) {
						node1.setObject(id, arrayObj[id]);
						id++;
					}

					// ������� ���� ������������� ��������� ��������
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
	 * ���������� ���, �� ������� ����� ����������� ������� ���� ��� ������������ ��� ������
	 * (� ������������ � ����������� R*-Tree).
	 *
	 * @param nodeFather ����, ������� �������
	 * @param nodeChild ����, ��-�� �������� ���������� ������� ����, � ������� ��� ���������
     * @return ���, �� ������� ����� ����������� ������� ����
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
		
		for(int i = 0; i <= 1; i++) { // ���
			perimeter = 0;
			
			for(int j = 0; j <= 1; j++) { // ����� � ������ �������
				node1.clearChildren();
				node2.clearChildren();
				
				quickSortNd(arrayNode, 0, arrayNode.length - 1, i, j);

				// ��������� ��������� �� ���� ��������� �����������
				for(int k = 1; k <= M - (m * 2) + 2; k++) {
					id = 0;

					// ������� ���� ������������� ������ (MIN_M - 1) + k ���������
					while(id < (m - 1) + k) {
						node1.setChild(id, arrayNode[id]);
						id++;
					}

					// ������� ���� ������������� ��������� ��������
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
	 * ��������� ���� �� ��� � ������������ � ����������� R*-tree (page 325:: The R*-tree: An
	 * Efficient and Robust Access Method for Points and Rectangles+).
	 *
	 * @param nodeId ������ ���� ��� ����������
	 * @param obj ������ ��� �������
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
		// ��� �������� ������� � ����������� ������� ���������� ��������
		double areaOverlapMin, areaOverlap;
		// ��� �������� ������� � ����������� �������� ��������
		double areaMin, area;
		
		if(!nodes[nodeId].isLeaf()) {
			return;
		}
		
		if(isRoot(nodeId)) {
			// ������� ����� ���� � ������� ����� ������ � �������� ��� ������������� (������)
			parentId = newNode();
			// ����������� ���� ������ ��������� ����
			nodes[root].setParent(parentId);
			// ����������� ������ ���� ������ ����� ��� ��������� ����
			nodes[parentId].setChild(0, root);
			// ����������� ������� ������ ���� �� 1
			nodes[parentId].setLevel(nodes[nodes[parentId].getChild(0)].getLevel() + 1);
			root = parentId; // �������� ������ ����� �� ������ ������ ����
			height++; // ����������� ������ ������
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
					// ���� ������� ���������� ����������
					if(areaOverlap == areaOverlapMin) {
						area = node1.area() + node2.area(); // ������� ������� �����
						
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
		
		nodes[nodeId].copy(node1Min); // ��������� ������ ���� �� ����� ������� (�������������) ����
		nodes[nodeId].setParent(parentId);
		
		updateMBR(nodeId);
		
		newChildId = newNode(); // ������� ����� ���� � ������� ����� ������
		nodes[newChildId].copy(node2Min); // ��������� � ������ ��� ��������� ���� ������ ����
		// ����������� ������ ���� �������� �������� (����������) parent ������ ����
		nodes[newChildId].setParent(parentId);
		
		updateMBR(newChildId);
		
		node1 = null;
		node2 = null;
		node1Min = null;
		node2Min = null;

		// ���� ������� ����� ��� ������� ������� ����
		if(nodes[parentId].getChildren().length < M) {
			// ����������� ������ ������ ����
			nodes[parentId].setChild(nodes[parentId].getChildren().length, newChildId);
			updateMBR(parentId);
		} else { // ���� ����� �� �������
			// �������� ��������� ������� ������������� ����
			splitNodeRStarNd(parentId, newChildId);
		}
	}

	/**
	 * ��������� ���� �� ��� � ������������ � ����������� R*-tree (page 325:: The R*-tree: An
	 * Efficient and Robust Access Method for Points and Rectangles+).
	 *
	 * @param splitedNodeId ������ ���� ��� ����������
	 * @param insertedNodeId ���� ��� �������
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
		// ��� �������� ������� � ����������� ������� ���������� ��������
		double areaOverlapMin, areaOverlap;
		// ��� �������� ������� � ����������� �������� ��������
		double areaMin, area;
		
		if(nodes[splitedNodeId].isLeaf()) {
			return;
		}
		
		if(isRoot(splitedNodeId)) {
			// ������� ����� ���� � ������� ����� ������ � �������� ��� ������������� (������)
			parentId = newNode();
			// ����������� ���� ������ ��������� ����
			nodes[root].setParent(parentId);
			// ����������� ������ ���� ������ ����� ��� ��������� ����
			nodes[parentId].setChild(0, root);
			// ����������� ������� ������ ���� �� 1
			nodes[parentId].setLevel(nodes[nodes[parentId].getChild(0)].getLevel() + 1);
			root = parentId; // �������� ������ ����� �� ������ ������ ����
			height++; // ����������� ������ ������
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
					// ���� ������� ���������� ����������
					if(areaOverlap == areaOverlapMin) {
						area = node1.area() + node2.area(); // ������� ������� �����
						
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

		// ��������� ������ ���� �� ����� ������� (�������������) ����
		nodes[splitedNodeId].copy(node1Min);
		nodes[splitedNodeId].setParent(parentId);
		
		newChildId = newNode(); // ������� ����� ���� � ������� ����� ������
		nodes[newChildId].copy(node2Min); // ��������� � ������ ��� ��������� ���� ������ ����
		// ����������� ������ ���� �������� �������� (����������) parent ������ ����
		nodes[newChildId].setParent(parentId);
		
		node1 = null;
		node2 = null;
		node1Min = null;
		node2Min = null;

		// ����������� ���������� parent ���� �������� ����� ������ ���� ��� ������
		for(int i = 0; i < nodes[newChildId].getChildren().length; i++) {
			nodes[nodes[newChildId].getChild(i)].setParent(newChildId);
		}

		// ���� ������� ����� ��� ������� ������� ����
		if(nodes[parentId].getChildren().length < M) {
			// ����������� ������ ������ ����
			nodes[parentId].setChild(nodes[parentId].getChildren().length, newChildId);
			updateMBR(parentId);
		} else { // ���� ����� �� �������
			// �������� ��������� ������� ������������� ����
			this.splitNodeRStarNd(parentId, newChildId);
		}
	}

	/**
	 * �������� ��������� ���� ��� ������� ������� (����� ������).
	 *
	 * @param obj ������ ������ (���� ������)
	 * @param nodeId ������ ��������� ����
     * @return ������ ��������� ���� ��� ������� ������� (����� ������)
     */
	public int chooseSubtree(GPSObject obj, int nodeId) {
		int idChild;
		// ����������� ���������� ���������� ���� � �������
		double minOverlapEnlargement;
		double overlapEnlargement;
		double areaEnlargement;

		/*
		������ �������� ����� � ����������� ����������� ����������. ��������� ������ ������, ���
		������� ������� ����� ������ �� ��������� ����������. ��� ����� ������� ��� ������� �
		������ � ������� ���� � ����������� ����������� ������� ��������������� �������������� ����
		 */
		int[] idChildOverlap = new int[1];
		
		/*
		������ �������� ����� � ����������� ����������� �������. ��������� ������ ������, ���
		������� ������� ����� ������ �� ��������� ������� ��������������� �������������� ����.
		��� ����� ������� ��� ������� � ������ � ������� ���� � ����������� ��������
		��������������� �������������� ����
		 */
		int[] idChildArea = new int[1];
		
		/*
		���������� ��� �������� ������� ��������� ���� ��� ������ ���� � ���������� ��������
		��������������� �������������� ���� (� ������, ����� �������  ��������� ����� ���
		���������� ��������������� �������������� ����)
		 */
		int idZero;

		// ��� �������� ��������� ��������������� �������������� ����
		JRect enlargementMbr = new JRect();
		// ��� �������� ���������� ��������������� �������������� ���� �� ���� x, y � �������
		double dspace;
		
		if(nodes[nodeId].isLeaf()) { // ���� ���� ��������, �� ���������� ���
			return nodeId;
		}

		dspace = 9999999;
		idZero = 0;
		minOverlapEnlargement = 999999;
		
		// ���� �������� ���� �������� ��������� (��������)
		if(nodes[nodes[nodeId].getChild(0)].isLeaf()) { 
			// ���������� ���� � ���������� ����������� ����������
			for(int i = 0; i < nodes[nodeId].getChildren().length; i++) {
				idChild = nodes[nodeId].getChild(i);
				overlapEnlargement = nodes[idChild].area(obj.getMbr()) - nodes[idChild].Overlap(obj.getMbr());
				
				if(overlapEnlargement <= minOverlapEnlargement) {
					// ���� ���������� ���������� ����� ����������� ������������
					if(overlapEnlargement == minOverlapEnlargement) {
						int[] tmp = idChildOverlap;
						int size = idChildOverlap.length;
						
						idChildOverlap = new int[size + 1];
						System.arraycopy(tmp, 0, idChildOverlap, 0, size);
						idChildOverlap[idChildOverlap.length - 1] = i;
					} else { // ���� ���������� ���������� ������ ������ ����������� ������������
						minOverlapEnlargement = overlapEnlargement;
						
						// ���� �� ����� �� ����������� ��� ���� � ���������� ����������� ���������
						if(idChildOverlap.length == 1) { 
							idChildOverlap[0] = i;
						} else {
							idChildOverlap = new int[] {i};
						}
					}
				}
			}
			
			/*
			 ���� � ������� ����� ���� �������, ����� ������ ������� � �����������
			 ����������� ����������
			  */
			if(idChildOverlap.length == 1) {
				nodeId = nodes[nodeId].getChild(idChildOverlap[0]);
				// ���������� �������� ��������� ������ ���������
				nodeId = chooseSubtree(obj, nodeId);
				
				return nodeId;
			} 
		} else { // ���� �� �������� ���� �� ��������
			idChildOverlap = new int[nodes[nodeId].getChildren().length];
			
			/*
			��������� ������� � ������ idChild_overlap, ��� ��� ������ ��������� ��������
			� ���� �������� (�� ������, ���� �������� ���� �������� � ������� ��������� �����
			� ���������� ����������� ����������, ����� �idChild_overlap ����� ������� �� ��� ����)
			 */
			for(int i = 0; i < nodes[nodeId].getChildren().length; i++) {
				idChildOverlap[i] = i;
			}
		}
			
		// ���������� ���� � ���������� ����������� �������
		for(int i = 0; i < idChildOverlap.length; i++) {
			idChild = nodes[nodeId].getChild(idChildOverlap[i]);
			
			enlargementMbr.left = Math.min(obj.getMbr().left, nodes[idChild].getMbr().left);
			enlargementMbr.top = Math.min(obj.getMbr().top, nodes[idChild].getMbr().top);
			enlargementMbr.right = Math.max(obj.getMbr().right, nodes[idChild].getMbr().right);
			enlargementMbr.bottom = Math.max(obj.getMbr().bottom, nodes[idChild].getMbr().bottom);
			
			areaEnlargement = nodes[idChild].area(enlargementMbr) - nodes[idChild].area();
			
			if(areaEnlargement < dspace) {
				// ���� ���������� ������� ����� ����������� ������������
				if(areaEnlargement == dspace) {
					int[] tmp = idChildArea;
					int size = idChildArea.length;
					
					idChildArea = new int[size + 1];
					System.arraycopy(tmp, 0, idChildArea, 0, size);
					idChildArea[idChildArea.length - 1] = i;
				} else { // ���� ���������� ������� ������ ������ ����������� ������������
					dspace = areaEnlargement;
					
					// ���� �� ����� �� ���������� ��� ���� � ���������� ����������� ���������
					if(idChildArea.length == 1) {
						idChildArea[0] = i;
					} else { // ���� �� �����������, ����� ��������� ����� ������� ������ 1
						idChildArea = new int[] {i};
					}
				}
			}
		}	
		
		/*
		���� � ������� ����� ���� �������, ����� ������ ���� � ����������� �����������
		��������������� �������������� ����
		 */
		if(idChildArea.length == 1) {
			nodeId = nodes[nodeId].getChild(idChildArea[0]);
			nodeId = chooseSubtree(obj, nodeId); // ���������� �������� ��������� ������ ���������
		} else {
			/*
			� ��������� ������ (������� ��������� ����� ��� ���������� ���������������
			�������������� ���� ���� � ���������� �����������) ������� ���� � �����������
			�������� ��������������� �������������� ����
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
	 * ���� ������� �������� � ������� �������� ��������������.
	 *
	 * @param mbr �������� �������
	 * @param nodeId ���� ������, � �������� ���������� ����� ��������
	 * @param objs ������, � ������� ���������� ��������� �������
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
	 * ��������� � ������ ������ ���������� ������ ��������� ���������� (�� ���������� ������� ��
	 * ���������� ��������������� ��������������).
	 *
	 * @param obj ������ ��� ������� � ������
	 * @param objects �������������� ������ ��������
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
     * ��������� ������� ��������������� �������������� ������� �����.
     *
     * @param obj ������ �����
     * @return ������� ��������������� �������������� ������� �����
     */

	private double area(GPSObject obj) {
        return (obj.getMbr().right - obj.getMbr().left) * (obj.getMbr().bottom  - obj.getMbr().top);
    }

	/**
	 * ���� ������� �������� � ������� �������� ��������������.
	 *
	 * @param mbr �������� �������
	 * @param obj ������, � ������� ���������� ��������� �������
     */

	public void findObjectsInArea(JRect mbr, ArrayList<GPSObject> objs) {
		findObjectsInArea(mbr, root, objs);
	}

	/**
	 * ��������� ������ (����) � ������.
	 *
	 * @param obj ������ (����), ������� ��������� � ������
     */
	public void insertObject(GPSObject obj) {
		int nodeId = root;
		
		nodeId = chooseSubtree(obj, nodeId);
		
		// ���� ���-�� �������� � ���� ������ ����������� �����������
		if(nodes[nodeId].getObjects().length < M) {
			nodes[nodeId].setObject(nodes[nodeId].getObjects().length, obj);
			
			updateMBR(nodeId);
		} else { // ���� ���-�� �������� � ���� �������� ����������� �����������
			splitNodeRStarObj(nodeId, obj); // ����� ����
		}
	}
}

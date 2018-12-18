import java.util.ArrayList;


public class RTree {
	
	public static final int M = 16; // максимальное количество обьектов в узле
	public static final int m = (int)(M * 0.4); // минимальное количество обьектов в узле
	// оси для сортировки
	private final int X = 0;
	private final int Y = 1;
	// границы для сортировки
	private final int Left = 0;
	private final int Right = 1;
//	private int Axis; // для chooseSplitAxis - по какой оси будет разделение
//	private int Bounds; // граница по какой будет идти сортировка (левая\правая)

	private RTNode[] nodes; // массив узлов дерева
	private int root; // ссылка на положение корневого узла в массиве узлов
	private int height; // высота дерева
	
	public RTree() {
		nodes = new RTNode[1];
		nodes[0] = new RTNode();
		root = 0;
		nodes[root].setLeaf(true);
	}
	
	// возвращает индекс корня
	public int getRoot() {
		return root;
	}
	
	// возвращает массив узлов дерева
	public RTNode[] getNodes() {
		return nodes;
	}
	
	// возвращает высоту дерева
	public int getHeight() {
		return height;
	}
	
	// устанавливает высоту дерева
	public void setHeight(int height) {
		this.height = height;
	}
	
	// метод определяющий является ли корнем узел с индексом nodeId
	public boolean isRoot(int nodeId) {
		if(nodeId == root)
			return true;
		else
			return false;
	}
	
	// метод создания нового узла. Возвращает индекс только что созданого узла
	public int newNode() {
		RTNode[] tmp = nodes;
		int size = nodes.length;
		
		nodes = new RTNode[size + 1];
		System.arraycopy(tmp, 0, nodes, 0, size);
		nodes[nodes.length - 1] = new RTNode();
		
		return nodes.length - 1;
	}
	
	// обновляет MBR узла
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
	
	// обновляет MBR узла
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
	
	// быстрая сортировка для обьектов по их MBR. axe - ось по которой происходит сортировка, 
	// bound - граница по которой происходит сортировка (левая/правая)
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
	
	// быстрая сортировка для узлов по их MBR. axe - ось по которой происходит сортировка, 
	// bound - граница по которой происходит сортировка (левая/правая)
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
	
	// метод выбирающий ось по которой будет происходить деление узла (в соответствии с алгоритмами R*-tree)
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
		
		for(int i = 0; i <= 1; i++) { // оси
			perimeter = 0;
			
			for(int j = 0; j <= 1; j++) { // левые и правые углы(границы)
//				node1.clearObjects();
//				node2.clearObjects();
				
				quickSortObj(arrayObj, 0, arrayObj.length - 1, i, j);
				
				for(int k = 1; k <= M - (m * 2) + 2; k++) { // высчитываем периметры во всех возможных комбинациях
					id = 0;
					node1.clearObjects();
					node2.clearObjects();
					
					while(id < (m - 1) + k) { // первому узлу присваиваются первые (MIN_M - 1) + k элементов
						node1.setObject(id, arrayObj[id]);
						id++;
					}
										
					for(id = id; id < arrayObj.length; id++) { // второму узлу присваиваем остальные элементы
						node2.setObject(id - ((m - 1) + k), arrayObj[id]);
					}
					
					updateMBR(node1);
					updateMBR(node2);
					
//					perimeter += (node1.getMbr().right - node1.getMbr().left) * 2 +
//							     (node2.getMbr().bottom - node2.getMbr().top) * 2;
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
	
	// метод выбирающий ось по которой будет происходить деление узла (в соответствии с алгоритмами R*-tree)
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
		
		for(int i = 0; i <= 1; i++) { // оси
			perimeter = 0;
			
			for(int j = 0; j <= 1; j++) { // левые и правые углы(границы)
				node1.clearChildren();
				node2.clearChildren();
				
				quickSortNd(arrayNode, 0, arrayNode.length - 1, i, j);
				
				for(int k = 1; k <= M - (m * 2) + 2; k++) { // высчитываем периметры во всех возможных комбинациях
					id = 0;
					
					while(id < (m - 1) + k) { // первому узлу присваиваются первые (MIN_M - 1) + k элементов
						node1.setChild(id, arrayNode[id]);
						id++;
					}
					
					for(id = id; id < arrayNode.length - 1; id++) { // второму узлу присваиваем остальные элементы
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
	
	// разделяет узел на 2 в соответствии с алгоритмами R*-tree (page 325:: The R*-tree: An 
	// Efficient and Robust Access Method for Points and Rectangles+)  nodeId = ссылка на узел 
	// для разделения obj = обьект для вставки
	public void splitNodeRStarObj(int nodeId, GPSObject obj) {
		int axe;
		int parentId;
		int newChildId;
		RTNode node1; 
		RTNode node2; 
		RTNode node1Min; 
		RTNode node2Min;
		GPSObject[] arrayObj = new GPSObject[M + 1];
		double areaOverlapMin, areaOverlap; // для расчета текущей и минимальной площади перекрытия областей
		double areaMin, area; // для расчета текущей и минимальной площадей областей
		
		if(!nodes[nodeId].isLeaf()) {
			return;
		}
		
		if(isRoot(nodeId)) {
			parentId = newNode(); // создаем новый узел в массиве дерева и получаем его id			
			nodes[root].setParent(parentId); // присваиваем этот id корневому узлу, как родитель
			nodes[parentId].setChild(0, root); // присваиваем новому узлу id корня как дочерний узел
			// увеличиваем уровень нового узла на 1
			nodes[parentId].setLevel(nodes[nodes[parentId].getChild(0)].getLevel() + 1);
			
			root = parentId; // изменяем id корня на id нового узла
			height++; 
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
////				
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
					if(areaOverlap == areaOverlapMin) { // если площади перекрытия одинаковые
						area = node1.area() + node2.area(); // считаем площади узлов
						
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
		
		nodes[nodeId].copy(node1Min); // вставляем первый узел на место старого (переполненого) узла
		nodes[nodeId].setParent(parentId);
		
		updateMBR(nodeId);
		
		newChildId = newNode(); // создаем новый узел в массиве узлов дерева
		nodes[newChildId].copy(node2Min); // вставляем в только что созданный узел  второй узел
		nodes[newChildId].setParent(parentId); // присваиваем id узла родителя значению parent нового узла
		
		updateMBR(newChildId);
		
		node1 = null;
		node2 = null;
		node1Min = null;
		node2Min = null;
		
		if(nodes[parentId].getChildren().length < M) { // если хватает места для вставки второго узла
			// присваиваем id нового узла
			nodes[parentId].setChild(nodes[parentId].getChildren().length, newChildId);
			updateMBR(parentId);
		} else { // если места не хватает
			splitNodeRStarNd(parentId, newChildId); // вызываем процедуру деления родительского узла
		}
	}
	
	// разделяет узел на 2 в соответствии с алгоритмами R*-tree (page 325:: The R*-tree: An 
	// Efficient and Robust Access Method for Points and Rectangles+)  splitedNodeId = ссылка 
	// на узел для разделения, insertedNodeId = узел для вставки
	public void splitNodeRStarNd(int splitedNodeId, int insertedNodeId) {
		int axe;
		int parentId;
		int newChildId;
		RTNode node1; 
		RTNode node2; 
		RTNode node1Min; 
		RTNode node2Min;
		int[] arrayNode = new int[M + 1];
		double areaOverlapMin, areaOverlap; // для расчета текущей и минимальной площади перекрытия областей
		double areaMin, area; // для расчета текущей и минимальной площадей областей 
		
		if(nodes[splitedNodeId].isLeaf()) {
			return;
		}
		
		if(isRoot(splitedNodeId)) {
			parentId = newNode(); // создаем новый узел в массиве дерева и получаем его id
			nodes[root].setParent(parentId); // присваиваем этот id корневому узлу, как родитель
			nodes[parentId].setChild(0, root); // присваиваем новому узлу id корня как дочерний узел
			// увеличиваем уровень нового узла на 1
			nodes[parentId].setLevel(nodes[nodes[parentId].getChild(0)].getLevel() + 1);
			root = parentId; // изменяем id корня на id нового узла
			height++; // увеличиваем высоту дерева
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
					if(areaOverlap == areaOverlapMin) { // если площади перекрытия одинаковые
						area = node1.area() + node2.area(); // считаем площади узлов
						
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
		
		nodes[splitedNodeId].copy(node1Min); // вставляем первый узел на место старого (переполненого) узла
		nodes[splitedNodeId].setParent(parentId);
		
		newChildId = newNode(); // создаем новый узел в массиве узлов дерева
		nodes[newChildId].copy(node2Min); // вставляем в только что созданный узел второй узел
		nodes[newChildId].setParent(parentId); // присваиваем id узла родителя значению parent нового узла
		
		node1 = null;
		node2 = null;
		node1Min = null;
		node2Min = null;

		// присваиваем значению Parent всех детей нового узла его id
		for(int i = 0; i < nodes[newChildId].getChildren().length; i++) {
			nodes[nodes[newChildId].getChild(i)].setParent(newChildId);
		}
		
		if(nodes[parentId].getChildren().length < M) { // если хватает места для вставки второго узла
			// присваиваем id нового узла
			nodes[parentId].setChild(nodes[parentId].getChildren().length, newChildId);
			updateMBR(parentId);
		} else { // если места не хватает
			this.splitNodeRStarNd(parentId, newChildId); // вызываем процедуру деления родительского узла
		}
	}
	
	// выбор поддерева узла с индексом node_id для вставки обьекта obj
	public int chooseSubtree(GPSObject obj, int nodeId) {
		int idChild;
		double minOverlapEnlargement; // минимальное увеличение перекрытия узла и обьекта
		double overlapEnlargement;
		double areaEnlargement;
		
		// массив индексов узлов с минимальным расширением
	    // перекрытия. Требуется массив потому, что вставка обьекта может вообще не расширять
	    // перекрытие, для этого занесем все индексы в массив и выберем узел с минимальным
	    // расширением площади MBR 
		int[] idChildOverlap = new int[1];
		
		// массив индексов узлов с минимальным расширением
	    // площади. Требуется массив потому, что вставка обьекта может вообще не расширять
	    // площадь MBR узла, для этого занесем все индексы в массив и выберем узел с
	    // минимальной площадью MBR
		int[] idChildArea = new int[1];
		
		// перемення для хранения индекса дочернего узла при поиске
	    // узла с наименьшей площадью MBR (в случае когда имеется несколько узлов без
	    // расширения MBR)
		int idZero;
		
		JRect enlargementMbr = new JRect(); // для расчета ИЗМЕНЕНИЯ MBR
//		double dx, dy; 
		double dspace; // для расчета УВЕЛИЧЕНИЯ MBR по x, y и площади
//		boolean hasNoEnlargement; // есть ли область без увеличения
		
		if(nodes[nodeId].isLeaf()) { // если узел конечный, возвращяем его
			return nodeId;
		}
		
//		dx = 0;
//		dy = 0;
		dspace = 9999999;
		idZero = 0;
//		hasNoEnlargement = false;
		minOverlapEnlargement = 999999;
		
		// если дочерние узлы являются конечными(листьями)
		if(nodes[nodes[nodeId].getChild(0)].isLeaf()) { 
			// определяем узел с наименьшим увеличением перекрытия 
			for(int i = 0; i < nodes[nodeId].getChildren().length; i++) {
				idChild = nodes[nodeId].getChild(i);
				overlapEnlargement = nodes[idChild].area(obj.getMbr()) - nodes[idChild].Overlap(obj.getMbr());
				
				if(overlapEnlargement <= minOverlapEnlargement) {
					// если увеличение перекытия равно предидущему минимальному
					if(overlapEnlargement == minOverlapEnlargement) {
						int[] tmp = idChildOverlap;
						int size = idChildOverlap.length;
						
						idChildOverlap = new int[size + 1];
						System.arraycopy(tmp, 0, idChildOverlap, 0, size);
						idChildOverlap[idChildOverlap.length - 1] = i;
					} else { // если увеличение перекрытия строго меньше предидущего минимального
						minOverlapEnlargement = overlapEnlargement;
						
						// если до этого не встречались два узла с одинаковым минимальным значением
						if(idChildOverlap.length == 1) { 
							idChildOverlap[0] = i;
						} else {
							idChildOverlap = new int[] {i};
						}
					}
				}
			}
			
			// если в массиве всего 1 элемент тогда найден элемент с минимальным расширением перекрытия
			if(idChildOverlap.length == 1) {
				nodeId = nodes[nodeId].getChild(idChildOverlap[0]);
				nodeId = chooseSubtree(obj, nodeId); // рекурсивно вызываем процедуру выбора поддерева
				
				return nodeId;
			} 
		} else { // если же дочерние узлы не конечные			     
			idChildOverlap = new int[nodes[nodeId].getChildren().length];
			
			// скопируем индексы в массив idChild_overlap, так как дальше процедура работает с этим 
		     // массивом(на случай если дочерние узлы конечные и имеется несколько узлов с одинаковым 
		     // увеличением перекрытия, тогда в idChild_overlap будут индексы на эти узлы)
			for(int i = 0; i < nodes[nodeId].getChildren().length; i++) {
				idChildOverlap[i] = i;
			}
		}
			
		// определяем узел с наименьшим увеличением площади
		for(int i = 0; i < idChildOverlap.length; i++) {
			idChild = nodes[nodeId].getChild(idChildOverlap[i]);
			
			enlargementMbr.left = Math.min(obj.getMbr().left, nodes[idChild].getMbr().left);
			enlargementMbr.top = Math.min(obj.getMbr().top, nodes[idChild].getMbr().top);
			enlargementMbr.right = Math.max(obj.getMbr().right, nodes[idChild].getMbr().right);
			enlargementMbr.bottom = Math.max(obj.getMbr().bottom, nodes[idChild].getMbr().bottom);
			
			areaEnlargement = nodes[idChild].area(enlargementMbr) - nodes[idChild].area();
			
			if(areaEnlargement < dspace) {
				if(areaEnlargement == dspace) { // если увеличение площади равно предидущему минимальному
					int[] tmp = idChildArea;
					int size = idChildArea.length;
					
					idChildArea = new int[size + 1];
					System.arraycopy(tmp, 0, idChildArea, 0, size);
					idChildArea[idChildArea.length - 1] = i;
				} else { // если увеличение площади строго меньше предидущего минимального
					dspace = areaEnlargement;
					
					// если до этого не встречались два узла с одинаковым минимальным значением
					if(idChildArea.length == 1) {
						idChildArea[0] = i;
					} else { // если же встречались, тогда установим длину массива равной 1
						idChildArea = new int[] {i};
					}
				}
			}
		}	
		
		// если в масиве всего один элемент, тогда найден узел с минимальным расширением MBR
		if(idChildArea.length == 1) {
			nodeId = nodes[nodeId].getChild(idChildArea[0]);
			nodeId = chooseSubtree(obj, nodeId); // рекурсивно вызываем процедуру выбора поддерева
		} else { // в противном случае (имееться несколько узлов без расширения MBR либо с одинаковым расширением) 
			     // находим узел с минимальной площадью MBR
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

	
	// метод поиска обьектов пересекающихся с областью mbr
	public void findObjectsInArea(JRect mbr, int nodeId, ArrayList<Integer> obj) {		
		if(isRoot(nodeId)) {
			obj.clear();
		}
		
		if(!nodes[nodeId].isLeaf()) {
			for(int i = 0; i < nodes[nodeId].getChildren().length; i++) {
				if(nodes[nodes[nodeId].getChild(i)].isIntersected(mbr)) {
					findObjectsInArea(mbr, nodes[nodeId].getChild(i), obj);
				}
			}
		} else {
			for(int i = 0; i < nodes[nodeId].getObjects().length; i++) {
				if(nodes[nodeId].isIntersected(mbr, nodes[nodeId].getObject(i).getMbr())) {
					obj.add(nodes[nodeId].getObject(i).getId());
				}
			}
		}
	}
	
	// метод для поиска обьектов попадающих в область mbr. Возвращает массив содержащий индексы на фигуры 
	// в массиве фигур(обьектов. т.е. домов, рек итд)
	public void findObjectsInArea(JRect mbr, ArrayList<Integer> obj) {
		findObjectsInArea(mbr, root, obj);
	}
	
	// метод для вставки обьекта в дерево
	public void insertObject(GPSObject obj) {
		int nodeId = root;
		
		nodeId = chooseSubtree(obj, nodeId);
		
		// если количетсво обьектов в узле меньше максимально допустимого
		if(nodes[nodeId].getObjects().length < M) {
			nodes[nodeId].setObject(nodes[nodeId].getObjects().length, obj);
			
			updateMBR(nodeId);
		} else { // если количество обьектов в узле достигло максимально допустимое
			splitNodeRStarObj(nodeId, obj); // делим узел
		}
	}
}

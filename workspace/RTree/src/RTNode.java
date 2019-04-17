
public class RTNode {
	
	private JRect MBR; // �������������� ������������� ����
	private int parent; // ������ � ������� ����� ������, ����������� �� ����-��������
	private int[] children; // ������ �������� �������� ����� � ������� ����� ������
	// ������ � ���������. (������ ������ ����������� ��� ������������� � ������(������) �� ������� � 
	// ������� �����(����, ����, ���)
	private GPSObject[] objects; 
	private boolean isLeaf; // �������� ������������ �������� �� ���� ���� ��������(������)
	private int level; // ������� ���� � ������ (0=����)
	
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
	
	// ����� ����������� ����
	public void copy(RTNode node) {		
		objects = new GPSObject[node.getObjects().length];
		children = new int[node.getChildren().length];
		
		if(objects.length > 0) {
			for(int i = 0; i < node.getObjects().length; i++) {
				
				objects[i] = new GPSObject();
				objects[i].setId(node.getObject(i).getId());
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
	
	// �������� ������ ��������
	public void clearObjects() {
		objects = new GPSObject[0];
	}
	
	// ������� ������ �������� �����
	public void clearChildren() {
		children = new int[0];
	}
	
	// ���������� ����������� �������������� ������������� ����
	public JRect getMbr() {
		return MBR;
	}
	
	// ������������� ����������� �������������� ������������� ����
	public void setMbr(JRect mbr) {
		this.MBR.left = mbr.left;
		this.MBR.top = mbr.top;
		this.MBR.right = mbr.right;
		this.MBR.bottom = mbr.bottom;
	}
	
	// ���������� �������� ������������ �������� �� ���� ���� ��������(������)
	public boolean isLeaf() {
		return isLeaf;
	}
	
	// ������������� �������� ������������ �������� �� ���� ���� ��������(������)
	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	
	// ���������� ������ �������� �������� ����� � ������� ����� ������
	public int[] getChildren() {
		return children;
	}
	
	// ���������� ������ ���� �� ������� ����� ������
	public int getChild(int index) {
//		if(children.length > index)
			return children[index];
		
//		return -1;
	}
	
	// ��������� ������ ��������� ���� � ������ ����� ������
	public void setChild(int index, int nodeId) {
		if(children.length > index && children.length != 0) {
			children[index] = nodeId;
			isLeaf = false;
		} else {
			if(index >= 0 && index < RTree.M) {
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
	
	// ���������� ������ � ���������
	public GPSObject[] getObjects() {
		return objects;
	}
		
	// ���������� ������ �� ������� ��������
	public GPSObject getObject(int index) {
//		if(objects.length > index)
			return objects[index];
		
//		return null;
	}
	
	// ��������� ������ � ������ � ���������
	public void setObject(int index, GPSObject obj) {
		if(objects.length > index && objects.length != 0) {
			objects[index] = obj;
			isLeaf = true;
		} else {
			if(index >= 0 && index < RTree.M) {
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
	
	// �������� ������ �������� ����� ��������
	public void setNewObjects(GPSObject[] objs) {
		objects = objs;
	}
	
	// ���������� ������ � ������� ����� ������, ����������� �� ����-��������
	public int getParent() {
		return parent;
	}
	
	// ������������� ������ � ������� ����� ������, ����������� �� ����-��������
	public void setParent(int parentId) {
		if(parentId >= 0)
			parent = parentId;
	}
	
	// ���������� ������� ���� � ������ (0=����)
	public int getLevel() {
		return level;
	}
	
	// ������������� ������� ���� � ������ (0=����)
	public void setLevel(int level) {
		if(level >= 0)
			this.level = level;
	}
	
	// ����� ������������ ������������ �� ��� ������� mbr1, mbr2
	public boolean isIntersected(JRect mbr1, JRect mbr2) {
		if(mbr1.left <= mbr2.right && mbr1.top <= mbr2.bottom) {
			if(mbr1.right >= mbr2.left && mbr1.bottom >= mbr2.top)
				return true;
		}
		
		return false;
	}
	
	// ����� ������������ ������������ �� MBR ���� � mbr ���������� ������
	public boolean isIntersected(JRect mbr) {
		if(MBR.left <= mbr.right && MBR.top <= mbr.bottom) {
			if(MBR.right >= mbr.left && MBR.bottom >= mbr.top)
				return true;
		}
		
		return false;
	}
	
	// ���������� ������� ���������� MBR ���� � �������� �������
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
	
	// ���������� ������� MBR ����
	public double area() {
		return (MBR.right - MBR.left) * (MBR.bottom - MBR.top);
	}
	
	// ���������� ������� MBR
	public double area(JRect mbr) {
		return (mbr.right - mbr.left) * (mbr.bottom - mbr.top);
	}
	
	// ���������� �������� MBR
	public double margin() {
		return ((MBR.right - MBR.left) + (MBR.bottom - MBR.top)) * 2;
	}
}

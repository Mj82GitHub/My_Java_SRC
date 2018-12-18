package Triangulation;

public class CompareFunc {

	/**
	 * ���������� ��� ������� (�� ����������).
	 * 
	 * @param v ������ �������
	 * @param w ������ �������
	 * @return 0 - ���� ������� �����, 1 - ���� ������ ������� ������ ������, 
	 * -1 - ���� ������ ������� ������ ������
	 */
	
	static int cmp(Mj_Vertex v, Mj_Vertex w) {
		if(v.equalsVertex(w)) {
			// ��������� ���� ������� � ��� X (������ ������� �������)
			Edge edge_v = new Edge(v.point(), ((Mj_Vertex) v.next()).point());
			Edge edge_w = new Edge(w.point(), ((Mj_Vertex) w.next()).point());
			
			// ��������� �������� ���� �����
			double angle_a = Math.round((edge_v.dest.subtraction(edge_v.org)).polarAngle());
			double angle_b = Math.round((edge_w.dest.subtraction(edge_w.org)).polarAngle());
				
			if(angle_a == angle_b)
				return 0;
			else if((angle_a > 0 && angle_a <= 180))
				return 1;
			else if((angle_a > 180 && angle_a <= 360))
				return -1;
		} else {
			if(v.point().less(w.point()))
				return -1;
			else if(v.point().more(w.point()))
				return 1;
			else 
				return 0;
		}
		
		return 0;
	}
	
	/**
	 * ���������� ��� ������� (�� ����������). ��������� �����������
	 * ��� X, ������� �������� ������.
	 * 
	 * @param v ������ �������
	 * @param w ������ �������
	 * @return 0 - ���� ������� �����, -1 - ���� ������ ������� ������ ������, 
	 * 1 - ���� ������ ������� ������ ������
	 */
	
	static int rightToLeftCmp(Mj_Vertex v, Mj_Vertex w) {
		int result = cmp(v, w);		
		
		if(v.point().x == w.point().x && v.point().y < w.point().y)
			return -1;
		else if(v.point().x == w.point().x && v.point().y > w.point().y)
			return 1;
		else if(result == -1)
			result = 1;
		else if(result == 1)
			result = -1;
		
		return result;
	}
	
	/**
	 * ���������� ��� ������� (�� ����������). ��������� �����������
	 * ��� X, ������� �������� �����.
	 * 
	 * @param v ������ �������
	 * @param w ������ �������
	 * @return 0 - ���� ������� �����, 1 - ���� ������ ������� ������ ������, 
	 * -1 - ���� ������ ������� ������ ������
	 */
	
	static int leftToRightCmp(Mj_Vertex v, Mj_Vertex w) {
		return cmp(v, w);
	}
}

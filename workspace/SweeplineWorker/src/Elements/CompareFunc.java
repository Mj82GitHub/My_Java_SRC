package Elements;

public class CompareFunc {

	static int edgeCmp2(Edge a, Edge b) {
		double ya = a.getY(Intersects.curx - Intersects.epselon);
		double yb = b.getY(Intersects.curx - Intersects.epselon);
		
		if(ya < yb)
			return -1;
		else if(ya > yb)
			return 1;
		
		double ma = a.slope();
		double mb = b.slope();
		
		if(ma > mb)
			return -1;
		else if(ma < mb)
			return 1;
				
		return 0;
	}
	
	static int eventCmp(EventPoint a, EventPoint b) {
		if(a.p.less(b.p))
			return -1;
		else if(a.p.more(b.p))
			return 1;
		
		return 0;
	}
	
	/**
	 * ���������� ��� ������� (�� ����������).
	 * 
	 * @param v ������ �������
	 * @param w ������ �������
	 * @return 0 - ���� ������� �����, 1 - ���� ������ ������� ������ ������, 
	 * -1 - ���� ������ ������� ������ ������
	 */
/*	
	static int cmp(Vertex v, Vertex w) {
		if(v.point().less(w.point()))
			return -1;
		else if(v.point().more(w.point()))
			return 1;
		else
			return 0;
	}
*/	
	/**
	 * ���������� ��� ������� (�� ����������). ��������� �����������
	 * ��� X, ������� �������� ������.
	 * 
	 * @param v ������ �������
	 * @param w ������ �������
	 * @return 0 - ���� ������� �����, -1 - ���� ������ ������� ������ ������, 
	 * 1 - ���� ������ ������� ������ ������
	 */
/*	
	static int rightToLeftCmp(Vertex v, Vertex w) {
		int result = cmp(v, w);
		
		if(result == -1)
			result = 1;
		else if(result == 1)
			result = -1;
		
		return result;
	}
*/	
	/**
	 * ���������� ��� ������� (�� ����������). ��������� �����������
	 * ��� X, ������� �������� �����.
	 * 
	 * @param v ������ �������
	 * @param w ������ �������
	 * @return 0 - ���� ������� �����, 1 - ���� ������ ������� ������ ������, 
	 * -1 - ���� ������ ������� ������ ������
	 */
/*	
	static int leftToRightCmp(Vertex v, Vertex w) {
		return cmp(v, w);
	}
*/
}

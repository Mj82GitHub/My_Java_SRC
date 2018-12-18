import Elements.Edge;
import Elements.EventPoint;
import Elements.Intersects;
import Elements.List;
import Elements.Point;


public class MySweepline {

/*	private double[] array = new double[] { 100, 250, 300, 100,
                                            150, 300, 350, 400, 
                                            200, 400, 400, 250,
                                            350, 100, 750, 300,
                                            450, 350, 950, 100,
                                            950, 200, 1100, 200,
                                            800, 400, 950, 250,
                                            1000, 150, 1100, 350 }; */
	
/*	private double[] array = new double[] { 100, 100, 350, 150,
                                            200, 200, 350, 100, 
                                            150, 400, 400, 300,
                                            450, 50, 800, 200,
                                            500, 100, 750, 400,
                                            600, 350, 750, 100,
                                            550, 400, 800, 350,
                                            900, 150, 1100, 300,
                                            850, 250, 1000, 250,
                                            1050, 350, 1150, 150 };*/
	private double[] array = new double[] { 100,600, 100, 100,
			                                250,150, 350,150};
	
	Edge[] edges;
	Intersects intersects;
	List<EventPoint> list;
	
	public MySweepline() {
		edges = new Edge[array.length / 4];
		insertPoints(array);
		
		intersects = new Intersects();
		list = intersects.intersectSegments(edges, edges.length);
		
		list.next();
		
		for(int i = 0; i < list.length(); i++) {
			System.out.println("List " + (i + 1) + ": [" + list.val().p.x + " - " + list.val().p.y + "]");
			list.next();
		}
	}
	
	private void insertPoints(double[] array) {		
		for(int i = 0, j = 0; j < array.length; i++, j+=4) {
			edges[i] = new Edge(new Point(array[j], array[j + 1]),
				                new Point(array[j + 2], array[j + 3]));
		}
	}
	
	public static void main(String[] args) {		
		new MySweepline();
	}
}

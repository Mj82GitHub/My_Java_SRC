package Elements;

import Elements.Edge.Intersect;

public class RightEndpoint extends EventPoint {

	public Edge e;
	
	public RightEndpoint(Edge e) {
		this.e = e;
		p = (e.org.less(e.dest)) ? e.dest : e.org;
	}
	
	public void handleTransition(RandomizedSearchTree<Edge> sweepline, 
                                 RandomizedSearchTree<EventPoint> schedule,
                                 List<EventPoint> result) {
		Edge b = sweepline.find(e);
		Edge c = sweepline.next();
		
		sweepline.prev();
		
		Edge a = sweepline.prev();
		
		if(a != null && c != null && (a.cross(c, Edge.t) == Intersect.SKEW_CROSS)) {
			Point p = a.point(Edge.t);
			
			if(Intersects.curx < p.x)
				schedule.insert(new Crossing(a, c, p));
		}
		
		sweepline.remove(b);
	}
}

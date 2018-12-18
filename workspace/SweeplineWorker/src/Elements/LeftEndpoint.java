package Elements;

import Elements.Edge.Intersect;

public class LeftEndpoint extends EventPoint {

	public Edge e;
	
	public LeftEndpoint(Edge e) {
		this.e = e;
		p = (e.org.less(e.dest)) ? e.org : e.dest;
	}
	
	public void handleTransition(RandomizedSearchTree<Edge> sweepline, 
                                 RandomizedSearchTree<EventPoint> schedule,
                                 List<EventPoint> result) {
		Edge b = sweepline.insert(e);
		Edge c = sweepline.next();
		
		sweepline.prev();
		
		Edge a = sweepline.prev();	
		
		if(a != null && c != null && (a.cross(c, Edge.t) == Intersect.SKEW_CROSS)) {
			Point p = a.point(Edge.t);
			
			if(Intersects.curx < p.x) {
				Crossing cev = new Crossing(a, c, p);
				schedule.remove(cev).delete_EventPoint();
			}
		}
		
		if(c != null && (b.cross(c, Edge.t) == Intersect.SKEW_CROSS))
			schedule.insert(new Crossing(b, c, b.point(Edge.t)));
		
		if(a != null && (b.cross(a, Edge.t) == Intersect.SKEW_CROSS))
			schedule.insert(new Crossing(a, b, b.point(Edge.t)));
	}
}

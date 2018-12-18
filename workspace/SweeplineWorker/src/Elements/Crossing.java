package Elements;

import Elements.Edge.Intersect;

public class Crossing extends EventPoint {

	public Edge e1;
	public Edge e2;
	
	public Crossing(Edge e1, Edge e2, Point p) {
		this.e1 = e1;
		this.e2 = e2;
		this.p = p;
	}
	
	public void handleTransition(RandomizedSearchTree<Edge> sweepline, 
                                 RandomizedSearchTree<EventPoint> schedule,
                                 List<EventPoint> result) {
		Edge b = sweepline.find(e1);
		Edge a = sweepline.prev();
		Edge c = sweepline.find(e2);
		Edge d = sweepline.next();
		
		if(a != null && (a.cross(c, Edge.t) == Intersect.SKEW_CROSS)) {
			Point p = a.point(Edge.t);
			
			if(Intersects.curx < p.x)
				schedule.insert(new Crossing(a, c, p));
		}
		
		if(d != null && (d.cross(b, Edge.t) == Intersect.SKEW_CROSS)) {
			Point p = d.point(Edge.t);
			
			if(Intersects.curx < p.x)
				schedule.insert(new Crossing(b, d, p));
		}
		
		if(a != null && (a.cross(b, Edge.t) == Intersect.SKEW_CROSS)) {
			Point p = a.point(Edge.t);
			
			if(Intersects.curx < p.x) {
				Crossing cev = new Crossing(a, b, p);
				schedule.remove(cev).delete_EventPoint();
			}
		}
		
		if(d != null && (d.cross(c, Edge.t) == Intersect.SKEW_CROSS)) {
			Point p = d.point(Edge.t);
			
			if(Intersects.curx < p.x) {
				Crossing cev = new Crossing(c, d, p);
				schedule.remove(cev).delete_EventPoint();
			}
		}
		
		sweepline.remove(b);
		Intersects.curx += 2 * Intersects.epselon;
		sweepline.insert(b);
		Intersects.curx -= 2 * Intersects.epselon;
		
		result.append(this);
	}
}

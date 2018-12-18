package Elements;

public class Intersects {

	public static double curx; // “екуща€ координата x на сканирующей линии
	public static double epselon = 0.0000000001;
	
	public List<EventPoint> intersectSegments(Edge[] s, int n) {
		RandomizedSearchTree<EventPoint> schedule = buildSchedule(s, n);
		RandomizedSearchTree<Edge> sweepline = new RandomizedSearchTree<Edge>(CompareFunc::edgeCmp2);
		List<EventPoint> result = new List<EventPoint>();
		
		while(!schedule.isEmpty()) {
			EventPoint ev = schedule.removeMin();
			curx = ev.p.x;
			ev.handleTransition(sweepline, schedule, result);
		}
		
		return result;
	}
	
	public RandomizedSearchTree<EventPoint> buildSchedule(Edge[] s, int n) {
		RandomizedSearchTree<EventPoint> schedule = new RandomizedSearchTree<EventPoint>(CompareFunc::eventCmp);
		
		for(int i = 0; i < n; i++) {
			schedule.insert(new LeftEndpoint(s[i]));
			schedule.insert(new RightEndpoint(s[i]));
		}
		
		return schedule;
	}
}

package Elements;

public class EventPoint implements EventPointImpl{

	public Point p;
	
	public void delete_EventPoint() {
		try {
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

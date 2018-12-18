import java.awt.Point;

public class GPSObject {

	private int id;
	private JRect mbr;
	private Point xy;
	
	public GPSObject() {
		id = 0;
		mbr = new JRect();
		xy = new Point();
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public JRect getMbr() {
		return mbr;
	}
	
	public Point getPoint() {
		return xy;
	}
	
	public void setMbr(JRect mbr) {
		this.mbr.left = mbr.left;
		this.mbr.top = mbr.top;
		this.mbr.right = mbr.right;
		this.mbr.bottom = mbr.bottom;
	}
	
	public void setPoint(Point xy) {
		this.xy = xy;
	}
}

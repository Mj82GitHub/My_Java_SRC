import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class RTreeScreen extends JFrame {
	
	private static final long serialVersionUID = 1L;
	// ����� �������� ������ ��������������� ��� �������������� � ������� ��������
	// ���������� ���������
	private Toolkit toolkit;
	// ������ ���� ����. ���������
	private Dimension screenSize;
	private PaintPanel pp;
	private MouseCapture mm;
	
	private ArrayList<GPSObject> points;
	
	static public int num = 555;
	
	// �����������
	public RTreeScreen() {
		super("R*-tree");
		
		points = new ArrayList<GPSObject>();
//		setPoints();
		setRandomPoints();
			
		toolkit=Toolkit.getDefaultToolkit();
		// �������� ������� ������
		screenSize=toolkit.getScreenSize(); 
		// ���� �� ������������ �� ���� �����, �� ������� ���� ����������
		// ������������� �������� ������
		setSize(screenSize.width, screenSize.height-35);
		// ���� ��� ������� ���������� ������������ �� ���� �����
		setExtendedState(MAXIMIZED_BOTH); 
		// ����������� ������ ���� ����. ���������
		setMinimumSize(new Dimension(640, 480));
		// ��� ������� �� "�������" ���� �����������
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		pp = new PaintPanel(screenSize, points);		
		this.add(pp);
		
		mm = new MouseCapture(pp.getRect(), pp);
		pp.addMouseMotionListener(mm);
		pp.addMouseListener(mm);
		
		setVisible(true);
	}
	
	public void setRandomPoints() {
		Random rndX = new Random();
		Random rndY = new Random();
		
		GPSObject tmp;
		
		for(int i = 0; i < num; i++) {
			int leftRight = rndX.nextInt(1200) + 50;
			int topBottom = rndY.nextInt(600) + 50;
//			int d = rndX.nextInt(50);
			int d = 10;
			tmp = new GPSObject();
			tmp.setId(i);
			tmp.setMbr(new JRect(leftRight, topBottom, leftRight + d, topBottom + d));
			
			points.add(tmp);
		}
		
		System.out.println("SIZE points = : " + points.size());
	}
	
	public static void main(String[] args) {
		// ������ ���������� � ������ ��������� �������
		SwingUtilities.invokeLater(new Runnable() {
		
			@Override
			public void run() {
				// ������������� ������� ��� ����������
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException | UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				
				new RTreeScreen();
			}
		});		
	}
	
	class MouseCapture implements MouseListener, MouseMotionListener {
		boolean pressed = false;
		int press_x, press_y;
		JRect r;
		PaintPanel pp;
		
		public MouseCapture(JRect r, PaintPanel pp) {
			this.r = r;
			this.pp = pp;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {
				
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			
			r.set(e.getX() - 100,
				  e.getY() - 200,
				  e.getX() + 100,
				  e.getY() + 200);
			pp.repaint();
			pp.find();
			pp.delete();
			pp.nodesSize();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
		
		public boolean isIntersected(int x, int y) {
			if(r.left <= x && r.top <= y) {
				if(r.right >= x && r.bottom >= y)
					return true;
			}
			
			return false;
		}
	}
}

class PaintPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private ArrayList<GPSObject> points;
	private RTree tree;
	ArrayList<Integer> gps;
	HashMap<Integer, GPSObject> objs;
	JRect r;
	int nodeSize = 0;
	int objSize = 0;
	
	public PaintPanel(Dimension size, ArrayList<GPSObject> points) {
		this.setOpaque(true);
		this.setPreferredSize(size);
		this.points = points;
		tree = new RTree();
		
		// ������������ ��������������� �������� ����� ��������
		objs = new HashMap<>();
		
		System.out.println("Start creating tree ...");		
		for(int i = 0; i < points.size(); i++) {
			tree.insertObject(points.get(i));
			objs.put(points.get(i).getId(), points.get(i));
		}
		
		nodeSize = tree.getNodes().length;
		objectsSize();
		
		System.out.println("Stop creating tree.");
		
		System.out.println("Start finding ...");
		gps = new ArrayList<Integer>();
		r = new JRect(100, 100, 300, 550);
		System.out.println("Stop finding .");
		
		// �����
//	    tree.findObjectsInArea(r, gps);	    
	}
	
	public JRect getRect() {
		return r;
	}
	
	public void find() {
		gps.clear();
		// �����
		tree.findObjectsInArea(r, gps);
	}
	
	public void find(JRect rr) {
		gps.clear();
		// �����
		tree.findObjectsInArea(rr, gps);
	}
	
	public void delete() {
		// �������� �������� �����
		for (int i = 0; i < gps.size(); i++) {
			tree.deleteObject(objs.get(gps.get(i)));
		}
		
		this.repaint();
	}
	
	// ���������� ����� ������
	public void nodesSize() {
		nodeSize = tree.getNodes().length;
		objectsSize();
	}
	
	public void objectsSize() {
		objSize = 0;
		
		for(RTNode node : tree.getNodes()) {
			objSize += node.getObjects().length;
		}
	}
		
	public void paintComponent(Graphics g) {
		super.paintComponent(g);		
		Graphics2D g2=(Graphics2D)g;
		
		for(int i = 0; i < tree.getNodes().length; i++) {
			if(tree.getNodes()[i].getParent() < 0) {
				// ������				
				g2.setColor(Color.GREEN);
				g2.setStroke(new BasicStroke(3.0f));
				g2.drawRect(tree.getNodes()[i].getMbr().left, 
		     	            tree.getNodes()[i].getMbr().top, 
			                tree.getNodes()[i].getMbr().width(), 
			                tree.getNodes()[i].getMbr().height());
			}
		}
		
		// ����� ������
		g2.setColor(Color.DARK_GRAY);
		g2.setStroke(new BasicStroke(5.0f));
		g2.drawRect(r.left, 
     	            r.top, 
	                r.width(), 
	                r.height());	
		
		for(int j = 0; j < tree.getNodes().length; j++) {
			if(tree.getNodes()[j].getParent() >= 0 && !tree.getNodes()[j].isLeaf()) {
				// ����
				g2.setColor(Color.BLUE);
				g2.setStroke(new BasicStroke(2.0f));
				g2.drawRect(tree.getNodes()[j].getMbr().left, 
			     	        tree.getNodes()[j].getMbr().top, 
				            tree.getNodes()[j].getMbr().width(), 
				            tree.getNodes()[j].getMbr().height());
			}
		}
		
		for(int i = 0; i < gps.size(); i++) {
			for(int j = 0; j < points.size(); j++) {
				g2.setColor(Color.MAGENTA);
				g2.setStroke(new BasicStroke(8.0f));
			
				if(gps.get(i) == points.get(j).getId()) {
					g2.drawRect(points.get(j).getMbr().left,
				    	        points.get(j).getMbr().top, 
				    	        points.get(j).getMbr().width(),
				    	        points.get(j).getMbr().height());
				}
			}
		}
		
		for(int k = 0; k < tree.getNodes().length; k++) {
			if(tree.getNodes()[k].isLeaf()) {
				// ������
				g2.setColor(Color.RED);
				g2.setStroke(new BasicStroke(1.0f));
				g2.drawRect(tree.getNodes()[k].getMbr().left, 
			     	        tree.getNodes()[k].getMbr().top, 
				            tree.getNodes()[k].getMbr().width(), 
				            tree.getNodes()[k].getMbr().height());
				// �������
				for(int l = 0; l < tree.getNodes()[k].getObjects().length; l++) {
					g2.setColor(Color.BLACK);
					g2.setStroke(new BasicStroke(3.0f));
					g2.drawRect(tree.getNodes()[k].getObject(l).getMbr().left, 
				     	        tree.getNodes()[k].getObject(l).getMbr().top, 
					            tree.getNodes()[k].getObject(l).getMbr().width(), 
					            tree.getNodes()[k].getObject(l).getMbr().height());					
				}
			}
		}
		
		g2.setColor(Color.BLACK);
		g2.setFont(new Font(null, 0, 20));
		g2.drawString("R*-Tree sort.", 20, 30);
		g2.drawString("���������� �������� = " + RTreeScreen.num, 300, 30);
		g2.drawString("���������� ����� = " + nodeSize + "     ���-�� �������� = " + objSize, 700, 30);
		
	}
}

package de.dualuse.util.geom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class IntersectionTest {

	public static void main(String[] args) {
		
		
		JFrame f = new JFrame();
		
		f.setContentPane(new JComponent() {
			
			double D = 1/0d;//1e6;
			
			Point2D A = new Point2D.Double(100,100);
			Point2D B = new Point2D.Double(150,500);
			
			
			Point2D P = new Point2D.Double(300, 600);
			Point2D Q = new Point2D.Double(300, -D);
			
			Point2D[] ABPQ = {A,B,P,Q};
			
			MouseMotionListener mover = new MouseMotionAdapter() {

				@Override public void mouseDragged(MouseEvent e) {
					Point2D M = e.getPoint();
					Arrays.stream(ABPQ).reduce((A,B)->A.distance(M)<B.distance(M)?A:B).get().setLocation( M );
					repaint();
						
				}
			};
			
			{
				addMouseMotionListener(mover);
			}
			
			@Override
			protected void paintComponent(Graphics g) {
				if (Edge.linesIntersect(A.getX(), A.getY(), B.getX(), B.getY(), P.getX(), P.getY(), Q.getX(), Q.getY())) {
					g.setColor(new Color(1, 0, 0, 0.1f));
					g.fillRect(0, 0, getWidth(), getHeight());
					g.setColor(Color.BLACK);
				}
				
				
				Graphics2D g2 = Graphics2D.class.cast(g.create());
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				double R = 5;
				for (Point2D M: ABPQ)
					g2.fill(new Ellipse2D.Double(M.getX()-R, M.getY()-R, 2*R, 2*R));
				
				g2.draw(new Line2D.Double(A, B));
				g2.draw(new Line2D.Double(P, Q));
				
				g2.dispose();
			}
			
		});
		
		f.setBounds(100, 100, 800, 800);
		f.setVisible(true);
	}
}

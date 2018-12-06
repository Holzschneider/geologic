package de.dualuse.util.geom;

import static java.lang.Math.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class DelauneyTest {
	
	static public<T> void traverse( Edge<T> e, Consumer<Edge<T>> c ) {
		List<Edge<T>> init = Arrays.asList(e);
		HashSet<Edge<T>> done = new HashSet<Edge<T>>( );
		ArrayDeque<Edge<T>> todo = new ArrayDeque<Edge<T>>( init );
		
		while (!todo.isEmpty()) {
			Edge<T> current = todo.remove();
			
//			if (current.node.value!=null && current.prev.node.value!=null)
			if (!done.contains(current))
				c.accept(current);
			
			done.add(current);
			
			if (!done.contains(current.next))
				todo.add(current.next);

			if (current.twin!=null && !done.contains(current.twin))
				todo.add(current.twin);
		}
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double radius = 1e7;
		
		Edge<Integer> a = new Edge<Integer>(Range.of(-radius,radius), Range.of(-radius,radius));
//		Edge<Integer> a = new Edge<Integer>(-1/0d, -1/0d, 1/0d, 1/0d);
		a.put(150, 140, 1);
		a.put(420, 180, 2);
		a.put(490, 390, 3);
		a.put(120, 430, 4);
		
		Random rnd = new Random(1337);
		try {
			for (int i=0;i<60;i++)
				a.put( rnd.nextInt(400)+100, rnd.nextInt(400)+100, i );
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		JFrame f = new JFrame();
		f.setContentPane(new JComponent() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
				
				
				g2.setColor(new Color(0,0,0,0.3f));
				traverse(a, e -> {
					Point2D.Double center = new Point2D.Double();
					
					e.face( f -> center.setLocation(center.x+f.node.x, center.y+f.node.y) );
					center.setLocation( center.x/3d, center.y/3d );
					
					double prevDx = center.x-e.prev.node.x, prevDy = center.y-e.prev.node.y, prevL = hypot(prevDx, prevDy);
					double curDx = center.x-e.node.x, curDy = center.y-e.node.y, curL = hypot(curDx, curDy);
					

					double dist = 2;
					g2.draw(new Ellipse2D.Double(center.x-1,center.y-1,2,2));
					g2.draw(new Line2D.Double(
							e.prev.node.x+prevDx/prevL*dist,e.prev.node.y+prevDy/prevL*dist, 
							e.node.x+curDx/curL*dist, e.node.y+curDy/curL*dist ) );
					
				});
//				traverse(a, e -> System.out.println(e) );
				
				g2.dispose();
			}
		});
		f.setBounds(100,100,800,800);
		f.setVisible(true);
	}
}










//package de.dualuse.util.geom;
//
//import static java.lang.Math.*;
//
//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.RenderingHints;
//import java.awt.geom.Ellipse2D;
//import java.awt.geom.Line2D;
//import java.util.ArrayDeque;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Random;
//import java.util.function.Consumer;
//
//import javax.swing.JComponent;
//import javax.swing.JFrame;
//
//public class DelauneyTest2 {
//	
//	static public<T> void traverse( DelauneyMesh<T> e, Consumer<DelauneyMesh<T>> c ) {
//		List<DelauneyMesh<T>> init = Arrays.asList(e);
//		HashSet<DelauneyMesh<T>> done = new HashSet<DelauneyMesh<T>>( );
//		ArrayDeque<DelauneyMesh<T>> todo = new ArrayDeque<DelauneyMesh<T>>( init );
//		
//		while (!todo.isEmpty()) {
//			DelauneyMesh<T> current = todo.remove();
//			
////			if (current.node.value!=null && current.prev.node.value!=null)
//			if (!done.contains(current))
//				c.accept(current);
//			
//			done.add(current);
//			
//			if (!done.contains(current.next))
//				todo.add(current.next);
//
//			if (current.twin!=null && !done.contains(current.twin))
//				todo.add(current.twin);
//		}
//	}
//	
//	
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
////		double radius = 1e3;
//		
//		ArrayList<Location> keys = new ArrayList<Location>();
//		
//		DelauneyMesh<Integer> a = new DelauneyMesh<Integer>(Range.of(100,700), Range.of(100,700));
//		
//		Random rng = new Random(13337);
//		for (int i=0;i<100;i++) {
//			Location l = Location.of(rng.nextInt(598)+101, rng.nextInt(598)+101);
//			a.put( l.x, l.y, i );
//			keys.add(l);
//		}
//		
////		for (int i=0;i<21;i++) {
//		for (int i=0;i<100;i++) {
//			Location l = keys.remove(rng.nextInt(keys.size()));
////			Location l = keys.get(rng.nextInt(keys.size()));
//			DelauneyMesh<Integer> b = a.locate(l.x, l.y);
//			
////			System.out.println( b.node + " vs " +l ); 
//			
//			a = b.remove(); //XXX make edge a detached edge that still works with put/remove/whatever by marking this.twin = this and next = something 
//		}
//		
//		
//		
//		Location l = Location.of( 108, 110 ); 
//////				keys.get(rng.nextInt(keys.size()));
////
////		Edge<Integer> b = a.locate(l.x, l.y);
//////		System.out.println();
////		System.out.println( b.node + " vs " +l );
//////		
////////		
//////		a = b.remove();
//		
////		Edge<Integer> ae = a.prev.prev;
////		b.remove
//		
//		DelauneyMesh<Integer> aeh = a;
//		
//		JFrame f = new JFrame();
//		f.setContentPane(new JComponent() {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			protected void paintComponent(Graphics g) {
//				DelauneyMesh<Integer> a = aeh;
//				
//				Graphics2D g2 = (Graphics2D) g.create();
//				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//				g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
//				
//				float zoom = 1.5f;
//				g2.scale(zoom,zoom);
//				
//				double R = 5;
//				g2.draw(new Ellipse2D.Double(l.x-R, l.y-R, 2*R, 2*R));
//				g2.setColor(Color.ORANGE);
//				g2.setStroke( new BasicStroke(5/zoom));
//
//				g2.draw(new Line2D.Double(a.node.x, a.node.y, a.prev.node.x, a.prev.node.y));
//				g2.setStroke( new BasicStroke(1/zoom));
//				
//				g2.setColor(new Color(0,0,0,0.3f));
//				traverse(a, e -> {
////					Point2D.Double center = new Point2D.Double();
////					
////					e.face( f -> center.setLocation(center.x+f.node.x, center.y+f.node.y) );
////					center.setLocation( center.x/3d, center.y/3d );
////					
////					double prevDx = center.x-e.prev.node.x, prevDy = center.y-e.prev.node.y, prevL = hypot(prevDx, prevDy);
////					double curDx = center.x-e.node.x, curDy = center.y-e.node.y, curL = hypot(curDx, curDy);
////					
////
////					double dist = 3;
//////					g2.draw(new Ellipse2D.Double(center.x-1,center.y-1,2,2));
////					g2.draw(new Line2D.Double(
////							e.prev.node.x+prevDx/prevL*dist,e.prev.node.y+prevDy/prevL*dist, 
////							e.node.x+curDx/curL*dist, e.node.y+curDy/curL*dist ) );
////					
////					System.out.println(e.prev.node+" -> "+e.node);
////					
//					
//					
//					double dx = e.node.x-e.prev.node.x, dy = e.node.y-e.prev.node.y, dl = hypot(dx,dy);
//					double nx = dy/dl, ny = -dx/dl;
//					
//					double M = 10, N = 2;
//					g2.setColor(new Color(0,0,0,0.3f));
//
//					g2.draw(new Line2D.Double(
//							e.prev.node.x+dx/dl*M+nx*N,e.prev.node.y+dy/dl*M+ny*N, 
//							e.node.x-dx/dl*M+nx*N, e.node.y-dy/dl*M+ny*N) );
//					g2.draw(new Line2D.Double(
//							e.node.x-dx/dl*M+nx*N*2-dx/dl*N*N, e.node.y-dy/dl*M+ny*N*2-dy/dl*N*N,
//							e.node.x-dx/dl*M+nx*N, e.node.y-dy/dl*M+ny*N) );
//					
//
//					g2.setColor(new Color(0,0,0,1f));
//					g2.draw(new Line2D.Double(
//							e.prev.node.x,e.prev.node.y, 
//							e.node.x, e.node.y) );
//
//					
////					g2.draw(new Line2D.Double(
////							e.node.x, 
////							e.node.x, e.node.y) );
//
//					
//				});
////				traverse(a, e -> System.out.println(e) );
//				
//				repaint();
//				g2.dispose();
//			}
//		});
//		f.setBounds(100,100,1200,1200);
//		f.setVisible(true);
//	}
//}
//
//
//
//
//
//
//
//
//

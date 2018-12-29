package de.dualuse.util.geom;

import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import de.dualuse.util.Geometry;


//addMouseMotionListener();

//Adapter.on(MouseMotionListener.class)
//Proxy.newProxyInstance(this.getClass().getClassLoader(), interfaces, h)

class Adapter {
	
	interface Connector<T> {
		<Q> Connector<T> on(String event, Consumer<Q> c);
		T build();
	}
	
	public static<T> Connector<T> wrap(Class<T> listenerClass) {
		
		HashMap<Method,Consumer<Object>> map = new HashMap<>();
		
		@SuppressWarnings("unchecked")
		T t = (T)Proxy.newProxyInstance(Adapter.class.getClassLoader(), new Class<?>[] { listenerClass }, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				
				map.get(method).accept(args[0]);
				return null;
			}
		});
		
		return new Connector<T>() {
//			@Override
//			public<R> Connector<T> connect(Method m, Consumer<Object> c) {
//				map.put(m, c);
//				return this;
//			}
			
			public T build() {
				return t;
			}

			@Override
			public <Q> Connector<T> on(String event, Consumer<Q> c) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
	
}


public class EdgeListInspector extends JComponent {

	private static final long serialVersionUID = 1L;
	
	
	public final AffineTransform canvasTransform = new AffineTransform();
	private final AffineTransform deltaTransform = new AffineTransform();
	
	private MouseWheelListener zoomer = new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent me) {
			double scale = pow(1.0337, -me.getWheelRotation());
			
			deltaTransform.setToIdentity();
			deltaTransform.translate(+me.getX(), +me.getY());
			deltaTransform.scale(scale, scale);
			deltaTransform.translate(-me.getX(), -me.getY());

			canvasTransform.preConcatenate(deltaTransform);
			
			repaint();
		}
	};
	
	private MouseMotionListener panner = new MouseMotionListener() {
		MouseEvent last;
		public void mouseMoved(MouseEvent e) { last = e; }

		public void mouseDragged(MouseEvent e) {
			if (last!=null) {
				deltaTransform.setToTranslation( e.getX()-last.getX(), e.getY()-last.getY());
				canvasTransform.preConcatenate(deltaTransform);
				repaint();
			} 
			mouseMoved(e);			
		}
	};
	
	
	
	public Edge<?> mesh;
	
	public EdgeListInspector(Edge<?> toBeDisplayed) {
		this();
		this.mesh = toBeDisplayed;
	}
	
	public EdgeListInspector() {
		addMouseWheelListener(zoomer);
		addMouseMotionListener(panner);
		repainter.start();
	}	
	
	Timer repainter = new Timer(500, e->repaint());
	
	double R = 20, D = 4;
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = Graphics2D.class.cast(g.create());
		
		g2.transform(canvasTransform);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		
		float S = 1/(float)canvasTransform.getScaleX();
		double A = 8, H = A/3, O = A*2;
		R = 30; D = 6;
		S = 1;

		BasicStroke solid = new BasicStroke(S, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
		BasicStroke dotted = new BasicStroke(S, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, new float[] { S }, 0);
		
		
		g2.setStroke(solid);
//		g2.setColor(new Color(0, 0, 0, 0.25f));
		Path2D line = new Path2D.Double();
		
		Edge<?> mesh = this.mesh;
		if (mesh!=null) {
			
			Point2D.Double head = new Point2D.Double(), tail = new Point2D.Double();
			HashSet<Edge<?>> edges = new HashSet<Edge<?>>();
			HashSet<Vertex<?>> vertices = new HashSet<Vertex<?>>();
			ArrayDeque<Edge<?>> todo = new ArrayDeque<>();
			todo.add(mesh);
			edges.add(mesh);
			
			while (!todo.isEmpty()) {
				Edge<?> edge = todo.removeFirst();
				
				if (drawable(edge)) {
					
					line(S, edge, tail, head);
					double x0 = tail.x, y0 = tail.y;
					double x1 = head.x, y1 = head.y;
					double dx = x1-x0, dy = y1-y0;
					double dl = sqrt(dx*dx+dy*dy);
					double nx = dy/dl, ny = -dx/dl;
					
					line.reset();
					
					double ax = +ny*A*S-nx*H*S, ay = -nx*A*S-ny*H*S;
					line.moveTo( tail.x, tail.y );
					line.lineTo( head.x, head.y );
					line.lineTo( head.x+ax, head.y+ay );
									
					g2.draw(line);
					
					vertices.add( edge.node );
					g2.setStroke(dotted);
					
					if (drawable(edge.twin)) {
						line(S, edge.twin, tail, head);

						
						line.reset();
						line.moveTo( (x1+x0)/2, (y1+y0)/2 );
						line.quadTo(
								(x1+x0)/2+dx/dl*O, (y1+y0)/2+dy/dl*O,
								(head.x+tail.x)/2+dx/dl*O, (head.y+tail.y)/2+dy/dl*O );
						g2.draw(line);

					}

					if (drawable(edge.next)) {
						line.reset();
						line.moveTo(x1, y1);
						
						line(S, edge.next,tail,head);


						double mx = tail.y-head.y, my = head.x-tail.x;
						double t = Geometry.lineIntersection(x1, y1, nx, ny, tail.x, tail.y, mx, my);
						double cx = x1+nx*t, cy = y1+ny*t, r = hypot(cx-x1, cy-y1);
						
						Ellipse2D.Double e = new Ellipse2D.Double();
						e.setFrameFromCenter(cx, cy, cx+r, cy+r);
						
						double a = Geometry.direction(x1-cx, y1-cy);
						double b = Geometry.direction(tail.x-cx, tail.y-cy);
						double d = Geometry.angle(a, b);
						
						g2.draw(new Arc2D.Double(cx-r,cy-r, r*2, r*2, -a*180/PI, -d*180/PI, Arc2D.OPEN));
					}
					g2.setStroke(solid);
				}
				
				

				Edge<?> adjacents[] = { edge.next, edge.prev, edge.twin };
				for (Edge<?> adjacent: adjacents)
					if (adjacent!=null && !edges.contains(adjacent)) {
						todo.add(adjacent);
						edges.add(adjacent);
					}
				
			}
			
			g2.setColor(new Color(0, 0, 0,0.2f));
			for (Vertex<?> v: vertices) {
				String label = ""+(""+v.value).charAt(0);
				g2.draw(new Ellipse2D.Double(v.x-(R-1)*S, v.y-(R-1)*S, 2*(R-1)*S, 2*(R-1)*S));
				Rectangle2D r = g2.getFontMetrics().getStringBounds(label, g2);
				g2.drawString(label, (float)(v.x-r.getCenterX()), (float)(v.y-r.getCenterY()-1));
			}
			
		}
		
		g2.dispose();
		
		
	}

	
	private boolean drawable(Edge<?> edge) { return edge!=null && edge.node!=null && edge.prev!=null && edge.prev.node!=null; }
	
	private void line(double S, Edge<?> edge, Point2D tail, Point2D head) {
		double x0 = edge.prev.node.x, y0 = edge.prev.node.y;
		double x1 = edge.node.x, y1 = edge.node.y;
		double dx = x1-x0, dy = y1-y0;
		double dl = sqrt(dx*dx+dy*dy);
		double nx = dy/dl, ny = -dx/dl; 
		
		double N = R-R*cos(asin(D/R));
		double ox = nx*D, oy = ny*D;
		double sx = dx/dl*(R-N), sy = dy/dl*(R-N);
		tail.setLocation( x0-ox*S+sx*S, y0-oy*S+sy*S );
		head.setLocation( x1-ox*S-sx*S, y1-oy*S-sy*S );
	}
	
	
	public static void main(String[] args) {

		Vertex<String> a = new Vertex<String>(100, 100, "A");
		Vertex<String> b = new Vertex<String>(500, 200, "B");
		Vertex<String> c = new Vertex<String>(200, 400, "C");
		
		Edge<String> ab = new Edge<String>();
		Edge<String> bc = new Edge<String>();
		Edge<String> ca = new Edge<String>();
		
		(((ca.next = ab).prev = ca).node = a).star = ca;
		(((ab.next = bc).prev = ab).node = b).star = ab;
		(((bc.next = ca).prev = bc).node = c).star = bc;
		
		Edge<String> ba = new Edge<String>();
		Edge<String> cb = new Edge<String>();
		Edge<String> ac = new Edge<String>();

		
		(ba.twin = ab).twin = ba;
		(cb.twin = bc).twin = cb;
		(ac.twin = ca).twin = ac;
		
		ac.next = ca.prev.twin;
		cb.next = bc.prev.twin;
		ba.next = ab.prev.twin;

		ac.prev = ca.next.twin;
		cb.prev = bc.next.twin;
		ba.prev = ab.next.twin;
		
		ac.node = c;
		cb.node = b;
		ba.node = a;
		
		//////////////
		JFrame f = new JFrame();
		
		f.setContentPane(new EdgeListInspector(ba));
		
		f.setBounds(100, 100, 800, 800);
		f.setVisible(true);
		
	}
}

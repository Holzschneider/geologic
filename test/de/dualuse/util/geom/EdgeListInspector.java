package de.dualuse.util.geom;

import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JComponent;
import javax.swing.Timer;

import de.dualuse.util.Geometry;



public class EdgeListInspector extends JComponent {
	private static final long serialVersionUID = 1L;
	
	interface ConsoleBuilder {
		ScriptEngine engine();
		
		default ConsoleBuilder publish(String name, Object value) {
			engine().getBindings(ScriptContext.ENGINE_SCOPE).put(name, value);
			return this;
		}
		
		default ConsoleBuilder eval(String statement) throws ScriptException {
			engine().eval(statement);
			return this;
		}
		
		default void loop() throws IOException {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
				for (String line = br.readLine(), last = line; line!=null; line = br.readLine()) try {
					Object result = engine().eval(last=(line.isEmpty()?last:line));
					if (result!=null)
						System.out.println(" -> "+result);
				} catch (Throwable thr) {
					System.err.println(thr);
				}
			}
		}
	}
	
	ConsoleBuilder createConsole() {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
		ConsoleBuilder builder = () -> engine; 
		return builder.publish("inspector", this);
	}
	
	
	public Object hud = "";
	
	/////////////
	
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
		public void mouseMoved(MouseEvent e) { 
			last = e;
			try {
				canvasTransform.inverseTransform(e.getPoint(), mouse);
				onmove.accept(e);
			} catch (NoninvertibleTransformException e1) { }
		}

		public void mouseDragged(MouseEvent e) {
			if (last!=null) {
				deltaTransform.setToTranslation( e.getX()-last.getX(), e.getY()-last.getY());
				canvasTransform.preConcatenate(deltaTransform);
				repaint();
			} 
			mouseMoved(e);			
		}
	};
	
	private MouseListener clicker = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			try {
				canvasTransform.inverseTransform(e.getPoint(), click);
				onclick.accept(e);
				repaint();
			} catch (NoninvertibleTransformException nte) { }
		}
	};
	
	public Edge<?> current = null;

	public final Point2D.Double click = new Point2D.Double(1/0d, 1/0d);
	public final Point2D.Double mouse = new Point2D.Double(1/0d, 1/0d);
	
	public Consumer<MouseEvent> onclick = e -> {};
	public Consumer<MouseEvent> onmove = e -> {};
	
	public Edge<?> mesh;
	
	public EdgeListInspector(Edge<?> toBeDisplayed) {
		this();
		this.mesh = toBeDisplayed;
	}
	
	public EdgeListInspector() {
		addMouseWheelListener(zoomer);
		addMouseMotionListener(panner);
		addMouseListener(clicker);
		repainter.start();
	}	
	
	Timer repainter = new Timer(50, e->repaint());
	
	double R = 20, D = 4;
	
	private static Color colorForEdge(Edge<?> edge, float alpha) {
		return new Color((Color.HSBtoRGB( edge.id*1337.1337f , .85f, 0.5f) & 0xFFFFFF)|((((int)(alpha*255))&0xFF)<<24),true);
	}
	
	public HashMap<String,Edge<?>> edgeMap = new HashMap<String,Edge<?>>();
	
	@Override
	protected void paintComponent(Graphics g) {
		HashMap<String,Edge<?>> edgeMap = new HashMap<String,Edge<?>>();
		
		g.drawString(hud.toString(), 10, 20);
		
		Graphics2D g2 = Graphics2D.class.cast(g.create());
		
		g2.transform(canvasTransform);
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		
		float S = 1/(float)canvasTransform.getScaleX();
		double A = 8, H = A/3, O = A*2;
		R = 50; D = 6;
//		S = 1;
		
//		S *= 1.337;
		
		

		BasicStroke solid = new BasicStroke(2*S, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
		BasicStroke selectedSolid = new BasicStroke(4*S, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
//		BasicStroke dotted = new BasicStroke(2*S, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, new float[] { 1*S,3*S }, 0);
//		BasicStroke shifted = new BasicStroke(2*S, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, new float[] { 1*S,3*S }, 2*S );
		BasicStroke dotted = new BasicStroke(2*S, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, new float[] { 3*S }, 0);
		BasicStroke shifted = new BasicStroke(2*S, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, new float[] { 3*S }, 3*S );
		
		g2.setFont(g2.getFont().deriveFont( g2.getFont().getSize2D()*S ));
		
		
		g2.setStroke(solid);
//		g2.setColor(new Color(0, 0, 0, 0.25f));
		Path2D line = new Path2D.Double();
		
		Edge<?> mesh = this.mesh;
		if (mesh!=null) {
			
			Point2D.Double head = new Point2D.Double(), tail = new Point2D.Double();
			HashSet<Edge<?>> edges = new HashSet<Edge<?>>();
			HashSet<Vertex<?>> vertices = new HashSet<Vertex<?>>();
			ArrayDeque<Edge<?>> todo = new ArrayDeque<>();
			todo.add(mesh); edges.add(mesh);
			
			while (!todo.isEmpty()) {
				Edge<?> edge = todo.removeFirst();
				
				boolean selected = (current == edge); //|| (Edge.current == edge); 
					
				if (drawable(edge)) {
					if (selected)
						g2.setStroke(selectedSolid);
					
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

					g2.setColor(colorForEdge(edge,1));
					
					String edgeLabel = edge.prev.node.value.toString()+edge.node.value.toString();
					Rectangle2D lb = g2.getFontMetrics().getStringBounds(edgeLabel, g2);
					double rt = max(lb.getMaxX(),lb.getMaxY());
					double px = (float)((tail.x+head.x)/2-nx*rt);
					double py = (float)((tail.y+head.y)/2-ny*rt);
					
					edgeMap.put(edgeLabel.toLowerCase(), edge);
					edgeMap.put(edgeLabel, edge);
					
					g2.drawString(edgeLabel, (float)(px-lb.getCenterX()), (float)(py-lb.getCenterY()));
									
					g2.draw(line);
					
					vertices.add( edge.node );
					g2.setStroke(dotted);
					
					if (drawable(edge.twin)) {
						line(S, edge.twin, tail, head);
						
						line.reset();
						line.moveTo( (x1+x0)/2, (y1+y0)/2 );
						line.quadTo(
								(x1+x0)/2+S*dx/dl*O, (y1+y0)/2+S*dy/dl*O,
								(head.x+tail.x)/2+S*dx/dl*O, (head.y+tail.y)/2+S*dy/dl*O );
						
						g2.draw(line);
					}

					g2.setColor(colorForEdge(edge,0.41f));

					if (drawable(edge.next)) {
						line(S, edge.next,tail,head);


						double mx = tail.y-head.y, my = head.x-tail.x;
						double t = Geometry.lineIntersection(x1, y1, nx, ny, tail.x, tail.y, mx, my);
						double cx = x1+nx*t, cy = y1+ny*t;;
						
						
						if (edge.next == edge.twin) {
							cx = (tail.x+x1)/2; 
							cy = (tail.y+y1)/2;
						}
							
						double r = hypot(cx-x1, cy-y1);
						
						Ellipse2D.Double e = new Ellipse2D.Double();
						e.setFrameFromCenter(cx, cy, cx+r, cy+r);
						
						double a = Geometry.direction(x1-cx, y1-cy);
						double b = Geometry.direction(tail.x-cx, tail.y-cy);
						double d = Geometry.angle(a, b);
						
						if (edge.next == edge.twin)
							g2.draw(new Arc2D.Double(cx-r,cy-r, r*2, r*2, -a*180/PI, d*180/PI, Arc2D.OPEN));
						else
							g2.draw(new Arc2D.Double(cx-r,cy-r, r*2, r*2, -a*180/PI, -d*180/PI, Arc2D.OPEN));
					}
					
					if (drawable(edge.prev)) {
						g2.setStroke(shifted);

						line(S, edge.prev,head,tail);

						double mx = tail.y-head.y, my = head.x-tail.x;
						double t = Geometry.lineIntersection(x0, y0, nx, ny, tail.x, tail.y, mx, my);
						double cx = x0+nx*t, cy = y0+ny*t;;
						
						
						if (edge.prev == edge.twin) {
							cx = (tail.x+x0)/2; 
							cy = (tail.y+y0)/2;
						}
							
						double r = hypot(cx-x0, cy-y0);
						
						Ellipse2D.Double e = new Ellipse2D.Double();
						e.setFrameFromCenter(cx, cy, cx+r, cy+r);
						
						double a = Geometry.direction(x0-cx, y0-cy);
						double b = Geometry.direction(tail.x-cx, tail.y-cy);
						double d = Geometry.angle(a, b);
						
						if (edge.prev== edge.twin)
							g2.draw(new Arc2D.Double(cx-r,cy-r, r*2, r*2, -(-a*180/PI-d*180/PI), -d*180/PI, Arc2D.OPEN));
						else
							g2.draw(new Arc2D.Double(cx-r,cy-r, r*2, r*2, -a*180/PI-d*180/PI, d*180/PI, Arc2D.OPEN));
					}
					g2.setColor(colorForEdge(edge,1));
					
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

		double L = S*5;
		g2.setStroke(solid);
		g2.setColor(Color.BLACK);
		g2.translate(click.getX(), click.getY());
		g2.draw(new Line2D.Double(-L, -L, +L, +L));
		g2.draw(new Line2D.Double(-L, +L, +L, -L));
		
		g2.dispose();
		
		this.edgeMap = edgeMap;
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
		
		Edge<String> ab = new Edge<String>("ab");
		Edge<String> bc = new Edge<String>("bc");
		Edge<String> ca = new Edge<String>("ca");
		
//		(((ca.next = ab).prev = ca).node = a).star = ca;
//		(((ab.next = bc).prev = ab).node = b).star = ab;
//		(((bc.next = ca).prev = bc).node = c).star = bc;
		((ca.next = ab).prev = ca).node = a;
		((ab.next = bc).prev = ab).node = b;
		((bc.next = ca).prev = bc).node = c;
		
		Edge<String> ba = new Edge<String>("BA");
		Edge<String> cb = new Edge<String>("CB");
		Edge<String> ac = new Edge<String>("AC");
		
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

		new EdgeListInspectorFrame(ba,EdgeListInspector.class.getSimpleName());
	}
}







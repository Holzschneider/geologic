package de.dualuse.util.geom;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class Edge<T> /*implements LocalMap<T>*/ {
	
	@Override
	public String toString() {
		if (node!=null && prev!=null && prev.node!=null)
			return "Edge"+id+"("+prev.node.value.toString()+node.value.toString()+")";
		else
			return "Edge"+id;
	}
	
	static int edgeCounter = 0;
	
	public String label = "";
	public int id = edgeCounter++;
	
	public Edge<T> next;
	public Edge<T> prev;
	public Edge<T> twin;
	
	public Vertex<T> node = null;
	
	//////////////////////////////
	
	public Edge() { }
	public Edge(String label) { this.label = label; };
	public Edge(Vertex<T> v) { this.node = v; }
	
	protected Edge<T> createEdge() {
		return new Edge<T>();
	}
	
	protected Vertex<T> createVertex(double x, double y, T value) {
		return new Vertex<T>(x,y,value);
	}
	
	////////
	private UnaryOperator<Edge<T>> starward() { return e->e.twin.prev; };
	private UnaryOperator<Edge<T>> backward() { return e->e.prev; };
	private UnaryOperator<Edge<T>> forward() { return e->e.next; };
	private Predicate<Edge<T>> looped() { return e->e==this; };

	public Edge<T> findForward(Predicate<Edge<T>> stop) { return find(stop, forward()); }
	public Edge<T> find( Predicate<Edge<T>> stop, Function<Edge<T>,Edge<T>> step ) {
		for( Edge<T> cursor = this;;cursor = step.apply(cursor) )
			if (stop.test(cursor))
				return cursor;
	}
	
	public Edge<T> walkLoop( Consumer<Edge<T>> visitor ) { return walk( looped(), forward(), visitor); }
	public Edge<T> walk(Predicate<Edge<T>> stop, Function<Edge<T>,Edge<T>> step, Consumer<Edge<T>> visitor) {
		Edge<T> cursor = this;
		
		do {
			visitor.accept(cursor);
			cursor = step.apply(cursor);
		} while ( !stop.test(cursor) ); 
		
		return cursor;
	}
	
	@Deprecated
	public Edge<T> reduce(Predicate<Edge<T>> stop, UnaryOperator<Edge<T>> step, BinaryOperator<Edge<T>> accumulator) {
		Edge<T> e = step.apply(this);
		return e.reduce(stop, step, e, accumulator );
	}
	
	public Edge<T> reduce(Predicate<Edge<T>> stop, UnaryOperator<Edge<T>> step, Edge<T> identity, BinaryOperator<Edge<T>> accumulator) {
		return reduce(stop, step, identity, accumulator);		
	}
	
	
	public<A> A reduceLoop(A accumulant, BiFunction<A,Edge<T>,A> accumulator) 
	{ return reduce( looped(), forward(), accumulant, accumulator); }
	
	public<A> A reduce(Predicate<Edge<T>> stop, UnaryOperator<Edge<T>> step, A accumulant, BiFunction<A,Edge<T>,A> accumulator) {
		Edge<T> cursor = this;
		do {
			accumulant = accumulator.apply(accumulant, cursor);
			cursor = step.apply(cursor);
		} while (!stop.test(cursor));
		return accumulant;
	}
	

	
	public <CollectionType extends Collection<? super Vertex<T>>> CollectionType collect(CollectionType collector) {
		return collectVertices(collector);
	}
	
	public <CollectionType extends Collection<? super Vertex<T>>> CollectionType collectVertices(CollectionType collector) {
		if (collector.contains(this.node))
			return collector;
		
		collector.add(this.node);
		walk( looped(), starward(), edge -> edge.twin.collectVertices(collector) ); //= this.star
		
		return collector;
	}

	protected boolean isDetached() { return node == null || prev==null || prev.node == null; }
	protected boolean isUnipolar() { return this==next; }
	protected boolean isBipolar() { return this!=next && this==next.next; }
	protected boolean isForwardTriangle() { return next.next.next == this ; }
	protected boolean isBackwardTriangle() { return prev.prev.prev == this ; }
	protected boolean isTriangle() { return isForwardTriangle() && isBackwardTriangle(); }
	protected boolean intersects(double x0, double y0, double x1, double y1) {
		System.out.println(this+" intersects "+linesIntersect(prev.node.x, prev.node.y, node.x, node.y, x0, y0, x1, y1) );
		return linesIntersect(prev.node.x, prev.node.y, node.x, node.y, x0, y0, x1, y1);
	}
	
	
	///https://stackoverflow.com/questions/1165647/how-to-determine-if-a-list-of-polygon-points-are-in-clockwise-order
	public double area() {
		return reduceLoop( 0.0, (acc,edge)->acc+(edge.next.node.x-edge.node.x)*(edge.next.node.y+edge.node.y) )/2;
	}
	
	
	public interface CentroidDefinition<C> { C defineCentroid(double x, double y); }
	public<C> C centroid(CentroidDefinition<C> c) {
		return next.centroid(c, this, node.x, node.y, 1);
	}
	
	private<C> C centroid(CentroidDefinition<C> c, Edge<T> end, double sx, double sy, int n) {
		if (end!=this) return next.centroid(c, end, sx+node.x, sy+node.y, n+1);
		else return c.defineCentroid(sx/n, sy/n);
	}
	
	
	public boolean contains(final double px, final double py) {
//		if ( this==next|| this==next.next ) // uni or bipolar mesh
//			return true;
//		else
//		if (isForwardTriangle()) 
//			return triangleIntersects(px, py, node.x, node.y, next.node.x, next.node.y, next.next.node.x, next.next.node.y);
//		else
//			return false;
		return reduceLoop(0, (i,e)->i+(e.intersects(px, py, px, 1/0d)?1:0) )  %2 == (area()<0?1:0);
	}
	
	
	public Edge<T> attach(Vertex<T> vertex) {
		Edge<T> that = this; 
		
		Edge<T> in = that.createEdge();
		Edge<T> out = that.createEdge();
		
		in.node = vertex;
		in.twin = in.next = out;
		out.prev = out.twin = in;
		
		out.next = that.next;
		in.prev = that;
		in.next = out;
		
		that.next.prev = out;
		that.next = in;
		
		out.node = that.node;
		
		in.next  = in .find( e->e.prev.node==vertex, backward() );
		out.prev = out.find( e->e.node==vertex, forward() );
		
		in.next.prev = in;
		out.prev.next = out;
		
		return this;
	}
	
	public static Edge<?> current = null;
	static<Q> Edge<Q> highlight(Edge<Q> e) { 
		current = e; 
		return e; 
	};
	
	//edge.locate(inspector.click.x, inspector.click.y)
	public Edge<T> locate(double qx, double qy) {
		highlight(this);
		if (this.contains(qx,qy)) 
			return reduceLoop( this, (a,b) -> { highlight(b); return b.node.isCloserTo(qx, qy).than( a.node ) ? b : a;} );
		
//		int n = reduceLoop( 0, (i,e) -> i+1 );
//		double cx = reduceLoop( 0.0, (sx,e) -> sx+e.node.x ) / n;
//		double cy = reduceLoop( 0.0, (sy,e) -> sy+e.node.y ) / n;
//		Edge<T> guess = reduceLoop( null, (a,b) -> a==null || b.intersects(cx, cy, qx, qy) && b.node.isCloserTo(qx, qy).than(a.node)?b:a);
		
		System.out.println(centroid( Point2D.Double::new ));
		
		Edge<T> guess = centroid( (cx,cy) -> reduceLoop( null, (a,b) -> {highlight(b); return b.intersects(cx, cy, qx, qy) && (a==null || b.node.isCloserTo(qx, qy).than(a.node))?b:a;} ) );
		return highlight(guess.twin.locate(qx, qy));
	}
	
	
	
//	public Edge<T> locate(final double qx, final double qy) {
//		if (this.twin == this) //XXX allow detached functions to work
//			return next.locate(qx, qy);
//		
//		if (this.contains(qx,qy)) { //wenn das Dreieck die qx,qy enthält
//			//alle (drei) in frage kommenden Knoten überprüfen
//			Edge<T> closest = this; 
//			double dx = closest.node.x-qx, dy = closest.node.y-qy;
//			double distanceSq = dx*dx+dy*dy;
//			
//			for (Edge<T> e = this.next;e!=this;e=e.next) {
//				dx = e.node.x-qx; dy = e.node.y-qy;
//				double dSq = dx*dx+dy*dy;
//				if (dSq>distanceSq) continue;
//				
//				closest = e;
//				distanceSq = dSq;
//			}
//			
//			return closest; //den nächsten zurückgeben
//		}
//		
//		//andernfalls weiter in Richtung Zielkoordinate wandern
//		final Vertex<T> a = this.node, b = next.node, c = prev.node;
//		final double mx = (a.x+b.x+c.x)/3.0, my = (a.y+b.y+c.y)/3.0;
//		
//		//schließlich schneidet der Weg vom Dreiecksmittelpunkt
//		//zum Ziel garantiert eine Kante, die uns dem Ziel näher bringt  
//		if (linesIntersect(	mx, my, qx, qy, a.x,a.y, b.x,b.y) )
//			return next.twin.locate(qx,qy);
//		//else
//		if (linesIntersect(	mx, my, qx, qy, b.x, b.y, c.x, c.y) )
//			return prev.twin.locate(qx,qy);
//		//else
//		if (linesIntersect(	mx, my, qx, qy, c.x, c.y, a.x, a.y) )
//			return this.twin.locate(qx,qy);
//		
//		return null; // keine geschnitten? -> null
//	}

	
	
	
	
	/**
	 * Entfernt den Knoten auf den die Kante zeigt aus dem Graphen.
	 * Dabei wird auch zwangsläufig die Kante aus dem Graphen entfernt, da 
	 * sie Teil des Sterns des zu entfernenden Knotens ist. Der sternförmige 
	 * Loch-Bereich des Graphen wird retrianguliert, die Delaunay-Eigenschaft
	 * wird anschließend wieder hergestellt.
	 *    
	 * @return einen gültige Kantenreferenz. Zeigt auf die zuletzt 
	 * korrigierte Randkante des retriangulierten Sternlochs.   
	 */	
//	public Edge<T> remove() {
//		if (this.twin == this)
//			return this;
//		
//		/*
//		 * 1. Punkt löschen und dabei das loch-Polygon sauber herstellen
//		 * 2. Lochpolygon ist stern, mit einzelecken, Triangulieren durch ecken abschneiden
//		 * 		http://www.iue.tuwien.ac.at/phd/fasching/node83.html <- OMFG fehlerhafter Algo
//		 * http://igitur-archive.library.uu.nl/math/2006-1212-200635/schoone_80_triangulating_a.pdf
//		 * der hier klingt besser
//		 * 3. fix.
//		 */
//		
//		if (this.node.value==null) //do not allow do remove boundary 
//			return this;
//		
//		/////////// 1
//		Edge<T> s = this; //Laufreferenz
//
//		//Knotenkoordinaten (im Kernel des Sternlochs)
//		final double x = s.node.x, y = s.node.y;
//
//		//Entlang der Sternkanten..
//		do {
//			//Kante und Umkehrkante entfernen und 
//			//die übriggebliebenen Sternlochrandkanten 
//			//miteinander verbinden
//			final Edge<T> sin = s.twin.next; 
//			final Edge<T> sp = s.prev;
//
//			sin.prev = sp;
//			sp.next = sin;
//			
//			s.node.value = null; //also remove ref to value 
//			
//			s = s.twin.prev; 
//			s.prev.node.star = s.prev;
//		} while ( s!=this );
//		
//		/////////// 2
//		//das Loch ist nun "sauber" ausgeschnitten.
//		//der Graph ist keine Triangulierung mehr, aber 
//		//eine gültige DCEL
//		
//		//e zeigt auf eine Innenkante des Sternlochs 
//		Edge<T> e = s.prev.next, start = e, l = null;
//		
//		//solange das Sternloch kein Dreieck ist
//		//also der nachfolger vom nachfolger des Nachfolgers von e nicht e ist
//		while (e.next.next.next!= e) { 
//			//die aufeinanderfolgneden Ecken a,b,c wählen
//			Vertex<T> a = e.prev.node, b = e.node, c = e.next.node;
//			
//			//ist b rechts von der Strecke ab und das Sternzentrum (x,y) links davon 
//			if(	rightOf(b.x, b.y, a.x, a.y, c.x, c.y) 
//				&& 
//				leftOf(x, y, a.x, a.y, c.x, c.y) )			
//			{
//				//ist abc ein Polygonohr und kann abgeschnitten werden 
//				Edge<T> ca = new Edge<T>();
//				Edge<T> ac = new Edge<T>();
//				
//				
//				//neue Kanten ca und ac in Hinzufügereihenfolge l verketten
//				ca.last = l;
//				ac.last = ca;
//				l = ac; 
//				
//				ca.node = a; //?????
//				ca.next = e;
//				ca.prev = e.next; //?
//				
//				//?????				
//				ac.node = c;
//				ac.prev = e.prev; //???
//				ac.next = e.next.next;
//				
//				(ca.twin = ac).twin = ca;
//				
//				e.next.next.prev = ac; //??? :)
//				e.prev.next = ac;
//				
//				e.prev = ca;
//				e.next.next = ca;
//				
//				ca.node.star = ca;
//				ca.next.node.star = ca.next;
//				ca.prev.node.star = ca.prev;
//				
//				e = ac.next;
//			} else //ansonsten 
//				e = e.next; //nächste Kante probieren
//		}
//		
//		
//		// mark this edge "detached"
//		this.next = start;
//		this.prev = start;
//		this.node.value = null;
//		this.twin = this;
//		
//		
//		return start; //start randkante zurückgeben
//		/**/
//	}
	
	
	

	///////////////////////

	public static boolean pointAbovePlane(
			double px, double py, double pz, 
			double ax, double ay, double az, 
			double bx, double by, double bz, 
			double qx, double qy, double qz) {
		
		//normale errechnen
		double nx = ay*bz-az*by, ny = az*bx-ax*bz, nz = ax*by-ay*bx, nl = Math.sqrt(nx*nx+ny*ny+nz*nz);
		nx/=nl;ny/=nl;nz/=nl;
		
		if (nz<0) { //sicherstellen, dass Normale nach "oben" zeigt (nz>0)
			nx =-nx;
			ny =-ny;
			nz =-nz;
		}
		//normalisieren
		
		//q von stützvektor p subtrahieren
		double qpx = px-qx, qpy = py-qy, qpz = pz-qz;
		
		//mit der normalen skalarmultiplizieren
		double d = nx*qpx+ny*qpy+nz*qpz;
		
		//drüber, wenn Abstand positiv
		return d>EPSILON;
	}
	
	static final double EPSILON = 0.00000001;
//	static final double EPSILON = 0.000000000000001;

	public static int relativeCCW(double x1, double y1, double x2, double y2, double px, double py) {
		x2 -= x1;
		y2 -= y1;
		px -= x1;
		py -= y1;
		double ccw = px * y2 - py * x2;
		if (ccw == 0.0) {
			// The point is colinear, classify based on which side of
			// the segment the point falls on. We can calculate a
			// relative value using the projection of px,py onto the
			// segment - a negative value indicates the point projects
			// outside of the segment in the direction of the particular
			// endpoint used as the origin for the projection.
			ccw = px * x2 + py * y2;
			if (ccw > 0.0) {
				// Reverse the projection to be relative to the original x2,y2
				// x2 and y2 are simply negated.
				// px and py need to have (x2 - x1) or (y2 - y1) subtracted
				// from them (based on the original values)
				// Since we really want to get a positive answer when the
				// point is "beyond (x2,y2)", then we want to calculate
				// the inverse anyway - thus we leave x2 & y2 negated.
				px -= x2;
				py -= y2;
				ccw = px * x2 + py * y2;
				if (ccw < 0.0) {
					ccw = 0.0;
				}
			}
		}
		return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);
	}

	public static boolean linesIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
		return ((relativeCCW(x1, y1, x2, y2, x3, y3)
				* relativeCCW(x1, y1, x2, y2, x4, y4) <= 0) && (relativeCCW(x3,
				y3, x4, y4, x1, y1) * relativeCCW(x3, y3, x4, y4, x2, y2) <= 0));
	}

	public static boolean insideCircumcircle(
			final double px, final double py, 
			final double ax, final double ay, 
			final double bx, final double by, 
			final double cx, final double cy) 
	{
		
		//Lifting-Komponenten der Punkte berechnen
		final double az = ax*ax+ay*ay;
		final double bz = bx*bx+by*by;
		final double cz = cx*cx+cy*cy;
		final double pz = px*px+py*py;

		//Orientierungstest in 3D liefert Umkreisantwort  
		return pointAbovePlane(
				ax, ay, az, 
				bx-ax, by-ay, bz-az,
				cx-ax, cy-ay, cz-az,
				px, py, pz);
	}

	static public boolean triangleIntersects(double px, double py, double ax, double ay, double bx, double by, double cx, double cy) {
		final double v0x = cx-ax, v0y = cy-ay;
		final double v1x = bx-ax, v1y = by-ay;
		final double v2x = px-ax, v2y = py-ay;
		
		final double dot00 = v0x*v0x+v0y*v0y;
		final double dot01 = v0x*v1x+v0y*v1y;
		final double dot02 = v0x*v2x+v0y*v2y;
		final double dot11 = v1x*v1x+v1y*v1y;
		final double dot12 = v1x*v2x+v1y*v2y;
		
		final double invDenom = 1 / (dot00*dot11-dot01*dot01);
		final double u = (dot11*dot02-dot01*dot12)*invDenom;
		final double v = (dot00*dot12-dot01*dot02)*invDenom;
		
		return (u>=0) && (v>=0) && (u+v<=1);
	}

    static public double orientation( final double px, final double py, final double ax, final double ay, final double bx, final double by ) {
    	double cax = ax-px, cay = ay-py;
    	double cbx = bx-px, cby = by-py;
    	
    	return (cax*cby-cay*cbx);
    }
    
//    static public boolean leftOf( final Point2D p, final Point2D a, final Point2D b) { return leftOf(p.getX(),p.getY(),a.getX(),a.getY(),b.getX(),b.getY()); }
    
    static public boolean leftOf( final double px, final double py, final double ax, final double ay, final double bx, final double by ) {
    	return orientation(px,py,ax,ay,bx,by)<0;
    }

    static public boolean rightOf( final double px, final double py, final double ax, final double ay, final double bx, final double by ) {
    	return orientation(px,py,ax,ay,bx,by)>0;
    }

}

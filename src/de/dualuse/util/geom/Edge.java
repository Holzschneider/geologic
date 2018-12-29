package de.dualuse.util.geom;

public class Edge<T> /*implements LocalMap<T>*/ {
	
	@Override
	public String toString() {
		return "Edge("+id+")";
	}
	
	static int edgeCounter = 0;
	
	int id = edgeCounter++;
	
	Edge<T> next;
	Edge<T> prev;
	Edge<T> twin;
	
	Vertex<T> node = null;
	
	//////////////////////////////
	
	
	protected Edge<T> createEdge() {
		return new Edge<T>();
	}
	
	protected Vertex<T> createVertex(double x, double y, T value) {
		return new Vertex<T>(x,y,value);
	}
	
	////////
	
	public Edge<T> put(double x, double y, T value) {
//		if (!this.contains(x, y))
//			return this.locate(x, y).put(x, y, value);
		
		
		return this;
	}
	
	
	
	
//	public boolean contains(final double px, final double py) {
//		final Vertex<T> a = node, b = next.node, c = prev.node;
//		if (a==b || b==c || a==c) // uni or bipolar mesh
//			return true;
//		else
//			return triangleIntersects(px, py, a.x, a.y, b.x, b.y, c.x, c.y);
//	}
	
	
//	public Edge<T> insert(double x, double y, T value) {
//		Vertex<T> node = createVertex(x,y,value);
//		Edge<T> first = this, in, out;
//
//		in = createEdge();
//		out = createEdge();
//		in.twin = out;
//		out.twin = in;
//		in.prev = this;
//		out.next = next;
//		out.prev = out;
//		in.next = in;
//		in.node = node;
//		out.node = this.node;
//		
//		return in;
//	}
	
	
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

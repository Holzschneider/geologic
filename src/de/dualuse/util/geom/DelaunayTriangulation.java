//package de.dualuse.util.geom;
//
//import java.io.Serializable;
//import java.util.function.Consumer;
//
///**
// * <b>Edge - Delaunay Map-Collection</b><br/ >
// * !!! Vorversion, nicht an Dritte weitergeben !!!<p />
// * 
// * (c) Philipp Holzschneider - Kommentare und veränderte Fassungen  
// * an philipp.holzschneider[at]inf.fu-berlin.de.<p />
// * 
// * Edge repräsentiert eine DCEL-Halbkante eines zusammenhängenden 
// * planaren Graphen. Der entlang verketteter Edges erreichbare Graph 
// * über eingefügte Container-Knoten (vom Typ Vertex), stellt in Grundzügen 
// * eine Map-Collection dar, die 2D-Koordinatenschlüssel Objekte vom Typ T
// * zuordnet. Edge bietet unter anderem die klassischen Map-Methoden 
// * put, remove und get, die auf dem erreichbaren Graph ausgeführt werden.<p />  
// * 
// * Der intern inkrementell erzeugte Graph über eingefügte 
// * Schlüssel-Werte-Paare ist eine vollwertige Delaunay-Triangulierung.
// * Bereichs-, Punkt- und Umfeldabfragen können so unter Nutzung der
// * Triangulierungseigenschaften effizient gestaltet werden.<p />
// * 
// * Die Triangulierung baut inkrementell auf der Trivialtriangulierung
// * von vier intern erzeugten Grenzknoten auf. Eine standalone Edge 
// * kann dabei nur unter Angabe einer rechteckigen Begrenzung erzeugt 
// * werden. (Einfügeoperationen mit Schlüsselkoordinaten ausserhalb
// * der Begrenzung überführen die Datenstruktur in einen undefinierten
// * Zustand).<br />
// * Halbkanten zwischen zwei Begrenzungsknoten haben keine Umkehrkante.
// * Die Begrenzungsknoten haben keinen abgelegten Wert (getValue()==null).<p />
// *
// * <pre>
// * a <--- d 
// * |`\    ^   <- Ascii Bild einer Trivialtriangulierung
// * | \\   |
// * |  \\  |
// * ,   \\ |
// * b --->`c
// * </pre><p />
// * 
// * (Zukünftige Versionen werden möglicherweise dieser 
// * Beschränkung nicht mehr unterlegen)<p />
// * 
// * Die meisten der global auf den Graph bezogenen Methoden von Edge
// * liefern eine Zielkante Edge, die direkt an der modifizierten 
// * Stelle im Graph anliegt (O(1)-Entfernung). Von dieser Zielkante aus 
// * können lokale Folgeoperationen so ohne wiederholte Suchkosten 
// * durchgeführt werden.<p />
// * 
// * <b>BUGFIXES BUGFIXES BUGFIXES BUGFIXES BUGFIXES BUGFIXES</b>
// * 
// * <i>20071203</i>: Add ersetzt den Wert eines Knoten für einen Koordinatenschlüssel (x,y),
// * sofern an exakt dieser Position bereits ein Wert hinterlegt wurde. -> Rename to put.
// * 
// * 
// * @author Philipp Holzschneider
// */
//
//class BoundaryEdge<T> extends DelauneyMesh<T> {
//	private static final long serialVersionUID = 1L;
//
//	
//}
//
//
//class DelauneyMesh<T> implements Serializable {
//
//	private static final long serialVersionUID = 1L;
//
//	Vertex<T> node;
//	DelauneyMesh<T> next=null, prev=null, twin=null;
//	transient DelauneyMesh<T> last=null;
//	
//	
//	@Override
//	public String toString() {
//		return "Edge( -> "+node.x+" "+node.y+" -> "+next.node.x+" "+next.node.y+" -> "+prev.node.x+" "+prev.node.y+" -> )";
//	}
//	
//	
//	
//	public DelauneyMesh(Bounds extent) {
//		this(extent.x, extent.y);
//	}
//	
//	/**
//	 * Erzeugt eine leeren Graphcontainer. Leere Grenzknoten und Kanten bilden eine 
//	 * Trivialtriangulierung des gegebenen Rechtecks. Die erzeugte Kante liegt 
//	 * entlang der Diagonalen.
//	 */
//	public DelauneyMesh(Range x, Range y) {
//		double minX = x.min, minY = y.min;
//		double maxX = x.max, maxY = y.max;
//		
//		//Grenzknoten
//		Vertex<T> a = new BoundingVertex<T>(minX,minY);
//		Vertex<T> b = new BoundingVertex<T>(minX,maxY);
//		Vertex<T> c = new BoundingVertex<T>(maxX,maxY);
//		Vertex<T> d = new BoundingVertex<T>(maxX,minY);
//		
//		
//		//Dreieck1
//		DelauneyMesh<T> e = this, f = new DelauneyMesh<T>(), g = new DelauneyMesh<T>();
//		
//		(e.node = a).star=e;
//		(f.node = b).star=f;
//		(g.node = c).star=g;
//		((e.next = f).next = g).next = e;
//		((e.prev = g).prev = f).prev = e;
//		
//		//Dreieck2
//		DelauneyMesh<T> h = new DelauneyMesh<T>(), i = new DelauneyMesh<T>(), j = new DelauneyMesh<T>();
//		
//		h.node = a;
//		i.node = c;
//		(j.node = d).star=j;
//		((h.next = i).next = j).next = h;
//		((h.prev = j).prev = i).prev = h;
//		
//		(i.twin = e).twin=i;
//	}
//	
//	
//	/**
//	 * interner Konstruktor neuer Kanten, ohne Zugriff für Aussenstehende,
//	 * um die Integrität der Triangulierung garantieren zu können 
//	 */
//	DelauneyMesh() {}
//	
//	
//	/**
//	 * Entfernt den Knoten auf den die Kante zeigt aus dem Graphen.
//	 * Dabei wird auch zwangsläufig die Kante aus dem Graphen entfernt, da 
//	 * sie Teil des Sterns des zu entfernenden Knotens ist. Der sternförmige 
//	 * Loch-Bereich des Graphen wird retrianguliert, die Delaunay-Eigenschaft
//	 * wird anschließend wieder hergestellt.
//	 *    
//	 * @return einen gültige Kantenreferenz. Zeigt auf die zuletzt 
//	 * korrigierte Randkante des retriangulierten Sternlochs.   
//	 */	
//	DelauneyMesh<T> remove() {
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
//		DelauneyMesh<T> s = this; //Laufreferenz
//
//		//Knotenkoordinaten (im Kernel des Sternlochs)
//		final double x = s.node.x, y = s.node.y;
//
//		//Entlang der Sternkanten..
//		do {
//			//Kante und Umkehrkante entfernen und 
//			//die übriggebliebenen Sternlochrandkanten 
//			//miteinander verbinden
//			final DelauneyMesh<T> sin = s.twin.next; 
//			final DelauneyMesh<T> sp = s.prev;
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
//		DelauneyMesh<T> e = s.prev.next, start = e, l = null;
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
//				DelauneyMesh<T> ca = new DelauneyMesh<T>(), ac = new DelauneyMesh<T>();
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
//		//Delaunay-Eigenschaft aller hinzugefügten Diagonalen reparieren 
//		for (DelauneyMesh<T> el = l, ell; el!=null; el = ell ) {
//			el.fix();
//			ell = el.last;
//			el.last = null; //Hinzufügereihenfolge löschen
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
//	
//	
//	private DelauneyMesh<T> setValue(T value) {
//		this.node.value = value;
//		return this;
//	}
//	
//
//	DelauneyMesh<T> face( Consumer<DelauneyMesh<T>> visitor ){
//		visitor.accept(this);
//		for (DelauneyMesh<T> it=this.next;it!=this;it=it.next)
//			visitor.accept(it);
//		
//		return this;
//	}
//	
//
//	/**
//	 * fügt einen Wert in den Triangulierungsgraphen an der 
//	 * Schlüsselposition x,y ein. Rückgabe ist eine neuerzeugte
//	 * Kante die auf den Knoten der den Wert beinhaltet zeigt 
//	 */
//	DelauneyMesh<T> put(double x, double y, T v) {
//		if (this.twin == this) // if this is detached, follow the chain of detacheds
//			this.next = put(x,y,v);
//		
//		if (!this.contains(x,y)) //koordinaten nicht im lokalen Dreieck
//			return locate(x,y).put(x,y,v); //add auf dem richtigen Dreieck aufrufen
//
//		DelauneyMesh<T> it = this;
//		
//		for (int i=0;i<=3;i++,it=it.next)
//			if (it.node.x == x && it.node.y == y)
//				return it.setValue(v);
//		
//		//Wert einfügen und erzeugten Knoten merken 
//		Vertex<T> ev = insert(x,y,v).node; 
//		DelauneyMesh<T> e = ev.star; //alle in Frage kommenden Halbkanten
//		
//		DelauneyMesh<T> ep = e.prev; //zuerst merken 
//		DelauneyMesh<T> epi = e.prev.twin; //und dann
//		
//		DelauneyMesh<T> ein = e.twin.next; //nacheinander fixen
//		DelauneyMesh<T> eini = e.twin.next.twin;
//		
//		DelauneyMesh<T> enip = e.next.twin.prev;
//		DelauneyMesh<T> enipi = e.next.twin.prev.twin;
//		
//		
//		ep.fix(); //fix
//		if (epi!=null) epi.fix();
//		ein.fix();
//		if (eini!=null) eini.fix();
//		enip.fix();
//		if (enipi!=null) enipi.fix();
//		
//		//im Anschluss eine Sternkante auf den Knoten zurückgeben
//		//(kann sich durch flips verändert haben)
//		return ev.star;
//	}
//	
//	/**
//	 * Fügt direkt einen neuen Knoten in das zur Kante gehörende
//	 * Dreieck ein, ungeachtet der Einfügekoordinaten
//	 */
//	private DelauneyMesh<T> insert(double x, double y, T value) {
//		Vertex<T> v = new Vertex<T>(x,y,value);
//		
//		final DelauneyMesh<T> abn = new DelauneyMesh<T>(); abn.node = v;
//		final DelauneyMesh<T> abnn = new DelauneyMesh<T>(); abnn.node = next.next.node; 
//		
//		final DelauneyMesh<T> bcn = new DelauneyMesh<T>(); bcn.node = v; 
//		final DelauneyMesh<T> bcnn = new DelauneyMesh<T>(); bcnn.node = this.node; 
//		
//		final DelauneyMesh<T> can = new DelauneyMesh<T>(); can.node = v; 
//		final DelauneyMesh<T> cann = new DelauneyMesh<T>(); cann.node = next.node;
//		
//		(abn.twin=bcnn).twin = abn;
//		(bcn.twin=cann).twin = bcn;
//		(can.twin=abnn).twin = can;
//		
//		((next.next.next = can).next=cann).next=next.next;
//		((next.next=bcn).next=bcnn).next = next;
//		((next=abn).next=abnn).next = this;
//		
//		abn.prev = abn.next.next;
//		abn.next.prev = abn;
//		abn.next.next.prev = abn.next;
//		
//		bcn.prev = bcn.next.next;
//		bcn.next.prev = bcn;
//		bcn.next.next.prev = bcn.next;
//		
//		can.prev = can.next.next;
//		can.next.prev = can;
//		can.next.next.prev = can.next;
//		abn.node.star=abn;
//		
//		return abn;
//	}
//
////	/**
////	 * @return true , sofern die Position des Knotens das zur Kante 
////	 * gehörende Dreieck schneidet 
////	 */
////	public boolean contains(final Vertex<?> q) { return contains(q.getX(),q.getY()); }
////	
//
//	/**
//	 * @return true , sofern der Punkt (x,y) das zur Kante gehörende 
//	 * Dreieck schneidet 
//	 */
//	public boolean contains(final double px, final double py) {
//		final Vertex<T> a = node, b = next.node, c = prev.node;
//		return triangleIntersects(px, py, a.x, a.y, b.x, b.y, c.x, c.y);
//	}
//	
////	/**
////	 * @return die Kante, die auf den Knoten zeigt, der den gegebenen 
////	 * Knoten am nächsten liegt.  
////	 */
////	public Edge<T> locate(final Vertex<?> q) { return locate(q.getX(), q.getY()); };
//	
//
//	/**
//	 * @return die Kante, die auf den Knoten zeigt, der den gegebenen 
//	 * Koordinaten am nächsten liegt.  
//	 */
//	public DelauneyMesh<T> locate(final double qx, final double qy) {
//		if (this.twin == this) //XXX allow detached functions to work
//			return next.locate(qx, qy);
//		
//		if (this.contains(qx,qy)) { //wenn das Dreieck die qx,qy enthält
//			//alle (drei) in frage kommenden Knoten überprüfen
//			DelauneyMesh<T> closest = this; 
//			double dx = closest.node.x-qx, dy = closest.node.y-qy;
//			double distanceSq = dx*dx+dy*dy;
//			
//			for (DelauneyMesh<T> e = this.next;e!=this;e=e.next) {
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
//	
//
//
//	
//	/**
//	 * @return true, wenn der Umkreis des der Kante zugehörigen Dreicks 
//	 * die gegebenen Koordinaten enthält
//	 */
//	boolean circumcircleContains(double qx, double qy) {
//		return insideCircumcircle(
//				qx,qy, 
//				this.node.x, this.node.y,
//				next.node.x, next.node.y, 
//				prev.node.x, prev.node.y);
//	}
//	
//
//	/**
//	 * Stellt rekursiv die Delaunayeigenschaft wieder her
//	 */
//	private void fix() {
//		//existiert eine Umkehrkante und liegt der umkehrkante gegenüberliegende 
//		//Knoten im Inkreis des eigenen Dreicks 
//		if (twin!=null && circumcircleContains( twin.next.node.x, twin.next.node.y )) {
//			flip(); //Kante flippen
//
//			DelauneyMesh<T> edgeNext = this.next;
//			DelauneyMesh<T> edgePrev = this.prev;
//
//			DelauneyMesh<T> twin = this.twin;
//			DelauneyMesh<T> twinNext = null;
//			DelauneyMesh<T> twinPrev = null;
//			
//			if (twin!=null) {
//				twinNext = this.twin.next;
//				twinPrev  = this.twin.prev;
//			}
//			
//			edgeNext.fix(); //fix auf nachfolgerkante
//			edgePrev.fix(); //vorgängerkante
//			
//			if (twin!=null) { //und sofern Umkehrkante
//				twin.fix(); //auf umkehrkante 
//				twinNext.fix(); //nachfolger der Umkehrkante
//				twinPrev .fix(); //vorgänger der Umkehrkante anwenden
//			}
//		}
//	}
//	
//	/**
//	 * Kippt eine Kante.
//	 */
//	private DelauneyMesh<T> flip() {
//		final DelauneyMesh<T> edge = this;
//		final DelauneyMesh<T> twin = this.twin;
//		final DelauneyMesh<T> edgeNext = this.next;
//		final DelauneyMesh<T> edgePrev = this.prev;
//		final DelauneyMesh<T> twinNext = this.twin.next;
//		final DelauneyMesh<T> twinPrev = this.twin.prev;
//		
//		edge.node = edgeNext.node;
//		edge.next = edgePrev;
//		edge.prev = twinNext;
//		
//		twin.node = twinNext.node;
//		twin.next = twinPrev;
//		twin.prev = edgeNext;
//		
//		edgePrev .next = twinNext;
//		edgePrev .prev = edge;
//		
//		twinNext .prev = edgePrev;
//		twinNext .next = edge;
//		
//		twinPrev .next = edgeNext;
//		twinPrev .prev = twin;
//		
//		edgeNext.next = twin ;
//		edgeNext.prev = twinPrev;
//
//		//fast vergessen
//		//die Stern-Garantie wieder herstellen.
//		this.node.star = this;
//		this.next.node.star = this.next;
//		this.prev.node.star = this.prev;
//		
//		this.twin.node.star = this.twin;
//		this.twin.next.node.star = this.twin.next;
//		this.twin.prev.node.star = this.twin.prev;			
//		
//		return this;
//	}
//	
//	
//	///////////////////////
//
//	public static boolean pointAbovePlane(
//			double px, double py, double pz, 
//			double ax, double ay, double az, 
//			double bx, double by, double bz, 
//			double qx, double qy, double qz) {
//		
//		//normale errechnen
//		double nx = ay*bz-az*by, ny = az*bx-ax*bz, nz = ax*by-ay*bx, nl = Math.sqrt(nx*nx+ny*ny+nz*nz);
//		nx/=nl;ny/=nl;nz/=nl;
//		
//		if (nz<0) { //sicherstellen, dass Normale nach "oben" zeigt (nz>0)
//			nx =-nx;
//			ny =-ny;
//			nz =-nz;
//		}
//		//normalisieren
//		
//		//q von stützvektor p subtrahieren
//		double qpx = px-qx, qpy = py-qy, qpz = pz-qz;
//		
//		//mit der normalen skalarmultiplizieren
//		double d = nx*qpx+ny*qpy+nz*qpz;
//		
//		//drüber, wenn Abstand positiv
//		return d>EPSILON;
//	}
//	
//	static final double EPSILON = 0.00000001;
////	static final double EPSILON = 0.000000000000001;
//
//	public static int relativeCCW(double x1, double y1, double x2, double y2, double px, double py) {
//		x2 -= x1;
//		y2 -= y1;
//		px -= x1;
//		py -= y1;
//		double ccw = px * y2 - py * x2;
//		if (ccw == 0.0) {
//			// The point is colinear, classify based on which side of
//			// the segment the point falls on. We can calculate a
//			// relative value using the projection of px,py onto the
//			// segment - a negative value indicates the point projects
//			// outside of the segment in the direction of the particular
//			// endpoint used as the origin for the projection.
//			ccw = px * x2 + py * y2;
//			if (ccw > 0.0) {
//				// Reverse the projection to be relative to the original x2,y2
//				// x2 and y2 are simply negated.
//				// px and py need to have (x2 - x1) or (y2 - y1) subtracted
//				// from them (based on the original values)
//				// Since we really want to get a positive answer when the
//				// point is "beyond (x2,y2)", then we want to calculate
//				// the inverse anyway - thus we leave x2 & y2 negated.
//				px -= x2;
//				py -= y2;
//				ccw = px * x2 + py * y2;
//				if (ccw < 0.0) {
//					ccw = 0.0;
//				}
//			}
//		}
//		return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);
//	}
//
//	public static boolean linesIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
//		return ((relativeCCW(x1, y1, x2, y2, x3, y3)
//				* relativeCCW(x1, y1, x2, y2, x4, y4) <= 0) && (relativeCCW(x3,
//				y3, x4, y4, x1, y1) * relativeCCW(x3, y3, x4, y4, x2, y2) <= 0));
//	}
//
//	public static boolean insideCircumcircle(
//			final double px, final double py, 
//			final double ax, final double ay, 
//			final double bx, final double by, 
//			final double cx, final double cy) 
//	{
//		
//		//Lifting-Komponenten der Punkte berechnen
//		final double az = ax*ax+ay*ay;
//		final double bz = bx*bx+by*by;
//		final double cz = cx*cx+cy*cy;
//		final double pz = px*px+py*py;
//
//		//Orientierungstest in 3D liefert Umkreisantwort  
//		return pointAbovePlane(
//				ax, ay, az, 
//				bx-ax, by-ay, bz-az,
//				cx-ax, cy-ay, cz-az,
//				px, py, pz);
//	}
//
//	static public boolean triangleIntersects(double px, double py, double ax, double ay, double bx, double by, double cx, double cy) {
//		final double v0x = cx-ax, v0y = cy-ay;
//		final double v1x = bx-ax, v1y = by-ay;
//		final double v2x = px-ax, v2y = py-ay;
//		
//		final double dot00 = v0x*v0x+v0y*v0y;
//		final double dot01 = v0x*v1x+v0y*v1y;
//		final double dot02 = v0x*v2x+v0y*v2y;
//		final double dot11 = v1x*v1x+v1y*v1y;
//		final double dot12 = v1x*v2x+v1y*v2y;
//		
//		final double invDenom = 1 / (dot00*dot11-dot01*dot01);
//		final double u = (dot11*dot02-dot01*dot12)*invDenom;
//		final double v = (dot00*dot12-dot01*dot02)*invDenom;
//		
//		return (u>=0) && (v>=0) && (u+v<=1);
//	}
//
//    static public double orientation( final double px, final double py, final double ax, final double ay, final double bx, final double by ) {
//    	double cax = ax-px, cay = ay-py;
//    	double cbx = bx-px, cby = by-py;
//    	
//    	return (cax*cby-cay*cbx);
//    }
//    
////    static public boolean leftOf( final Point2D p, final Point2D a, final Point2D b) { return leftOf(p.getX(),p.getY(),a.getX(),a.getY(),b.getX(),b.getY()); }
//    
//    static public boolean leftOf( final double px, final double py, final double ax, final double ay, final double bx, final double by ) {
//    	return orientation(px,py,ax,ay,bx,by)<0;
//    }
//
//    static public boolean rightOf( final double px, final double py, final double ax, final double ay, final double bx, final double by ) {
//    	return orientation(px,py,ax,ay,bx,by)>0;
//    }
//
//}
//
//
//
///**
// * keine Ahnung wieso ich einen extra BoundingVertex-Typ nutze.
// */
//class BoundingVertex<T> extends Vertex<T> {
//	private static final long serialVersionUID = 1L;
//	public BoundingVertex(double x, double y) { super(x,y,null); };
//}
//
//
//
//
//public class DelaunayTriangulation {
//	
//	/*
//	public static interface TriangleVisitor<T> {
//		public void meet(Vertex<T> a, Vertex<T> b, Vertex<T> c);
//	}
//	
//	static public<T> int triangles(TriangleVisitor<T> tm, Deque<Edge<T>> todo, Set<Edge<T>> done ) {
//		int triangleCounter = 0;
//		
//		while (!todo.isEmpty()) {
//			Edge<T> e = todo.pop();
//			<
//			Vertex<T> v1 = (e = e.getNext()).getVertex();
//			Vertex<T> v2 = (e = e.getNext()).getVertex();
//			Vertex<T> v3 = (e = e.getNext()).getVertex();
//
//			T p1 = v1.getValue(), p2 = v2.getValue(), p3 = v3.getValue();
//			if (p1!=null && p2!=null && p3!=null) 
//			{ 
//				tm.meet( v1, v2, v3 );
//				triangleCounter++;
//			}
//						
//			done.add(e=e.getNext());
//			done.add(e=e.getNext());
//			done.add(e=e.getNext());
//	
//			for (int i=0;i<3;i++)
//				if (!done.contains((e=e.getNext()).getInverse())) { 
//		 			if (e.getInverse()!=null) { 
//						todo.push(e.getInverse());
//						done.add(e.getInverse());
//						done.add(e.getInverse().getNext());
//						done.add(e.getInverse().getPrevious());
//					}
//				}			
//		}
//		
//		return triangleCounter;
//	}
//	*/
//	
//	
//	
//	
//
////    static public boolean rightOf( final Point2D p, final Point2D a, final Point2D b) { return rightOf(p.getX(),p.getY(),a.getX(),a.getY(),b.getX(),b.getY()); }
//
//}

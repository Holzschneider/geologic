package de.dualuse.util.geom;

import java.io.Serializable;
import java.util.Collection;
import java.util.Deque;
import java.util.Set;

public class DelaunayTriangulation {
	
	static public class Monitor {
		public int allocationCounter = 0, accessCounter = 0;
		
		public int getAllocationCounter() { return allocationCounter; }
		public int getAccessCounter() { return accessCounter; }
		
		public void reset() {allocationCounter =0; accessCounter = 0;};
		public void allocation() { allocationCounter++; }
		public void access() { accessCounter++; };
	};

	public static interface TriangleVisitor<T> {
		public void meet(Vertex<T> a, Vertex<T> b, Vertex<T> c);
	}
	
	static public<T> int triangles(TriangleVisitor<T> tm, Deque<Edge<T>> todo, Set<Edge<T>> done ) {
		int triangleCounter = 0;
		
		while (!todo.isEmpty()) {
			Edge<T> e = todo.pop();
			
			Vertex<T> v1 = (e = e.getNext()).getVertex();
			Vertex<T> v2 = (e = e.getNext()).getVertex();
			Vertex<T> v3 = (e = e.getNext()).getVertex();

			T p1 = v1.getValue(), p2 = v2.getValue(), p3 = v3.getValue();
			if (p1!=null && p2!=null && p3!=null) 
			{ 
				tm.meet( v1, v2, v3 );
				triangleCounter++;
			}
						
			done.add(e=e.getNext());
			done.add(e=e.getNext());
			done.add(e=e.getNext());
	
			for (int i=0;i<3;i++)
				if (!done.contains((e=e.getNext()).getInverse())) { 
		 			if (e.getInverse()!=null) { 
						todo.push(e.getInverse());
						done.add(e.getInverse());
						done.add(e.getInverse().getNext());
						done.add(e.getInverse().getPrevious());
					}
				}			
		}
		
		return triangleCounter;
	}
	
	public static interface Vertex<T> {
		public double getX();
		public double getY();
		public T getValue();
		public T setValue( T v );
		
		public<Q extends Collection<? super Vertex<T>>> Q getNeighbors(Q q);
		
		/**
		 * Entfernt den Knoten aus der Triangulierung.
		 * @return eine Kante in der angepassten Triangulierung
		 */
		public Edge<T> remove();
	}
	
	/**
	 * Interner Containerknoten. Speichert Koordinaten-Schlüssel und 
	 * Wert als direktreferenzen. Implementiert das Knoteninterface Vertex
	 * welches alle Zugriffsfunktionen spezifiziert.
	 */
	private static class EntryVertex<T> implements Vertex<T>, Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * Koordinaten-Schlüssel
		 */
		private double x, y;

		/**
		 * Wertreferenz
		 */
		private T value;

		/**
		 * Halbkante des Sterns des Knotens mit 
		 * this.star.getVertex() == this;
		 */
		private Edge<T> star;
		
		public double getX() { return x; }
		public double getY() { return y; }
		public T getValue() { return value; }
		public T setValue(T v) {
			T old = value;
			value = v;
			return old;
		}
		
		public EntryVertex(double x, double y, T v) {
			this.x = x;
			this.y = y;
			this.value = v; 
		}
		
		public Edge<T> remove() { return star.remove(); }
		
		
		/**
		 * Fügt einer gegebenen Collection alle direkt benachbarten
		 * Vertices hinzu. Fügt keine Randknoten hinzu.
		 */
		public<Q extends Collection<? super Vertex<T>>> Q getNeighbors(Q q) {

			//Startzeiger für Umläufe im und gegen den Uhrzeigersinn
			Edge<T> l = star.i, r = star;

			do { //umlaufschleife 
				if (l!=null) { 
					//l!=null? dann zeigt l auf einen direkten Nachbarn
					
					if (l.v!=null) //evtl ist es eine Aussenrandkante
						q.add(l.v);
					
					l= l.p.i; //der vorgänger ist daher nicht null und 
					//zeigt also wieder auf this und hat evtl. eine Inverskante 
				}
				
				if (r!=null) { 
					//dann zeigt der Nachfolger auf einen direkten Nachbarn
					
					if (r.n.v!=null) //evtl ist es eine Aussenrandkante
						q.add(r.n.v); //nicht? dann den Knoten mitnehmen
					
					//der Nachfolger hat evtl eine Inverse
					r=r.n.i;
				}
				
				//solange bis...
				//beide null sind oder identisch.. ka funktioniert aber
			} while ( (l!=null && l.i!=r) || (r!=null && r.i!=l) );
			
			
			return q;
		}
	}
	
	/**
	 * keine Ahnung wieso ich einen extra BoundingVertex-Typ nutze.
	 */
	private static class BoundingVertex<T> extends EntryVertex<T> {
		private static final long serialVersionUID = 1L;
		public BoundingVertex(double x, double y) { super(x,y,null); };
	}
	
	/**
	 * <b>Edge - Delaunay Map-Collection</b><br/ >
	 * !!! Vorversion, nicht an Dritte weitergeben !!!<p />
	 * 
	 * (c) Philipp Holzschneider - Kommentare und veränderte Fassungen  
	 * an philipp.holzschneider[at]inf.fu-berlin.de.<p />
	 * 
	 * Edge repräsentiert eine DCEL-Halbkante eines zusammenhängenden 
	 * planaren Graphen. Der entlang verketteter Edges erreichbare Graph 
	 * über eingefügte Container-Knoten (vom Typ Vertex), stellt in Grundzügen 
	 * eine Map-Collection dar, die 2D-Koordinatenschlüssel Objekte vom Typ T
	 * zuordnet. Edge bietet unter anderem die klassischen Map-Methoden 
	 * put, remove und get, die auf dem erreichbaren Graph ausgeführt werden.<p />  
	 * 
	 * Der intern inkrementell erzeugte Graph über eingefügte 
	 * Schlüssel-Werte-Paare ist eine vollwertige Delaunay-Triangulierung.
	 * Bereichs-, Punkt- und Umfeldabfragen können so unter Nutzung der
	 * Triangulierungseigenschaften effizient gestaltet werden.<p />
	 * 
	 * Die Triangulierung baut inkrementell auf der Trivialtriangulierung
	 * von vier intern erzeugten Grenzknoten auf. Eine standalone Edge 
	 * kann dabei nur unter Angabe einer rechteckigen Begrenzung erzeugt 
	 * werden. (Einfügeoperationen mit Schlüsselkoordinaten ausserhalb
	 * der Begrenzung überführen die Datenstruktur in einen undefinierten
	 * Zustand).<br />
	 * Halbkanten zwischen zwei Begrenzungsknoten haben keine Umkehrkante.
	 * Die Begrenzungsknoten haben keinen abgelegten Wert (getValue()==null).<p />
	 *
	 * <pre>
	 * a <--- d 
	 * |`\    ^   <- Ascii Bild einer Trivialtriangulierung
	 * | \\   |
	 * |  \\  |
	 * ,   \\ |
	 * b --->`c
	 * </pre><p />
	 * 
	 * (Zukünftige Versionen werden möglicherweise dieser 
	 * Beschränkung nicht mehr unterlegen)<p />
	 * 
	 * Die meisten der global auf den Graph bezogenen Methoden von Edge
	 * liefern eine Zielkante Edge, die direkt an der modifizierten 
	 * Stelle im Graph anliegt (O(1)-Entfernung). Von dieser Zielkante aus 
	 * können lokale Folgeoperationen so ohne wiederholte Suchkosten 
	 * durchgeführt werden.<p />
	 * 
	 * <b>BUGFIXES BUGFIXES BUGFIXES BUGFIXES BUGFIXES BUGFIXES</b>
	 * 
	 * <i>20071203</i>: Add ersetzt den Wert eines Knoten für einen Koordinatenschlüssel (x,y),
	 * sofern an exakt dieser Position bereits ein Wert hinterlegt wurde. -> Rename to put.
	 * 
	 * 
	 * @author Philipp Holzschneider
	 */


	public static class Edge<T> implements Serializable {

		private static final long serialVersionUID = 1L;

	
		

		/**
		 * für Debugzwecke
		 */
//		int drawStamp;
		
		/**
		 * Zielknoten
		 */
		private EntryVertex<T> v;
		
		/**
		 * Nachfolger n, Vorgänger p, Umkehrkante i 
		 */
		private Edge<T> n=null, p=null, i=null;
		
		/**
		 * Zuvoreingefügte Kante l
		 */
		private transient Edge<T> l=null;
		
		/**
		 * Zugriffsmonitor m
		 */
		private transient Monitor m;

		/**
		 * @return Knoten auf den die Halbkante zeigt
		 */
		public Vertex<T> getVertex() { return v; }
		
		/**
		 * @return Vorgängerkante
		 */
		public Edge<T> getPrevious() { return p; }
		
		/**
		 * @return Nachfolgerkante
		 */
		public Edge<T> getNext() { return n; }
		
		/**
		 * @return Umkehrkante
		 */
		public Edge<T> getInverse() { return i; }
		
		/**
		 * Erzeugt eine leeren Graphcontainer. Leere Grenzknoten und Kanten bilden eine 
		 * Trivialtriangulierung des gegebenen Rechtecks. Die erzeugte Kante liegt 
		 * entlang der Diagonalen.
		 */
//		public Edge(Rectangle2D r) { this(r.getX(),r.getY(),r.getWidth(),r.getHeight(), new Monitor()); };
//		public Edge(Rectangle2D r,Monitor m) { this(r.getX(),r.getY(),r.getWidth(),r.getHeight(),m); };
		
		public Edge(double x, double y, double width, double height) { this(x,y,width,height, new Monitor()); } 
		public Edge(double x, double y, double width, double height, Monitor m) {
			this.m = m;
			//Grenzknoten
			EntryVertex<T> a = new BoundingVertex<T>(x,y);
			EntryVertex<T> b = new BoundingVertex<T>(x,y+height);
			EntryVertex<T> c = new BoundingVertex<T>(x+width,y+height);
			
			EntryVertex<T> d = new BoundingVertex<T>(x+width,y);
			
			//Dreieck1
			Edge<T> e = this, f = newEdge(), g = newEdge();
			
			(e.v = a).star=e;
			(f.v = b).star=f;
			(g.v = c).star=g;
			((e.n = f).n = g).n = e;
			((e.p = g).p = f).p = e;
			
			//Dreieck2
			Edge<T> h = newEdge(), i = newEdge(), j = newEdge();
			
			h.v = a;
			i.v = c;
			(j.v = d).star=j;
			((h.n = i).n = j).n = h;
			((h.p = j).p = i).p = h;
			
			(i.i = e).i=i;
		}
		
		/**
		 * interner Konstruktor neuer Kanten, ohne Zugriff für Aussenstehende,
		 * um die Integrität der Triangulierung garantieren zu können 
		 */
		private Edge() {}
		
		/**
		 * Monitorüberwachter Allokator, setzt den selbst verwendeten Monitor
		 * auch in der neuerzeugnten Kante. Eine Allokation wird verbucht 
		 */
		private Edge<T> newEdge() {
			Edge<T> e = new Edge<T>();
			(e.m = m).allocation();
			return e;
		};

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
		public Edge<T> remove() {
			/*
			 * 1. Punkt löschen und dabei das loch-Polygon sauber herstellen
			 * 2. Lochpolygon ist stern, mit einzelecken, Triangulieren durch ecken abschneiden
			 * 		http://www.iue.tuwien.ac.at/phd/fasching/node83.html <- OMFG fehlerhafter Algo
			 * http://igitur-archive.library.uu.nl/math/2006-1212-200635/schoone_80_triangulating_a.pdf
			 * der hier klingt besser
			 * 3. fix.
			 */
			
			
			/////////// 1
			Edge<T> s = this; //Laufreferenz

			//Knotenkoordinaten (im Kernel des Sternlochs)
			final double x = s.v.getX(), y = s.v.getY();

			@SuppressWarnings("unused")
			int N = 0; // Zähler der entfernten Kanten
			
			//Entlang der Sternkanten..
			
			do {
				m.access(); //Entfernungsoperation zählen
				
				//Kante und Umkehrkante entfernen und 
				//die übriggebliebenen Sternlochrandkanten 
				//miteinander verbinden
				final Edge<T> sin = s.i.n; 
				final Edge<T> sp = s.p;

				sin.p = sp;
				sp.n = sin;
				
				s = s.i.p; 
				
				s.p.v.star = s.p;
				N++; //Kante zählen
			} while ( s!=this );
			
			/////////// 2
			//das Loch ist nun "sauber" ausgeschnitten.
			//der Graph ist keine Triangulierung mehr, aber 
			//eine gültige DCEL
			
			//e zeigt auf eine Innenkante des Sternlochs 
			Edge<T> e = s.p.n, start = e, l = null;
			
			//solange das Sternloch kein Dreieck ist
			//also der nachfolger vom nachfolger des Nachfolgers von e nicht e ist
			while (e.n.n.n!= e) { 
				m.access(); // einen Zugriff verbuchen
				
				//die aufeinanderfolgneden Ecken a,b,c wählen
				EntryVertex<T> a = e.p.v, b = e.v, c = e.n.v;
				
				//ist b rechts von der Strecke ab und das Sternzentrum (x,y) links davon 
				if(	rightOf(b.getX(), b.getY(), a.getX(), a.getY(), c.getX(), c.getY()) 
					&& 
					leftOf(x, y, a.getX(), a.getY(), c.getX(), c.getY()) )			
				{
					//ist abc ein Polygonohr und kann abgeschnitten werden 
					N--;
					Edge<T> ca = newEdge(), ac = newEdge();
					
					
					//neue Kanten ca und ac in Hinzufügereihenfolge l verketten
					ca.l = l;
					ac.l = ca;
					l = ac; 
					
					ca.v = a; //?????
					ca.n = e;
					ca.p = e.n; //?
					
					//?????				
					ac.v = c;
					ac.p = e.p; //???
					ac.n = e.n.n;
					
					(ca.i = ac).i = ca;
					
					e.n.n.p = ac; //??? :)
					e.p.n = ac;
					
					e.p = ca;
					e.n.n = ca;
					
					ca.v.star = ca;
					ca.n.v.star = ca.n;
					ca.p.v.star = ca.p;
					
					
					e = ac.n;
				} else //ansonsten 
					e = e.n; //nächste Kante probieren
			}
			
			//Delaunay-Eigenschaft aller hinzugefügten Diagonalen reparieren 
			for (Edge<T> el = l, ell; el!=null; el = ell ) {
				el.fix();
				ell = el.l;
				el.l = null; //Hinzufügereihenfolge löschen
			}

			return start; //start randkante zurückgeben
			/**/
		}
		
		
		/**
		 * fügt einen Wert in den Triangulierungsgraphen an der 
		 * Schlüsselposition x,y ein. Rückgabe ist eine neuerzeugte
		 * Kante die auf den Knoten der den Wert beinhaltet zeigt 
		 */
		public Edge<T> put(double x, double y, T v) {
			if (!this.contains(x,y)) //koordinaten nicht im lokalen Dreieck
				return locate(x,y).put(x,y,v); //add auf dem richtigen Dreieck aufrufen
			
			//zeigt der Koordinatenschlüssel x,y exakt auf einen der Dreiecksknoten, 
			//wird dessen Wert ersetzt, anstatt ein neuer Knoten hinzugefügt
			if (this.v.getX()==x && this.v.getY()==y) 
			{ this.v.setValue(v); return this; }
			else
			if (this.n.v.getX()==x && this.n.v.getY()==y) 
			{ this.v.setValue(v); return this; }
			else
			if (this.p.v.getX()==x && this.p.v.getY()==y) 
			{ this.v.setValue(v); return this; };
			
			
			//Wert einfügen und erzeugten Knoten merken 
			EntryVertex<T> ev = insert(x,y,v).v; 
			Edge<T> e = ev.star; //alle in Frage kommenden Halbkanten
			
			Edge<T> ep = e.p; //zuerst merken 
			Edge<T> epi = e.p.i; //und dann
			
			Edge<T> ein = e.i.n; //nacheinander fixen
			Edge<T> eini = e.i.n.i;
			
			Edge<T> enip = e.n.i.p;
			Edge<T> enipi = e.n.i.p.i;
			
			
			ep.fix(); //fix
			if (epi!=null) epi.fix();
			ein.fix();
			if (eini!=null) eini.fix();
			enip.fix();
			if (enipi!=null) enipi.fix();
			
			//im Anschluss eine Sternkante auf den Knoten zurückgeben
			//(kann sich durch flips verändert haben)
			return ev.star;
		}
		
		/**
		 * Fügt direkt einen neuen Knoten in das zur Kante gehörende
		 * Dreieck ein, ungeachtet der Einfügekoordinaten
		 */
		private Edge<T> insert(double x, double y, T value) {
			m.allocation();
			EntryVertex<T> v = new EntryVertex<T>(x,y,value);
			
			final Edge<T> abn = newEdge(); abn.v = v;
			final Edge<T> abnn = newEdge(); abnn.v = n.n.v; 
			
			final Edge<T> bcn = newEdge(); bcn.v = v; 
			final Edge<T> bcnn = newEdge(); bcnn.v = this.v; 
			
			final Edge<T> can = newEdge(); can.v = v; 
			final Edge<T> cann = newEdge(); cann.v = n.v;
			
			(abn.i=bcnn).i = abn;
			(bcn.i=cann).i = bcn;
			(can.i=abnn).i = can;
			
			((n.n.n = can).n=cann).n=n.n;
			((n.n=bcn).n=bcnn).n = n;
			((n=abn).n=abnn).n = this;
			
			abn.p = abn.n.n;
			abn.n.p = abn;
			abn.n.n.p = abn.n;
			
			bcn.p = bcn.n.n;
			bcn.n.p = bcn;
			bcn.n.n.p = bcn.n;
			
			can.p = can.n.n;
			can.n.p = can;
			can.n.n.p = can.n;
			
			
			abn.v.star=abn;
			
			return abn;
		}

		/**
		 * @return true , sofern die Position des Knotens das zur Kante 
		 * gehörende Dreieck schneidet 
		 */
		public boolean contains(final Vertex<?> q) { return contains(q.getX(),q.getY()); }
		

		/**
		 * @return true , sofern der Punkt (x,y) das zur Kante gehörende 
		 * Dreieck schneidet 
		 */
		public boolean contains(final double px, final double py) {
			final EntryVertex<T> a = v, b = n.v, c = p.v;
			return triangleIntersects(px, py, a.getX(), a.getY(), b.getX(), b.getY(), c.getX(), c.getY());
		}
		
		/**
		 * @return die Kante, die auf den Knoten zeigt, der den gegebenen 
		 * Knoten am nächsten liegt.  
		 */
		public Edge<T> locate(final Vertex<?> q) { return locate(q.getX(), q.getY()); };
		

		/**
		 * @return die Kante, die auf den Knoten zeigt, der den gegebenen 
		 * Koordinaten am nächsten liegt.  
		 */
		public Edge<T> locate(final double qx, final double qy) {
			m.access(); //Zugriff verbuchen
			
			if (this.contains(qx,qy)) { //wenn das Dreieck die qx,qy enthält
				//alle (drei) in frage kommenden Knoten überprüfen
				Edge<T> closest = this; 
				double dx = this.v.getX()-qx, dy = this.v.getY()-qy;
				double distanceSq = dx*dx+dy*dy;
				
				for (Edge<T> e = this.n;e!=this;e=e.n) {
					dx = e.v.getX()-qx; dy = e.v.getY()-qy;
					double dSq = dx*dx+dy*dy;
					if (dSq>distanceSq) continue;
					
					closest = e;
					distanceSq = dSq;
				}
				
				return closest; //den nächsten zurückgeben
			}
			
			//andernfalls weiter in Richtung Zielkoordinate wandern
			final EntryVertex<T> a = this.v, b = n.v, c = p.v;
			final double ax = a.getX(), ay = a.getY();
			final double bx = b.getX(), by = b.getY();
			final double cx = c.getX(), cy = c.getY();
			final double mx = (ax+bx+cx)/3.0, my = (ay+by+cy)/3.0;
			
			//schließlich schneidet der Weg vom Dreiecksmittelpunkt
			//zum Ziel garantiert eine Kante, die uns dem Ziel näher bringt  
			if (linesIntersect(	mx, my, qx, qy, ax,ay, bx,by) )
				return n.i.locate(qx,qy);
			//else
			if (linesIntersect(	mx, my, qx, qy, bx, by, cx, cy) )
				return p.i.locate(qx,qy);
			//else
			if (linesIntersect(	mx, my, qx, qy, cx, cy, ax, ay) )
				return this.i.locate(qx,qy);
			
			return null; // keine geschnitten? -> null
		}
		


		/**
		 * @return true, wenn der Umkreis des der Kante zugehörigen Dreicks 
		 * den gegebenen Knoten enthält
		 */
		public boolean circumcircleContains(Vertex<T> q) { return circumcircleContains(q.getX(), q.getY()); }
		
		/**
		 * @return true, wenn der Umkreis des der Kante zugehörigen Dreicks 
		 * die gegebenen Koordinaten enthält
		 */
		public boolean circumcircleContains(double qx, double qy) {
			final EntryVertex<T> a = v, b = n.v, c = p.v;
			return insideCircumcircle(qx,qy, a.getX(),a.getY(),b.getX(),b.getY(), c.getX(), c.getY());
		}
		

		/**
		 * Stellt rekursiv die Delaunayeigenschaft wieder her
		 */
		private void fix() {
			//existiert eine Umkehrkante und liegt der umkehrkante gegenüberliegende 
			//Knoten im Inkreis des eigenen Dreicks 
			if (i!=null && circumcircleContains( i.n.v.x, i.n.v.y )) {
				flip(); //Kante flippen

				Edge<T> _n = this.n;
				Edge<T> _p = this.p;

				Edge<T> _i = this.i,_in=null,_ip=null;

				if (i!=null) {
					_in = this.i.n;
					_ip = this.i.p;
				}
				

				_n.fix(); //fix auf nachfolgerkante
				_p.fix(); //vorgängerkante
				
				if (_i!=null) { //und sofern Umkehrkante
					_i.fix(); //auf umkehrkante 
					_in.fix(); //nachfolger der Umkehrkante
					_ip.fix(); //vorgänger der Umkehrkante anwenden
				}
			}
		}
		
		/**
		 * Kippt eine Kante.
		 */
		private Edge<T> flip() {
			m.access();
			
			final Edge<T> _=this;
			final Edge<T> _n=this.n;
			final Edge<T> _p=this.p;

			final Edge<T> _i=this.i;
			final Edge<T> _in=this.i.n;
			final Edge<T> _ip=this.i.p;
			
			_.v = _n.v;
			_.n = _p;
			_.p = _in;
			
			_i.v = _in.v;
			_i.n = _ip;
			_i.p = _n;
			
			
			_p.n = _in;
			_p.p = _;
			
			_in.p = _p;
			_in.n = _;
			
			_ip.n = _n;
			_ip.p = _i;
			
			_n.n = _i;
			_n.p = _ip;

			//fast vergessen
			//die Stern-Garantie wieder herstellen.
			this.v.star=this;
			this.n.v.star=this.n;
			this.p.v.star=this.p;
			
			this.i.v.star=this.i;
			this.i.n.v.star=this.i.n;
			this.i.p.v.star=this.i.p;			
			
			return this;
		}
	}

	
	

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
		
		//q von stÃ¼tzvektor p subtrahieren
		double qpx = px-qx, qpy = py-qy, qpz = pz-qz;
		
		//mit der normalen skalarmultiplizieren
		double d = nx*qpx+ny*qpy+nz*qpz;
		
		//drÃ¼ber, wenn Abstand positiv
		return d>EPSILON;
	}
	
	static final double EPSILON = 0.00001;

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

//    static public boolean rightOf( final Point2D p, final Point2D a, final Point2D b) { return rightOf(p.getX(),p.getY(),a.getX(),a.getY(),b.getX(),b.getY()); }

}

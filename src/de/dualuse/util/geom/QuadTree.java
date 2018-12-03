/*
 * Quad.java
 *
 * Created on 27. Februar 2007, 17:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.dualuse.util.geom;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


class Leaf<T> extends Bud<T> {
	public final Location key;
	public final T value;
	
	public Leaf(Location key, T value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	protected Node<T> put(double minX, double maxX, double minY, double maxY, Leaf<T> that) {
		if (that.key.equals(this.key)) 
			return that;
		else
			return super.put(minX, maxX, minY, maxY, this).put(minX, maxX, minY, maxY, that);
	}

}


abstract class Triplet<T> extends Pair<T> {
	Node<T> c;
	Triplet(Node<T> a, Node<T> b, Node<T> c) { super(a,b); this.c=c; };
	Node<T> set(Node<T> a, Node<T> b, Node<T> c) { this.a=a; this.b=b; this.c=c; return this; }
}

class Triplet012<T> extends Triplet<T> {
	Triplet012(Node<T> n, Node<T> m, Node<T> o) { super(n, m, o); }
	Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l) { return set(a.put(minX, cx, minY, cy, l),b,c); }
	Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l) { return set(a,b.put(cx, maxX, minY, cy, l),c); }
	Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l) { return set(a,b,c.put(minX, cx, cy, maxY, l)); }
	Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l) { return new Quadruplet<T>(a,b,c,l); }
}

class Triplet013<T> extends Triplet<T> {
	Triplet013(Node<T> n, Node<T> m, Node<T> o) { super(n, m, o); }
	Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l) { return set(a.put(minX, cx, minY, cy, l),b,c); }
	Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l) { return set(a,b.put(cx, maxX, minY, cy, l),c); }
	Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l) { return new Quadruplet<T>(a,b,l,c); }
	Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l) { return set(a,b,c.put(cx, maxX, cy, maxY, l)); }
}

class Triplet023<T> extends Triplet<T> {
	Triplet023(Node<T> n, Node<T> m, Node<T> o) { super(n, m, o); }
	Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l) { return set(a.put(minX, cx, minY, cy, l),b,c); }
	Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l) { return new Quadruplet<T>(a,l,b,c); }
	Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l) { return set(a,b.put(minX, cx, cy, maxY, l),c); }
	Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l) { return set(a,b,c.put(cx, maxX, cy, maxY, l)); }
}

class Triplet123<T> extends Triplet<T> {
	Triplet123(Node<T> n, Node<T> m, Node<T> o) { super(n, m, o); }
	Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l) { return new Quadruplet<T>(l,a,b,c); }
	Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l) { return set(a.put(cx, maxX, minY, cy, l),b,c); }
	Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l) { return set(a,b.put(minX, cx, cy, maxY, l),c); }
	Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l) { return set(a,b,c.put(cx, maxX, cy, maxY, l)); }
}


abstract class Pair<T> extends Single<T> {
	Node<T> b;
	Pair(Node<T> a, Node<T> b) { super(a); this.b=b; }
	Node<T> set(Node<T> a, Node<T> b) { this.a=a; this.b=b; return this; }
}

class Pair01<T> extends Pair<T> {
	public Pair01(Node<T> a, Node<T> b) { super(a, b); }
	Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l) { return set(a.put(minX, cx, minY, cy, l),b); }
	Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l) { return set(a,b.put(cx, maxX, minY, cy, l)); }
	Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l) { return new Triplet012<T>(a,b,l); }
	Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l) { return new Triplet013<T>(a,b,l); }
}

class Pair02<T> extends Pair<T> {
	public Pair02(Node<T> a, Node<T> b) { super(a, b); }
	Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l) { return set(a.put(minX, cx, minY, cy, l),b); }
	Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l) { return new Triplet012<T>(a,l,b); }
	Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l) { return set(a,b.put(minX, cx, cy, maxY, l)); }
	Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l) { return new Triplet023<T>(a,b,l); }
}

class Pair03<T> extends Pair<T> {
	Pair03(Node<T> a, Node<T> b) { super(a, b); }
	Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l) { return set(a.put(minX, cx, minY, cy, l),b); }
	Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l) { return new Triplet013<T>(a,l,b); }
	Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l) { return new Triplet023<T>(a,l,b); }
	Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l) { return set(a,b.put(cx, maxX, cy, maxY, l)); }
}

class Pair12<T> extends Pair<T> {
	Pair12(Node<T> a, Node<T> b) { super(a, b); }
	Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l) { return new Triplet012<T>(l,a,b); }
	Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l) { return set(a.put(cx, maxX, minY, cy, l),b); }
	Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l) { return set(a,b.put(minX, cx, cy, maxY, l)); }
	Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l) { return new Triplet123<T>(a,b,l); }
}


class Pair13<T> extends Pair<T> {
	Pair13(Node<T> a, Node<T> b) { super(a, b); }
	Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l) { return new Triplet012<T>(l,a,b); }
	Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l) { return set(a.put(cx, maxX, minY, cy, l),b); }
	Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l) { return new Triplet123<T>(a,b,l); }
	Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l) { return set(a,b.put(cx, maxX, cy, maxY, l)); }
}

class Pair23<T> extends Pair<T> {
	Pair23(Node<T> a, Node<T> b) { super(a, b); }
	Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l) { return new Triplet023<T>(l,a,b); }
	Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l) { return new Triplet123<T>(l,a,b); }
	Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l) { return set(a.put(minX, cx, cy, maxY, l),b); }
	Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l) { return set(a,b.put(cx, maxX, cy, maxY, l)); }
}


//////

abstract class Single<T> extends Node<T> {
	Node<T> a;
	Single(Node<T> a) { this.a=a; }
	Node<T> set(Node<T> a) { this.a=a; return this; }
}

class Single0<T> extends Single<T> {
	Single0(Node<T> n) { super(n); }
	Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l) { return set(a.put(minX, cx, minY, cy, l)); }
	Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l) { return new Pair01<T>(a,l); }
	Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l) { return new Pair02<T>(a,l); }
	Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l) { return new Pair03<T>(a,l); }
}


class Single1<T> extends Single<T> {
	public Single1(Node<T> n) { super(n); }
	Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l) { return new Pair01<T>(l,a); }
	Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l) { return set(a.put(cx, maxX, minY, cy, l)); }
	Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l) { return new Pair12<T>(a,l); }
	Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l) { return new Pair13<T>(a,l); }
}


class Single2<T> extends Single<T> {
	public Single2(Node<T> n) { super(n); }
	Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l) { return new Pair02<T>(l,a); }
	Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l) { return new Pair12<T>(l,a); }
	Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l) { return set(a.put(minX, cx, cy, maxY, l)); }
	Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l) { return new Pair23<T>(a,l); }
}


class Single3<T> extends Single<T> {
	Single3(Node<T> n) { super(n); }
	Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l) { return new Pair03<T>(l,a); }
	Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l) { return new Pair13<T>(l,a); }
	Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l) { return new Pair23<T>(l,a); }
	Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l) { return set(a.put(cx, maxX, cy, maxY,l)); }
}

class Bud<T> extends Node<T> {
	@Override Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l) { return new Single0<T>(l); }
	@Override Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l) { return new Single1<T>(l); }
	@Override Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l) { return new Single2<T>(l); }
	@Override Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l) { return new Single3<T>(l); }
}



class Quadruplet<T> extends Triplet<T> {
	Node<T> d;
	
	Quadruplet(Node<T> a, Node<T> b, Node<T> c, Node<T> d) { super(a,b,c); this.d = d; }
	Node<T> set(Node<T> a, Node<T> b, Node<T> c, Node<T> d) { this.a=a; this.b=b; this.c=c; this.d=d; return this; }
	
	Node<T> ul(double x0, double xc, double y0, double yc, Leaf<T> l) { return set(a.put(x0, xc, y0, yc, l), b, c, d); }
	Node<T> ur(double xc, double x1, double y0, double yc, Leaf<T> l) { return set(a, b.put(xc, x1, y0, yc, l), c, d); }
	Node<T> ll(double x0, double xc, double yc, double y1, Leaf<T> l) { return set(a, b, c.put(x0, xc, yc, y1, l), d); }
	Node<T> lr(double xc, double x1, double yc, double y1, Leaf<T> l) { return set(a, b, c, d.put(xc, x1, yc, y1, l)); }
}


abstract class Node<T> {
	protected Node<T> put(double minX, double maxX, double minY, double maxY, Leaf<T> l) {
		final double x = l.key.x, y = l.key.y;
		final double cx = (minX+maxX)/2, cy = (minY+maxY)/2;
		if (minX<=x && x<maxX && minY<=y && y<=maxY) 
			switch ((x<cx?1:0) | (y<cy?2:0)) {
			case 0: return ul(minX, cx, minY, cy, l);
			case 1: return ur(cx, maxX, minY, cy, l);
			case 2: return ll(minX, cx, cy, maxY, l);
			case 3: return lr(cx, maxX, cy, maxY, l);
			}
		else
			throw new IllegalArgumentException(); 
			
		return this;
	}
	
	abstract Node<T> ul(double minX, double cx, double minY, double cy, Leaf<T> l);
	abstract Node<T> ur(double cx, double maxX, double minY, double cy, Leaf<T> l);
	abstract Node<T> ll(double minX, double cx, double cy, double maxY, Leaf<T> l);
	abstract Node<T> lr(double cx, double maxX, double cy, double maxY, Leaf<T> l);
}



//class Quad<T> extends QuadNode<T> {
//	final BoundingBox dimension;
//	
//	public Quad(BoundingBox limits) {
//		this.dimension = limits;
//	}
//	
//	public Quad<T> put(Location key, T value) {
//		return new QuadRoot<T>(dimension, new QuadLeaf<T>(key, value));
//	}
//	
//	
//	@Override
//	protected QuadNode<T> put(double minX, double maxX, double minY, double maxY, QuadLeaf<T> l) {
//		return null;
//	}
//}
//
//class QuadRoot<T> extends Quad<T> {
//	
//	public final QuadNode<T> entry;
//	
//	public QuadRoot(BoundingBox limits, QuadLeaf<T> entry) {
//		super(limits);
//		this.entry = entry;
//	}
//	
//	@Override
//	public Quad<T> put(Location key, T value) {
//		entry.put(dimension.x.min, dimension.x.max, dimension.y.min, dimension.y.max, new QuadLeaf<T>(key, value));
//		return this;
//	}
//	
//}


//class QuadRoot<T> extends QuadBranch<T> {
//	final BoundingBox dimension;
//	
//	public QuadRoot(Range horizontal, Range vertical) {
//		dimension = new BoundingBox(horizontal, vertical);
//	}
//	
//	
//	public QuadRoot<T> put(Location l, T value) {
//		return null;
//	}
//}

public class QuadTree<T> implements CartesianMap<T> {
	
	double minX = -1, maxX = +1;
	double minY = -1, maxY = +1;
	
	Node<T> root = null;
	
	public QuadTree() { }
	
	
	
	
	
	
	
	@Override
	public Collection<T> collect(Region a, Collection<T> c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CartesianMap<T> visit(Region a, Consumer<T> c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CartesianMap<T> visit(Region a, BiConsumer<Location, T> c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CartesianMap<T> find(Location l, Consumer<T> c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CartesianMap<T> find(Location l, BiConsumer<Location, T> c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T find(Location l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T put(Location key, T value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends Location, ? extends T> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Location> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<T> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Entry<Location, T>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	
}

/**
 * PROBLEMS:
 * Write-on-Border - dyn. Abstieg solange sich Zellen finden lassen, die
 * ein Element beinhalten funktioniert nicht fuer Quad-Grenzen - etvl.
 * auf fixed-depth-tree wechseln, und jeden Ecke als Quad-Element benennen
 * ach ka alles mist :(
 *
 *
 */

//
//public class QuadTree<T extends Shape> extends Rectangle2D.Double implements Iterable<T> {
//	public static interface Visitor<T> { 
//		void meet(T t);
//	}
//
//	private static final long serialVersionUID = 1L;
//	
//	private int treeSize = 0;
//    //private ArrayList<T> shapes = new ArrayList<T>();
//    private Collection<T> shapes = new LinkedList<T>();
//    private QuadTree<T> ul = null, ur = null, ll = null, lr = null, p = null;
//    
//    private QuadTree(QuadTree<T> parent, double x, double y, double w, double h ) { super(x,y,w,h); this.p = parent;  }
//    public QuadTree(double x, double y, double w, double h) { super(x,y,w,h);  }
//    
//    public QuadTree<T> upperLeft() { return ul==null?(ul=new QuadTree<T>(this,getX(),getY(),getWidth()/2,getHeight()/2)):ul; }
//    public QuadTree<T> upperRight() { return ur==null?(ur=new QuadTree<T>(this,getX()+getWidth()/2,getY(),getWidth()/2,getHeight()/2)):ur; }
//    public QuadTree<T> lowerLeft() { return ll==null?(ll=new QuadTree<T>(this,getX(),getY()+getHeight()/2,getWidth()/2,getHeight()/2)):ll; }
//    public QuadTree<T> lowerRight() { return lr==null?(lr=new QuadTree<T>(this,getX()+getHeight()/2,getY()+getHeight()/2,getWidth()/2,getHeight()/2)):lr; }
//
//
//    public int size() { return treeSize; }
//    
//    public QuadTree<T> getRoot() { for (QuadTree<T> q = this;;q=q.p) if (q.p==null) return q; }
//
//
//    public QuadTree<T> find(Rectangle2D r) { return find(r.getX(),r.getY(),r.getWidth(),r.getHeight()); }
//    
//    public QuadTree<T> find(final double x, final double y, final double w, final double h)     
//    {
//        if (w<=0||h<=0) throw new IllegalArgumentException("Rectangle must not be empty");
//    	
//    
//        if (upperLeft().contains(x,y,w,h)) return upperLeft().find(x,y,w,h); 
//        else if (upperRight().contains(x,y,w,h)) return upperRight().find(x,y,w,h); 
//        else if (lowerLeft().contains(x,y,w,h)) return lowerLeft().find(x,y,w,h); 
//        else if (lowerRight().contains(x,y,w,h)) return lowerRight().find(x,y,w,h); 
//        else if (this.contains(x,y,w,h)) return this;
//        else if (p!=null) return p.find(x,y,w,h);
//        
//        final double cx = x+w/2, cy = y+h/2;
//        p = new QuadTree<T>(this.getX()-(cx<this.getCenterX()?1:0)*getWidth(),
//                                this.getY()-(cy<this.getCenterY()?1:0)*getHeight(),
//                                this.getWidth()*2,this.getHeight()*2);
//        p.treeSize = treeSize;
//        
//        if (cx<this.getCenterX()) if (cy<this.getCenterY()) p.lr = this; else p.ur = this;
//        else if (cy<this.getCenterY()) p.ll = this; else p.ul = this;
//
//        return p.find(x,y,w,h);
//    }
//    
//    
//    public QuadTree<T> add( T a ) { return add(a, a.getBounds2D()); }
//    public QuadTree<T> add( T a, Rectangle2D r ) { return add(a,r.getX(),r.getY(),r.getWidth(),r.getHeight()); }
//    
//    public QuadTree<T> add( T a, double x, double y, double w, double h) {
//        QuadTree<T> q = find(x,y,w,h);
//        q.shapes.add(a);
//        for (QuadTree<T> s=q;s!=null;s=s.p) s.treeSize++; //log n <- sucks
//        return q;
//    }
//    
//    
//    public QuadTree<T> remove( T a ) { return remove(a, a.getBounds2D()); }
//    public QuadTree<T> remove( T a, Rectangle2D r ) { return remove( a, r.getX(),r.getY(),r.getWidth(),r.getHeight()); }
//    public QuadTree<T> remove( T a, double x, double y, double w, double h) {
//        QuadTree<T> q = find(x,y,w,h);
//        for (QuadTree<T> s=q;s!=null;s=s.p) s.treeSize--;
//        q.shapes.remove(a);
//        return q;
//    }
//    
//    // public<R super T> Collection<R> get( Rectangle2D r, Collection<R> storage ) {
//    // R ist mindestens eine Collection<mit Platz f�r 'egal' aber T muss passen>
//    public<R extends Collection<? super T>> R get( Rectangle2D r, R storage ) 
//    { return get(r.getX(),r.getY(),r.getWidth(),r.getHeight(),storage); }
//    
//    public<R extends Collection<? super T>> R get( double x, double y, double w, double h, R storage ) { 
//       
//    	for (T s: shapes)
//    		if (s.getBounds2D().intersects(x,y,w,h)) //cached rectangle -> cheap!
//	    		if (s.intersects(x,y,w,h))
//	                storage.add(s);
//        
//        /*
//        for (int i=0,l=shapes.size();i<l;i++)
//            if (shapes.get(i).intersects(x,y,w,h))
//                storage.add(shapes.get(i));
//        */
//        
//        if (size()>shapes.size()) { //sth. stored in child nodes
//            if (upperLeft().intersects(x,y,w,h)) upperLeft().get(x,y,w,h,storage);
//            if (upperRight().intersects(x,y,w,h)) upperRight().get(x,y,w,h,storage);
//            if (lowerLeft().intersects(x,y,w,h)) lowerLeft().get(x,y,w,h,storage);
//            if (lowerRight().intersects(x,y,w,h)) lowerRight().get(x,y,w,h,storage);
//        }
//            
//        return storage;
//    }
//        
//    
//    
//    public void visit( Rectangle2D r, Visitor<T> v ) { visit(r.getX(),r.getY(),r.getWidth(),r.getHeight(),v); }
//    public void visit( double x, double y, double w, double h, Visitor<T> v) {
//    
//    	for (T s: shapes)
//            if (s.intersects(x,y,w,h))
//                v.meet(s);
//        
//        if (size()>shapes.size()) { //sth. stored in child nodes
//            if (upperLeft().intersects(x,y,w,h)) upperLeft().visit(x,y,w,h,v);
//            if (upperRight().intersects(x,y,w,h)) upperRight().visit(x,y,w,h,v);
//            if (lowerLeft().intersects(x,y,w,h)) lowerLeft().visit(x,y,w,h,v);
//            if (lowerRight().intersects(x,y,w,h)) lowerRight().visit(x,y,w,h,v);
//        }
//    }
//    
//    
//    public Iterator<T> iterator() { 
//        return new Iterator<T>() {
//            Iterator<T> current = shapes.iterator();
//            LinkedList<QuadTree<T>> suspects = new LinkedList<QuadTree<T>>();
//            {
//                if (upperLeft().size()>0) suspects.add(upperLeft());
//                if (upperRight().size()>0) suspects.add(upperRight());
//                if (lowerLeft().size()>0) suspects.add(lowerLeft());
//                if (lowerRight().size()>0) suspects.add(lowerRight());                
//            }
//            
//            public boolean hasNext() { 
//                if (current.hasNext()) return true;
//                for (QuadTree<T> s: suspects) 
//                    if (s.size()>0) return true;
//                
//                return false;
//            }
//            
//            public T next() { 
//                if (current.hasNext()) return current.next();
//                QuadTree<T> q = suspects.removeFirst();
//                
//                if (q.upperLeft().size()>0) suspects.add(q.upperLeft());
//                if (q.upperRight().size()>0) suspects.add(q.upperRight());
//                if (q.lowerLeft().size()>0) suspects.add(q.lowerLeft());
//                if (q.lowerRight().size()>0) suspects.add(q.lowerRight());
//
//                current = q.shapes.iterator();
//                return next();
//            }
//            
//            public void remove() { throw new RuntimeException("operation not supported"); }
//        };    
//    }
//
//    
//
//    //untested
//    public int getMaxNodeSize() { 
//    	int max = shapes.size();
//
//    	if (upperLeft().size()>0) max = Math.max( max, upperLeft().getMaxNodeSize() );
//        if (upperRight().size()>0) max = Math.max( max, upperRight().getMaxNodeSize() );
//        if (lowerLeft().size()>0) max = Math.max( max, lowerLeft().getMaxNodeSize() );
//        if (lowerRight().size()>0) max = Math.max( max, lowerRight().getMaxNodeSize() );
//        
//        return max;    	
//    };
//    
//    
//    
//    public void paintNodeBoundaries(Graphics2D g) { paintNodeBoundaries(g,g.getClipBounds()); }
//    public void paintNodeBoundaries(Graphics2D g, Rectangle clipBounds) {
//    	g.draw(this);
//    	
//    	if (upperLeft().size()>0 && upperLeft().intersects(clipBounds) ) upperLeft().paintNodeBoundaries(g, clipBounds);
//        if (upperRight().size()>0 && upperRight().intersects(clipBounds)) upperRight().paintNodeBoundaries(g, clipBounds);
//        if (lowerLeft().size()>0  && lowerLeft().intersects(clipBounds)) lowerLeft().paintNodeBoundaries(g, clipBounds);
//        if (lowerRight().size()>0  && lowerRight().intersects(clipBounds)) lowerRight().paintNodeBoundaries(g, clipBounds);
//    }
//
//
//    
//    final static Color shapeColor = new Color(0,0,0,0.4f); 
//    	
//    public void paint(Graphics2D g) { paint(g,g.getClipBounds()); }
//    public void paint(Graphics2D g, Rectangle clipBounds) {
//    	g.setColor(Color.BLACK);
//    	g.draw(this);
//    	
//    	g.setColor(shapeColor);
//    	for (Shape s: shapes)
//    		g.fill(s);
//    	
//    	if (upperLeft().size()>0 && upperLeft().intersects(clipBounds) ) upperLeft().paint(g, clipBounds);
//        if (upperRight().size()>0 && upperRight().intersects(clipBounds)) upperRight().paint(g, clipBounds);
//        if (lowerLeft().size()>0  && lowerLeft().intersects(clipBounds)) lowerLeft().paint(g, clipBounds);
//        if (lowerRight().size()>0  && lowerRight().intersects(clipBounds)) lowerRight().paint(g, clipBounds);
//    }
//
//    
//    public static void main(String args[]) {
//        
//        JFrame f = new JFrame();
//        f.getContentPane().add(new JComponent() {
//			private static final long serialVersionUID = 1L;
//			QuadTree<Ellipse2D> q = new QuadTree<Ellipse2D>(100,100,200,200);
//            LinkedList<Shape> shapes = new LinkedList<Shape>();
//            
//            
//            Shape current;
//            Point2D center;
//            {
//                this.addMouseListener( new MouseAdapter() {
//                    public void mousePressed(MouseEvent e) { center = e.getPoint(); }
//                    public void mouseReleased(MouseEvent e) { 
//                        Ellipse2D ellipse = new Ellipse2D.Double();
//                        ellipse.setFrameFromCenter(center, e.getPoint());
//                        if (ellipse.isEmpty()) return;
//                        shapes.add( ellipse );
//                        
//                        q = q.add( ellipse ).getRoot();
//                        repaint();
//                        center = null;
//                        
//                        System.out.println(shapes.size()+" vs "+q.size());
//                        
//                        Collection<Ellipse2D> l = q.get(q,new HashSet<Ellipse2D>());
//                        
//                        HashSet<Shape> m = new HashSet<Shape>(shapes);
//                        
//                        System.out.println(l.equals(m));
//                        //System.out.println(shapes);
//                        //System.out.println(l);
//                    }
//                });
//                
//                this.addMouseMotionListener( new MouseMotionListener() {
//                    public void mouseDragged(MouseEvent mouseEvent) {
//                        Ellipse2D ellipse = new Ellipse2D.Double();
//                        ellipse.setFrameFromCenter(center, mouseEvent.getPoint());
//                        current = ellipse;
//                        repaint();
//                    }
//                    public void mouseMoved(MouseEvent mouseEvent) {}
//                });
//            }
//            
//            
//            public void paint(Graphics2D g, QuadTree<? extends Shape> q) {
//                g.draw(q);
//                    
//                if (q.size()>0) {
//                    paint(g,q.upperLeft());
//                    paint(g,q.upperRight());
//                    paint(g,q.lowerLeft());
//                    paint(g,q.lowerRight());
//                }
//            }
//            
//            public void paint(Graphics g) {
//                paint((Graphics2D)g, q);
//                
//                for (Shape s: shapes) ((Graphics2D)g).draw(s);
//                
//                if (current!=null) ((Graphics2D)g).draw(current);
//            }
//            
//        });
//        
//        
//        f.setBounds(300,100,600,400);
//        f.setVisible(true);
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    }
//}  
///*
//class Quad<T extends Shape> extends Rectangle2D.Double implements Iterable<T> {
//    
//    private LinkedList<T> strokes = new LinkedList<T>();
//    private Quad<T> ul = null, ur = null, ll = null, lr = null;
//
//    private Quad(Rectangle2D r) { this(r.getX(), r.getY(), r.getWidth(), r.getHeight());};
//    public Quad(double x, double y, double w, double h) { super(x,y,w,h); }
//    
//    public Quad<T> upperLeft() { return ul==null?(ul=new Quad(getX(),getY(),getWidth()/2,getHeight()/2)):ul; }
//    public Quad<T> upperRight() { return ur==null?(ur=new Quad(getX()+getWidth()/2,getY(),getWidth()/2,getHeight()/2)):ul; }
//    public Quad<T> lowerLeft() { return ll==null?(ll=new Quad(getX(),getY()+getHeight()/2,getWidth()/2,getHeight()/2)):ul; }
//    public Quad<T> lowerRight() { return lr==null?(lr=new Quad(getX()+getHeight()/2,getY()+getHeight()/2,getWidth()/2,getHeight()/2)):ul; }
//    
//    Quad add( T a ) {        
//        Rectangle2D ar = a.getBounds2D();
//        if (upperLeft().contains(ar)) upperLeft().add(a);
//        else if (upperRight().contains(ar)) upperRight().add(a);
//        else if (lowerRight().contains(ar)) lowerRight().add(a);
//        else if (lowerLeft().contains(ar)) lowerLeft().add(a);
//        else if (this.contains(ar)) strokes.add(a);
//        else {
//            // omfg verify this !!1 total blind gecodet 
//            Quad<T> q = new Quad(this.getX()-(ar.getCenterX()<this.getCenterX()?1:0)*getWidth(),this.getY()-(ar.getCenterY()<this.getCenterY()?1:0)*getHeight(),this.getWidth()*2,this.getHeight()*2);
//            if (ar.getCenterX()<this.getCenterX()) if (ar.getCenterY()<this.getCenterY()) q.lr = this; else q.ur = this;
//            else if (ar.getCenterY()<this.getCenterY()) q.ll = this; else q.ul = this;
//            
//            return q.add(a);
//        }
//        
//        return this;
//    };
//    
//    Quad remove( T a ) {
//        return null; //... mann ich kann mich nich mehr konzentrieren
//    }
//
//    Iterator<T> iterator() { strokes.iterator(); };
//    
//    
//    public static void main(String args[]) {
//        JFrame f = new JFrame();
//        f.getContentPane().add(new JComponent() {
//            Quad<Line2D> q = new Quad2D<Line2D>(100,100,400,400);
//             
//            {
//                
//            }
//            
//            
//            public void paint(Graphics2D g, Quad q) {
//                
//            }
//            
//            public void paint(Graphics g) {
//                paint((Graphics2D)g, q);
//            }
//            
//        });
//    }
//}
//*/
//
//
//
//
//
//

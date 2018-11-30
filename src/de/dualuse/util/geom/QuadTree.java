/*
 * Quad.java
 *
 * Created on 27. Februar 2007, 17:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.dualuse.util.geom;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * PROBLEMS:
 * Write-on-Border - dyn. Abstieg solange sich Zellen finden lassen, die
 * ein Element beinhalten funktioniert nicht fuer Quad-Grenzen - etvl.
 * auf fixed-depth-tree wechseln, und jeden Ecke als Quad-Element benennen
 * ach ka alles mist :(
 *
 *
 */


public class QuadTree<T extends Shape> extends Rectangle2D.Double implements Iterable<T> {
	public static interface Visitor<T> { 
		void meet(T t);
	}

	private static final long serialVersionUID = 1L;
	
	private int treeSize = 0;
    //private ArrayList<T> shapes = new ArrayList<T>();
    private Collection<T> shapes = new LinkedList<T>();
    private QuadTree<T> ul = null, ur = null, ll = null, lr = null, p = null;
    
    private QuadTree(QuadTree<T> parent, double x, double y, double w, double h ) { super(x,y,w,h); this.p = parent;  }
    public QuadTree(double x, double y, double w, double h) { super(x,y,w,h);  }
    
    public QuadTree<T> upperLeft() { return ul==null?(ul=new QuadTree<T>(this,getX(),getY(),getWidth()/2,getHeight()/2)):ul; }
    public QuadTree<T> upperRight() { return ur==null?(ur=new QuadTree<T>(this,getX()+getWidth()/2,getY(),getWidth()/2,getHeight()/2)):ur; }
    public QuadTree<T> lowerLeft() { return ll==null?(ll=new QuadTree<T>(this,getX(),getY()+getHeight()/2,getWidth()/2,getHeight()/2)):ll; }
    public QuadTree<T> lowerRight() { return lr==null?(lr=new QuadTree<T>(this,getX()+getHeight()/2,getY()+getHeight()/2,getWidth()/2,getHeight()/2)):lr; }


    public int size() { return treeSize; }
    
    public QuadTree<T> getRoot() { for (QuadTree<T> q = this;;q=q.p) if (q.p==null) return q; }


    public QuadTree<T> find(Rectangle2D r) { return find(r.getX(),r.getY(),r.getWidth(),r.getHeight()); }
    
    public QuadTree<T> find(final double x, final double y, final double w, final double h)     
    {
        if (w<=0||h<=0) throw new IllegalArgumentException("Rectangle must not be empty");
    	
    
        if (upperLeft().contains(x,y,w,h)) return upperLeft().find(x,y,w,h); 
        else if (upperRight().contains(x,y,w,h)) return upperRight().find(x,y,w,h); 
        else if (lowerLeft().contains(x,y,w,h)) return lowerLeft().find(x,y,w,h); 
        else if (lowerRight().contains(x,y,w,h)) return lowerRight().find(x,y,w,h); 
        else if (this.contains(x,y,w,h)) return this;
        else if (p!=null) return p.find(x,y,w,h);
        
        final double cx = x+w/2, cy = y+h/2;
        p = new QuadTree<T>(this.getX()-(cx<this.getCenterX()?1:0)*getWidth(),
                                this.getY()-(cy<this.getCenterY()?1:0)*getHeight(),
                                this.getWidth()*2,this.getHeight()*2);
        p.treeSize = treeSize;
        
        if (cx<this.getCenterX()) if (cy<this.getCenterY()) p.lr = this; else p.ur = this;
        else if (cy<this.getCenterY()) p.ll = this; else p.ul = this;

        return p.find(x,y,w,h);
    }
    
    
    public QuadTree<T> add( T a ) { return add(a, a.getBounds2D()); }
    public QuadTree<T> add( T a, Rectangle2D r ) { return add(a,r.getX(),r.getY(),r.getWidth(),r.getHeight()); }
    
    public QuadTree<T> add( T a, double x, double y, double w, double h) {
        QuadTree<T> q = find(x,y,w,h);
        q.shapes.add(a);
        for (QuadTree<T> s=q;s!=null;s=s.p) s.treeSize++; //log n <- sucks
        return q;
    }
    
    
    public QuadTree<T> remove( T a ) { return remove(a, a.getBounds2D()); }
    public QuadTree<T> remove( T a, Rectangle2D r ) { return remove( a, r.getX(),r.getY(),r.getWidth(),r.getHeight()); }
    public QuadTree<T> remove( T a, double x, double y, double w, double h) {
        QuadTree<T> q = find(x,y,w,h);
        for (QuadTree<T> s=q;s!=null;s=s.p) s.treeSize--;
        q.shapes.remove(a);
        return q;
    }
    
    // public<R super T> Collection<R> get( Rectangle2D r, Collection<R> storage ) {
    // R ist mindestens eine Collection<mit Platz f�r 'egal' aber T muss passen>
    public<R extends Collection<? super T>> R get( Rectangle2D r, R storage ) 
    { return get(r.getX(),r.getY(),r.getWidth(),r.getHeight(),storage); }
    
    public<R extends Collection<? super T>> R get( double x, double y, double w, double h, R storage ) { 
       
    	for (T s: shapes)
    		if (s.getBounds2D().intersects(x,y,w,h)) //cached rectangle -> cheap!
	    		if (s.intersects(x,y,w,h))
	                storage.add(s);
        
        /*
        for (int i=0,l=shapes.size();i<l;i++)
            if (shapes.get(i).intersects(x,y,w,h))
                storage.add(shapes.get(i));
        */
        
        if (size()>shapes.size()) { //sth. stored in child nodes
            if (upperLeft().intersects(x,y,w,h)) upperLeft().get(x,y,w,h,storage);
            if (upperRight().intersects(x,y,w,h)) upperRight().get(x,y,w,h,storage);
            if (lowerLeft().intersects(x,y,w,h)) lowerLeft().get(x,y,w,h,storage);
            if (lowerRight().intersects(x,y,w,h)) lowerRight().get(x,y,w,h,storage);
        }
            
        return storage;
    }
        
    
    
    public void visit( Rectangle2D r, Visitor<T> v ) { visit(r.getX(),r.getY(),r.getWidth(),r.getHeight(),v); }
    public void visit( double x, double y, double w, double h, Visitor<T> v) {
    
    	for (T s: shapes)
            if (s.intersects(x,y,w,h))
                v.meet(s);
        
        if (size()>shapes.size()) { //sth. stored in child nodes
            if (upperLeft().intersects(x,y,w,h)) upperLeft().visit(x,y,w,h,v);
            if (upperRight().intersects(x,y,w,h)) upperRight().visit(x,y,w,h,v);
            if (lowerLeft().intersects(x,y,w,h)) lowerLeft().visit(x,y,w,h,v);
            if (lowerRight().intersects(x,y,w,h)) lowerRight().visit(x,y,w,h,v);
        }
    }
    
    
    public Iterator<T> iterator() { 
        return new Iterator<T>() {
            Iterator<T> current = shapes.iterator();
            LinkedList<QuadTree<T>> suspects = new LinkedList<QuadTree<T>>();
            {
                if (upperLeft().size()>0) suspects.add(upperLeft());
                if (upperRight().size()>0) suspects.add(upperRight());
                if (lowerLeft().size()>0) suspects.add(lowerLeft());
                if (lowerRight().size()>0) suspects.add(lowerRight());                
            }
            
            public boolean hasNext() { 
                if (current.hasNext()) return true;
                for (QuadTree<T> s: suspects) 
                    if (s.size()>0) return true;
                
                return false;
            }
            
            public T next() { 
                if (current.hasNext()) return current.next();
                QuadTree<T> q = suspects.removeFirst();
                
                if (q.upperLeft().size()>0) suspects.add(q.upperLeft());
                if (q.upperRight().size()>0) suspects.add(q.upperRight());
                if (q.lowerLeft().size()>0) suspects.add(q.lowerLeft());
                if (q.lowerRight().size()>0) suspects.add(q.lowerRight());

                current = q.shapes.iterator();
                return next();
            }
            
            public void remove() { throw new RuntimeException("operation not supported"); }
        };    
    }

    

    //untested
    public int getMaxNodeSize() { 
    	int max = shapes.size();

    	if (upperLeft().size()>0) max = Math.max( max, upperLeft().getMaxNodeSize() );
        if (upperRight().size()>0) max = Math.max( max, upperRight().getMaxNodeSize() );
        if (lowerLeft().size()>0) max = Math.max( max, lowerLeft().getMaxNodeSize() );
        if (lowerRight().size()>0) max = Math.max( max, lowerRight().getMaxNodeSize() );
        
        return max;    	
    };
    
    
    
    public void paintNodeBoundaries(Graphics2D g) { paintNodeBoundaries(g,g.getClipBounds()); }
    public void paintNodeBoundaries(Graphics2D g, Rectangle clipBounds) {
    	g.draw(this);
    	
    	if (upperLeft().size()>0 && upperLeft().intersects(clipBounds) ) upperLeft().paintNodeBoundaries(g, clipBounds);
        if (upperRight().size()>0 && upperRight().intersects(clipBounds)) upperRight().paintNodeBoundaries(g, clipBounds);
        if (lowerLeft().size()>0  && lowerLeft().intersects(clipBounds)) lowerLeft().paintNodeBoundaries(g, clipBounds);
        if (lowerRight().size()>0  && lowerRight().intersects(clipBounds)) lowerRight().paintNodeBoundaries(g, clipBounds);
    }


    
    final static Color shapeColor = new Color(0,0,0,0.4f); 
    	
    public void paint(Graphics2D g) { paint(g,g.getClipBounds()); }
    public void paint(Graphics2D g, Rectangle clipBounds) {
    	g.setColor(Color.BLACK);
    	g.draw(this);
    	
    	g.setColor(shapeColor);
    	for (Shape s: shapes)
    		g.fill(s);
    	
    	if (upperLeft().size()>0 && upperLeft().intersects(clipBounds) ) upperLeft().paint(g, clipBounds);
        if (upperRight().size()>0 && upperRight().intersects(clipBounds)) upperRight().paint(g, clipBounds);
        if (lowerLeft().size()>0  && lowerLeft().intersects(clipBounds)) lowerLeft().paint(g, clipBounds);
        if (lowerRight().size()>0  && lowerRight().intersects(clipBounds)) lowerRight().paint(g, clipBounds);
    }

    
    public static void main(String args[]) {
        
        JFrame f = new JFrame();
        f.getContentPane().add(new JComponent() {
			private static final long serialVersionUID = 1L;
			QuadTree<Ellipse2D> q = new QuadTree<Ellipse2D>(100,100,200,200);
            LinkedList<Shape> shapes = new LinkedList<Shape>();
            
            
            Shape current;
            Point2D center;
            {
                this.addMouseListener( new MouseAdapter() {
                    public void mousePressed(MouseEvent e) { center = e.getPoint(); }
                    public void mouseReleased(MouseEvent e) { 
                        Ellipse2D ellipse = new Ellipse2D.Double();
                        ellipse.setFrameFromCenter(center, e.getPoint());
                        if (ellipse.isEmpty()) return;
                        shapes.add( ellipse );
                        
                        q = q.add( ellipse ).getRoot();
                        repaint();
                        center = null;
                        
                        System.out.println(shapes.size()+" vs "+q.size());
                        
                        Collection<Ellipse2D> l = q.get(q,new HashSet<Ellipse2D>());
                        
                        HashSet<Shape> m = new HashSet<Shape>(shapes);
                        
                        System.out.println(l.equals(m));
                        //System.out.println(shapes);
                        //System.out.println(l);
                    }
                });
                
                this.addMouseMotionListener( new MouseMotionListener() {
                    public void mouseDragged(MouseEvent mouseEvent) {
                        Ellipse2D ellipse = new Ellipse2D.Double();
                        ellipse.setFrameFromCenter(center, mouseEvent.getPoint());
                        current = ellipse;
                        repaint();
                    }
                    public void mouseMoved(MouseEvent mouseEvent) {}
                });
            }
            
            
            public void paint(Graphics2D g, QuadTree<? extends Shape> q) {
                g.draw(q);
                    
                if (q.size()>0) {
                    paint(g,q.upperLeft());
                    paint(g,q.upperRight());
                    paint(g,q.lowerLeft());
                    paint(g,q.lowerRight());
                }
            }
            
            public void paint(Graphics g) {
                paint((Graphics2D)g, q);
                
                for (Shape s: shapes) ((Graphics2D)g).draw(s);
                
                if (current!=null) ((Graphics2D)g).draw(current);
            }
            
        });
        
        
        f.setBounds(300,100,600,400);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}  
/*
class Quad<T extends Shape> extends Rectangle2D.Double implements Iterable<T> {
    
    private LinkedList<T> strokes = new LinkedList<T>();
    private Quad<T> ul = null, ur = null, ll = null, lr = null;

    private Quad(Rectangle2D r) { this(r.getX(), r.getY(), r.getWidth(), r.getHeight());};
    public Quad(double x, double y, double w, double h) { super(x,y,w,h); }
    
    public Quad<T> upperLeft() { return ul==null?(ul=new Quad(getX(),getY(),getWidth()/2,getHeight()/2)):ul; }
    public Quad<T> upperRight() { return ur==null?(ur=new Quad(getX()+getWidth()/2,getY(),getWidth()/2,getHeight()/2)):ul; }
    public Quad<T> lowerLeft() { return ll==null?(ll=new Quad(getX(),getY()+getHeight()/2,getWidth()/2,getHeight()/2)):ul; }
    public Quad<T> lowerRight() { return lr==null?(lr=new Quad(getX()+getHeight()/2,getY()+getHeight()/2,getWidth()/2,getHeight()/2)):ul; }
    
    Quad add( T a ) {        
        Rectangle2D ar = a.getBounds2D();
        if (upperLeft().contains(ar)) upperLeft().add(a);
        else if (upperRight().contains(ar)) upperRight().add(a);
        else if (lowerRight().contains(ar)) lowerRight().add(a);
        else if (lowerLeft().contains(ar)) lowerLeft().add(a);
        else if (this.contains(ar)) strokes.add(a);
        else {
            // omfg verify this !!1 total blind gecodet 
            Quad<T> q = new Quad(this.getX()-(ar.getCenterX()<this.getCenterX()?1:0)*getWidth(),this.getY()-(ar.getCenterY()<this.getCenterY()?1:0)*getHeight(),this.getWidth()*2,this.getHeight()*2);
            if (ar.getCenterX()<this.getCenterX()) if (ar.getCenterY()<this.getCenterY()) q.lr = this; else q.ur = this;
            else if (ar.getCenterY()<this.getCenterY()) q.ll = this; else q.ul = this;
            
            return q.add(a);
        }
        
        return this;
    };
    
    Quad remove( T a ) {
        return null; //... mann ich kann mich nich mehr konzentrieren
    }

    Iterator<T> iterator() { strokes.iterator(); };
    
    
    public static void main(String args[]) {
        JFrame f = new JFrame();
        f.getContentPane().add(new JComponent() {
            Quad<Line2D> q = new Quad2D<Line2D>(100,100,400,400);
             
            {
                
            }
            
            
            public void paint(Graphics2D g, Quad q) {
                
            }
            
            public void paint(Graphics g) {
                paint((Graphics2D)g, q);
            }
            
        });
    }
}
*/







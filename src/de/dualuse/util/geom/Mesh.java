package de.dualuse.util.geom;

public class Mesh<T extends Location> extends Graph<T> {
	
	public Mesh() { }
	public Mesh(T vertex) { this.node = vertex; }
	
	@Override protected Mesh<T> createEdge() { return new Mesh<>(); }
	
	
	
	@Override
	public boolean contains(double px, double py) {
		if ( this==next|| this==next.next ) // uni or bipolar mesh
			return true;
		else
		if (isForwardTriangle()) 
			return triangleIntersects(px, py, node.x, node.y, next.node.x, next.node.y, next.next.node.x, next.next.node.y);
		else
			return super.contains(px, py);
	}
	
	
	@SuppressWarnings("unchecked")
	public Mesh<T> locate(double qx, double qy) {
		//if target is inside current edge loop 
		if (this.contains(qx,qy)) 
			//finds the node's edge which is closest to target q 
			return (Mesh<T>) reduceLoop( self(), (a,b) -> b.node.isCloserTo(qx, qy).than(a.node) ? b: a);
		
		//computes a centroid, then loops over edges and keeps the closest one that intersects the line between centroid and target point q  
		Edge<T> guess = centroid( (cx,cy) -> reduceLoop( null, (a,b) -> b.intersects(cx, cy, qx, qy) && (a==null || b.node.isCloserTo(qx, qy).than(a.node))?b:a ) );
		//XXX may return null as intersects fails if point is on line (= find solution for degenerate cases)
		
		//locates the edge loop that contains it on the twin side of it
		return Mesh.class.cast(guess.twin).locate(qx, qy);
	}

	@Override public Mesh<T> attach(T vertex) { return (Mesh<T>) super.attach(vertex); }
	@Override public Mesh<T> detach() { return (Mesh<T>) super.detach(); }

	
	
//	/**
//	 * Attaches a given vertex to all vertices of the locally convex portion of this edge's loop 
//	 * @param v
//	 * @return
//	 */
//	public Edge<T> mesh( Vertex<T> v ) {
//		Edge<T> next = this.next, prev = this.prev;
//		attach(v);
//
//
//		//memorize this' next and prev, as topology changes due to insertion
//		int prevOrientation = orientationTo(v.x, v.y); 
//		int nextOrientation = next.orientationTo(v.x, v.y);
//		
//		prev.walk( 
//				e-> highlight(e.next).orientationTo(v.x, v.y)!=prevOrientation || e.node==v, 
//				backward(), 
//				e-> highlight( e.attach(v)) 
//		);
//		
//		
//		highlight(next);
//		next.walk( 
//				e->highlight(e).orientationTo(v.x, v.y)!=nextOrientation || e.next.node==v, 
//				e->highlight(e.next.twin.next), // as it has been modified 
//				e->highlight(e.attach(v)) );
//		
//		
//		return this;
//	}
//	
	
		
	
}
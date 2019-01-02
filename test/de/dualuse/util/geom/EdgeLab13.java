package de.dualuse.util.geom;

import java.util.ArrayList;

public class EdgeLab13 {
	public static void main(String[] args) throws Exception {
		Edge<Vertex<Integer>> e = Graphs.circle(500, 500, 400, 7);
		
		
//		Vertex<Integer> v = new Vertex<Integer>(250, 610, 6);
		Vertex<Integer> v = new Vertex<Integer>(400, 450, 8);


		//////////
		EdgeListInspectorFrame elif = new EdgeListInspectorFrame(e);
		
		
		elif.inspector.click.setLocation(v.x, v.y);
		e.mesh(v.x,v.y);
		
		Edge<Vertex<Integer>> g = e.next.next.twin;

		
//		g.attach( v );
//		g.prev.attach(v);
//		g.prev.twin.prev.prev.attach(v);

		g = g.attachLoop( v );
		
		g.next.fixStar(g.next.next.twin);		
		
//		Edge<Vertex<Integer>> h = g.next.twin;
////		Edge.highlight(	h );
////		System.out.println(h.isTriangle());
////		System.out.println(h.isForwardTriangle());
////		System.out.println(h.isBackwardTriangle());
//		
//		Edge.highlight(	h.next );
//		Edge.highlight(	h.next.next );		
//		
//		
//		ArrayList<Edge<Vertex<Integer>>> fixme = new ArrayList<>();
//		h = g.next;
//		do {
////			elif.inspector.selected.add(h);
////			elif.inspector.selected.add(h.next);
//			
//			fixme.add(h);
//			fixme.add(h.next);
//			
//			h = h.next.twin;
//		} while ( h!=g.next );
//		
//		elif.inspector.selected.add( h );
//		
//		for (Edge<Vertex<Integer>> ev: fixme) {
//			Edge.highlight(ev);
//			ev.fix();
//		}
		
		
		
		
//		e.fix();
//		e.next.twin.twin.attach(v);
//		e.next.next.twin.attachStar(v);
//		e.next.next.twin.fix();
		
//		
//		
//		
////		g.flip();
//		g.fix();
//		
//		Location l = e.next.next.twin.prev.twin.prev.twin.next.node;
//		elif.inspector.click.setLocation(l.x,l.y);
//		
//		System.out.println(g+ " vs "+g.next.twin);
		
//		g = g.next.twin;
////		g.flip();
//		System.out.println(g);
//		System.out.println(g.prev);
//		System.out.println(g.next);
//		System.out.println(g.twin);
//////		
//		elif.inspector.selected.add( g );
//		
		
//		boolean contains = e.next.next.twin.circumcircleContainsReally(l.x, l.y);
//		System.out.println(contains);
		
		
		
		
	}
}

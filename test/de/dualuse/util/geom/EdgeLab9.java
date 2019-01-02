package de.dualuse.util.geom;

public class EdgeLab9 {
	public static void main(String[] args) throws Exception {
		Edge<Vertex<Integer>> e = Graphs.circle(500, 500, 400, 5);
		Edge<Vertex<Integer>> d = e.prev, f = e.next;
		
		Vertex<Integer> v = new Vertex<Integer>(400, 400, 6);
		d.attach(v);
		e.attach(v);
		f.attach(v);
		
		Vertex<Integer> w = new Vertex<Integer>(280,550, 7);
		

		//////////
		EdgeListInspectorFrame elif = new EdgeListInspectorFrame(e);
		
		e.next.detachStar();
		
		
		elif.inspector.selected.add( e.next );
		
		
	}
}

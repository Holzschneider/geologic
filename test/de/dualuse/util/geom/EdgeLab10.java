package de.dualuse.util.geom;

public class EdgeLab10 {
	public static void main(String[] args) throws Exception {
		Edge<Vertex<Integer>> e = Graphs.circle(500, 500, 400, 7);
		Edge<Vertex<Integer>> d = e.prev, f = e.next;
		
//		Vertex<Integer> v = new Vertex<Integer>(450, 490, 6);
		
		Vertex<Integer> v = new Vertex<Integer>(900, 910, 6);
//		Vertex<Integer> v = new Vertex<Integer>(700, 1200, 6);
//		Vertex<Integer> v = new Vertex<Integer>(1200, 910, 6);
		
//		d.attach(v);
//		e.attach(v);
//		f.attach(v);
//		
//		Vertex<Integer> w = new Vertex<Integer>(280,550, 7);
		

		//////////
		EdgeListInspectorFrame elif = new EdgeListInspectorFrame(e);
		
		elif.inspector.selected.add( e.twin );
		
		e.twin.attachLoop(v);
//		e.mesh(v);
//		e.attachStar(v);
		
	}
}

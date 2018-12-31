package de.dualuse.util.geom;

public class EdgeAttachLab7 {
	public static void main(String[] args) throws Exception {
		Edge<Integer> e = Edges.circle(500, 500, 400, 5);
		Edge<Integer> d = e.prev, f = e.next;
		
		Vertex<Integer> v = new Vertex<Integer>(400, 400, 6);
		d.attach(v);
		e.attach(v);
		
		Vertex<Integer> w = new Vertex<Integer>(280,550, 7);
		

		//////////
		EdgeListInspectorFrame elif = new EdgeListInspectorFrame(e);
		
		
		
		
//		e.twin.next.next.twin.prev.prev.prev
//		e.locate(w.x, w.y).attach(w);
		
//		e.locate(w.x, w.y).mesh(w);
		
		
		////
		
		
		
		elif.inspector.createConsole()
		.publish("edge", e.twin.next.next.twin)
		.eval("inspector.onmove = function() { inspector.current = edge.locate(inspector.mouse.x, inspector.mouse.y); }")
		.loop();
		
		
	
	}
}

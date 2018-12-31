package de.dualuse.util.geom;

public class EdgeAttachLab7 {
	public static void main(String[] args) throws Exception {
		Edge<Vertex<Integer>> e = Graphs.circle(500, 500, 400, 5);
		Edge<Vertex<Integer>> d = e.prev, f = e.next;
		
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
		
		Edge<Vertex<Integer>> start = e.twin.next.next.twin;
		
		elif.inspector.click.setLocation(325, 870);
//		start.locate(325, 870);
		
//		elif.inspector.selected.add(start);
		
//		elif.inspector.createConsole()
//		.publish("edge", start)
//		.eval("inspector.onmove = function() { inspector.hud=inspector.mouse.x+\",\"+inspector.mouse.y; inspector.current = edge.locate(inspector.mouse.x, inspector.mouse.y); }")
//		.loop();
//		
		
	
	}
}

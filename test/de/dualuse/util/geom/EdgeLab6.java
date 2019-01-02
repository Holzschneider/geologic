package de.dualuse.util.geom;

public class EdgeLab6 {
	public static void main(String[] args) throws Exception {
		Edge<Vertex<Integer>> e = Graphs.circle(500, 500, 300, 5);
		Edge<Vertex<Integer>> d = e.prev, f = e.next;
		
		Vertex<Integer> v = new Vertex<Integer>(500,500, 6);
		d.attach(v);
		e.attach(v);
		f.attach(v);
		
		
		EdgeListInspectorFrame elif = new EdgeListInspectorFrame(e);
		elif.inspector.createConsole()
		.publish("edge", e.twin.next.next.twin)
		.eval("inspector.onmove = function() { inspector.current = edge.locate(inspector.mouse.x, inspector.mouse.y); }")
		.loop();
	}
}

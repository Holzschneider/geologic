package de.dualuse.util.geom;

public class EdgeAttachLab6 {
	public static void main(String[] args) throws Exception {
		Edge<Integer> e = Edges.circle(500, 500, 300, 5);
		Edge<Integer> d = e.prev, f = e.next;
		
		Vertex<Integer> v = new Vertex<Integer>(500,500, 6);
		d.attach(v);
		e.attach(v);
		f.attach(v);
		
		
		EdgeListInspectorFrame elif = new EdgeListInspectorFrame(e);
		elif.inspector.createConsole()
		.publish("edge", e.twin.next.next.twin)
		.loop();
		
	}
}

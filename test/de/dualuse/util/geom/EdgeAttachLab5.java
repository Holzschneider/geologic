package de.dualuse.util.geom;

public class EdgeAttachLab5 {
	public static void main(String[] args) throws Exception {
		
		Vertex<String> a = new Vertex<String>(100, 100, "A");
		Vertex<String> b = new Vertex<String>(500, 200, "B");
		Vertex<String> c = new Vertex<String>(200, 400, "C");
		
		Edge<String> ab = new Edge<String>("ab");
		Edge<String> bc = new Edge<String>("bc");
		Edge<String> ca = new Edge<String>("ca");
		
		((ca.next = ab).prev = ca).node = a;
		((ab.next = bc).prev = ab).node = b;
		((bc.next = ca).prev = bc).node = c;
		
		Edge<String> ba = new Edge<String>("BA");
		Edge<String> cb = new Edge<String>("CB");
		Edge<String> ac = new Edge<String>("AC");
		
		(ba.twin = ab).twin = ba;
		(cb.twin = bc).twin = cb;
		(ac.twin = ca).twin = ac;
		
		ac.next = ca.prev.twin;
		cb.next = bc.prev.twin;
		ba.next = ab.prev.twin;

		ac.prev = ca.next.twin;
		cb.prev = bc.next.twin;
		ba.prev = ab.next.twin;
		
		ac.node = c;
		cb.node = b;
		ba.node = a;
		
		//////////////

		Vertex<String> h = new Vertex<String>(100,650,"H");
		
		cb.attach( h );
		ac.attach( h );
		ba.attach( h );
		
		///
		
		ac.next.twin.next = ca;
		ca.prev = ac.next.twin;
		ab.next = cb.next;
		cb.next.prev = ab;
		
		//////////////////////
		
		EdgeListInspectorFrame elif = new EdgeListInspectorFrame(ba);
		elif.setLocation(2700, 100);
		elif.inspector.createConsole()
		.publish("ab", ab)
		.publish("bc", bc)
		.publish("ca", ca)
		.publish("ba", ba)
		.publish("cb", cb)
		.publish("ac", ac)
		.publish("h", h)
		.publish("edge", ab)
		.eval("inspector.onmove = function() { inspector.hud = edge.contains(inspector.mouse.x,inspector.mouse.y); }") 
		.loop();
		
	}
}

package de.dualuse.util.geom;

public class EdgeAttachLab5 {
	public static void main(String[] args) throws Exception {
		
		VertexLabel a = new VertexLabel(100, 100, "A");
		VertexLabel b = new VertexLabel(500, 200, "B");
		VertexLabel c = new VertexLabel(200, 400, "C");
		
		Mesh<VertexLabel> ab = new Mesh<VertexLabel>();
		Mesh<VertexLabel> bc = new Mesh<VertexLabel>();
		Mesh<VertexLabel> ca = new Mesh<VertexLabel>();
		
		((ca.next = ab).prev = ca).node = a;
		((ab.next = bc).prev = ab).node = b;
		((bc.next = ca).prev = bc).node = c;
		
		Mesh<VertexLabel> ba = new Mesh<VertexLabel>();
		Mesh<VertexLabel> cb = new Mesh<VertexLabel>();
		Mesh<VertexLabel> ac = new Mesh<VertexLabel>();
		
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

		VertexLabel h = new VertexLabel(100,650,"H");
		
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

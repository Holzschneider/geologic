package de.dualuse.util.geom;

public class EdgeLab2 {
	public static void main(String[] args) {
		
		VertexLabel a = new VertexLabel(100, 100, "A");
		VertexLabel b = new VertexLabel(500, 200, "B");
		VertexLabel c = new VertexLabel(200, 400, "C");
		
		Mesh<VertexLabel> ab = new Mesh<VertexLabel>();
		Mesh<VertexLabel> bc = new Mesh<VertexLabel>();
		Mesh<VertexLabel> ca = new Mesh<VertexLabel>();
		
//		(((ca.next = ab).prev = ca).node = a).star = ca;
//		(((ab.next = bc).prev = ab).node = b).star = ab;
//		(((bc.next = ca).prev = bc).node = c).star = bc;
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

		new EdgeListInspectorFrame(ba, EdgeLab2.class.getSimpleName());
		
//		ab.attach(250, 260, "H");
		
		VertexLabel h = new VertexLabel(250,250,"H");
		
		bc.attach( h );
		ab.attach( h );
		ca.attach( h );
		
		
	}
}

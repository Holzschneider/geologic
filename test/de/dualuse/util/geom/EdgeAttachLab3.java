package de.dualuse.util.geom;

public class EdgeAttachLab3 {
	public static void main(String[] args) {
		
		Vertex<String> a = new Vertex<String>(100, 100, "A");
		Vertex<String> b = new Vertex<String>(500, 200, "B");
		Vertex<String> c = new Vertex<String>(200, 400, "C");
		
		Edge<String> ab = new Edge<String>("ab");
		Edge<String> bc = new Edge<String>("bc");
		Edge<String> ca = new Edge<String>("ca");
		
//		(((ca.next = ab).prev = ca).node = a).star = ca;
//		(((ab.next = bc).prev = ab).node = b).star = ab;
//		(((bc.next = ca).prev = bc).node = c).star = bc;
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

		new EdgeListInspectorFrame(ba, EdgeAttachLab3.class.getSimpleName());
		
//		ab.attach(250, 260, "H");
		
		Vertex<String> h = new Vertex<String>(250,250,"H");
		
		bc.attach( h );
		ab.attach( h );
		ca.attach( h );
		
		
	}
}

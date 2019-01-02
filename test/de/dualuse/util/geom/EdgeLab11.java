package de.dualuse.util.geom;

public class EdgeLab11 {
	public static void main(String[] args) throws Exception {
		Edge<Vertex<Integer>> e = Graphs.circle(500, 500, 400, 7);
		
		
//		Vertex<Integer> v = new Vertex<Integer>(250, 610, 6);
		Vertex<Integer> v = new Vertex<Integer>(400, 530, 6);


		//////////
		EdgeListInspectorFrame elif = new EdgeListInspectorFrame(e);
		
		elif.inspector.selected.add( e);
		
		elif.inspector.click.setLocation(v.x, v.y);
//		e.attachStar(v);
		e.mesh(v.x,v.y);
//		e.attachStar(v);
		
	}
}

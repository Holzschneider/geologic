package de.dualuse.util.geom;

public class VertexLabel extends Vertex<Object> {
	private static final long serialVersionUID = 1L;

	
	public VertexLabel(double x, double y, String v) {
		super(x, y, v);
	}

	public VertexLabel(double x, double y) {
		super(x, y);
	}


	@Override
	public String toString() {
		return value.toString();
	}
	
}

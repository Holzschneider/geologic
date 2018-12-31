package de.dualuse.util.geom;

public class Graph<T extends Location> extends Edge<T> {

	@Override protected Edge<T> createEdge() { return new Graph<T>(); }

	
	/// basically edge with non-internal methods made public
}

package de.dualuse.util.geom;

public class MeshMap<T> extends Mesh<Vertex<T>> /* implements LocalMap<T> */ {

	@Override
	protected MeshMap<T> createEdge() { return new MeshMap<>(); }

	@Override
	protected MeshMap<T> self() { return this; }
	

	
	

}

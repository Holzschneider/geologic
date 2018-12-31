package de.dualuse.util.geom2;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import de.dualuse.util.geom.Location;
import de.dualuse.util.geom.Vertex;

class Edge<T extends Location, EdgeType extends Edge<T,EdgeType>> {
	protected EdgeType next, prev, twin;
	protected T node;
	
	
	
//	private UnaryOperator<Edge<T>> starward() { return e->e.twin.prev; };
//	private UnaryOperator<Edge<T>> backward() { return e->e.prev; };
//	private UnaryOperator<Edge<T>> forward() { return e->e.next; };
//	private Predicate<Edge<T>> looped() { return e->e==this; };
	native protected boolean isDetached();
	native protected boolean isUnipolar();
	native protected boolean isBipolar();
	native protected boolean isTriangle();
	native protected boolean isConvex();

	
	native public EdgeType findForward( Predicate<EdgeType> stop );
	native public EdgeType find( Predicate<EdgeType> stop, UnaryOperator<EdgeType> step );
	
	native public EdgeType walkLoop( Consumer<EdgeType> visitor );
	native public EdgeType walk(Predicate<EdgeType> stop, UnaryOperator<EdgeType> step, Consumer<EdgeType> visitor);

	native public EdgeType reduce(Predicate<EdgeType> stop, UnaryOperator<EdgeType> step, BinaryOperator<EdgeType> accumulator);
	native public EdgeType reduce(Predicate<EdgeType> stop, UnaryOperator<EdgeType> step, EdgeType identity, BinaryOperator<EdgeType> accumulator);
	
	native public<A> A reduceLoop(A accumulant, BiFunction<A,EdgeType,A> accumulator);
	native public<A> A reduce(Predicate<EdgeType> stop, UnaryOperator<EdgeType> step, A accumulant, BiFunction<A,EdgeType,A> accumulator);
	
	////////
	 
	native protected EdgeType createEdge();
	
	native public double area();	
	native public<C> C centroid(CentroidDefinition<C> c);
	public interface CentroidDefinition<C> { C defineCentroid(double x, double y); }
	native public boolean contains(final double px, final double py);
	
	native public EdgeType attach(T node);
	native public EdgeType detach(T node);
	
	native public <CollectionType extends Collection<? super T>> CollectionType collect(CollectionType collector);
}

class Graph<NodeType extends Location> extends Edge<NodeType,Graph<NodeType>> {
	
	
	
}


class Mesh<T> extends Graph<Vertex<T>> {
	
	
	
}


class DelauneyMesh<T> extends Mesh<T> {
	
}



package de.dualuse.util.geom2;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import de.dualuse.util.geom.Location;
import de.dualuse.util.geom.Vertex;

class Graph<T extends Location> {
	//evtl final public
	protected Graph<T> next, prev, twin;
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

	protected Graph<T> findForward(Predicate<Graph<T>> stop) { return find(stop, e->e.next); }
	protected Graph<T> find( Predicate<Graph<T>> stop, UnaryOperator<Graph<T>> step ) {
		for( Graph<T> cursor = this;;cursor = step.apply(cursor) )
			if (stop.test(cursor))
				return cursor;
	}

	
	native protected Graph<T> walkLoop( Consumer<Graph<T>> visitor );
	native protected Graph<T> walk(Predicate<Graph<T>> stop, UnaryOperator<Graph<T>> step, Consumer<Graph<T>> visitor);

	native protected Graph<T> reduce(Predicate<Graph<T>> stop, UnaryOperator<Graph<T>> step, BinaryOperator<Graph<T>> accumulator);
	native protected Graph<T> reduce(Predicate<Graph<T>> stop, UnaryOperator<Graph<T>> step, Graph<T> identity, BinaryOperator<Graph<T>> accumulator);
	
	native protected<A> A reduceLoop(A accumulant, BiFunction<A,Graph<T>,A> accumulator);
	native protected<A> A reduce(Predicate<Graph<T>> stop, UnaryOperator<Graph<T>> step, A accumulant, BiFunction<A,Graph<T>,A> accumulator);
	
	////////
	 
	native protected Graph<T> createEdge();
	
	native public double area();	
	native public<C> C centroid(CentroidDefinition<C> c);
	public interface CentroidDefinition<C> { C defineCentroid(double x, double y); }
	native public boolean contains(final double px, final double py);
	
	native public Graph<T> attach(T node);
	native public Graph<T> detach(T node);
	
	native public <CollectionType extends Collection<? super T>> CollectionType collect(CollectionType collector);
	//other collect methods mit radius, oder steps
	//public walker mit node predicates
}

class Mesh<T extends Location> extends Graph<T> {
	
	// returner methods mit upcasting und mesh only adds
	native public Mesh<T> attach(T node);
	native public Mesh<T> detach(T node);
}

class MeshMap<T> extends Mesh<Vertex<T>> /*implements LocalMap<T>*/ {
	//same aber mit LocalMap
}

class DelaunayMap<T> extends MeshMap<T> {
	//und edge flipping
}


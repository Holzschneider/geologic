package de.dualuse.util.geom;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Graphs {
	
	static public<T> Mesh<Vertex<T>> circle(double cx, double cy, double r, T[] elements) {
		return circle(cx,cy,r, elements.length, i->elements[i]);
	}

	
	static public Mesh<Vertex<Integer>> circle(double cx, double cy, double r, int n) {
		return circle(cx,cy,r, n, Integer::valueOf );
	}

	static public<T> Mesh<Vertex<T>> circle(double cx, double cy, double r, int n, IntFunction<T> elements) {
		return loop( i-> v -> v.define(-sin(2*i*PI/n)*r+cx,cos(2*i*PI/n)*r+cy), n, elements);
	}

	
	interface VertexSource<T> { Vertex<T> define(double x, double y); } 
	interface VertexProducer<T> { Vertex<T> get(VertexSource<T> cs); }
	interface VertexProvider<T> { VertexProducer<T> coordiantes(int n); }
	static public<T> Mesh<Vertex<T>> loop(VertexProvider<T> c, int n, IntFunction<T> elements ) {
		List<Vertex<T>> vertices = IntStream.range(0, n)
				.mapToObj( i -> c.coordiantes(i).get( Vertex<T>::new ).value( elements.apply(i) ) )
				.collect(Collectors.toList());

		List<Mesh<Vertex<T>>> edges = IntStream.range(0, n)
				.mapToObj( i -> new Mesh<Vertex<T>>(vertices.get(i)) )
				.collect(Collectors.toList());
		
		List<Mesh<Vertex<T>>> twins = IntStream.range(0, n)
				.mapToObj( i -> new Mesh<Vertex<T>>(vertices.get((i+n-1)%n)) )
				.collect(Collectors.toList());
		
		IntStream.range(0, n).forEach( i-> edges.get(i).next = edges.get( (i+1)%n ) );
		IntStream.range(0, n).forEach( i-> edges.get(i).prev = edges.get( (i+n-1)%n ) );
		IntStream.range(0, n).forEach( i-> edges.get(i).twin = twins.get( i ) );
		IntStream.range(0, n).forEach( i-> twins.get(i).next = edges.get( i ).prev.twin );
		IntStream.range(0, n).forEach( i-> twins.get(i).prev = edges.get( i ).next.twin );
		IntStream.range(0, n).forEach( i-> twins.get(i).twin = edges.get( i ) );
		
		return edges.get(0);
	}
	
		
	public static void main(String[] args) {
		
		Edge<Vertex<Integer>> e = circle(500, 500, 300, 5);
		Edge<Vertex<Integer>> d = e.prev, f = e.next;
		
		Vertex<Integer> v = new Vertex<Integer>(500,500, 6);
		d.attach(v);
		e.attach(v);
		f.attach(v);
		
		new EdgeListInspectorFrame( e )
		.inspector
		.current = e;
		
	}
	
}

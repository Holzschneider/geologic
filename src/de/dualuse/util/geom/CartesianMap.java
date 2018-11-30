package de.dualuse.util.geom;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;



public interface CartesianMap<T> extends Map<Location, T> {
	
	default public T put(double x, double y, T value) {
		return this.put(Location.of(x, y), value);
	}
	
	default public T putIfAbsent(double x, double y, T value) {
		return Map.super.putIfAbsent(Location.of(x,y), value);
	}
	
	default public T get(double x, double y) {
		return get(Location.of(x, y));
	}
	
	///////////////
	
	default public Collection<T> collect(Region v) { return collect(v, new LinkedList<>()); }
	public Collection<T> collect(Region a, Collection<T> c);
	
	public CartesianMap<T> visit(Region a, Consumer<T> c);
	public CartesianMap<T> visit(Region a, BiConsumer<Location,T> c);
	
	public CartesianMap<T> find(Location l, Consumer<T> c); 
	public CartesianMap<T> find(Location l, BiConsumer<Location,T> c); 
	public T find(Location l);
	

}


interface CursorMap<T> extends CartesianMap<T> {


	public CartesianMap<T> approach(Location l);
	public CartesianMap<T> approach(Region l);
}






//quadtree, delauney, gridhash(2d)  (boxhash (3d), (octree))




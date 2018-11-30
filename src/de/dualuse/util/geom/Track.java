package de.dualuse.util.geom;

import java.util.NavigableMap;


public interface Track<T> extends NavigableMap<Double,T> {
	public static enum Wrap {
		NONE,
		CLAMP,
		MIRROR,
		WRAP;
	}

	
	public static interface BezierInterpolator<V> extends LinearInterpolator<V> {
		@Override
		default V interpolate(double key, Track<V> map) {
			double floorKey = map.floorKey(key);
			double lowerKey = map.lowerKey(floorKey);
			
			double higherKey = map.higherKey(key);
			double higherHigherKey = map.higherKey(higherKey);
			
			double ratio = (key-floorKey)/(higherKey-floorKey);
			
			V lowerValue = map.get(lowerKey);
			V floorValue = map.get(floorKey);
			V higherValue = map.get(higherKey);
			V higherHigherValue = map.get(higherHigherKey);
			
			return mix(ratio, mix(ratio , lowerValue, floorValue ), mix(ratio, higherValue, higherHigherValue));
		}
	}
	
	public static interface LinearInterpolator<V> extends Interpolator<V> {
		public V mix(double t, V from, V to);
		
		@Override
		default V interpolate(double key, Track<V> map) {
			double floorKey = map.floorKey(key);
			double higherKey = map.higherKey(key);
			
			return mix( (key-floorKey)/(higherKey-floorKey), map.get( floorKey ), map.get( higherKey ) );
		}
	}
	
	public static interface Interpolator<V> {
		public V interpolate(double key, Track<V> map);
	}
	
	public Interpolator<T> interpolator();
	
	default public double floorKey( double key ) { return floorKey((Double)key); }
	default public double ceilKey( double key ) { return ceilKey((Double)key); }
	default public double lowerKey( double key ) { return lowerKey((Double)key); }
	default public double higherKey( double key ) { return higherKey((Double)key); }
	
	
	default public T put(double key, T value) { return put((Double)key, value); }
	default public T get(double key) { return get((Double)key); };
	
	public T get(Double key);
	
	
	
}
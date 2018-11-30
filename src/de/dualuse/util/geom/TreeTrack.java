package de.dualuse.util.geom;

import java.util.Comparator;
import java.util.TreeMap;

//public class TreeTrack<T> extends TreeMap<Double, T> implements Track<T> {
//	
//	private static final long serialVersionUID = 1L;
//	
//	private final Interpolator<T> interpolator;
//	private final Wrap wrap;
//	
//	public TreeTrack(Comparator<Double> keyComparator, Interpolator<T> valueInterpolator, Wrap keyWrap) {
//		super(keyComparator);
//		this.interpolator = valueInterpolator;
//		this.wrap = keyWrap;
//		
//	}
//	
//	public TreeTrack(Comparator<Double> keyComparator, Interpolator<T> valueInterpolator) {
//		this(keyComparator, valueInterpolator, Wrap.NONE);
//	}
//	
//	public TreeTrack(Interpolator<T> valueInterpolator, Wrap keyWrap) {
//		super();
//		this.interpolator = valueInterpolator;
//		this.wrap = keyWrap;
//	}
//	
//	public TreeTrack(Interpolator<T> valueInterpolator) {
//		this(valueInterpolator, Wrap.NONE);
//	}
//	
//	public TreeTrack() {
//		super();
//		this.interpolator = null; //key -> get(floorKey(key));
//		this.wrap = Wrap.NONE;
//	}
//	
//
//	@Override
//	public T get(Double key) {
//		if (key==null) throw new IllegalArgumentException();
//		
//		//XXX apply wrap here
//		
//		double floorKey = this.floorKey(key);
//		double higherKey = this.higherKey(key);
//
//		double lowerFloorKey = this.lowerKey(floorKey);
//		double higherHigherKey = this.higherKey( higherKey );
//
//		
//		return interpolator.interpolate(key, 
//				lowerFloorKey, get(lowerFloorKey),
//				floorKey, get(floorKey),
//				higherKey, get(higherKey),
//				higherHigherKey, get(higherHigherKey)
//			);
//	}
//	
//}
//
//

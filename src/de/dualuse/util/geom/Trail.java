package de.dualuse.util.geom;

import java.util.Collection;
import java.util.function.Consumer;

public interface Trail<T> extends Track<T> {
	
	static public interface Odometer<T> {
		double pathLength(double leftKey, T leftValue, double rightKey, T rightValue);
	}
	
	
	public double walk(double distance);
	public void walk(double distance, double step, Consumer<T> vistor);
	
	public<C extends Collection<? super T>> C walk(double distance, double step, Collection<T> collector); 
	
}

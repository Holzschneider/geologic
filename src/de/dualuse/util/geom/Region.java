package de.dualuse.util.geom;

interface Region {
			
	boolean intersects( double ax, double ay, double bx, double by, double cx, double cy );
	
//	final static public double I = 1/0d; 
//	default boolean isEmpty() {
//		return !intersects(-I, -I, I, -I, I, I) && !intersects(-I, -I, -I, I, I, I);
//	}
}
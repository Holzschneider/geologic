package de.dualuse.util.geom;

import java.util.Comparator;

public class Hull extends Loop {
	
	
	public Hull(Location... l) {
	}

	@Override
	native public boolean intersects(double ax, double ay, double bx, double by, double cx, double cy);
	
	
	/*
	public final static Comparator<Location> HORIZONTAL_ORDER = new Comparator<Location>() {
		private int identity(Object o) { return System.identityHashCode(o); }
		private int compare(int a, int b) { return a<b?-1:(a>b?1:0); };
		
		public int compare(Location a, Location b) {
			return a.x<b.x?-1:(a.x>b.x?1:(a.y<b.y?-1:(a.y>b.y?1:compare(identity(a),identity(b)))));
		}
	};
	
	
	public native static int hull(Location[] sequence, int offset, int length);
//	{
//		Arrays.sort(sequence, offset, offset+length, HORIZONTAL_ORDER); 
//		
//		
//		return length;
//	}
	
	 */
}
package de.dualuse.util.geom;

public class Location {
	public final double x, y;
	
	static public Location of(double x, double y) { 
		return new Location(x,y); 
	};
	
	public Location(double x, double y) { 
		this.x = x; 
		this.y = y; 
	}
}
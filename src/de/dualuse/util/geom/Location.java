package de.dualuse.util.geom;

import static java.lang.Math.sqrt;

/* probably change to Coordinates */
public class Location {
	public final double x, y;
	
	static public Location of(double x, double y) { 
		return new Location(x,y); 
	};
	
	public Location(double x, double y) { 
		this.x = x; 
		this.y = y; 
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		else if (obj instanceof Location)
			return this.equals((Location)obj);
			else return false;
	}
	
	public boolean equals(Location that) {
		return this.x == that.x && this.y == that.y;
	}
	
	@Override
	public int hashCode() {
		return Double.hashCode(this.x)^Double.hashCode(this.y);
	}

	@Override
	public String toString() {
		return "Location( "+x+", "+y+" )";
	}
	
	////////////////
	
	
	public double distance(double x, double y) {
		return sqrt(quadrance(x,y));
	}
	
	public double quadrance(double x, double y) {
		double dx = this.x-x, dy = this.y-y;
		return dx*dx+dy*dy;	
	}
	
	interface LocationComparable {
		boolean than(Location that);
	}
	
	public LocationComparable isCloserTo(double x, double y) {
		return that -> this.quadrance(x,y)<that.quadrance(x, y);
	}
	
}



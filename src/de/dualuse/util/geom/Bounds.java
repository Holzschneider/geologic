package de.dualuse.util.geom;

class Bounds implements Region {
	public final Range x, y;
	
	static
	public Bounds of(Range x, Range y) {
		return new Bounds(x,y);
	}

	public Bounds(Range x, Range y) {
		this.x = x;
		this.y = y;
	}
	
	public Bounds extend(double x, double y) {
		return Bounds.of( this.x.extend(x), this.y.extend(y) );
	}
	
	public Bounds intersect(Bounds that) {
		return Bounds.of( this.x.intersect(that.x), this.y.intersect(that.y) );
	}
	
	public boolean isEmpty() {
		return x.isEmpty() || y.isEmpty();
	}
	
	public boolean intersects(Location l) {
		return this.intersects(l.x, l.y);
	}
	
	public boolean intersects(double x, double y) {
		return this.x.min<=x && x<this.x.max && this.y.min<=y && y<this.y.max;
	}
	
	@Override
	native public boolean intersects(double ax, double ay, double bx, double by, double cx, double cy);
}
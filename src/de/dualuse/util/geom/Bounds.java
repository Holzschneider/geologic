package de.dualuse.util.geom;

class Bounds implements Region {
	public final Range x, y;
	
	static
	public Bounds of(Range x, Range y) {
		return new Bounds(x,y);
	}

	public Bounds(double x, double y) {
		this.x = Range.of(x, x);
		this.y = Range.of(y, y);
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

	@Override
	native public boolean intersects(double ax, double ay, double bx, double by, double cx, double cy);
}
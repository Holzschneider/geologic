package de.dualuse.util.geom;

class BoundingBox implements Region {
	public final Range x, y;
	
	static
	public BoundingBox of(Range x, Range y) {
		return new BoundingBox(x,y);
	}

//	public Bounds(double x, double y) {
//		this.x = Range.of(x, x);
//		this.y = Range.of(y, y);
//	}
	
	public BoundingBox(Range x, Range y) {
		this.x = x;
		this.y = y;
	}
	
	public BoundingBox extend(double x, double y) {
		return BoundingBox.of( this.x.extend(x), this.y.extend(y) );
	}
	
	public BoundingBox intersect(BoundingBox that) {
		return BoundingBox.of( this.x.intersect(that.x), this.y.intersect(that.y) );
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
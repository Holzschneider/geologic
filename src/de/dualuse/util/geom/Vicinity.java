package de.dualuse.util.geom;

class Vicinity extends Location implements Region {
	final double r;
	
	static 
	public Vicinity of(double x, double y, double r) {
		return new Vicinity(x, y, r);
	}
	
	public Vicinity(double x, double y, double r) {
		super(x, y);
		this.r = r;
	}

	@Override
	native public boolean intersects(double ax, double ay, double bx, double by, double cx, double cy);
}
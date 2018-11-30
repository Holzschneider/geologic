package de.dualuse.util.geom;

class Loop implements Region {
	private Location[] corners;
	
	public Loop(Location... corners) {
		this.corners = corners.clone();
	}
	
	
	@Override
	native public boolean intersects(double ax, double ay, double bx, double by, double cx, double cy);
	
}
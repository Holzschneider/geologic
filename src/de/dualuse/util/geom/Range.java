package de.dualuse.util.geom;

class Range {
	static 
	public final Range EMPTY = Range.of(+1/0d, -1/0d);
	
	public final double min, max;
	
	static
	public Range of(double min, double max) { 
		return new Range(min,max);
	}
	
	public Range(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	public Range extend(double value) {
		return Range.of( min(value,this.min), max(value, this.max) );
	}
	
	public Range intersect(Range that) {
		return Range.of( max(this.min,that.min), min(this.max,that.max) );
	}
	
	private static double min(double a, double b) {
		return a<b?a:b;
	}
	
	private static double max(double a, double b) {
		return a>b?a:b;
	}
	
	public boolean isEmpty() { return this.min>this.max; }
}
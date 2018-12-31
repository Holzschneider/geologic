package de.dualuse.util.geom;

interface LocalMap<T> extends CartesianMap<T> {

	//evtl locate
	public LocalMap<T> locate(Location l);
	public LocalMap<T> locate(Region l);
	
	
	
//  public LocalMap<T> subMap(Region l);

	
}
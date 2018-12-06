package de.dualuse.util.geom;

interface LocalMap<T> extends CartesianMap<T> {

	//evtl locate
	public LocalMap<T> approach(Location l);
	public LocalMap<T> approach(Region l);
	
//	public LocalMap<T> root();
	
}
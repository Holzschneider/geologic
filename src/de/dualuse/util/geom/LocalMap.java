package de.dualuse.util.geom;

import java.util.SortedMap;

interface LocalMap<T> extends CartesianMap<T> {

	//evtl locate
	public LocalMap<T> approach(Location l);
	public LocalMap<T> approach(Region l);
	
	
	
//  public LocalMap<T> subMap(Region l);

	
}
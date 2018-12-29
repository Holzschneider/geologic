package de.dualuse.util.geom;

import java.io.Serializable;

/**
 * Interner Containerknoten. Speichert Koordinaten-Schlüssel und 
 * Wert als direktreferenzen. Implementiert das Knoteninterface Vertex
 * welches alle Zugriffsfunktionen spezifiziert.
 */
class Vertex<T> extends Location implements Serializable {
	private static final long serialVersionUID = 1L;

	T value;
//	Edge<T> star;
	
	public Vertex(double x, double y, T v) {
		super(x,y);
		
		this.value = v; 
	}

	@Override
	public String toString() {
		return "Vertex( "+value+" @ "+x+", "+y+" )";
	}

}
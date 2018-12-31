package de.dualuse.util.geom;

import java.io.Serializable;

/**
 * Interner Containerknoten. Speichert Koordinaten-Schlüssel und 
 * Wert als direktreferenzen. Implementiert das Knoteninterface Vertex
 * welches alle Zugriffsfunktionen spezifiziert.
 */
public class Vertex<T> extends Location implements Serializable {
	private static final long serialVersionUID = 1L;

	public T value;
//	Edge<T> star;
	
	public Vertex(double x, double y) {
		super(x,y);
	}
	
	public Vertex(double x, double y, T v) {
		this(x,y);
		
		this.value = v; 
	}

	@Override
	public String toString() {
//		return "Vertex( "+value+" @ "+x+", "+y+" )";
		return value.toString();
	}

	
	public T value() { return value; }
	public Vertex<T> value(T value) { this.value = value; return this; }
}
package de.dualuse.util.geom;

import static java.lang.Math.max;

import java.util.Arrays;
import java.util.HashSet;

import javax.swing.JFrame;

public class EdgeListInspectorFrame extends JFrame {
	private static final long serialVersionUID = 1L;


	final public EdgeListInspector inspector;
	
	public EdgeListInspectorFrame(Edge<?> e) {
		this(e, Arrays
				.stream(new Throwable().getStackTrace())
				.filter( ste->!ste.isNativeMethod())
				.filter( ste->ste.getLineNumber()>0)
				.reduce((first,second)->second)
				.map(ste->ste.getClassName().replaceAll("^.*\\.", ""))
				.orElse("Inspector") );
	}

	public EdgeListInspectorFrame(Edge<?> e, String title) {
		super(title);

		int SIZE = 800;
		Bounds b = Bounds.EMPTY;
		for (Vertex<?> v: e.collectVertices(new HashSet<Vertex<?>>()))
			b = b.extend(v.x, v.y);
		
		b = b.grow(100);
		
		double scale = SIZE/max(b.x.spread(),b.y.spread());
		
		inspector = new EdgeListInspector(e);
		inspector.canvasTransform.scale(scale, scale);
		inspector.canvasTransform.translate(-b.x.min, -b.y.min);

		this.setContentPane(inspector);
		this.setBounds(100, 100, SIZE, SIZE);
		this.setVisible(true);
	}
	
}
package de.dualuse.util.geom;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

public class EdgeListInspectorFrame extends JFrame {
	private static final int DEFAULT_SIZE = 800;
	private static final String HEIGHT = "height";
	private static final String WIDTH = "width";
	private static final String X = "x";
	private static final String Y = "y";
	
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

		int x = properties.getInt(X, 100);
		int y = properties.getInt(Y, 100);
		
		int width = properties.getInt(WIDTH, DEFAULT_SIZE);
		int height = properties.getInt(HEIGHT, DEFAULT_SIZE);
		
		int SIZE = min(width,height);
		Bounds b = Bounds.EMPTY;
		for (Vertex<?> v: e.collect(new HashSet<Vertex<?>>()))
			b = b.extend(v.x, v.y);
		
		
		b = b.grow(100);
		
		double scale = SIZE/max(b.x.spread(),b.y.spread());
		
		inspector = new EdgeListInspector(e);
		inspector.canvasTransform.scale(scale, scale);
		inspector.canvasTransform.translate(-b.x.min, -b.y.min);

		this.setContentPane(inspector);
		this.setBounds(x,y, width, height );
		
		this.setVisible(true);
		
		addComponentListener(moveListener);
	}
	
	private Preferences properties = Preferences
			.userNodeForPackage(EdgeListInspectorFrame.class)
			.node(EdgeListInspectorFrame.class.getSimpleName());
	
	private void memorizeBounds() {
		properties.putInt(X, getX());
		properties.putInt(Y, getY());
		properties.putInt(WIDTH, getWidth());
		properties.putInt(HEIGHT, getHeight());
	};
	
	ComponentListener moveListener = new ComponentAdapter() {
		public void componentResized(ComponentEvent e) { memorizeBounds(); }
		public void componentMoved(ComponentEvent e) { memorizeBounds(); }
	};
}











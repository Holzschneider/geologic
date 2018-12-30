package de.dualuse.util.geom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.util.stream.IntStream;

import org.junit.Test;

public class EdgeTest {
	
	@Test
	public void findTest() {
		
		int n = 4,k =3;
		Edge<Integer> e = Edges.circle(100, 100, 100, n);
		
		e = e.find( 
				f->f.node.value==k, 
				f->f.next 
			);
		
		assertTrue( e.node.value == k);

	}
	
	
	@Test
	public void findTest2() {
		int n = 10, m = n;
		int r = 100, cx = r, cy = r;
		
		IntStream.range(0, m)
		.forEach(
				k -> IntStream.range(0, n).forEach( 
						i -> assertTrue( Edges.circle(cx, cy, r, m).find( f->f.node.value==i, f->f.next ) .node.value == i ) 
				) 
		);
		
	}

	@Test
	public void centroidTest() {
				
		assertEquals(Edges.circle(100, 100, 100, 4).centroid( Point2D.Double::new ), new Point2D.Double(100,100));
	}
}

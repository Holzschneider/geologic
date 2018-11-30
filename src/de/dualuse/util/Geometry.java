package de.dualuse.util;


public class Geometry {

	public static double direction(final double dx, final double dy) {
		return Math.atan2(dy, dx);
	}
	
	public static double angle(double a, double b) {
		assert -Math.PI <= a && a <= Math.PI;
		assert -Math.PI <= b && b <= Math.PI;

		double delta = b - a;

		if (delta > Math.PI) {
			return -2.0 * Math.PI + delta;
		} else if (delta <= -Math.PI) {
			return 2.0 * Math.PI + delta;
		} else {
			return delta;
		}
	}
	
	public static double angle(final double ax, final double ay, final double bx, final double by) {
		final double al = Math.sqrt(ax*ax+ay*ay)*1.00000000001, bl = Math.sqrt(bx*bx+by*by)*1.00000000001, albl = al*bl;
		final double dot = ax*bx/albl+ay*by/albl;
		return Math.acos(dot);
	}
	
	/**
	 * substracts two rotation angles so that the absolute result is always < Math.PI
	 * @param a first rotation angle
	 * @param b second rotation angle
	 * @return r return value is a-b but abs(r) < Math.PI (smaller than 180 degree)
	 */		
	public static double rotationSub(double a, double b) {
		//TODO: This only works for angles with less than 540 degree apart... no time to think now
		double d = a-b;
		if (d > Math.PI)  return d - Math.PI*2;
		if (d < -Math.PI) return d + Math.PI*2;
		return d;
	}
	
	/**
	 * Flips the angle a so that abs(a-b) < Math.PI
	 * @param a angle to flip
	 * @param b angle to flip to
	 * @return returns flipped a so that abs(a-b) < Math.PI
	 */
	public static double rotationFlip(double a, double b) {
		//TODO: This only works for angles with less than 540 degree apart... no time to think now
		if (a-b < -Math.PI) return a + Math.PI*2;
		if (a-b > +Math.PI) return a - Math.PI*2;
		return a;
	}
	
	/**
	 * Flips the angle a so that abs(a-b) < Math.PI
	 * @param a angle to flip
	 * @param b angle to flip to
	 * @return returns flipped a so that abs(a-b) < Math.PI
	 */
	public static float rotationFlip(float a, float b) {
		//TODO: This only works for angles with less than 540 degree apart... no time to think now
		if (a-b < -Math.PI) return a + (float)Math.PI*2;
		if (a-b > +Math.PI) return a - (float)Math.PI*2;
		return a;
	}


	/**
	 * returns the value v clamped inside the interval a,b. a is supposed to be lower than b
	 * @param value value to clamp
	 * @param lowerLimit 
	 * @param upperLimit
	 * @return value clamped
	 */
	public static int clamp(int value, int lowerLimit, int upperLimit) { 
		if (value < lowerLimit) return lowerLimit; 
		if (value > upperLimit) return upperLimit; 
		return value; 
	}
	

	/**
	 * returns the value v clamped inside the interval a,b. a is supposed to be lower than b
	 * @param value value to clamp
	 * @param lowerLimit 
	 * @param upperLimit
	 * @return value clamped
	 */
	public static float clamp(float value, float lowerLimit, float upperLimit) { 
		if (value < lowerLimit) return lowerLimit; 
		if (value > upperLimit) return upperLimit; 
		return value; 
	}
	
	/**
	 * returns the value v clamped inside the interval a,b. a is supposed to be lower than b
	 * @param value value to clamp
	 * @param lowerLimit 
	 * @param upperLimit
	 * @return value clamped
	 */
	public static double clamp(double value, double lowerLimit, double upperLimit) { 
		if (value < lowerLimit) return lowerLimit; 
		if (value > upperLimit) return upperLimit; 
		return value; 
	}


	public static double smoothStep(double a, double b, double t) {
		
		double c = Math.max(0,(t-a)/(b-a));
		
		if (c>0.5) {
			double x = (c-0.5)*3;
			double xx = x*x;
			
			return (1.+(x/(1+Math.sqrt(xx))))/2.;
		} else
			return (Math.sin(c*Math.PI-Math.PI/2.)+1)/2.;
		
	}
	
	
	
	/**
	 * Rechnet ueber die Hessesche Normalform die Distanz zwischen der Punkt q
	 * zu der Ebene mit dem Stuetzvektor p und den Richtungs-Vektoren a und b 
	 * aus.
	 * 
	 * @return true, wenn Abstand positiv ist = entlang der Normalen
	 */
	public static boolean pointAbovePlane(
			double px, double py, double pz, 
			double ax, double ay, double az, 
			double bx, double by, double bz, 
			double qx, double qy, double qz) {
		
		//normale errechnen
		double nx = ay*bz-az*by, ny = az*bx-ax*bz, nz = ax*by-ay*bx, nl = Math.sqrt(nx*nx+ny*ny+nz*nz);
		nx/=nl;ny/=nl;nz/=nl;
		
		if (nz<0) { //sicherstellen, dass Normale nach "oben" zeigt (nz>0)
			nx =-nx;
			ny =-ny;
			nz =-nz;
		}
		//normalisieren
		
		//q von stuetzvektor p subtrahieren
		double qpx = px-qx, qpy = py-qy, qpz = pz-qz;
		
		//mit der normalen skalarmultiplizieren
		double d = nx*qpx+ny*qpy+nz*qpz;
		
		//drueber, wenn Abstand positiv
		return d>EPSILON;
	}
	
	static final double EPSILON = 0.00001;
	
	

	/**
	 * p innerhalb Umkreistest unter Anwendung des Orientierungstests fuer 
	 * auf einen 3d-paraboloid "geliftete" Punkte a,b,c,p
	 */
	public static boolean insideCircumcircle(
			final double px, final double py, 
			final double ax, final double ay, 
			final double bx, final double by, 
			final double cx, final double cy) 
	{
		
		//Lifting-Komponenten der Punkte berechnen
		final double az = ax*ax+ay*ay;
		final double bz = bx*bx+by*by;
		final double cz = cx*cx+cy*cy;
		final double pz = px*px+py*py;

		//Orientierungstest in 3D liefert Umkreisantwort  
		return pointAbovePlane(
				ax, ay, az, 
				bx-ax, by-ay, bz-az,
				cx-ax, cy-ay, cz-az,
				px, py, pz);
	}
	
	
	/**
	 * @return true if triangle interior a,b,c intersects point p
	 */
	static public boolean triangleContains(double px, double py, double ax, double ay, double bx, double by, double cx, double cy) {
		final double v0x = cx-ax, v0y = cy-ay;
		final double v1x = bx-ax, v1y = by-ay;
		final double v2x = px-ax, v2y = py-ay;
		
		final double dot00 = v0x*v0x+v0y*v0y;
		final double dot01 = v0x*v1x+v0y*v1y;
		final double dot02 = v0x*v2x+v0y*v2y;
		final double dot11 = v1x*v1x+v1y*v1y;
		final double dot12 = v1x*v2x+v1y*v2y;
		
		final double invDenom = 1 / (dot00*dot11-dot01*dot01);
		final double u = (dot11*dot02-dot01*dot12)*invDenom;
		final double v = (dot00*dot12-dot01*dot02)*invDenom;
		
		return (u>0) && (v>0) && (u+v<1);
	}
	
	/**
	 * @return true if triangle a,b,c intersects point p
	 */
	static public boolean triangleIntersects(double px, double py, double ax, double ay, double bx, double by, double cx, double cy) {
		final double v0x = cx-ax, v0y = cy-ay;
		final double v1x = bx-ax, v1y = by-ay;
		final double v2x = px-ax, v2y = py-ay;
		
		final double dot00 = v0x*v0x+v0y*v0y;
		final double dot01 = v0x*v1x+v0y*v1y;
		final double dot02 = v0x*v2x+v0y*v2y;
		final double dot11 = v1x*v1x+v1y*v1y;
		final double dot12 = v1x*v2x+v1y*v2y;
		
		final double invDenom = 1 / (dot00*dot11-dot01*dot01);
		final double u = (dot11*dot02-dot01*dot12)*invDenom;
		final double v = (dot00*dot12-dot01*dot02)*invDenom;
		
		return (u>=0) && (v>=0) && (u+v<=1);
	}
	
//	/**
//	 * @return true, if the line segment between a,b intersects the line segment between p,q 
//	 */
//	static public boolean lineSegmentsIntersect(double ax, double ay, double bx,double by, double px, double py, double qx, double qy) {
//		return Line2D.linesIntersect(ax, ay, bx, by, px, py, qx, qy);
//	}
	
	
	static public boolean rectangleIntersectsLine(double rx, double ry, double rwidth, double rheight, double px, double py, double qx, double qy) {
		int o = 0;
		o += leftOf(rx, ry, px, py, qx, qy)?1:-1;
		o += leftOf(rx+rwidth, ry, px, py, qx, qy)?1:-1;
		o += leftOf(rx+rwidth, ry+rheight, px, py, qx, qy)?1:-1;
		o += leftOf(rx, ry+rheight, px, py, qx, qy)?1:-1;
		
		switch(o) {
		case -4:
		case 4: return false;
		default: return true;
		}
	}
	
	
	
	static public double pointLineQuadrance( final double ox, final double oy, final double px, final double py, final double dx, final double dy) {
		final double qx_ = ox-px, qy_ = oy-py;
		final double oodlsq = 1/(dx*dx+dy*dy);
		final double ax = qx_-(dx*dx*qx_+dx*dy*qy_)*oodlsq, ay = qy_-(dy*dx*qx_+dy*dy*qy_)*oodlsq;
		return ax*ax+ay*ay;
	}
	
	/**
     * returns t such that two given straight functions (point-direction form)
     * p + a.t in { p | p(u) = q + b.u, u in R }
     *
     * @return t or NAN for parallel or colinear lines
     */
    static public double lineIntersection( final double px, final double py, final double ax, final double ay,
                                  final double qx, final double qy, final double bx, final double by)
    {
        return Math.abs(bx)>Math.abs(by)
                    ?
            ( py-qy - (by/bx)*(px-qx)  ) / ( (by/bx)*ax  - ay )
                    :
            ( px-qx - (bx/by)*(py-qy)  ) / ( (bx/by)*ay  - ax );
    };
    

    
    static public double lineIntersection( 
	    		final double px, final double py, final double pz, final double nx, final double ny, final double nz,
	    		final double ax, final double ay, final double az, final double dx, final double dy, final double dz
    		)
    {
    	final double d = -(px*nx+py*ny+pz*nz);
    	
    	double t = (-d-(ax*nx+ay*ny+az*nz)) / (dx*nx+dy*ny + dz*nz);  
    	
    	return t;
    }
    
    
    /**
     * 
     * @return true, if p is truly left of a,b
     */
    static public boolean leftOf( final double px, final double py, final double ax, final double ay, final double bx, final double by ) {
    	return orientation(px,py,ax,ay,bx,by)<0;
    }

    /**
     * 
     * @return true, if p is truly right of a,b
     */
    static public boolean rightOf( final double px, final double py, final double ax, final double ay, final double bx, final double by ) {
    	return orientation(px,py,ax,ay,bx,by)>0;
    }


    static public double orientation( final double px, final double py, final double ax, final double ay, final double bx, final double by ) {
    	double cax = ax-px, cay = ay-py;
    	double cbx = bx-px, cby = by-py;
    	
    	return (cax*cby-cay*cbx);
    }
    
    
    static public float fastSqrt(float number) {
        int i;
        float x, y;
        final float f = 1.5F;

        x = number * 0.5F;
        y  = number;
        i = Float.floatToIntBits(y);
        
        i  = 0x5f3759df - ( i >> 1 );
        y = Float.intBitsToFloat(i);
        
        y  = y * ( f - ( x * y * y ) );
        y  = y * ( f - ( x * y * y ) );
        return number * y;
    }
    
    public static float invSqrt(float x)
    {
      float xhalf = 0.5f*x;
      int i = Float.floatToRawIntBits(x); // get bits for floating value
      i = 0x5f375a86- (i>>1); // gives initial guess y0
      x = Float.intBitsToFloat(i); // convert bits back to float
      x = x*(1.5f-xhalf*x*x); // Newton step, repeating increases accuracy
      return x;
    }
    
}

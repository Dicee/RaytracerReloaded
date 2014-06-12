package objects;

import utils.math.CustomMath;
import static utils.math.CustomMath.compare;
import utils.math.Point;
import utils.math.Vector3D;


public class QuadFace extends Face {
	public QuadFace(Point a, Point b, Point c, Texture texture)	{
		super(a,b,c,texture);
	}	
	
	public QuadFace(Point center, double height, double width, double alpha, Texture texture) {
		super(center.z,texture);
		
		if (compare(height,0) == 0 || compare(width,0) == 0 || alpha < 0)
			throw new IllegalArgumentException();
		
		Vector3D[] canonicBase = CustomMath.canonicBase;
		Vector3D   ex          = canonicBase[0].scale(Math.cos(alpha)).sum(
									canonicBase[2].scale(Math.sin(alpha))).scale(width/2);
		Vector3D   ez          = canonicBase[2].scale(height/2);
		
		a                      = center.translate(ex.opposed()).translate(ez.opposed());
		b                      = center.translate(ex)          .translate(ez.opposed());
		c                      = center.translate(ex)          .translate(ez)          ;
	}

	@Override
	public Point intersection(Point p, Vector3D incidentRay) {
		Point q = super.intersection(p,incidentRay);		
		if (q != null && !belongs(q))
			return null;		
		return q;
	}
	
	@Override
	public boolean belongs(Point P)	{
		Vector3D  u = new Vector3D(a,b).scale(1/a.distance(b));
		Vector3D  v = new Vector3D(a,c).scale(1/a.distance(c));
		Vector3D nu = Vector3D.linearCombination(v,u,1,- v.dot(u));
		nu          = nu.scale(1/nu.norm());
		Vector3D nv = Vector3D.linearCombination(u,v,1,- v.dot(u));
		nv          = nv.scale(1/nv.norm());
		
		Vector3D  w = new Vector3D(a,P);
		double   d  = nu.dot(w);
		if (d < 0 && Math.abs(d) > Vector3D.epsilon)
			return false;
		
		w         = new Vector3D(c,P);
		d         = nv.dot(w);
		if (d < 0 && Math.abs(d) > Vector3D.epsilon)
			return false;
		
		w         = new Vector3D(b,P);
		d         = - nv.dot(w);
		if (d < 0 && Math.abs(d) > Vector3D.epsilon)
			return false;
		
		w         = new Vector3D(b.translate(v.scale(a.distance(c))),P);
		d         = - nu.dot(w);
		if (d < 0 && Math.abs(d) > Vector3D.epsilon)
			return false;
		
		return super.belongs(P);
	}
	
	@Override
	public Point getCenter() {
		Vector3D v = new Vector3D(a,b);
		Vector3D w = new Vector3D(a,c);
		return a.translate(Vector3D.linearCombination(v,w,0.5,0.5));
	}
	
	@Override
	public String getName() {
		return "quadFace";
	}
}

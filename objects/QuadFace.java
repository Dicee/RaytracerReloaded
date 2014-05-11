package objects;

import utils.math.Point;
import utils.math.Vector3D;


public class QuadFace extends Face {
	public QuadFace(Point a, Point b, Point c, Texture texture)	{
		super(a,b,c,texture);
	}	

	@Override
	public Point intersection(Point p, Vector3D incidentRay) {
		Point q = super.intersection(p,incidentRay);		
		if (!belongs(q))
			return null;		
		return q;
	}
	
	@Override
	public boolean belongs(Point P)	{
		Vector3D  u = new Vector3D(a,b).multScal(1/a.distance(b));
		Vector3D  v = new Vector3D(a,c).multScal(1/a.distance(c));
		Vector3D nu = Vector3D.linearCombination(v,u,1,- v.dot(u));
		nu          = nu.multScal(1/nu.norm());
		Vector3D nv = Vector3D.linearCombination(u,v,1,- v.dot(u));
		nv          = nv.multScal(1/nv.norm());
		
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
		
		w         = new Vector3D(b.translate(v.multScal(a.distance(c))),P);
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
}

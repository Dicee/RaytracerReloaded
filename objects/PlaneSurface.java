package objects;

import org.jdom2.Element;

import XML.basicTypes.XMLVector;
import utils.math.CustomMath;
import utils.math.Point;
import utils.math.Vector3D;

public class PlaneSurface extends Object3D {

	protected Point a,b,c;
	
	public PlaneSurface(double z, Texture texture) {
		this(new Point(1,0,z),new Point(0,1,z),new Point(0,0,z),texture);
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param texture
	 * @throws IllegalArgumentException if the points are aligned
	 */
	public PlaneSurface(Point a, Point b, Point c, Texture texture) {
		super(texture);
		
		if(new Vector3D(a,b).cross(new Vector3D(a,c)).equals(new Vector3D()))
			throw new IllegalArgumentException();
		
		  this.a = a;
		  this.b = b;
		  this.c = c;
	}

	@Override
	public void rotateX(double u) {
		a = a.rotateX(u);
		b = b.rotateX(u);
		c = c.rotateX(u);
	}

	@Override
	public void rotateY(double u) {
		a = a.rotateY(u);
		b = b.rotateY(u);
		c = c.rotateY(u);
	}

	@Override
	public void rotateZ(double u) {
		a = a.rotateZ(u);
		b = b.rotateZ(u);
		c = c.rotateZ(u);
	}
	
	@Override
	public void rotateXYZ (double u, double v, double w) {
		rotateX(u);
		rotateY(v);
		rotateZ(w);
	}
	
	@Override
	public Vector3D normal(Point p) {		
		Vector3D normal = new Vector3D(a,b).cross(new Vector3D(a,c));
		normal.normalize();
		return normal;
	}	
	
	@Override
	public Point intersection(Point p, Vector3D rayonIncident) {					
		Vector3D direction = 
		  rayonIncident.multScal(1/rayonIncident.norm());
		
		double[] c0 = { b.x - a.x,b.y - a.y,b.z - a.z };
		double[] c1 = { c.x - a.x,c.y - a.y,c.z - a.z };
		double[] c2 = { - direction.x,- direction.y,- direction.z };
		
		double[] b  = { p.x - a.x, p.y - a.y, p.z - a.z };		
		double det  = CustomMath.det(c0,c1,c2);
		
		if (Math.abs(det) <= Vector3D.epsilon)
			return null;
		
		double gamma = CustomMath.cramer(c0,c1,b,det);
		
		if (Math.abs(gamma) <= Vector3D.epsilon || gamma < 0)
			return null;			
			
		return p.translate(direction.multScal(gamma));
	}
	
	@Override
	public boolean belongs(Point p) {		
	  //We compute the coefficients of the Cartesian equation : u.x + v.y + w.z + t = 0
	  Vector3D normal = this.normal(p);	
	  double u = normal.x, v = normal.y, w = normal.z;
	  double t = - (u*a.x + v*a.y + w*a.z); 
	  return Math.abs(u*p.x + v*p.y + w*p.z + t) <= Vector3D.epsilon;
	}
	
	@Override
	public String toString() {
	  return  
	    "      PLAN INFINI\n"    + "\n" +		
	    "      Point 1 :  "  + a + "\n" +		
	    "      Point 2 :  "  + b + "\n" +	
	    "      Point 3 :  "  + c + "\n" +	
	    "\n" + super.toString();				
	}

	@Override
	public int getPriority() {
		return 0;
	}
	
	@Override
	public Element toXML() {	
		Element result = new Element("PlaneSurface");
		result.addContent(new XMLVector("A",a));
		result.addContent(new XMLVector("B",b));
		result.addContent(new XMLVector("C",c));
		return result;
	}

	@Override
	public void translate(Vector3D v) {
		this.a = this.a.translate(v);
		this.b = this.b.translate(v);
		this.c = this.c.translate(v);
	}
	
	public Point[] getPoints(){
		return new Point[] { a,b,c };
	}
	
	@Override
	public Point getCenter(){
		return a.clone();
	}
	
	@Override
	public Object3D clone()	{
		return new PlaneSurface(a,b,c,texture);
	}
	
	@Override
	public void copy(Object clone) {
		PlaneSurface plan = (PlaneSurface) clone;	
		Point[] pts       = plan.getPoints();
		a                 = pts[0].clone();
		b                 = pts[1].clone();
		c                 = pts[2].clone();
		texture           = plan.texture.clone();
	}
	
	@Override
	public Vector3D[] getAdaptedBase() {
		return new Vector3D[] { new Vector3D(a,b),new Vector3D(a,c) };
	}
	
	@Override
	public float[] Ka(Point p) {	
		//Orthonormal base (i,j) of the plane surface
		Vector3D i = new Vector3D(a,b);
		Vector3D j = new Vector3D(a,c);
		i = Vector3D.linearCombination(i,i,1/i.norm(),0);
		j = Vector3D.linearCombination(j,i,1,- i.dot(j));
		j = Vector3D.linearCombination(j,j,1/j.norm(),0);
		
		//Compute the (u,v) coordinates of p in this base
		Point q       = p.clone();
		q             = q.translate(new Vector3D(- a.x,- a.y,- a.z));
		Vector3D vect = new Vector3D(q.x,q.y,q.z);
		double u      = vect.dot(i);
		double v      = vect.dot(j);
		return texture.Ka((int) (patternRepeat*u),(int) (patternRepeat*v));
	}

	@Override
	protected void checkedResize(double factor) {
		//Nothing to do since the plane surface is unlimited
	}
	
	@Override
	public String getName() {
		return "infinitePlaneSurface";
	}
}




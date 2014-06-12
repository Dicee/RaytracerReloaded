package objects;

import org.jdom2.Element;

import utils.math.CustomMath;
import utils.math.Matrix;
import utils.math.Point;
import utils.math.Vector3D;
import XML.basicTypes.XMLDouble;
import XML.basicTypes.XMLVector;

public class Sphere extends Object3D {
	protected Point c;
	protected double r;
	protected Vector3D[] base = CustomMath.canonicBase;	
	
	/**
	 * 
	 * @param c
	 * @param r
	 * @param texture
	 * @throws IllegalArgumentException if r <= 0
	 */
	public Sphere(Point c, double r, Texture texture) {
		super(texture);
		if (r <= 0)
			throw new IllegalArgumentException();
		this.c = new Point(c);
		this.r = r;
	}
	
	 @Override
	public void rotateX(double u) {
		base[0] = base[0].rotateX(u);
		base[1] = base[1].rotateX(u);
	}

	 @Override
	public void rotateY(double v) {
		base[0] = base[0].rotateY(v);
		base[1] = base[1].rotateY(v);
	}

	@Override
	public void rotateZ(double w) {
		base[0] = base[0].rotateZ(w);
		base[1] = base[1].rotateZ(w);
	}
	
	@Override
	public void rotateXYZ(double u, double v, double w) {
		rotateX(u);
		rotateY(v);
		rotateZ(w);
	}
	
	@Override
	public Vector3D normal(Point p)	{
		return (new Vector3D(c,p)).scale(1/r);
	}		
	
	@Override
	public Point intersection(Point p, Vector3D incidentRay) {
		Vector3D direction  = new Vector3D(incidentRay);
		Vector3D vect       = new Vector3D(p,c);
		direction.normalize();
		
		double dot  = direction.dot(vect);
		
		double delta = r*r + dot*dot - vect.sqrNorm();	
		double t1 = 0, t2 = 0;
		
		if (delta >= 0)	{
			t2 = dot + Math.sqrt(delta);
			t1 = dot - Math.sqrt(delta);
			
			t2 = t2 >= 0 ? t2 : 0;
			t2 = t2 <= Vector3D.epsilon ? 0 : t2;
			
			t1 = t1 >= 0 ? t1 : 0;
			t1 = t1 <= Vector3D.epsilon ? 0 : t1;			
		}		
		if (t1 == 0 && t2 == 0) return null;
		else if (t1 == 0) t1 = t2;
		else if (t2 == 0) t2 = t1;
		return p.translate(direction.scale(Math.min(t1,t2)));		
	}
	
	@Override
	public boolean belongs(Point p)	{
		return Math.abs(p.distance(c) - r) <= Vector3D.epsilon;
	}
	
	@Override
	public String toString() {
		return  "      Sphere"        + "\n" +
				"      Centre :   " + c + "\n" +
				"      Rayon :   "  + r + "\n" +
				"\n" + super.toString();				
	}
	
	@Override
	public int getPriority() {
		return -1;
	}
		
	@Override
	public Element toXML() {	
		Element result = new Element("Sphere");
		result.addContent(new XMLDouble("ray",r));
		result.addContent(new XMLVector("center",c));
		return result;
	}

	@Override
	public void translate(Vector3D v) {
		c = c.translate(v);
	}
	
	@Override
	public Point getCenter() {
		return c.clone();
	}
	
	public double getRay() {
		return r;
	}
	
	@Override
	public Object3D clone()	{
		return new Sphere(c,r,texture);
	}
	
	@Override
	public void copy(Object3D clone) {
		Sphere s = (Sphere) clone;			
		c.setLocation(s.c);
		r        = s.r;
		texture  = s.texture;
		base[0]  = new Vector3D(s.getAdaptedBase()[0]);
		base[1]  = new Vector3D(s.getAdaptedBase()[1]);
	}
	
	@Override
	public Vector3D[] getAdaptedBase() {
		return new Vector3D[] { base[0],base[1],base[0].cross(base[1]) };
	}
		
	@Override
	public float[] Ka(Point p) {
		Vector3D[] baseAdaptee   = { base[0],base[1],base[0].cross(base[1]) };
		Vector3D vect            = new Vector3D(p,getCenter());
		vect                     = vect.scale(1/vect.norm());
		Vector3D[] baseCanonique = { new Vector3D(1,0,0),new Vector3D(0,1,0),new Vector3D(0,0,1) };
		Matrix passage           = Matrix.transferMatrix(baseAdaptee,baseCanonique);
		Vector3D res             = passage.mult(vect);			
	
		int rep                  = repeat ? patternRepeat : 1;
		int u                    = (int) (rep*(0.5 + Math.atan2(res.z,res.x)/(2*Math.PI))*texture.getWidth());
		int v                    = (int) (rep*(0.5 - Math.asin(res.y)/Math.PI)*texture.getHeight());		
		return texture.Ka(u,v,adapt);
	}
	
	@Override
	protected void checkedResize(double factor) {
		r *= factor;
	}
	
	@Override
	public String getName() {
		return "sphere";
	}
}


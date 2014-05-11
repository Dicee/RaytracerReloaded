package objects;

import utils.math.Translatable;
import XML.XMLable;
import utils.math.Point;
import utils.math.Rotatable;
import utils.math.Vector3D;

public abstract class Object3D implements XMLable, Translatable, Rotatable {	
	
	protected Texture texture;	
	private boolean   shown;
	protected int	  patternRepeat;
	protected boolean repeat, adapt;
	
	public Object3D(Texture texture) {	    
		this.texture       = texture == null ? Texture.defaultTexture : texture.clone();	
	  	this.shown         = true;
	  	this.patternRepeat = 1;
	  	this.repeat        = true;
	  	this.adapt         = true;
	}	

	public Vector3D reflectedRay(Point p, Vector3D incidentRay) {
		Vector3D normal = this.normal(p);	
		double dot      = incidentRay.dot(normal);
		
		if (dot < 0) {
			normal = normal.multScal(-1);
			dot   *= -1;
		}
		return Vector3D.linearCombination(incidentRay,normal,1,-2 * dot); 
	}

	public Vector3D refractedRay(Point p, Vector3D incidentRay, double envIndex) throws TotalReflection {
		Vector3D normal = this.normal(p);		
		double   dot    = incidentRay.dot(normal);
		
		if (dot < 0) {
			dot *= -1;
			normal   = normal.multScal(-1); 
		}
		double x         = envIndex / texture.refraction();
		double radicande = 1 - x*x*(1 - dot*dot);
		
		if (radicande < 0 && - radicande > Vector3D.epsilon) 
			throw new TotalReflection();
		return Vector3D.linearCombination(incidentRay,normal,x,dot*x - Math.sqrt(radicande));
	}	
	
	public abstract void rotateX( double u);
	public abstract void rotateY( double u);
	public abstract void rotateZ( double u);  
	public abstract void rotateXYZ (double u, double v, double w);
	
	public abstract Vector3D normal(Point p);
	public abstract Point intersection(Point p, Vector3D incidentRay);
	public abstract boolean belongs(Point p);
	
	public abstract int getPriority();
	public abstract Vector3D[] getAdaptedBase();
	
	@Override
	public abstract void translate(Vector3D v);	
	@Override
	public abstract Point getCenter();
	@Override
	public abstract Object3D clone();
	
	public void resize(double factor) {
		if (factor <= 0 || factor <= Vector3D.epsilon)
			throw new IllegalArgumentException("The resizement factor should be > 0");
		checkedResize(factor);
	}
	protected abstract void checkedResize(double factor);
	
	public String toXML(String align, String attributes) {
		return texture.toXML(align,attributes);
	}
	
	public String toString() {
	  return String.format("State : %s, Texture : ",isShown() ? "Shown" : "Hided",texture);
	}

	public Texture getTexture() {
		return texture;
	}
	
	public void setTexture(Texture texture) {
		if (texture == null)
			throw new NullPointerException();
		this.texture = texture;
	}
	
	public abstract float[] Ka(Point p);
	
	public float[] Kr() {
		
	  return texture.Kr();
	}
	
	public float[] Kt() {
		return texture.Kt();
	}
	
	public float[] reflectance() {
	  return texture.reflectance();
	}
	
	public float[] brillance() {
	  return texture.brillance();
	}
	
	public void setShown(boolean b)	{
		shown = b;
	}
	
	public boolean isShown() {
		return shown;
	}

	/**
	 * 
	 * @param repeat
	 * @throws IllegalArgumentException if n <= 0
	 */
	public void setPatternRepeat(int n)	{
		if (n <= 0)
			throw new IllegalArgumentException();
		patternRepeat = n;
	}
	
	public boolean repeats() {
		return repeat;
	}
	
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public boolean adapts() {
		return adapt;
	}

	public void setAdapt(boolean adapt) {
		this.adapt = adapt;
	}

	public int getPatternRepeat() {
		return patternRepeat;
	}

	public abstract void copy(Object clone);
}


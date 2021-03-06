package objects;

import utils.Copiable;
import utils.Hidable;
import utils.math.Point;
import utils.math.Rotatable;
import utils.math.Translatable;
import utils.math.Vector3D;
import XML.XMLable;
import org.jdom2.Element;

public abstract class Object3D implements XMLable, Translatable, Rotatable, Cloneable, Copiable<Object3D>, Hidable {	
	
	protected Texture texture;	
	private boolean   shown;
	protected int	  patternRepeat;
	protected boolean repeat, adapt; 
	
	public Object3D(Texture texture) {	    
		this.texture       = texture == null ? Texture.DEFAULT_TEXTURE : texture;	
	  	this.shown         = true;
	  	this.patternRepeat = 1;
	  	this.repeat        = true;
	  	this.adapt         = true;
	}	

	public Vector3D reflectedRay(Point p, Vector3D incidentRay) {
		Vector3D normal = this.normal(p);	
		double dot      = incidentRay.dot(normal);
		
		if (dot < 0) {
			normal = normal.scale(-1);
			dot   *= -1;
		}
		return Vector3D.linearCombination(incidentRay,normal,1,-2 * dot); 
	}

	public Vector3D refractedRay(Point p, Vector3D incidentRay, double envIndex) throws TotalReflectionException {
		Vector3D normal = this.normal(p);		
		double   dot    = incidentRay.dot(normal);
		
		if (dot < 0) {
			dot *= -1;
			normal   = normal.scale(-1); 
		}
		double x         = envIndex / texture.refractiveIndex();
		double radicande = 1 - x*x*(1 - dot*dot);
		
		if (radicande < 0 && - radicande > Vector3D.epsilon) 
			throw new TotalReflectionException();
		return Vector3D.linearCombination(incidentRay,normal,x,dot*x - Math.sqrt(radicande));
	}	

	public void resize(double factor) {
		if (factor <= 0 || factor <= Vector3D.epsilon)
			throw new IllegalArgumentException("The resizement factor should be > 0");
		checkedResize(factor);
	}
	protected abstract void checkedResize(double factor);
	
	protected Element setXMLTextureProperties(Element e) {
		e.setAttribute("repeat",repeat + "");
		e.setAttribute("patternRepeat",patternRepeat + "");
		e.setAttribute("adapt",adapt + "");
		return e;
	}
	
	@Override
	public final Element toXML() {
		return setXMLTextureProperties(toXMLImpl());
	}
	protected abstract Element toXMLImpl();
	
	@Override
	public abstract void rotateX( double u);
	@Override
	public abstract void rotateY( double u);
	@Override
	public abstract void rotateZ( double u);  
	@Override
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
	public abstract String getName();
	
	@Override
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
	
    public double refractiveIndex() {
        return texture.refractiveIndex();
    }
    
	public float[] Kr() {
	  return texture.Kr();
	}
	
	public float[] Kt() {
		return texture.Kt();
	}
	
	public float[] reflectance() {
	  return texture.reflectance();
	}
	
	public float brilliance() {
	  return texture.brillance();
	}
	
	@Override
	public void setShown(boolean b)	{
		shown = b;
	}
	
	@Override
	public boolean isShown() {
		return shown;
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
	
	/**
	 * 
	 * @param n
	 * @throws IllegalArgumentException if n <= 0
	 */
	public void setPatternRepeat(int n)	{
		if (n <= 0)
			throw new IllegalArgumentException();
		patternRepeat = n;
	}
}


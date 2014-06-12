package objects;

import utils.math.Point;
import utils.math.Vector3D;

public abstract class MeshedObject extends WrappedObject {
	protected Face[] faces;
	
	public MeshedObject (Texture texture) {
		super(texture);
	}
	
	public abstract void generateFaces();
	
	@Override
	public final int getPriority() {
		return - faces.length;
	}
	
	@Override
	public Vector3D normal(Point p)	{		
		Face facette = null;		
		for (int i=0 ; i<faces.length && facette == null; i++)		
			facette = faces[i].belongs(p) ? faces[i] : null;
		
		if (facette == null) 
			return null;		
		return facette.normal(p);
	}		
	
	@Override
	public Point intersection(Point p, Vector3D rayonIncident) {		
		if (getWrappingObject().intersection(p,rayonIncident) == null)
			return null;
		
		Point result = null;
		double minDist = Double.MAX_VALUE;
		for (int i=0 ; i<faces.length ; i++) {	
			Point intersection = faces[i].intersection(p,rayonIncident);
			double distance = intersection == null ? Double.MAX_VALUE : p.distance(intersection);
			if (distance < minDist && distance > Vector3D.epsilon) {
				minDist = distance;
				result = intersection; 
			}			
		}			
		return result;
	}	

	@Override
	public boolean belongs(Point p)	{
		boolean test = false;		
		for (int i=0 ; i<faces.length && !test ; i++)		
			test = faces[i].belongs(p);			
		return test;
	}
	
	/**
	 * 
	 * @param p
	 * @throws IllegalArgumentException if p does not belong to the MeshedObject
	 */
	@Override
	public float[] Ka(Point p) {
		Face face = null;	
		for (int i=0 ; i<faces.length && face == null ; i++)		
			if (faces[i].belongs(p))
				face = faces[i];
		if (face == null)
			throw new IllegalArgumentException();
		return face.Ka(p);
	}
}

package objects;

import utils.math.CustomMath;
import utils.math.Point;
import utils.math.Vector3D;

public class TriFace extends Face {
	
	public TriFace(Point a, Point b, Point c, Texture texture) {
		super(a,b,c,texture);
	}	

	@Override
	public Point intersection(Point p, Vector3D incidentRay) {
		Vector3D direction = new Vector3D(incidentRay);
		direction.normalize();
		
		//First column of the system
		double[] c0 = { b.getX() - a.getX(),
						b.getY() - a.getY(),
						b.getZ() - a.getZ()
		              };
		
		//Second column of the system
		double[] c1 = { c.getX() - a.getX(),
				        c.getY() - a.getY(),
				        c.getZ() - a.getZ() 
				      };
		
		//Third column of the system
		double[] c2 = { - direction.getX(),
				        - direction.getY(),
				        - direction.getZ() 
					  };
		
		//Right side
		double[] b  = { p.getX() - a.getX(),
						p.getY() - a.getY(),
						p.getZ() - a.getZ() 
					  };		
		
		double det = CustomMath.det(c0,c1,c2);
		
		if (Math.abs(det) <= Vector3D.epsilon)
			return null;
		
		double alpha = CustomMath.cramer(b,c1,c2,det);
		double beta  = CustomMath.cramer(c0,b,c2,det);
		double gamma = CustomMath.cramer(c0,c1,b,det);		
		
		//We attempt to return as soon as possible
		boolean condAlpha = (alpha >= 0 || Math.abs(alpha) <= Vector3D.epsilon) 
				&& (alpha <= 1 || Math.abs(alpha-1) <= Vector3D.epsilon);		
		if (!condAlpha)	return null;
		
		boolean condBeta = (beta >= 0 || Math.abs(beta) <= Vector3D.epsilon) 
				&& (beta <= 1-alpha || Math.abs(beta-1+alpha) <= Vector3D.epsilon);		
		if (!condBeta) return null;
		
		boolean condGamma = Math.abs(gamma) <= Vector3D.epsilon || gamma > 0;
		if (!condGamma) return null;
				
		return p.translate(direction.multScal(gamma));
	}
	
	@Override
	public boolean belongs(Point P)	{
		Vector3D vab = new Vector3D(a,b);
		Vector3D vac = new Vector3D(a,c);
		Vector3D vap = new Vector3D(a,P);
		
		double coteX = vab.norm();
		double coteY = (new Vector3D(a,c)).norm();
		
		double projX = vap.dot(vab) / coteX;
		double projY = vap.dot(vac) / coteY;
		
		boolean b1   = (projX >= 0 || -projX <= Vector3D.epsilon) && (projY >= 0  || -projX <= Vector3D.epsilon);
		if (!b1) return false;
		
		boolean b2   = projX <= coteX + Vector3D.epsilon && projY <= coteY + Vector3D.epsilon;
		if (!b2) return false;
		
		return super.belongs(P);
	}
	
	public Point getCenter() {
		Vector3D v = new Vector3D(a,b);
		Vector3D w = new Vector3D(a,c);
		return a.translate(Vector3D.linearCombination(v,w,0.5,0.5));
	}
}

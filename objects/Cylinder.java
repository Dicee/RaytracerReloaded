package objects;

import utils.math.Vector3D;
import utils.math.Matrix;
import utils.math.CustomMath;
import utils.math.Point;

public class Cylinder extends Object3D 
{
	protected Vector3D axis;
	protected Vector3D ex;
	protected Point baseCenter;
	protected double h, r;
	
	/**
	 * 
	 * @param h
	 * @param r
	 * @param texture
	 * @throws IllegalArgumentException if r or h <= 0
	 */
	public Cylinder(double h, double r, Texture texture) {
		super(texture);
		
		this.h          = h;
		this.r          = r;
		//Default values of the other attributes
		this.ex         = new Vector3D(1,0,0);
		this.axis       = new Vector3D(0,0,1);
		this.baseCenter = new Point();
	}	
	
	public Cylinder(double h, double r, Point centre, Vector3D axe, Texture texture) {
		super(texture);
		this.h          = h;
		this.r          = r;
		this.ex         = CustomMath.adaptedBase(axe)[0];
		this.axis       = axe.multScal(1/axe.norm());
		this.baseCenter = centre.clone();
	}

	@Override
	public void copy(Object clone) {		
		Cylinder cylindre = (Cylinder) clone;
		Point[] centers   =  cylindre.getBasesCenters();
		
		axis       = new Vector3D(centers[0],centers[1]);
		axis       = axis.multScal(1/axis.norm());
		h          = centers[0].distance(centers[1]);
		baseCenter = centers[0].clone();
		r          = cylindre.getRay();
		ex         = cylindre.getAdaptedBase()[0].clone();
		texture    = cylindre.texture.clone();
	}
	
	@Override
	public Vector3D[] getAdaptedBase() {
		return new Vector3D[] { ex,ex.cross(axis),axis };
	}

	@Override
	public void rotateX(double u) {		
		Vector3D t = new Vector3D(new Point(0,0,0),getCenter());
		baseCenter = baseCenter.translate(t.multScal(-1)).rotateX(u).translate(t);
		axis       = axis.rotateX(u);	
		ex         = ex.rotateX(u);
	}

	@Override
	public void rotateY(double v) {		
		Vector3D t = new Vector3D(new Point(),getCenter());
		baseCenter = baseCenter.translate(t.multScal(-1)).rotateY(v).translate(t);
		axis       = axis.rotateY(v);	
		ex         = ex.rotateY(v);
	}

	@Override
	public void rotateZ(double w) {		
		Vector3D t = new Vector3D(new Point(0,0,0),getCenter());
		baseCenter = baseCenter.translate(t.multScal(-1)).rotateZ(w).translate(t);
		axis       = axis.rotateZ(w);	
		ex         = ex.rotateZ(w);
	}

	@Override
	public void rotateXYZ(double u, double v, double w) {
		rotateX(u);
		rotateY(v);
		rotateZ(w);
	}

	/*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 *                       Old code ! Need to check later !
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	@Override
	public Vector3D normal(Point p) {
		//if (!appartient(p)) {  System.out.println(p); throw new IntersectionVideException(); }
		
		//On commence par calculer une equation cartesienne du plan des deux bases pour verifier
		//si p y est contenu		
		double u  = axis.getX(), v = axis.getY(), w = axis.getZ();
		Point c1  = baseCenter, c2 = baseCenter.translate(axis.multScal(h));
		double t0 = - (u*c1.getX() + v*c1.getY() + w*c1.getZ());
		double t1 = - (u*c2.getX() + v*c2.getY() + w*c2.getZ());

		//Test sur la premiere base
		if (Math.abs(u*p.getX() + v*p.getY() + w*p.getZ() + t0) <= Vector3D.epsilon)
			if (c1.distance(p) <= Vector3D.epsilon + r) {// System.out.println(p+" - 1 - "+axe.multScal(-1));
				return axis.multScal(-1); }
		
		//Test sur la deuxieme base
		if (Math.abs(u*p.getX() + v*p.getY() + w*p.getZ() + t1) <= Vector3D.epsilon) 
			if (c2.distance(p) <= Vector3D.epsilon + r) {// System.out.println(p+" - 2 - "+axe);
			    return axis; }
		
		//Si p n'appartient au plan d'aucune des bases alors on le projette sur l'axe du cylindre et on renvoie
		//le vecteur norme de direction (resultat projection)->(point p)
		Vector3D vect = new Vector3D(baseCenter,p);
		Point m = baseCenter.translate(axis.multScal(vect.dot(axis)));
		//System.out.println(p+" - 3 - "+(new Vecteur(m,p)).multScal(1/r)+"  "+axe+ "  "+m);
		return (new Vector3D(m,p)).multScal(1/r);
	}

	/*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 *                       Old code ! Need to check later !
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	@Override
	public Point intersection(Point p, Vector3D rayonIncident) {
		//On commence par chercher des intersections avec les plans des deux bases. S'il en existe, on verifie
		//que la distance a l'axe est inferieure au rayon, sinon, on cherche une expression parametrique de la 
		//droite definie par (p,rayonIncident) dans une base adaptee au cylindre. Enfin, on resout un polynome
		//du second degre pour trouver d'eventuelle(s) solution(s).	
		
		Vector3D[] baseAdaptee = { ex,ex.cross(axis),axis };		
		Vector3D b0 = baseAdaptee[0];
		Vector3D b1 = baseAdaptee[1], b2 = baseAdaptee[2];
		PlaneSurface planBase1 = new PlaneSurface(baseCenter,baseCenter.translate(b0),baseCenter.translate(b1),
									          Texture.defaultTexture);
		PlaneSurface planBase2 = new PlaneSurface(baseCenter.translate(b2.multScal(h)),
											  baseCenter.translate(b0).translate(b2.multScal(h)),
											  baseCenter.translate(b1).translate(b2.multScal(h)),
											  Texture.defaultTexture);		
		//Recherche sur les bases
		Point inter1 = null;
		Point inter2 = null;
		
		//Hormis le cas ou la droite delta = (p,rayonIncident) est incluse dans un des plans (cas qu'on ignore), si
		//elle intersecte un plan alors elle intersecte les deux
		inter1 = planBase1.intersection(p,rayonIncident);		
		if (inter1 != null && inter1.distance(baseCenter) > Vector3D.epsilon + r || inter1.equals(p)) 
			 inter1 = null;
			
		inter2 = planBase2.intersection(p,rayonIncident);
		if (inter2 != null && inter2.distance(baseCenter.translate(b2.multScal(h))) > Vector3D.epsilon + r || inter2.equals(p)) 
			inter2 = null;
			
		//On stocke la meilleure solution dans inter1
		if (inter1 == null && inter2 != null)
			inter1 = inter2;
		else if (inter1 != null && inter2 != null)
			inter1 = p.distance(inter1) <= p.distance(inter2) ? inter1 : inter2;
		else if (inter1 == null && inter2 == null)
			return null;
		
		//Recherche sur la surface laterale
		Vector3D v = new Vector3D(baseCenter,p);
		//Changement de repere pour p
		double  x = v.dot(b0);
		double  y = v.dot(b1);
		double  z = v.dot(b2);
		//Changement de repere pour rayonIncident
		double  a = rayonIncident.dot(b0);
		double  b = rayonIncident.dot(b1);
		double  c = rayonIncident.dot(b2);
		//Coefficients du polynome
		double lambda = x*x + y*y - r*r;
		double mu     = 2*(x*a + y*b);
		double nu     = a*a + b*b;
		//Resolution
		double delta  = mu*mu - 4*lambda*nu;

		Point i1 = null, i2 = null;

		if (delta >= 0)
		{
			double t1 = (- mu + Math.sqrt(delta))/(2*nu);
			double t2 = (- mu - Math.sqrt(delta))/(2*nu);
			
			i1        = new Point(x + t1*a,y + t1*b,z + t1*c); 
			i2        = new Point(x + t2*a,y + t2*b,z + t2*c); 				
				
			//On verifie, dans une base adaptee au cylindre, que i1 et i2 sont bien des points du cylindre de hauteur h (on
			//sait qu'ils sont des points du cylindre infini de meme axe et de meme rayon)
			i1 = i1.getZ() <= h + Vector3D.epsilon && 
					(i1.getZ() > 0 || Math.abs(i1.getZ()) <= Vector3D.epsilon) ? i1 : null;			
			i2 = i2.getZ() <= h + Vector3D.epsilon && 
					(i2.getZ() > 0 || Math.abs(i2.getZ()) <= Vector3D.epsilon) ? i2 : null;
			
			//S'ils appartiennent bien au cylindre de hauteur h, on replace i1 et i2 dans la base canonique de l'espace
			Vector3D[] baseCanonique = { new Vector3D(1,0,0),new Vector3D(0,1,0),new Vector3D(0,0,1) };
			Matrix passage          = Matrix.transferMatrix(baseAdaptee,baseCanonique);	
			Vector3D translateRepere = new Vector3D(new Point(0,0,0),baseCenter);			
			Vector3D res;
						
			if (i1 != null) { 
				res = passage.mult(new Vector3D(i1.getX(),i1.getY(),i1.getZ()));
				i1  = new Point(res.x,res.y,res.z).translate(translateRepere);	
			}
			
			if (i2 != null) { 
				res = passage.mult(new Vector3D(i2.getX(),i2.getY(),i2.getZ()));
				i2  = new Point(res.x,res.y,res.z).translate(translateRepere);				
			}
		
			//On verifie que i1 et i2 sont bien differents de p
			i1 = i1 == null || i1.equals(p) ? null : i1;
			i2 = i2 == null || i2.equals(p) ? null : i2;
			
			//On stocke la meilleure solution dans i1
			if (i1 == null && i2 != null)
				i1 = i2;
			else if (i1 != null && i2 != null) 
			  	i1 = p.distance(i1) <= p.distance(i2) ? i1 : i2; 
		}	
		
		//On selectionne ensuite la meilleure solution entre i1 et inter1
		if (i1 == null && inter1 != null)
			return inter1;
		else if (i1 != null && inter1 == null)
			 return i1;
		else if (i1 != null && inter1 != null)
			return p.distance(i1) <= p.distance(inter1) ? i1 : inter1; 	
		else 
			return null;
	}

	/*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 *                       Old code ! Need to check later !
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	@Override
	public boolean belongs(Point p) {
		//Creation d'une base orthonormee directe adaptee
		Vector3D[] baseAdaptee = CustomMath.adaptedBase(axis);
		Vector3D u = baseAdaptee[0];
		Vector3D v = baseAdaptee[1], w = baseAdaptee[2];
		
		Vector3D vect = new Vector3D(baseCenter,p);
		double x     = vect.dot(u);
		double y     = vect.dot(v);
		double z     = vect.dot(w);
			
		//Test de l'appartenance de p aux bases
		boolean bBase1 = Math.abs(z)   <= Vector3D.epsilon && x*x + y*y <= r*r + Vector3D.epsilon;
		boolean bBase2 = Math.abs(z-h) <= Vector3D.epsilon && x*x + y*y <= r*r + Vector3D.epsilon;
		//Test de l'appartenance a la surface laterale
		boolean bLat   = z + Vector3D.epsilon >= 0 && z <= h + Vector3D.epsilon && Math.abs(x*x + y*y - r*r) <= Vector3D.epsilon;
				
		return bBase1 || bBase2 || bLat;
	}

	/*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 *                       Old code ! Need to check later !
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	@Override
	public float[] Ka(Point p) {		
		Vector3D u = ex;
		//Creation d'une base orthonormee directe adaptee
		Vector3D v = axis.cross(ex), w = axis;				
		Vector3D vect = new Vector3D(baseCenter,p);
		//On part du principe que p appartient forcement au cylindre, ainsi si |x| != r on est forcement sur une des bases
		double x     = vect.dot(u);
		double y     = vect.dot(v);
		double z     = vect.dot(w);	
		double theta = y < 0 ? Math.acos(x/r) : Math.acos(x/r);
	
		if (Math.abs(r*r - x*x - y*y) <= Vector3D.epsilon) 
			return texture.Ka((int) (patternRepeat*theta/Math.PI*(texture.getWidth()-1)),(int) ((1-z)/h*texture.getHeight()));		
		else 
		{
			p     = p.translate(axis.multScal(h-z));
			vect  = new Vector3D(baseCenter.translate(axis.multScal(h)),p);
			x     = vect.dot(u);
			y     = vect.dot(v);
			theta = y < 0 ? Math.acos(x/vect.norm()) : Math.acos(x/vect.norm());
			
			theta    = theta/Math.PI;
			double R = (x*x + y*y)/(r*r);
			return texture.Ka((int) (patternRepeat*theta*(texture.getWidth()-1)),(int) ((1-R)*texture.getHeight()));
		}
	}

	@Override
	public int getPriority() {		
		return -3;
	}

	@Override
	public void translate(Vector3D v) {		
		baseCenter = baseCenter.translate(v);
	}

	@Override
	public Point getCenter() {
		return baseCenter.translate(axis.multScal(h/2));
	}

	@Override
	public Object3D clone() {
		Point[] centres = getBasesCenters();
		return new Cylinder(h,r,centres[0],new Vector3D(centres[0],centres[1]),texture);
	}	
	
	public double getRay() {
		return r;
	}
	
	public Point[] getBasesCenters() {
		return new Point[] { baseCenter,baseCenter.translate(axis.multScal(h)) } ;
	}
	
	@Override
	public String toXML(String align, String attributes) {		
		return align + "<Cylindre>"                                                                 + "\n"  +
			   align + "\t" + h                                                                     + "\n"  +
			   align + "\t" + r                                                                     + "\n"  +
			   align + "\t" + baseCenter.getX() + " " + baseCenter.getY() + " " + baseCenter.getZ() + "\n"  +
			   align + "\t" + axis.getX()        + " " + axis.getY()        + " " + axis.getZ()        + "\n"  +
			   super.toXML(align+"\t",attributes)  + "\n"              + align                   + "</Cylindre>";
	}
	
	@Override
	public String toString() {
		return  "      CYLINDRE\n"                                   + "\n" +
				"      Hauteur :   "                    +  h         + "\n" +
				"      Rayon :   "                      +  r         + "\n" +
				"      Centre de la premiere base :   " + baseCenter + "\n" +
				"      Axe :   "                        + axis        + "\n" +
				"\n"                                    + super.toString();				
	}
	
	@Override
	protected void checkedResize(double facteur) {
		Point c    = getCenter();
		Vector3D v = (new Vector3D(c,baseCenter)).multScal(2/h);
		h         *= facteur;
		r         *= facteur;
		baseCenter = baseCenter.translate(v.multScal(h/2));
	}
}

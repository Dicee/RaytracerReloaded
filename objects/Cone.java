package objects;

import org.jdom2.Element;

import XML.basicTypes.XMLDouble;
import XML.basicTypes.XMLVector;
import utils.math.Matrix;
import utils.math.Vector3D;
import utils.math.CustomMath;
import utils.math.Point;

public class Cone extends WrappedObject {
	protected Point baseCenter, vertice;
	protected Vector3D ex;
	protected double baseRay;
	
	/**
	 * 
	 * @param C
	 * @param S
	 * @param r
	 * @param texture
	 * @throws IllegalArgumentException if r <= 0
	 */
	public Cone (Point C, Point S, double r, Texture texture) {
		super(texture);
		
		boolean verifNull    = C == null || S == null;		
		boolean verifNombres = r <= 0;
		boolean verifPoints  = C.equals(S); 
		
		if (verifNull || verifNombres || verifPoints) {
			String err1 = "at least one of the points is null";
			String err2 = "r should be > 0";
			String err3 = "C and S should verify !C.equals(S)";
			String msg  = "";
			msg = verifNull    ? "\n. " + err1 + msg : msg;
			msg = verifNombres ? "\n. " + err2 + msg : msg;
			msg = verifPoints  ? "\n. " + err3 + msg : msg;
			
			throw new IllegalArgumentException(msg);
		}
		
		this.baseCenter = C;
		this.vertice    = S;
		this.baseRay    = r;
		this.ex         = CustomMath.adaptedBase(new Vector3D(C,vertice))[0];
	}
	
	@Override
	public Vector3D[] getAdaptedBase() {
		Vector3D uz = (new Vector3D(baseCenter,vertice)).multScal(1/vertice.distance(baseCenter));	
		return new Vector3D[] { ex,uz.cross(ex),uz };
	}
		
	@Override
	public Object3D getWrappingObject() {
		return new Sphere(baseCenter,Math.max(baseRay,baseCenter.distance(vertice)),Texture.defaultTexture);
	}

	@Override
	public void rotateX(double u) {
		Vector3D v = new Vector3D(getCenter(),new Point(0,0,0));
		vertice    = vertice.translate(v).rotateX(u).translate(v.multScal(-1));
		baseCenter = baseCenter.translate(v).rotateX(u).translate(v.multScal(-1));	
		ex         = ex.rotateX(u);
	}

	@Override
	public void rotateY(double u) {
		Vector3D v = new Vector3D(getCenter(),new Point(0,0,0));
		vertice    = vertice.translate(v).rotateY(u).translate(v.multScal(-1));
		baseCenter = baseCenter.translate(v).rotateY(u).translate(v.multScal(-1));	
		ex         = ex.rotateY(u);
	}

	@Override
	public void rotateZ(double u)
	{
		Vector3D v = new Vector3D(getCenter(),new Point(0,0,0));
		vertice    = vertice.translate(v).rotateZ(u).translate(v.multScal(-1));
		baseCenter = baseCenter.translate(v).rotateZ(u).translate(v.multScal(-1));	
		ex         = ex.rotateZ(u);
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
	public Vector3D normal(Point p) 
	{
		//On commence par calculer le plan de la base pour verifier si p y est contenu		
		
		
		/*Vecteur[] v = getBaseLocale();	
		PlanInfini planBase = new PlanInfini(centreBase,centreBase.translate(b[0]),centreBase.translate(b[1]),
				Texture.defaultTexture);
		if (planBase.appartient(p))
			return*/		
		
		
		Vector3D normale = (new Vector3D(vertice,baseCenter)).multScal(1/vertice.distance(baseCenter));
		double u = normale.getX(), v = normale.getY(), w = normale.getZ();
		double t = - (u*baseCenter.getX() + v*baseCenter.getY() + w*baseCenter.getZ());		

	    if (Math.abs(u*p.getX() + v*p.getY() + w*p.getZ() + t) <= Vector3D.epsilon)
	    	if (baseCenter.distance(p) <= Vector3D.epsilon + baseRay)
	    		return normale; 
	    	else
	    		return null;		
	    
	    /* Si a ce stade la methode n'a pas renvoye de resultat ou leve une exception, on cherche sur la surface
	     * laterale. D'abord, on projette le point dans la base locale pour determiner z. De la, on calcule les
	     * coordonnees du point aligne M avec S et p et se situant sur le plan de la base. On en deduit une base 
	     * polaire de la base du cone (ux,uy) pointant les coordonnes de M, il  ne reste alors plus qu'a faire le
	     * produit vectoriel du vecteur directeur unitaire (sortant) de la droite passant par p et sa projectionsur
	     * l'axe avec uy.
	     */
	    
	    //On ecarte le sommet qui est un cas particulier	
	    normale      = normale.multScal(-1);
	    if (p.equals(vertice))
	    	return normale;
	    
	    double  z    = new Vector3D(baseCenter,p).dot(normale);
	    double  h    = vertice.distance(baseCenter);
	    double  d    = vertice.distance(p);
	    Vector3D vect = (new Vector3D(vertice,p)).multScal(1/d);
	    Point   M    = p.translate(vect.multScal(d*(h/(h-z)-1)));	    
	    Vector3D uy   = normale.cross(new Vector3D(baseCenter,M));
	    uy           = uy.multScal(1/uy.norm());
	    Point   N    = baseCenter.translate(normale.multScal(z));
	    Vector3D ux   = uy.cross(new Vector3D(N,p));
	    ux           = ux.multScal(1/ux.norm());
		
	    try { intersection(p,ux.cross(uy));}catch(Exception e) { System.out.println(ux+"    "+uy+"   "+vect);}
	    return ux.cross(uy);
	}
	
	/*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 *                       Old code ! Need to check later !
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	@Override
	public Point intersection(Point p, Vector3D rayonIncident) {
		//On commence par tester l'intersection avec la base en creant une instance du PlanInfini la contenant et 
		//en testant l'appartenance a la base d'un eventuel point d'intersection entre ce plan et le rayon incident 
		//issu de p. 
		Point inter1          = null;
		Vector3D[] baseAdaptee = this.getAdaptedBase();		
		Vector3D b0 = baseAdaptee[0];
		Vector3D b1 = baseAdaptee[1], b2 = baseAdaptee[2];
		
		PlaneSurface planBase = new PlaneSurface(baseCenter,baseCenter.translate(b0),baseCenter.translate(b1),
				Texture.defaultTexture);
		
		Point intersection = planBase.intersection(p,rayonIncident);
		if (intersection != null && intersection.distance(baseCenter) <= Vector3D.epsilon + baseRay) 
			inter1 = intersection;
		else 
			return null;		
				
		/* On passe a une recherche sur la surface laterale (meme si on a deja trouve un point, rien n'assure qu'il est le
		 * plus proche de p). On va d'abord transformer dans une base adaptee l'equation de la droite parametrisee par 
		 * (rayon,p). On resout alors une equation du second degre menant a une solution (ou non) dans la base adaptee, qu'il
		 * faut enfin replacer dans le repere canonique de l'espace.
		 */
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
		double h  = vertice.distance(baseCenter);
		//L'equation prend une forme plus simple avec le changement de variables (x,y,z)->(x_,y_,z_) et 
		double x_ = x/baseRay, y_ = y/baseRay, z_ = z/h;
		double a_ = a/baseRay, b_ = b/baseRay, c_ = c/h;
		double lambda = (x_*x_ + y_*y_) - (1-z_)*(1-z_);
		double mu     = 2*(x_*a_ + y_*b_ + (1-z_)*c_);
		double nu     = a_*a_ + b_*b_ - c_*c_; 
		
		double delta  = mu*mu - 4*lambda*nu; 
		Point i1 = null, i2 = null;

		if (delta >= 0)
		{			
			double t1 = (- mu + Math.sqrt(delta))/(2*nu);
			double t2 = (- mu - Math.sqrt(delta))/(2*nu);
			
			i1        = new Point(x + t1*a,y + t1*b,z + t1*c); 
			i2        = new Point(x + t2*a,y + t2*b,z + t2*c);			
				
			//On verifie, dans une base adaptee au cone, que i1 et i2 sont bien des points du cone de hauteur h (on
			//sait qu'ils sont des points du cone de meme axe et de meme rayon)
			i1 = i1.getZ() <= h + Vector3D.epsilon && (Math.abs(i1.getZ()) <= Vector3D.epsilon || i1.getZ() > 0) ? i1 : null;			
			i2 = i2.getZ() <= h + Vector3D.epsilon && (Math.abs(i2.getZ()) <= Vector3D.epsilon || i2.getZ() > 0) ? i2 : null;
			
			//S'ils appartiennent bien au cone de hauteur h, on replace i1 et i2 dans la base canonique de l'espace
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
		return null;
	}
	
	/*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 *                       Old code ! Need to check later !
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	public float[] Ka(Point p) {
		int repeat   = texture.getPatternRepeat();
		Vector3D[] b  = getAdaptedBase();
		
		/* On va mapper separement la base et la surface laterale :
		 * 
		 * - Dans les deux cas, on calcule les coordonnees du point M defini comme dans la methode de calcul de la normale
		 * afin d'obtenir l'angle theta entre la projection de vect sur le repere de la base du cone et le premier vecteur 
		 * de ce repere.
		 *
		 * - Pour un point de la base, on doit connaitre en plus de theta que la distance de p au centre de la base.
		 *  
		 * - Pour un point de la surface laterale, on utilise egalement theta mais il faut en plus calculer la distance de p
		 * au sommet S.
		 */		
		double  h   = vertice.distance(baseCenter);	    
	    //On ecarte le sommet qui est un cas particulier
	    if (p.equals(vertice))
	    	return texture.Ka(0,0);
	    
	    double  z     = new Vector3D(baseCenter,p).dot(b[2]);	    
	    double  d     = vertice.distance(p);	  
	    Vector3D vect  = (new Vector3D(vertice,p)).multScal(1/d);
	    Point   M     = p.translate(vect.multScal(d*(h/(h-z)-1)));
	    Vector3D pvect = new Vector3D(baseCenter,M);	    
	    double  xm    = pvect.dot(b[0]);
	    double  ym    = pvect.dot(b[1]);
	    double theta  = ym < 0 ? Math.acos(xm/pvect.norm()) : Math.acos(xm/pvect.norm());
	    
	    if (Math.abs(z) <= Vector3D.epsilon)
	    {
	    	Vector3D w = new Vector3D(baseCenter,p);
	    	double x  = w.dot(b[0]);	    
	    	double y  = w.dot(b[1]);	
	    	double R  = (x*x + y*y)/(baseRay*baseRay);
	    	
	    	return texture.Ka((int) (repeat*theta/Math.PI*(texture.getWidth()-1)),(int) ((1-R)*texture.getHeight()));
	    }
	    else 
	    {
	    	double lmax = Math.sqrt(baseRay*baseRay + h*h);
	    	return texture.Ka((int) (repeat*theta/Math.PI*(texture.getWidth()-1)),
	    			(int) (p.distance(vertice)/lmax*texture.getHeight()));
	    }
	}

	@Override
	public boolean belongs(Point p) {	
		Vector3D[] b  = getAdaptedBase();
		Vector3D   v  = new Vector3D(baseCenter,p);		
		
		double     x  = v.dot(b[0]);
		double     y  = v.dot(b[1]);
		double     z  = v.dot(b[2]);
		double     h  = vertice.distance(baseCenter);
		
		//Cheap test to eliminate trivial cases faster
		if (Math.abs((x*x + y*y)/baseRay/baseRay - (1-z/h)*(1-z/h)) <= Vector3D.epsilon)
			return true;
		
		PlaneSurface plan = new PlaneSurface(baseCenter.translate(b[0]),baseCenter.translate(b[1]),baseCenter,
					Texture.defaultTexture);
		return x*x + y*y <= Vector3D.epsilon + baseRay*baseRay && plan.belongs(p); 	
	}
	
	@Override
	protected void checkedResize(double factor) {
		Point   c  = getCenter();		
		Vector3D v = new Vector3D(c,vertice);
		baseCenter = c.translate(v.multScal(-factor));
		vertice    = c.translate(v.multScal(factor));
		baseRay    = baseRay*factor;
	}
	
	@Override
	public int getPriority() {		
		return -2;
	}
	
	@Override
	public String toString() {
		return  "      CONE\n"                        + "\n" +
				"      Vertice :      " + vertice     + "\n" +
				"      Base center :  " + baseCenter  + "\n" +
				"      Base ray :     " + baseRay     + "\n" +
				"\n" + super.toString();				
	}
	
	@Override
	public Element toXML() {	
		Element result = new Element("Cone");
		result.addContent(new XMLDouble("baseRay",baseRay));
		result.addContent(new XMLVector("vertice",vertice));
		result.addContent(new XMLVector("baseCenter",baseCenter));
		return result;
	}
	
	@Override
	public void translate(Vector3D v) {
		baseCenter = baseCenter.translate(v);
		vertice    = vertice.translate(v);
	}
	
	public Point getBaseCenter() {
		return baseCenter.clone();
	}
	
	public Point getVertice() {
		return vertice.clone();
	}
	
	public double getBaseRay() {
		return baseRay;
	}		
	
	public Point getCenter() {
		Vector3D v = new Vector3D(baseCenter,vertice).multScal(0.5);
		return baseCenter.translate(v);
	}
	
	public Object3D clone()	{
		return new Cone(baseCenter,vertice,baseRay,texture.clone());
	}
	
	public void copy(Object clone) {
		Cone cone = (Cone) clone;	
		baseCenter.setLocation(cone.baseCenter);
		vertice.setLocation(cone.vertice);
		baseRay = cone.baseRay;
		texture = cone.getTexture();
		ex      = new Vector3D(cone.getAdaptedBase()[0]);
	}
}

package objects;

import org.jdom2.Element;

import XML.basicTypes.XMLVector;
import utils.math.CustomMath;
import utils.math.Point;
import utils.math.Vector3D;

public class Cube extends MeshedObject 
{
	private Point O,X,Y,Z;
	
	/**
	 * 
	 * @param center
	 * @param a
	 * @param texture
	 * @throws IllegalArgumentException if a <= 0
	 */
	public Cube (Point center, double a, Texture texture) {
		super(texture);
		
		if (a <= 0)
			throw new IllegalArgumentException();
		
		Vector3D[] canonicBase = CustomMath.canonicBase;
		Vector3D   Ox          = canonicBase[0].multScal(a/2);
		Vector3D   Oy          = canonicBase[1].multScal(a/2);
		Vector3D   Oz          = canonicBase[2].multScal(a/2);		
		
		O                      = center.translate(Ox.opposed()).translate(Oy.opposed()).translate(Oz.opposed());
		X                      = center.translate(Ox)          .translate(Oy.opposed()).translate(Oz.opposed());
		Y                      = center.translate(Ox.opposed()).translate(Oy)          .translate(Oz.opposed());
		Z                      = center.translate(Ox.opposed()).translate(Oy.opposed()).translate(Oz);
		generateFaces();
	}
	
	public Cube(Point O, Point X, Point Y, Point Z, Texture t) {
		super(t.clone());
		this.O = new Point(O);
		this.X = new Point(X);
		this.Y = new Point(Y);
		this.Z = new Point(Z);
	}

	@Override
	public void rotateXYZ (double u, double v, double w) {
		Vector3D t = new Vector3D(getCenter(),new Point(0,0,0));		
		O = O.translate(t).rotateX(u).rotateY(v).rotateZ(w).translate(t.opposed());
		X = X.translate(t).rotateX(u).rotateY(v).rotateZ(w).translate(t.opposed());
		Y = Y.translate(t).rotateX(u).rotateY(v).rotateZ(w).translate(t.opposed());
		Z = Z.translate(t).rotateX(u).rotateY(v).rotateZ(w).translate(t.opposed());

		generateFaces();
	}
	
	@Override
	public void rotateX (double u) {
		rotateXYZ(u,0,0);
	}

	@Override
	public void rotateY (double u) {
		rotateXYZ(0,u,0);
	}

	@Override
	public void rotateZ( double u) {
		rotateXYZ(0,0,u);
	}
	
	@Override
	public void generateFaces() {		
		Vector3D u  = new Vector3D(O,X);
		Vector3D v  = new Vector3D(O,Y);
		
		Point A     = Z.translate(v);
		Point B     = Z.translate(u);
		Point C     = A.translate(u);
		Point D     = Y.translate(u);	
		
		faces    = new Face[6];
		faces[0] = new QuadFace(O,X,Z,texture);
		faces[1] = new QuadFace(O,Y,Z,texture);
		faces[2] = new QuadFace(Y,A,D,texture);
		faces[3] = new QuadFace(D,X,C,texture);
		faces[4] = new QuadFace(Z,B,A,texture);
		faces[5] = new QuadFace(O,X,Y,texture);
	}	
	
	public double getSideLength() {
		return O.distance(X);
	}
	
	public String toString() {
		return  "      CUBE\n"           + "\n" +
				"      Vertice 1 :  " + O + "\n" +
				"      Vertice 2 :  " + X + "\n" +
				"      Vertice 3 :  " + Y + "\n" +
				"      Vertice 4 :  " + Z + "\n" +
				"\n" + super.toString();				
	}
	
	@Override
	public Element toXML() {	
		Element result = new Element("Cube");
		result.addContent(new XMLVector("O",O));
		result.addContent(new XMLVector("X",X));
		result.addContent(new XMLVector("Y",Y));
		result.addContent(new XMLVector("Z",Z));
		return result;
	}

	public Object3D getWrappingObject() {		
		return new Sphere(getCenter(),Math.sqrt(3)*getSideLength()/2,Texture.defaultTexture);
	}

	public void translate(Vector3D v) {
		O = O.translate(v);
		X = X.translate(v);
		Y = Y.translate(v);
		Z = Z.translate(v);
		generateFaces();
	}
	
	public Point[] getPoints() {
		return new Point[] { O,X,Y,Z };
	}
	
	public Point getCenter() {
		double d   = this.getSideLength()/2;
		Vector3D u = new Vector3D(O,X);
		Vector3D v = new Vector3D(O,Y);
		Vector3D w = new Vector3D(O,Z);
		
		u = u.multScal(d/u.norm());
		v = v.multScal(d/v.norm());
		w = w.multScal(d/w.norm());	

		return O.translate(u).translate(v).translate(w);
	}
	
	public Object3D clone()	{
		return new Cube(O,X,Y,Z,texture);
	}	

	public void copy(Object clone) {
		Cube cube   = (Cube) clone;	
		Point[] pts = cube.getPoints();
		O           = pts[0];
		X           = pts[1];
		Y           = pts[2];
		Z           = pts[3];
		texture     = cube.getTexture().clone();
		generateFaces();
	}
	
	public Vector3D[] getAdaptedBase() {
		return new Vector3D[] { new Vector3D(O,X),new Vector3D(O,Y),new Vector3D(O,Z) };
	}
	
	@Override
	protected void checkedResize(double factor) {
		super.resize(factor);
		
		Point   C   = getCenter();
		double  a   = O.distance(X);
		Vector3D e1 = (new Vector3D(O,X)).multScal(1/a);
		Vector3D e2 = (new Vector3D(O,Y)).multScal(1/a);
		Vector3D e3 = (new Vector3D(O,Z)).multScal(1/a);
		a           = a*factor;
		
		O = C.translate(e1.multScal(-a/2)).translate(e2.multScal(-a/2)).translate(e3.multScal(-a/2));
		X = C.translate(e1.multScal(a/2)).translate(e2.multScal(-a/2)).translate(e3.multScal(-a/2));
		Y = C.translate(e1.multScal(-a/2)).translate(e2.multScal(a/2)).translate(e3.multScal(-a/2));
		Z = C.translate(e1.multScal(-a/2)).translate(e2.multScal(-a/2)).translate(e3.multScal(a/2));
		generateFaces();
	}	
	
	@Override
	public boolean belongs(Point p)	{
		Vector3D   v = new Vector3D(getCenter(),p);
		Vector3D[] b = getAdaptedBase();		
		
		double x    = v.dot(b[0]);
		double y    = v.dot(b[1]);
		double z    = v.dot(b[2]);
		double a    = O.distance(X) + Vector3D.epsilon;
		
		return Math.abs(x) <= a/2 && Math.abs(y) <= a/2 && Math.abs(z) <= a/2;
	}
}

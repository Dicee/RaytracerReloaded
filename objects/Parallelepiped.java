package objects;

import java.util.Arrays;

import org.jdom2.Element;

import XML.basicTypes.XMLVector;
import java.util.List;
import static utils.math.CustomMath.*;
import utils.math.CustomMath;
import utils.math.Point;
import utils.math.Vector3D;

public class Parallelepiped extends MeshedObject {
	protected Point O, X, Y, Z;
	
	public Parallelepiped(Point center, double height, double width, double depth, 
			double alpha, double beta, Texture texture) {
		super(texture);
		
		boolean b0 = compare(height,0) == 0 || compare(width,0) == 0 || compare(depth,0) == 0;
		boolean b1 = alpha < 0 || beta < 0;
		
		if (b0 || b1)
			throw new IllegalArgumentException();
		
		Vector3D[] canonicBase = CustomMath.canonicBase;
		Vector3D   ex          = canonicBase[0].multScal(Math.cos(alpha)).sum(
									canonicBase[2].multScal(Math.sin(alpha))).multScal(width/2);
		Vector3D   ey          = canonicBase[2].multScal(height/2);
		Vector3D   ez          = canonicBase[0].multScal(Math.cos(beta)).sum(
									canonicBase[1].multScal(Math.sin(beta))).multScal(depth/2);
		
		O                      = center.translate(ex.opposed()).translate(ey.opposed()).translate(ez.opposed());
		X                      = center.translate(ex)          .translate(ey.opposed()).translate(ez.opposed());
		Y                      = center.translate(ex.opposed()).translate(ey)          .translate(ez.opposed());
		Z                      = center.translate(ex.opposed()).translate(ey.opposed()).translate(ez);
		generateFaces();
	}	
	
	public Parallelepiped(Point O, Point X, Point Y, Point Z, Texture texture) {
		super(texture);
		
		List<Point> points = Arrays.asList(O,X,Y,Z);
		for (int i=0 ; i<points.size() ; i++) 
			for (int j=i+1 ; j<points.size() ; j++)
				if (points.get(i).equals(points.get(j)))
					throw new IllegalArgumentException("All the given points must be distinct");
		this.O = O;
		this.X = X;
		this.Y = Y;
		this.Z = Z;
		generateFaces();
	}
	
	@Override
	public void copy(Object3D clone) {
		Parallelepiped p = (Parallelepiped) clone;	
		O                = new Point(p.O);
		X                = new Point(p.X);
		Y                = new Point(p.Y);
		Z                = new Point(p.Z);
		texture          = p.texture.clone();
		generateFaces();
	}
	
	public Point[] getPoints() {
		return new Point[] { O,X,Y,Z };
	}
	
	@Override
	public void generateFaces() {
		Vector3D[] base = getAdaptedBase();
		Point A         = this.Z.translate(base[1]);
		Point C         = this.Z.translate(base[0]);
		Point D         = this.Y.translate(base[0]);
		
		faces           = new Face[6];
		faces[0]        = new QuadFace(this.O,this.Z,X,texture);
		faces[1]        = new QuadFace(A,this.Y,this.Z,texture);
		faces[2]        = new QuadFace(this.Y,A,D,texture);
		faces[3]        = new QuadFace(X,C,D,texture);
		faces[4]        = new QuadFace(this.Z,C,A,texture);		
		faces[5]        = new QuadFace(X,D,this.O,texture);
	}

	@Override
	public Object3D getWrappingObject() {	
		double d = Math.max(Math.max(O.distance(X),O.distance(Y)),O.distance(Z));
		return new Sphere(getCenter(),3*Math.sqrt(3)*d/2,Texture.defaultTexture);
	}
	
	@Override
	public void rotateXYZ (double u, double v, double w) {
		Vector3D t = new Vector3D(getCenter(),new Point());
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
	public void translate(Vector3D v) {
		O = O.translate(v);
		X = X.translate(v);
		Y = Y.translate(v);
		Z = Z.translate(v);
		generateFaces();
	}
	
	@Override
	public String toString() {
		return  "      PARALLELEPIPEDE\n"           + "\n" +
				"      Sommet 1 :  " + O + "\n" +
				"      Sommet 2 :  " + X + "\n" +
				"      Sommet 3 :  " + Y + "\n" +
				"      Sommet 4 :  " + Z + "\n" +
				"\n" + super.toString();				
	}
	
	@Override
	public Element toXML() {	
		Element result = new Element("Parallelepiped");
		result.addContent(new XMLVector("O",O));
		result.addContent(new XMLVector("X",X));
		result.addContent(new XMLVector("Y",Y));
		result.addContent(new XMLVector("Z",Z));
		return result;
	}

	@Override
	public Point getCenter() {
		Vector3D u0 = new Vector3D(O,X);
		Vector3D v0 = new Vector3D(O,Y);
		Vector3D w0 = new Vector3D(O,Z);
		
		u0 = u0.multScal(O.distance(X)/2/u0.norm());
		v0 = v0.multScal(O.distance(Y)/2/v0.norm());
		w0 = w0.multScal(O.distance(Z)/2/w0.norm());		
		
		return O.translate(u0).translate(v0).translate(w0);
	}

	@Override
	public Object3D clone()	{
		return new Parallelepiped(O,X,Y,Z,texture);
	}	

	@Override
	public Vector3D[] getAdaptedBase() {
		return new Vector3D[] { new Vector3D(O,X),new Vector3D(O,Y),new Vector3D(O,Z) };
	}
	
	@Override
	protected void checkedResize(double facteur) {
		Point   C   = getCenter();
		double  a   = O.distance(X);
		double  b   = O.distance(this.Y);
		double  c   = O.distance(Z);
		Vector3D e1 = (new Vector3D(O,X)).multScal(1/a);
		Vector3D e2 = (new Vector3D(O,this.Y)).multScal(1/b);
		Vector3D e3 = (new Vector3D(O,Z)).multScal(1/c);
		a          *= facteur;
		b          *= facteur;
		c          *= facteur;
		
		O = C.translate(e1.multScal(-a/2)).translate(e2.multScal(-b/2)).translate(e3.multScal(-c/2));
		X = C.translate(e1.multScal( a/2)).translate(e2.multScal(-b/2)).translate(e3.multScal(-c/2));
		this.Y = C.translate(e1.multScal(-a/2)).translate(e2.multScal( b/2)).translate(e3.multScal(-c/2));
		Z = C.translate(e1.multScal(-a/2)).translate(e2.multScal(-b/2)).translate(e3.multScal( c/2));
		generateFaces();
	}	
	
	@Override
	public String getName() {
		return "parallelepiped";
	}
}

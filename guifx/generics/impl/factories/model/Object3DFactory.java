package guifx.generics.impl.factories.model;

import objects.Cone;
import objects.Cube;
import objects.Cylinder;
import objects.Sphere;
import objects.Texture;
import utils.math.Point;
import utils.math.Vector3D;

public final class Object3DFactory {	
	public static Sphere createSphere(double scale, Point center, double u, double v, double w, Texture t) {
		Sphere result = new Sphere(center,scale,t);
		result.rotateXYZ(u,v,w);
		return result;
	}
	
	public static Cube createCube(double scale, Point center, double u, double v, double w, Texture t) {
		Cube result = new Cube(center,scale,t);
		result.rotateXYZ(u,v,w);
		return null;
	}
	
	public static Cylinder createCylinder(double scale, Point center, double u, double v, double w, 
			double heightRayRatio, Texture t) {
		Cylinder result = new Cylinder(scale,scale/heightRayRatio,t);
		result.translate(new Vector3D(result.getCenter(),center));
		result.rotateXYZ(u,v,w);
		return null;
	}
	
	public static Cone createCone(double scale, Point center, double u, double v, double w, 
			double heightRayRatio, Texture t) {
		Cone result = new Cone(center,scale,scale/heightRayRatio,t);
		result.rotateXYZ(u,v,w);
		return null;
	}
}

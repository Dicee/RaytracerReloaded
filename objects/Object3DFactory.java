package objects;

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
		return result;
	}
	
	public static Cylinder createCylinder(double scale, Point center, double u, double v, double w, 
			double heightRayRatio, Texture t) {
		Cylinder result = new Cylinder(scale,scale/heightRayRatio,t);
		result.translate(new Vector3D(result.getCenter(),center));
		result.rotateXYZ(u,v,w);
		return result;
	}
	
	public static Cone createCone(double scale, Point center, double u, double v, double w, 
			double heightRayRatio, Texture t) {
		Cone result = new Cone(center,scale,scale/heightRayRatio,t);
		result.rotateXYZ(u,v,w);
		return result;
	}
	
	public static PlaneSurface createInfiniteSurface(double z, double u, double v, double w, Texture t) {
		PlaneSurface result = new PlaneSurface(z,t);
		result.rotateXYZ(u,v,w);
		return result;
	}
	
	public static Parallelepiped createParallelepiped(double scale, Point center, double u, double v, double w, 
			double heightRatio, double depthRatio, double alpha, double beta, Texture t) {
		Parallelepiped result = new Parallelepiped(center,scale,scale/heightRatio,scale/depthRatio,alpha,beta,t);
		result.rotateXYZ(u,v,w);
		return result;
	}
	
	public static QuadFace createQuadFace(double scale, Point center, double u, double v, double w, 
			double heightRatio, double alpha, Texture t) {
		QuadFace result = new QuadFace(center,scale,scale/heightRatio,alpha,t);
		result.rotateXYZ(u,v,w);
		return result;
	}
}

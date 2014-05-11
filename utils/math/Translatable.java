package utils.math;

import utils.math.Point;
import utils.math.Vector3D;

public interface Translatable {
	public void translate(Vector3D v);	
	public Point getCenter();
}

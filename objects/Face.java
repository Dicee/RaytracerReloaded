package objects;

import utils.math.Point;

/**
 * This class has no use except giving a clearer semantic to the relations between QuadFace, TriFace
 * and PlaneSurface.
 * @author David Courtinot
 */

public abstract class Face extends PlaneSurface {
	public Face(Point a, Point b, Point c, Texture texture) {
		super(a,b,c,texture);
	}
}

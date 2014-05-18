package XML.basicTypes;

import org.jdom2.Attribute;
import org.jdom2.Element;

import utils.math.Point;
import utils.math.Vector3D;

public class XMLVector extends Element {
	private static final long	serialVersionUID	= 1L;
	
	public XMLVector(String name, double x, double y, double z) {
		super("Vector");
		setAttribute(new Attribute("name",name));
		setAttribute(new Attribute("X",x + ""));
		setAttribute(new Attribute("Y",y + ""));
		setAttribute(new Attribute("Z",z + ""));
	}
	
	public XMLVector(String name, Point p) {
		this(name,p.x,p.y,p.z);
	}
	
	public XMLVector(String name, Vector3D v) {
		this(name,v.x,v.y,v.z);
	}

	public static Point xmlToPoint(Element elt) {
		double x = Double.parseDouble(elt.getAttributeValue("X"));
		double y = Double.parseDouble(elt.getAttributeValue("Y"));
		double z = Double.parseDouble(elt.getAttributeValue("Z"));
		return new Point(x,y,z);
	}
}

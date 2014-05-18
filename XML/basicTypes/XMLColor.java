package XML.basicTypes;

import java.awt.Color;

import org.jdom2.Attribute;
import org.jdom2.Element;

public class XMLColor extends Element {
	private static final long	serialVersionUID	= 1L;
	
	public XMLColor(String name, float r, float g, float b) {
		super("Color");
		setAttribute(new Attribute("name",name));
		setAttribute(new Attribute("R",r + ""));
		setAttribute(new Attribute("G",g + ""));
		setAttribute(new Attribute("B",b + ""));
	}
	
	public XMLColor(String name, Color c) {
		this(name,c.getRed(),c.getGreen(),c.getBlue());
	}
	
	public XMLColor(String name, float[] rgb) {
		this(name,new Color(rgb[0],rgb[1],rgb[2]));
	}
	
	public static Color xmlToColor(Element elt) {
		float R = Float.parseFloat(elt.getAttributeValue("R"));
		float G = Float.parseFloat(elt.getAttributeValue("G"));
		float B = Float.parseFloat(elt.getAttributeValue("B"));
		return new Color(R,G,B);
	}
}

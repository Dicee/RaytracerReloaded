package scene;

import java.awt.Color;

import org.jdom2.Element;

import utils.math.Point;
import utils.math.Translatable;
import utils.math.Vector3D;
import XML.XMLable;
import XML.basicTypes.XMLColor;
import XML.basicTypes.XMLVector;
import utils.Copiable;

public class Source implements XMLable, Translatable, Cloneable, Copiable<Source> {
	public Color color;
	public Point pos;

	public Source(Color color, Point pos) {
		this.color = color;
		this.pos   = pos;
	}	
		
	@Override
	public Point getCenter() {
		return pos;
	}

	@Override
	public void translate(Vector3D v) {
		pos.translate(v);		
	}	
	
	@Override
	public void copy(Source s) {
		pos.setLocation(s.pos);
		color = new Color(s.color.getRed(),s.color.getGreen(),s.color.getBlue());
	}
	
	@Override
	public Source clone() {
		return new Source(new Color(color.getRed(),color.getGreen(),color.getBlue()),pos.clone());
	}

	@Override
	public Element toXML() {
		Element result = new Element("Source");
		result.addContent(new XMLVector("pos",pos));
		result.addContent(new XMLColor("color",color));
		return result;
	}
}


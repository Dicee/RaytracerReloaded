package scene;

import javafx.scene.paint.Color;

import org.jdom2.Element;

import utils.Copiable;
import utils.Hidable;
import utils.math.Point;
import utils.math.Translatable;
import utils.math.Vector3D;
import XML.XMLable;
import XML.basicTypes.XMLColor;
import XML.basicTypes.XMLVector;

public class Source implements XMLable, Translatable, Cloneable, Copiable<Source>, Hidable {
	public Color color;
	public Point pos;
	private boolean shown = true;

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
		color = s.color;
	}
	
	@Override
	public Source clone() {
		return new Source(color,pos.clone());
	}

	@Override
	public Element toXML() {
		Element result = new Element("Source");
		result.addContent(new XMLVector("pos",pos));
		result.addContent(new XMLColor("color",color));
		return result;
	}

	@Override
	public void setShown(boolean shown) {
		this.shown = shown;
	}

	@Override
	public boolean isShown() {
		return shown;
	}
}


package scene;

import java.awt.Color;

import utils.math.Point;
import utils.math.Translatable;
import utils.math.Vector3D;
import XML.XMLable;

public class Source implements XMLable, Translatable {
	public Color color;
	public Point pos;

	public Source(Color color, Point pos) {
		this.color = color;
		this.pos   = pos;
	}
	
	@Override
	public String toXML(String align, String attributes)	{		
		float R = (float) color.getRed() / (float) 255;
		float V = (float) color.getGreen() / (float) 255;
		float B = (float) color.getBlue() / (float) 255;
		
		return align + "<Source>"                                               + "\n" +
			   align + "\t" + pos.getX() + " " + pos.getY() + " "  + pos.getZ() + "\n" + 
			   align + "\t" + R          + " " + V    +              " "  + B   + "\n" +
			   align + "</Source>";
	}
	
	public String toXML(String align) {		
		return toXML(align,"");
	}
	
	public Point getCenter() {
		return pos;
	}

	public void translate(Vector3D v) {
		pos.translate(v);		
	}	
	
	public void copy(Object clone) {
		Source s = (Source) clone;			
		pos.setLocation(s.pos);
		color = new Color(s.color.getRed(),s.color.getGreen(),s.color.getBlue());
	}
	
	public Source clone() {
		return new Source(new Color(color.getRed(),color.getGreen(),color.getBlue()),pos.clone());
	}
}


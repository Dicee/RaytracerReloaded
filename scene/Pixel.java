package scene;
import java.awt.Color;

import utils.math.Point;

public class Pixel {
	public Point pos;
	public Color color;
	
	public Pixel(Point pt, Color color)	{		
		this.pos   = pt;
		this.color = color;
	}	
	
	public Color sumIntensities(Color c) {
		float R1 = ((float) color.getRed())/255; 
		float V1 = ((float) color.getGreen())/255; 
		float B1 = ((float) color.getBlue())/255; 
		
		float R2 = ((float) c.getRed())/255; 
		float V2 = ((float) c.getGreen())/255; 
		float B2 = ((float) c.getBlue())/255; 		
		 
		return new Color(1-(1-R1)*(1-R2),
						 1-(1-V1)*(1-V2),
						 1-(1-B1)*(1-B2));		 
	 }
}

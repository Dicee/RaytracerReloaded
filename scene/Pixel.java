package scene;
import javafx.scene.paint.Color;

import utils.math.Point;

public class Pixel {
	public Point pos;
	public Color color;
	
	public Pixel(Point pt, Color color)	{		
		this.pos   = pt;
		this.color = color;
	}	

    public static Color colorSynthesis(Color c1, Color c2) {
        double R1 =  c1.getRed(); 
		double V1 =  c1.getGreen(); 
		double B1 =  c1.getBlue(); 
		
		double R2 =  c2.getRed(); 
		double V2 =  c2.getGreen(); 
		double B2 =  c2.getBlue(); 		
		 
		return new Color(1-(1-R1)*(1-R2),
						 1-(1-V1)*(1-V2),
						 1-(1-B1)*(1-B2),
                         1);
    }
    
    public static Color colorSynthesis(Color... colors) {
        if (colors.length == 0)
            throw new IllegalArgumentException();
        
       Color result = colors[0];
       for (int i=1 ; i<colors.length ; i++)
           result = colorSynthesis(result,colors[i]);
		return result;
    }
    
    public static int toRGB(Color c) {
        return (new java.awt.Color((float) c.getRed(),(float) c.getGreen(),(float) c.getBlue(),(float) c.getOpacity())).getRGB();
    }
}

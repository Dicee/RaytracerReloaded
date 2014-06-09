package objects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import org.jdom2.Attribute;
import org.jdom2.Element;
import utils.math.CustomMath;

public class AdvancedTexture extends Texture {
	protected BufferedImage texture;
	protected String path;
	
	public AdvancedTexture(double indice, float brillance, Color reflectance, Color Kr, Color Kt, 
			File image) throws IOException {
		super(indice,brillance,reflectance,Kr,Kt);
		this.path    = image.getAbsolutePath();
		this.texture = ImageIO.read(image);
	}	

	@Override
	protected Element getKaXML() {
		Element elt = new Element("Color");
		elt.setAttribute(new Attribute("name","Ka"));
		elt.setAttribute(new Attribute("url",path));
		return elt;
	}
	
	@Override
	public float[] Ka(int u, int v, boolean adapt) {	
		int w = texture.getWidth(), h = texture.getHeight();
		int i = CustomMath.mod(u,(adapt ? w - 1 : 0) + w);
		int j = CustomMath.mod(v,(adapt ? h - 1 : 0) + h);
		 
		int[] coords;
		if (i < w && j < h) 	    			
			coords = new int[] { i,j };    			
		else if (i < w && j >= h)
			coords = new int[] { i,2*h-j-1 };
		else if (i >= w && j < h)
			coords = new int[] { 2*w-i-1,j };
		else
			coords = new int[] { 2*w-i-1,2*h-j-1 };	
		  
		int rgb = texture.getRGB(coords[0],coords[1]);
		return new java.awt.Color(rgb).getRGBColorComponents(null);
  }

	@Override
	public int getHeight() {
		return texture.getHeight();
	}

	@Override
	public int getWidth() {
		return texture.getWidth();
	}
	
	@Override
	public String getName() {
		return "advancedTexture";
	}
}

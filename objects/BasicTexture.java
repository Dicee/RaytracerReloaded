package objects;

import XML.basicTypes.XMLColor;
import javafx.scene.paint.Color;
import org.jdom2.Element;

public class BasicTexture extends Texture {
	protected float[] Ka;

	public BasicTexture(double indice, float brillance, Color reflectance, Color Kr, Color Kt, Color Ka) {
		super(indice,brillance,reflectance,Kr,Kt);
		float[] arr = colorToArr(Ka);
		if (!testParam(arr,0,1)) 
			throw new IllegalArgumentException();		
		this.Ka = roundInIntervalle(arr,0,1);
	}

	@Override
	protected Element getKaXML() {
		return new XMLColor("Ka",Ka);
	}
	
	@Override
	public float[] Ka(int u, int v, boolean adapt) {	
		return Ka;	  	   
	}
	
	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getWidth() {
		return 0;
	}
	
	@Override
	public String getName() {
		return "basicTexture";
	}
}

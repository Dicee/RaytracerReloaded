package objects;

import XML.XMLable;
import XML.basicTypes.XMLColor;
import XML.basicTypes.XMLFloat;
import java.util.Arrays;
import javafx.scene.paint.Color;
import org.jdom2.Element;

public abstract class Texture implements XMLable {
	public static final float epsilon = (float) 0.01;
	public static final Texture defaultTexture;
	
	static {
	  Color c0   = new Color(0.5,0.5,0.5,0);
	  Color c1   = new Color(0,0,1,0);
	  defaultTexture = new BasicTexture(1,5,c0,c0,c0,c1);
	}
	
	protected double indice;
	protected float brillance;
	protected float[] reflectance; 
	protected float[] Kr;
	protected float[] Kt;
	
	public Texture(double indice, float brillance, Color reflectance, Color Kr, Color Kt) {
		this(indice,brillance,toArr(reflectance),toArr(Kr),toArr(Kt));
	}
	
	public Texture(double indice, float brillance, float[] reflectance, float[] Kr, float[] Kt) {
		if (indice < 1 || !intervalle(brillance,5,300) || !testParam(reflectance,0,1) || 
				!testParam(Kr,0,1) || !testParam(Kt,0,1)) 
			throw new IllegalArgumentException();		
     
		this.Kr          = roundInIntervalle(Kr,0,1);
		this.Kt          = roundInIntervalle(Kt,0,1);
		this.indice      = indice;
		this.brillance   = roundInIntervalle(brillance,5,300);
		this.reflectance = roundInIntervalle(reflectance,0,1);
	}
	 
	protected final boolean testParam(float[] param, float inf, float sup) {
		return param.length == 3 && intervalle(param[0],inf,sup) && 
			intervalle(param[1],inf,sup) && intervalle(param[2],inf,sup);
	 }

	protected final boolean intervalle(float value, float lowerBound, float upperBound) {
		return value >= lowerBound - epsilon && value <= upperBound + epsilon;
	}
  
	protected final float[] roundInIntervalle(float[] arr, float inf, float sup) {
		return new float[] { roundInIntervalle(arr[0], inf, sup),
							 roundInIntervalle(arr[1], inf, sup),
							 roundInIntervalle(arr[2], inf, sup) };
	}
	
	protected final float roundInIntervalle(float param, float inf, float sup) {
		return param <= inf ? inf : param >= sup ? sup : param;
	}
	
	protected static final Color toColor(float[] arr) {
		return new Color(arr[0],arr[1],arr[2],0);
	}
	
	protected static final float[] toArr(Color c) {
		return new float[] { (float) c.getRed(), (float) c.getGreen(), (float) c.getBlue() };
	}
	
	public double indice() {
		return indice;
	}

	public float brillance() {
		return brillance;
	}

	public float[] reflectance() {
		return reflectance;
	}
	
	public abstract float[] Ka(int u, int v, boolean adapt);
	
	public float[] Kr() {
		return Kr;
	}

	public float[] Kt() {
		return Kt;
	}
	
	public abstract int getHeight();
	public abstract int getWidth();
	
	@Override
	public Element toXML() {	
		Element result = new Element("Texture");
		result.addContent(new XMLFloat("indice",indice));
		result.addContent(new XMLFloat("brillance",brillance));
		result.addContent(new XMLColor("reflectance",reflectance));
		result.addContent(getKaXML());
		result.addContent(new XMLColor("Kr",Kr));
		result.addContent(new XMLColor("Kt",Kt));
		return result;
	}
	
	protected abstract Element getKaXML();
	public abstract String getName();
}

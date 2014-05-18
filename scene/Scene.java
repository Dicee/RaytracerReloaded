package scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import objects.Object3D;

public class Scene {
	private Color				ambientColor;
	private ArrayList<Source>	sources;
	private ArrayList<Object3D>	objects;
	private double				refraction;
	
	public Scene(Color ambientColor, double refraction)	{			
		this.sources      = new ArrayList<>();
		this.objects      = new ArrayList<>();
		this.refraction   = refraction;
		this.ambientColor = ambientColor;	
	}
	
	public Scene(Color ambientColor, double refraction, List<Object3D> obj, List<Source> sources) {
		this(ambientColor,refraction);
		this.objects.addAll(obj);
		this.sources.addAll(sources);
	}	
	
	public List<Object3D> getObjects() {
		return objects;
	}
	
	public List<Source> getSources() {
		return sources;
	}
	
	public Color getAmbientColor() {
		return ambientColor;
	}
	
	public void setAmbientColor(Color color)	{
		ambientColor = color;
	}
	
	public double getIndice() {
		return refraction;
	}
	
	/**
	 * 
	 * @param indice
	 * @throws IllegalArgumentException if indice < 1
	 */
	public void setIndice(double indice) {
		if (indice < 1)
			throw new IllegalArgumentException();
		refraction = indice;
	}

	public Scene clone() {
		List<Object3D> obj = new ArrayList<Object3D>();
		List<Source>   src = new ArrayList<Source>();
		
		for (Object3D object3d : obj)
			obj.add(object3d.clone()); 
		
		for (Source source : sources)
			src.add(source.clone());
			
		return new Scene(ambientColor,refraction,obj,src);
	}
}


package scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import objects.Object3D;

import org.jdom2.Element;

import XML.XMLable;
import XML.basicTypes.XMLColor;
import XML.basicTypes.XMLFloat;

public class Scene implements XMLable, Cloneable {
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
	
	public void setAmbientColor(Color color) {
		ambientColor = color;
	}
	
	public double getIndice() {
		return refraction;
	}
	
	public void setIndice(double indice) {
		if (indice < 1)
			throw new IllegalArgumentException();
		refraction = indice;
	}

	public Scene clone() {
		List<Object3D> obj = objects.stream().map(object -> object.clone()).collect(Collectors.toList());
		List<Source>   src = sources.stream().map(source -> source.clone()).collect(Collectors.toList());
		return new Scene(ambientColor,refraction,obj,src);
	}

	@Override
	public Element toXML() {
		Element result = new Element("Scene");
		result.addContent(new XMLFloat("indice",refraction));
		result.addContent(new XMLColor("ambient",ambientColor));
		
		Element objectsElt = new Element("Objects");
		objects.stream().forEachOrdered(object -> objectsElt.addContent(object.toXML()));
		
		Element sourcesElt = new Element("Sources");
		sources.stream().forEachOrdered(s -> sourcesElt.addContent(s.toXML()));
		
		result.addContent(objectsElt);
		result.addContent(sourcesElt);
		return result;
	}
}


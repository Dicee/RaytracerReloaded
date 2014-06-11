package scene;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import objects.Object3D;

import org.jdom2.Element;

import XML.XMLable;
import XML.basicTypes.XMLColor;
import XML.basicTypes.XMLFloat;
import XML.basicTypes.XMLTextureRef;
import objects.Texture;
import utils.Copiable;

public class Scene implements XMLable, Cloneable, Copiable<Scene> {
	private Color			ambientColor;
	private List<Source>	sources;
	private List<Object3D>	objects;
	private double			refractiveIndex;
	
	public Scene(Color ambientColor, double refraction)	{			
		this.sources      = new ArrayList<>();
		this.objects      = new ArrayList<>();
		this.ambientColor = ambientColor;	
        setIndice(refraction);
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
	
	public double getRefractiveIndex() {
		return refractiveIndex;
	}
	
	public final void setIndice(double indice) {
		if (indice < 1)
			throw new IllegalArgumentException();
		refractiveIndex = indice;
	}

	@Override
	public Scene clone() {
		List<Object3D> obj = objects.stream().map(object -> object.clone()).collect(Collectors.toList());
		List<Source>   src = sources.stream().map(source -> source.clone()).collect(Collectors.toList());
		return new Scene(ambientColor,refractiveIndex,obj,src);
	}

	@Override
	public Element toXML() {
		Element result = new Element("Scene");
		result.addContent(new XMLFloat("indice",refractiveIndex));
		result.addContent(new XMLColor("ambient",ambientColor));
		
		List<Texture> textures = objects.stream().map(object -> object.getTexture()).distinct().collect(Collectors.toList());
		textures.remove(Texture.DEFAULT_TEXTURE);
		
		Element objectsElt = new Element("Objects");
		objects.stream().forEachOrdered(object -> { 
			int index   = textures.indexOf(object.getTexture());
			Element elt = object.toXML();
			elt.addContent(index == -1 ? new XMLTextureRef() : new XMLTextureRef(index));
			objectsElt.addContent(elt); 
		});
		
		Element sourcesElt = new Element("Sources");
		sources.stream().forEachOrdered(s -> sourcesElt.addContent(s.toXML()));
		
		result.addContent(objectsElt);
		result.addContent(sourcesElt);
		return result;
	}

    @Override
    public void copy(Scene other) {
        refractiveIndex = other.refractiveIndex;
        ambientColor    = other.ambientColor;
        objects.clear();
        sources.clear();
        objects.addAll(other.getObjects());
        sources.addAll(other.getSources());
    }
    
    public final void setSources(List<Source> sources) {
		this.sources = new ArrayList<>(sources);
	}
    
    public final void setObjects(List<Object3D> objects) {
		this.objects = new ArrayList<>(objects);
	}
}
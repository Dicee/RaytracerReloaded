package XML;

import guifx.Project;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import objects.Object3D;
import objects.Texture;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import scene.Scene;
import scene.Screen;
import scene.Source;
import utils.math.Point;
import XML.basicTypes.XMLColor;
import XML.basicTypes.XMLVector;

public class XMLSceneLoader {
	private static XMLObjectLoader objectLoader = new XMLObjectLoader();
	
	public static Project load(File f) throws JDOMException, IOException {
		SAXBuilder     builder     = new SAXBuilder();
		Document       doc         = builder.build(f);
		Element        root        = doc.getRootElement(); 
		
		Element        sceneElt    = root.getChild("Scene");
		Element        texturesElt = root.getChild("Textures");
		Element        viewsElt    = root.getChild("Views");
		
		List<Screen>   screens     = xmlToScreens(viewsElt);
		List<Texture>  textures    = xmlToTextures(texturesElt);
		Scene          scene       = xmlToScene(sceneElt);
		
		return new Project(scene,screens,textures);
	}

	private static Scene xmlToScene(Element sceneElt) {
		float          indice  = Float.parseFloat(sceneElt.getChild("Float").getAttributeValue("value"));
		Color          ambient = XMLColor.xmlToColor(sceneElt.getChild("Color"));
		List<Object3D> objects = objectLoader.xmlToObjects(sceneElt.getChild("Objects"));
		List<Source>   sources = xmlToSources(sceneElt.getChild("Sources"));
		return new Scene(ambient,indice,objects,sources);
	}
	
	private static List<Source> xmlToSources(Element sourcesElt) {
		return mapAndCollect(elt -> {
			Point pos   = XMLVector.xmlToPoint(elt.getChild("Vector"));
			Color color = XMLColor.xmlToColor(elt.getChild("Color"));
			return new Source(color,pos);
		},sourcesElt,"Source");
	}

	private static List<Screen> xmlToScreens(Element viewsElt) {
		return mapAndCollect(elt -> {
			List<Element> points  = elt.getChildren("Vector");
			Point         A       = XMLVector.xmlToPoint(findByName(points,"A"));
			Point         B       = XMLVector.xmlToPoint(findByName(points,"B"));
			Point         C       = XMLVector.xmlToPoint(findByName(points,"C"));
			Point         pView   = XMLVector.xmlToPoint(findByName(points,"pointOfView"));
			int           density = Integer.parseInt(elt.getChild("Integer").getAttributeValue("value"));
			return new Screen(A,B,C,pView,density);
		},viewsElt,"Screen");
	}
	
	private static List<Texture> xmlToTextures(Element texturesElt) {
		return mapAndCollect(elt -> {
			List<Element> floats = elt.getChildren("Float");
			float indice         = Float.parseFloat(findByName(floats,"indice").getAttributeValue("value"));
			float brillance      = Float.parseFloat(findByName(floats,"brillance").getAttributeValue("value"));
			
			List<Element> colors = elt.getChildren("Color");
			Color reflectance    = XMLColor.xmlToColor(findByName(colors,"reflectance"));
			Color Ka             = XMLColor.xmlToColor(findByName(colors,"Ka"));
			Color Kr             = XMLColor.xmlToColor(findByName(colors,"Kr"));
			Color Kt             = XMLColor.xmlToColor(findByName(colors,"Kt"));
			return new Texture(indice,brillance,reflectance,Ka,Kr,Kt);
		},texturesElt,"Texture");
	}

	static <R> List<R> mapAndCollect(Function<Element,R> mapper, Element elt, String cname) {
		return elt.getChildren(cname).
					stream().map(mapper).
					collect(Collectors.toList());
	}
	
	private static Element findByName(List<Element> elts, String name) {
		Optional<Element> result = elts.stream().filter(elt -> elt.getName().equals(name)).findFirst();
		return result.isPresent() ? result.get() : null;
	}
}

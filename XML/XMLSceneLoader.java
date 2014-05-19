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
import static XML.basicTypes.XMLVector.xmlToPoint;
import static XML.basicTypes.XMLColor.xmlToColor;
import static XML.XMLObjectLoader.xmlToObjects;

public class XMLSceneLoader {
	public static Project load(File f) throws JDOMException, IOException {
		SAXBuilder     builder     = new SAXBuilder();
		Document       doc         = builder.build(f);
		Element        root        = doc.getRootElement(); 
		
		Element        sceneElt    = root.getChild("Scene");
		Element        texturesElt = root.getChild("Textures");
		Element        viewsElt    = root.getChild("Views");
		
		List<Screen>   screens     = xmlToScreens(viewsElt);
		List<Texture>  textures    = xmlToTextures(texturesElt);
		Scene          scene       = xmlToScene(sceneElt,textures);
		
		return new Project(scene,screens,textures);
	}

	private static Scene xmlToScene(Element sceneElt, List<Texture>  textures) {
		float          indice  = Float.parseFloat(sceneElt.getChild("Float").getAttributeValue("value"));
		Color          ambient = xmlToColor(sceneElt.getChild("Color"));
		List<Object3D> objects = xmlToObjects(sceneElt.getChild("Objects"),textures);
		List<Source>   sources = xmlToSources(sceneElt.getChild("Sources"));
		return new Scene(ambient,indice,objects,sources);
	}
	
	private static List<Source> xmlToSources(Element sourcesElt) {
		return mapAndCollect(elt -> {
			Point pos   = xmlToPoint(elt.getChild("Vector"));
			Color color = xmlToColor(elt.getChild("Color"));
			return new Source(color,pos);
		},sourcesElt,"Source");
	}

	private static List<Screen> xmlToScreens(Element viewsElt) {
		return mapAndCollect(elt -> {
			List<Element> points  = elt.getChildren("Vector");
			//points.stream().map(p -> p.getAttributeValue("name")).forEach(System.out::println);
			Point         A       = xmlToPoint(findByName(points,"A"));
			Point         B       = xmlToPoint(findByName(points,"B"));
			Point         C       = xmlToPoint(findByName(points,"C"));
			Point         pView   = xmlToPoint(findByName(points,"pointOfView"));
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
			Color reflectance    = xmlToColor(findByName(colors,"reflectance"));
			Color Kr             = xmlToColor(findByName(colors,"Kr"));
			Color Kt             = xmlToColor(findByName(colors,"Kt"));
			
			Element Ka           = findByName(colors,"Ka");
			String path;
			if ((path = Ka.getAttributeValue("url")) != null) {
				try {
					return new Texture(indice,brillance,reflectance,new File(path),Kr,Kt);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} else
				return new Texture(indice,brillance,reflectance,xmlToColor(Ka),Kr,Kt);
		},texturesElt,"Texture");
	}

	static <R> List<R> mapAndCollect(Function<Element,R> mapper, Element elt, String cname) {
		return elt.getChildren(cname).
					stream().map(mapper).
					collect(Collectors.toList());
	}
	
	static Element findByName(List<Element> elts, String name) {
		Optional<Element> result = elts.stream().filter(elt -> elt.getAttributeValue("name").equals(name)).findFirst();
		return result.isPresent() ? result.get() : null;
	}
}

package XML;

import java.util.List;
import java.util.stream.Collectors;

import objects.Cone;
import objects.Cube;
import objects.Cylinder;
import objects.Object3D;
import objects.Parallelepiped;
import objects.PlaneSurface;
import objects.Sphere;
import objects.Texture;

import org.jdom2.Element;

import utils.math.Point;
import utils.math.Vector3D;
import static XML.XMLSceneLoader.findByName;
import static XML.basicTypes.XMLVector.xmlToPoint;
import static XML.basicTypes.XMLVector.xmlToVector;
import java.util.function.Function;

public class XMLObjectLoader {
	public static List<Object3D> xmlToObjects(Element objectsElt, List<Texture>  textures) {
		return objectsElt.getChildren().
				stream().map(elt -> xmlToObject(elt,textures)).
				collect(Collectors.toList());
	}

	private static Object3D xmlToObject(Element elt, List<Texture> textures) {
		Object3D result;
		switch (elt.getName()) {
			case "Sphere"         : result = xmlToSphere(elt,textures);         break; 
			case "Cube"           : result = xmlToCube(elt,textures);           break;
			case "Cone"           : result = xmlToCone(elt,textures);           break;
			case "PlaneSurface"   : result = xmlToPlaneSurface(elt,textures);   break;
			case "Parallelepiped" : result = xmlToParallelepiped(elt,textures); break;
			case "Cylinder"       : result = xmlToCylinder(elt,textures);       break;
			default               : throw new UnsupportedOperationException(String.
					format("Unknown object \"%s\"",elt.getName()));		
		}
		String id = elt.getChild("TextureRef").getAttributeValue("id");
		result.setTexture(id.equals("default") ? Texture.DEFAULT_TEXTURE : textures.get(Integer.parseInt(id)));
		return setTextureProperties(elt,result);
	}
	
	private static Object3D setTextureProperties(Element elt, Object3D obj) {
		obj.setRepeat(safeReadAttribute(elt,"repeat",Boolean::parseBoolean,true));
		obj.setAdapt(safeReadAttribute(elt,"adapt",Boolean::parseBoolean,true));
		obj.setPatternRepeat(safeReadAttribute(elt,"patternRepeat",Integer::parseInt,3));
		return obj;
	}

	private static <T> T safeReadAttribute(Element elt, String attributeName, Function<String,T> mapper, T defaultValue) {
		T result = defaultValue;
		try {
			result = mapper.apply(elt.getAttributeValue(attributeName));
		} catch (Throwable t) {	}
		return result;
	}
			
	private static Object3D xmlToCylinder(Element elt, List<Texture> textures) {
		List<Element> points     = elt.getChildren("Vector");
		Vector3D      axis       = xmlToVector(findByName(points,"axis"));
		Point         baseCenter = xmlToPoint(findByName(points,"baseCenter"));
		
		List<Element> doubles    = elt.getChildren("Double");
		double        baseRay    = Double.parseDouble(findByName(doubles,"ray").getAttributeValue("value"));
		double        height     = Double.parseDouble(findByName(doubles,"height").getAttributeValue("value"));
		return new Cylinder(height,baseRay,baseCenter,axis,null);
	}

	private static Object3D xmlToParallelepiped(Element elt, List<Texture> textures) {
		List<Element> points = elt.getChildren("Vector");
		Point         O      = xmlToPoint(findByName(points,"O"));
		Point         X      = xmlToPoint(findByName(points,"X"));
		Point         Y      = xmlToPoint(findByName(points,"Y"));
		Point         Z      = xmlToPoint(findByName(points,"Z"));
		return new Parallelepiped(O,X,Y,Z,null);
	}

	private static Object3D xmlToPlaneSurface(Element elt, List<Texture> textures) {
		List<Element> points  = elt.getChildren("Vector");
		Point         A       = xmlToPoint(findByName(points,"A"));
		Point         B       = xmlToPoint(findByName(points,"B"));
		Point         C       = xmlToPoint(findByName(points,"C"));
		return new PlaneSurface(A,B,C,null);
	}

	private static Object3D xmlToCone(Element elt, List<Texture> textures) {
		List<Element> points     = elt.getChildren("Vector");
		Point         vertice    = xmlToPoint(findByName(points,"vertice"));
		Point         baseCenter = xmlToPoint(findByName(points,"baseCenter"));
		double        baseRay    = Double.parseDouble(elt.getChild("Double").getAttributeValue("value"));
		return new Cone(baseCenter,vertice,baseRay,null);
	}

	private static Object3D xmlToCube(Element elt, List<Texture> textures) {
		List<Element> points = elt.getChildren("Vector");
		Point         O      = xmlToPoint(findByName(points,"O"));
		Point         X      = xmlToPoint(findByName(points,"X"));
		Point         Y      = xmlToPoint(findByName(points,"Y"));
		Point         Z      = xmlToPoint(findByName(points,"Z"));
		return new Cube(O,X,Y,Z,null);
	}

	private static Object3D xmlToSphere(Element elt, List<Texture> textures) {
		Point  center = xmlToPoint(elt.getChild("Vector"));
		double ray    = Double.parseDouble(elt.getChild("Double").getAttributeValue("value"));
		return new Sphere(center,ray,null);
	}
}

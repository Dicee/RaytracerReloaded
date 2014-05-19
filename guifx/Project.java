package guifx;

import java.util.ArrayList;
import java.util.List;

import objects.Texture;

import org.jdom2.Attribute;
import org.jdom2.Element;

import scene.Scene;
import scene.Screen;
import XML.XMLable;

public class Project implements XMLable {
	private Scene			scene;
	private List<Screen>	screens;
	private List<Texture>	textures;
	
	public Project(Scene scene, List<Screen> screens, List<Texture> textures) {
		this.scene = scene;
		setScreens(screens);
		setTextures(textures);
	}
	
	@Override
	public Element toXML() {
		Element result      = new Element("Project");
		Element sceneElt    = scene.toXML();
		Element texturesElt = new Element("Textures");
		Element viewsElt    = new Element("Views");	
		
		int i = 0;
		for (Texture t : textures) {
			Element elt = t.toXML();
			elt.setAttribute(new Attribute("id",(i++) + ""));
			elt.setAttribute(new Attribute("show","true"));
			texturesElt.addContent(elt);
		}
		screens.stream().forEach(s -> viewsElt.addContent(s.toXML())); 
		
		result.addContent(texturesElt);
		result.addContent(sceneElt);
		result.addContent(viewsElt);
		return result;
	}
	
	public Scene getScene()	{
		return scene;
	}
	
	public List<Screen> getScreens() {
		return screens;
	}
	
	public List<Texture> getTextures()	{
		return textures;
	}
	
	public void setScene(Scene scene) {
		this.scene = scene.clone();
	}
	
	public void setScreens(List<Screen> screens) {
		this.screens = new ArrayList<>(screens);
	}
	
	public void setTextures(List<Texture> textures) {
		this.textures = new ArrayList<>(textures);
	}
}

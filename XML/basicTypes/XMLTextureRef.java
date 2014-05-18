package XML.basicTypes;

import org.jdom2.Attribute;
import org.jdom2.Element;

public class XMLTextureRef extends Element {
	private static final long	serialVersionUID	= 1L;

	public XMLTextureRef(int id) {
		super("TextureRef");
		setAttribute(new Attribute("id",id + ""));
	}
	
	public XMLTextureRef() {
		super("TextureRef");
		setAttribute(new Attribute("id","default"));
	}
}

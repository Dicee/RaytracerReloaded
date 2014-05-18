package XML.basicTypes;

import org.jdom2.Attribute;
import org.jdom2.Element;

public class XMLInteger extends Element {
	private static final long	serialVersionUID	= 1L;
	
	public XMLInteger(String name, int value) {
		super("Integer");
		setAttribute(new Attribute("name",name));
		setAttribute(new Attribute("value",value + ""));
	}
}

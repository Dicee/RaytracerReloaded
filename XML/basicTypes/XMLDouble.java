package XML.basicTypes;

import org.jdom2.Attribute;
import org.jdom2.Element;

public class XMLDouble extends Element {
	private static final long	serialVersionUID	= 1L;
	
	public XMLDouble(String name, double value) {
		super("Double");
		setAttribute(new Attribute("name",name));
		setAttribute(new Attribute("value",value + ""));
	}
}

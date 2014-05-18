package XML.basicTypes;

import org.jdom2.Attribute;
import org.jdom2.Element;

public class XMLFloat extends Element {
	private static final long	serialVersionUID	= 1L;
	
	public XMLFloat(String name, double value) {
		super("Float");
		setAttribute(new Attribute("name",name));
		setAttribute(new Attribute("value",value + ""));
	}
}

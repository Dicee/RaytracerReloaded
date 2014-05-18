package XML;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public abstract class TestJDOM {
	public static void main(String[] args) {
		Element root = new Element("bibliotheque");
		root.setAttribute(new Attribute("ouverte","true"));
		root.addContent(new Text("Bijour je suis le rat de la bibliothèque"));
		Document doc = new Document(root);
//		doc.addContent(new Text("Salut les amis"));
		afficher(doc);
	}

	protected static void afficher(Document document) {
		try {
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			sortie.output(document,System.out);
		} catch (java.io.IOException e) { 
			e.printStackTrace();
		}
	}

}

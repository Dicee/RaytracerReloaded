package XML;

import guifx.Project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public abstract class TestJDOM {
	public static void main(String[] args) throws JDOMException, IOException {
//		Element root = new Element("bibliotheque");
//		root.setAttribute(new Attribute("ouverte","true"));
//		root.addContent(new Text("Bijour je suis le rat de la bibliothèque"));
//		Document doc = new Document(root);
//		doc.addContent(new Text("Salut les amis"));
//		afficher(doc);
		
		
		
		Project p = XMLSceneLoader.load(new File("Sofia.xml"));
		Element elt = p.toXML();
		Document d = new Document(elt);
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		out.output(d,new BufferedWriter(new FileWriter(new File("Sofia_bis.xml"))));
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

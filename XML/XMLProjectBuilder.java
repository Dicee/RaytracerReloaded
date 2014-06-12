package XML;

import java.io.File;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import scene.Project;
import utils.WrappedBufferedWriter;

public class XMLProjectBuilder {
    public static final void save(File f, Project p) throws IOException {
		Document d       = new Document(p.toXML());
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		out.output(d,new WrappedBufferedWriter(f));
    }
}

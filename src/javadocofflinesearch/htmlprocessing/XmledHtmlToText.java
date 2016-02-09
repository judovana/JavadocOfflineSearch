package javadocofflinesearch.htmlprocessing;

import java.io.IOException;
import javadocofflinesearch.extensions.Vocabulary;
import javadocofflinesearch.tools.TitledByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javadocofflinesearch.extensions.HrefCounter;
import javadocofflinesearch.tools.LevenshteinDistance;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author jvanek
 */
public class XmledHtmlToText {

    private final HrefCounter hc;
    private final Vocabulary vc;

    public XmledHtmlToText(HrefCounter hc, Vocabulary vc) {
        this.hc = hc;
        this.vc = vc;
    }

    public HrefCounter getHc() {
        return hc;
    }

    public Vocabulary getVc() {
        return vc;
    }

    public InputStream parseAnother(InputStream hoefullyXmlizedInputStream, URL current) {
        try {
            String[] l = parseAnotherII(hoefullyXmlizedInputStream, current, true);
            return new TitledByteArrayInputStream(l[0], l[1].getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            //plain text?
            e.printStackTrace();
            return hoefullyXmlizedInputStream;
        }
    }

    public String[] parseAnotherII(InputStream hoefullyXmlizedInputStream, URL current, boolean stats) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuild = dbf.newDocumentBuilder();
        Document doc = docBuild.parse(hoefullyXmlizedInputStream);
        String body = "";
        String title = null;
        NodeList nodeList = doc.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equalsIgnoreCase("title")) {
                    title = node.getTextContent();
                } else if (node.getNodeName().equalsIgnoreCase("body")) {
                    body = node.getTextContent();
                } else if (node.getNodeName().equalsIgnoreCase("a") && stats) {
                    NamedNodeMap ats = node.getAttributes();
                    if (ats != null) {
                        Node href = ats.getNamedItem("href");
                        if (href != null) {
                            hc.addLink(href.getTextContent(), current);
                        }
                    }
                }
            }

        }
        String s = body.replaceAll("\\s+", " ");
        if (title != null) {
            s = title + "\n" + s;

        }
        if (title == null && current != null) {
            title = LevenshteinDistance.sanitizeFileUrl(current);
        }
        hoefullyXmlizedInputStream.close();
        if (stats) {
            String[] vocabulary1 = s.split("\\s+"); //java.security
            String[] vocabulary2 = s.split("[\\W]"); //java security
            vc.add(vocabulary1);
            vc.add(vocabulary2);
        }
        return new String[]{title, s};

    }

}

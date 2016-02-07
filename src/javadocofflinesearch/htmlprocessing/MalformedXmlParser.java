
package javadocofflinesearch.htmlprocessing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.ccil.cowan.tagsoup.XMLWriter;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 *
 * @author jvanek
 */
public class MalformedXmlParser {

    public static InputStream xmlizeInputStream(InputStream original) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            HTMLSchema schema = new HTMLSchema();
            XMLReader reader = new Parser();

            //TODO walk through the javadoc and tune more settings
            //see tagsoup javadoc for details 
            reader.setProperty(Parser.schemaProperty, schema);
            reader.setFeature(Parser.bogonsEmptyFeature, false);
            reader.setFeature(Parser.ignorableWhitespaceFeature, true);
            reader.setFeature(Parser.ignoreBogonsFeature, false);

            Writer writeger = new OutputStreamWriter(out);
            XMLWriter x = new XMLWriter(writeger);

            reader.setContentHandler(x);

            InputSource s = new InputSource(original);

            reader.parse(s);
            original.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            return original;
        }

    }
}

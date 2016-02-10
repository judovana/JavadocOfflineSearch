/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.htmlprocessing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javadocofflinesearch.extensions.HrefCounter;
import javadocofflinesearch.extensions.Vocabulary;
import javadocofflinesearch.tools.TitledByteArrayInputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author jvanek
 */
public class StreamCrossroad {

    private final XmledHtmlToText htmlizer;
    private static final String pdfCheck = "org.apache.pdfbox.pdfparser.PDFParser";
    private static Boolean isPdfBox = null;

    public StreamCrossroad(HrefCounter hc, Vocabulary vocabualry) {
        htmlizer = new XmledHtmlToText(hc, vocabualry);
    }

    public void saveCacheMetadata() throws IOException {
        try {
            htmlizer.getHc().saveHrefs();
        } finally { //lets try to save both
            htmlizer.getVc().saveVocs();
        }
    }

    private static boolean checkPdfBox() {
        if (isPdfBox == null) {
            try {
                Class clazz = Class.forName(pdfCheck);
                isPdfBox = clazz != null;
            } catch (Exception ex) {
                isPdfBox = false;
            }
        }
        return isPdfBox;
    }

    static boolean checkPdfBox(String file) {
        return checkPdfBox() && file.toLowerCase().endsWith(".pdf");
    }

    public InputStream tryURL(URL file) throws IOException {
        InputStream result = null;
        try {
            //first pdf otherwise tagsoup will get med
            if (checkPdfBox(file.getFile())) {
                try (InputStream is = file.openStream()) {
                    String s = PdfAttempter.pdftoText(is);
                    result = new TitledByteArrayInputStream(new File(file.getFile()).getName(), s.getBytes("utf-8"));
                }
            }
        } catch (Throwable e) {

        } finally {
            //always try non pdf unless it really was an pdf
            if (result == null) {
                return htmlizer.parseAnother(MalformedXmlParser.xmlizeInputStream(file.openStream()), file);
            } else {
                return result;
            }
        }
    }

    public String tryURL2(String url) throws ParserConfigurationException, SAXException, IOException {
        String result = null;
        try {
            //first pdf otherwise tagsoup will get med
            if (checkPdfBox(url)) {
                try (InputStream is = new URL(url).openStream()) {
                    result = PdfAttempter.pdftoText(is);

                }
            }
        } catch (Throwable e) {

        } finally {
            //always try non pdf unless it really was an pdf
            if (result == null) {
                return new XmledHtmlToText(null, null).parseAnotherII(MalformedXmlParser.xmlizeInputStream(new URL(url).openStream()), null, false)[1];
            } else {
                return result;
            }

        }
    }
}

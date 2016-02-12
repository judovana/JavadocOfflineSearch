/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.htmlprocessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javadocofflinesearch.extensions.Vocabulary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 *
 * @author jvanek
 */
public class PdfAttempter {

    private final Vocabulary vc;

    PdfAttempter(Vocabulary vocabualry) {
        this.vc = vocabualry;
    }

    String pdftoText(InputStream is, boolean stats) throws IOException {
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        try {
            PDFParser parser = new PDFParser(is);
            parser.parse();
            cosDoc = parser.getDocument();
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            String text = pdfStripper.getText(pdDoc);
            if (stats) {
                vc.addAll(text);
            }
            return text;
        } finally {
            if (cosDoc != null) {
                cosDoc.close();
            }
            if (pdDoc != null) {
                pdDoc.close();
            }
        }
    }

}

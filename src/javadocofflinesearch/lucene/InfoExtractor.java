/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javadocofflinesearch.formatters.Formatter;
import javadocofflinesearch.formatters.SearchableHtmlFormatter;
import javadocofflinesearch.htmlprocessing.StreamCrossroad;
import javadocofflinesearch.tools.HardcodedDefaults;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author jvanek
 */
public class InfoExtractor {

    public static String extract(String url, String queryString, Formatter f, int lShift, int rShift) throws IOException, SAXException, ParserConfigurationException {
        String s = new StreamCrossroad(null, null).tryURL2(url);
        s = s.replaceAll("<!--.*?-->", "");//?
        Pattern p = Pattern.compile("(?i)" + queryString.trim().replaceAll("\\s+", "|"));
        Matcher m = p.matcher(s);
        int noMoreNeeded = HardcodedDefaults.getInfoLoad();
        int show = HardcodedDefaults.getInfoShow();

        if (f != null && f instanceof SearchableHtmlFormatter) {
            SearchableHtmlFormatter w = (SearchableHtmlFormatter) f;
            if (w.getDefaults() != null) {
                if (w.getDefaults().getInfoLoad() != null) {
                    noMoreNeeded = w.getDefaults().getInfoLoad();
                }
                if (w.getDefaults().getInfoShow() != null) {
                    show = w.getDefaults().getInfoShow();
                }
            }
        }
        List<String> hunks = new ArrayList<>(noMoreNeeded + 1);
        while (m.find()) {

            int l = m.end() - m.start();
            int start = m.start() + lShift;
            int stop = m.end() + rShift;
            if (start < 0) {
                lShift = lShift - start;
                start = 0;
            }
            if (stop >= s.length()) {
                stop = s.length() - 1;
            }
            try {
                StringBuilder target = new StringBuilder(s.substring(start, stop));
                //must be first, otherwise inserting to beggining move the end...
                target.insert(-lShift + l, f.highlightEnd());
                target.insert(-lShift, f.highlitStart());
                hunks.add("..." + (target));
            } catch (java.lang.StringIndexOutOfBoundsException ex) {

            }
            //ok thats enough
            if (hunks.size() > noMoreNeeded) {
                break;
            }
        }
        if (show == 0) {
            show = Integer.MAX_VALUE;
        }
        double increment;
        if (show >= hunks.size()) {
            increment = 1;
        } else {
            increment = ((double) hunks.size()) / ((double) show);
        }
        int lastInt = -1;
        StringBuilder result = new StringBuilder();
        for (double i = 0; i < hunks.size(); i += increment) {
            int nwInt = (int) Math.round(i);
            if (nwInt != lastInt && nwInt < hunks.size()) { //ensure not duplicated ints
                result.append(hunks.get(nwInt));
                lastInt = nwInt;
            }
        }
        return result.append("...").toString();
    }

}

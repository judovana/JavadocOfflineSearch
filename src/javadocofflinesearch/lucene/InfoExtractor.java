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
import javadocofflinesearch.htmlprocessing.StreamCrossroad;
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
        int noMoreNeeded = 30;
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
        int start = 0;
        int end = 20;
        if (hunks.size() > 5) {
            start = 5;
        }
        if (end >= hunks.size()) {
            end = hunks.size();
        }
        StringBuilder result = new StringBuilder();
        for (int i = start; i < end; i++) {
            result.append(hunks.get(i));

        }
        return result.append("...").toString();
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.lucene;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javadocofflinesearch.formatters.Formatter;
import javadocofflinesearch.htmlprocessing.StreamCrossroad;
import javadocofflinesearch.tools.LibrarySetup;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author jvanek
 */
public class InfoExtractor {

    private final LibrarySetup setup;

    public InfoExtractor(LibrarySetup l) {
        this.setup = l;
    }

    public String extract(String url, String queryString, Formatter f, int lShift, int rShift) throws IOException, SAXException, ParserConfigurationException {
        String s = new StreamCrossroad(null, null).tryURL2(url);
        s = s.replaceAll("<!--.*?-->", "");//?
        Pattern p = Pattern.compile("(?i)" + queryString.trim().replaceAll("\\s+", "|"));
        Matcher m = p.matcher(s);
        int noMoreNeeded = setup.getMaxLoad();
        int show = setup.getMaxShow();

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

    public static String highlightInDoc(String url, String queryString, Formatter f) throws IOException, SAXException, ParserConfigurationException {
        String s = new StreamCrossroad(null, null).tryURL2(url);
        return highlightInString(s, queryString, f);

    }

    public static String highlightInString(String s, String queryString, Formatter f) throws IOException, SAXException, ParserConfigurationException {
        Pattern p = Pattern.compile("(?i)" + queryString.trim().replaceAll("\\s+", "|"));
        Matcher m = p.matcher(s);
        StringBuilder result = new StringBuilder();
        int start = 0;
        Map<String, Color> colors = new HashMap();
        while (m.find()) {

            String match = s.substring(m.start(), m.end());
            Color c = getColor(match.toLowerCase(), colors);
            result.append(s.substring(start, m.start()));
            result.append(f.highlitStart(c));
            result.append(match);
            result.append(f.highlitEnd(c));
            start = m.end();

        }

        return result.toString();
    }

    private static Color getColor(String s, Map<String, Color> colors) {
        Color c = colors.get(s);
        if (c != null) {
            return c;
        }
        c = createColor(colors.size());
        colors.put(s, c);
        return c;
    }

    private static Color createColor(int size) {
        int i = size % 11;
        switch (i) {
            case 0:
                return Color.ORANGE;
            case 1:
                return Color.CYAN;
            case 2:
                return Color.MAGENTA;
            case 3:
                return Color.PINK;
            case 4:
                return Color.GREEN;
            case 5:
                return Color.RED;
            case 6:
                return Color.blue;
            case 7:
                return Color.DARK_GRAY;
            case 8:
                return new Color(175, 0, 175);
            case 9:
                return new Color(175, 175, 0);
            case 10:
                return new Color(0, 175, 175);
        }
        return Color.YELLOW;
    }

}

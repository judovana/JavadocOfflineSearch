/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.formatters;

import java.awt.Color;
import java.io.PrintStream;
import java.util.List;
import javadocofflinesearch.lucene.InfoExtractor;
import javadocofflinesearch.tools.LibrarySetup;

/**
 *
 * @author jvanek
 */
public class PlainTextFormatter implements Formatter {

    private final PrintStream out;
    private final LibrarySetup setup;
    private final InfoExtractor infoExtractor;

    public PlainTextFormatter(PrintStream out, LibrarySetup setup) {
        this.out = out;
        this.setup = setup;
        infoExtractor = new InfoExtractor(setup);
    }

    @Override
    public String highlitStart() {
        return "*";
    }

    @Override
    public String highlightEnd() {
        return "*";
    }

    @Override
    public void title(int current, int totalHits, String title) {
        out.println(current + "/" + totalHits + ") " + title);
    }

    @Override
    public void haders() {
        //nothing, happy plaintext:)
    }

    @Override
    public void tail() {
        //nothing, happy plaintext:)
    }

    @Override
    public void file(String string, int page, float score) {
        out.println(string + " :  " + page + "/" + score);
    }

    @Override
    public void searchStarted(String info, String what) {
        out.println(info + what);
    }

    @Override
    public void couldYouMeant(String title, List<String>... l) {
        out.println();
        out.println(title);
        boolean linked = false;
        for (List<String> l1 : l) {
            for (String l11 : l1) {
                linked = true;
                out.println(l11);
            }
        }
        if (linked) {
            out.println("");
        }
    }

    @Override
    public void resulsSummary(String foundTitle, long totalHits, String timeTitle, long time, String units) {
        out.println(foundTitle + " " + totalHits);
        out.println(timeTitle + " " + time + units);
        out.println("");
    }

    @Override
    public String summary(String path, String queryString, int infoBefore, int infoAfter) {
        String sumamry;
        try {
            sumamry = infoExtractor.extract(path, queryString, this, infoBefore, infoAfter);

        } catch (Exception ex) {
            ex.printStackTrace();
            sumamry = ex.toString();
        }
        out.println(sumamry);
        out.println("\n");
        return sumamry;
    }

    @Override
    public void initializationFailed(String s) {
        out.println(s);
    }

    @Override
    public void resultsIn(String title, long l, String unit) {
        out.println(title + " " + l + unit);
    }

    @Override
    public void pages(int from, int to, int total) {

    }

    @Override
    public void printLibrary(String library) {
        out.println("Using library: " + library);
    }

    @Override
    public String highlitStart(Color c) {
        return "\n!!!\n";
    }

    @Override
    public String highlitEnd(Color c) {
        return "\n!!!\n";
    }

    @Override
    public String anchors(String string, String mach) {
        return "";
    }

}

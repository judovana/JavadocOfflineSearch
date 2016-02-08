/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.formatters;

import java.io.PrintStream;
import java.util.List;
import javadocofflinesearch.lucene.InfoExtractor;

/**
 *
 * @author jvanek
 */
public class PlainTextFormatter implements Formatter {

    private final PrintStream out;

    public PlainTextFormatter(PrintStream out) {
        this.out = out;
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
    public void resulsSummary(String foundTitle, int totalHits, String timeTitle, long time, String units) {
        out.println(foundTitle + " " + totalHits);
        out.println(timeTitle + " " + time + units);
        out.println("");
    }

    @Override
    public void summary(String path, String queryString, int infoBefore, int infoAfter) {
        String sumamry;
        try {
            sumamry = InfoExtractor.extract(path, queryString, this, infoBefore, infoAfter);

        } catch (Exception ex) {
            ex.printStackTrace();
            sumamry = ex.toString();
        }
        out.println(sumamry);
        out.println("\n");
    }

    @Override
    public void initializationFailed(String s) {
        out.println(s);
    }

    @Override
    public void resultsIn(String title, long l, String unit) {
        out.println(title + " " + l + unit);
    }

}

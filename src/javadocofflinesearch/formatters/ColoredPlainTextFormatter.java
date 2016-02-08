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

/**
 *
 * @author jvanek
 */
public class ColoredPlainTextFormatter implements Formatter {

    private final PrintStream out;

    public ColoredPlainTextFormatter(PrintStream out) {
        this.out = out;
    }

    @Override
    public void title(int current, int totalHits, String title) {
        out.println(colorToEscapedString(Color.yellow, true) + current + "/" + totalHits + ") " + title + reset(true));
    }

    @Override
    public String highlitStart() {
        return colorToEscapedString(Color.red, true);
    }

    @Override
    public String highlightEnd() {
        return reset(true);
    }

    private static final Color darkOrange = new Color(255, 125, 0);

    @Override
    public void file(String string, int page, float score) {

        out.println(colorToEscapedString(Color.cyan, true) + string + reset(true) + " :  " + colorToEscapedString(darkOrange, true) + page + "/" + score + reset(true));
    }

    @Override
    public void searchStarted(String info, String what) {
        out.println(info + colorToEscapedString(Color.GREEN, true) + what + reset(true));
    }

    @Override
    public void resulsSummary(String foundTitle, int totalHits, String timeTitle, long time, String units) {
        out.println(foundTitle + " " + colorToEscapedString(Color.GREEN, true) + totalHits + reset(true));
        out.println(timeTitle + " " + colorToEscapedString(Color.GREEN, true) + time + reset(true) + units);
        out.println("");
    }

    @Override
    public void resultsIn(String title, long l, String unit) {
        out.println(title + " " + colorToEscapedString(Color.GREEN, true) + l + reset(true) + unit);
    }

    @Override
    public void couldYouMeant(String title, List<String>... l) {
        out.println();
        out.println(colorToEscapedString(Color.yellow, true) + title + reset(true));
        boolean linked = false;
        boolean color = true;
        for (List<String> l1 : l) {
            if (l1 != null && l1.size() > 0) {
                for (String l11 : l1) {
                    color = !color;
                    if (color) {
                        out.print(colorToEscapedString(Color.gray, color));
                    }
                    out.print(l11);
                    out.print(reset(true));
                    out.print(" ");
                    linked = true;
                }
                out.println("");
            }
        }
        if (linked) {
            out.println("");
        }
    }

    private static String reset(boolean fg1) {
        if (fg1) {
            return ansiColorToEscapedString(15, true);
        } else {
            return ansiColorToEscapedString(colorToAnsiColor(Color.black, false), false);
        }
    }

    private static String ansiColorToEscapedString(int ansi, boolean fg) {
        if (fg) {
            return "\033[38;5;" + ansi + "m"; //fg
        } else {
            return "\033[48;5;" + ansi + "m"; //bg
        }

    }

    private static String colorToEscapedString(Color c, boolean fg) {
        return ansiColorToEscapedString(colorToAnsiColor(c, false), fg);

    }

    /**
     * r=3; g=0; b=2; let number=16+36*r+6*g+b; COLOR='\033[38;5;'$number'm' ;
     * NC='\033[0m' ; echo -e "I ${COLOR}love${NC} Stack"
     * http://stackoverflow.com/questions/15682537/ansi-color-specific-rgb-sequence-bash
     *
     * @param pixelColor
     * @return
     */
    private static int colorToAnsiColor(Color pixelColor, boolean negative) {
        int n = 0;
        if (negative) {
            n = 255;
        }
        int ansiR = Math.abs(n - pixelColor.getRed()) / 50;
        int ansiG = Math.abs(n - pixelColor.getGreen()) / 50;
        int ansiB = Math.abs(n - pixelColor.getBlue()) / 50;
        int ansi = 16 + 36 * ansiR + 6 * ansiG + ansiB;
        return ansi;
    }

    @Override
    public void haders() {
        out.print(reset(true));
        out.print(reset(false));
    }

    @Override
    public void tail() {
        out.print(reset(true));
        out.print(reset(false));
    }

    @Override
    public void summary(String path, String queryString, int infoBefore, int infoAfter) {
        String sumamry = "";
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
        out.print(colorToEscapedString(Color.red, true));
        out.println(s);
        out.print(reset(true));
    }
}

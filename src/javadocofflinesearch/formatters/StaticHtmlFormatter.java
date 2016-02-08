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
public class StaticHtmlFormatter implements Formatter {

    protected final PrintStream out;
    private static final boolean cdata = false;

    public StaticHtmlFormatter(PrintStream out) {
        this.out = out;
    }

    @Override
    public String highlitStart() {
        return "<b>";
    }

    @Override
    public String highlightEnd() {
        return "</b>";
    }

    @Override
    public void file(String string, int page, float score) {
        out.println("<big><b><a href='" + string + "'>" + string + "</a>: </b></big>" + page + "/" + score + "<br/>");
    }

    @Override
    public void title(int current, int totalHits, String title) {
        out.println("<h3 style='margin-bottom: 0;'>" + current + "/" + totalHits + ") " + title + "</h3>");
    }

    @Override
    public void haders() {
        out.println("<html>"
                + "<meta http-equiv='Content-Type' content='text/html;charset=UTF-8'>"
                + "<meta charset='UTF-8'>"
                + "<body>");
    }

    @Override
    public void tail() {
        out.println("</body></html>");
    }

    @Override
    public void searchStarted(String info, String what) {
        out.println(info + "<b>" + what + "</b>");
    }

    @Override
    public void couldYouMeant(String title, List<String>... l) {
        out.print("<div>");
        out.println("<h4 style='margin-bottom: 0;'>" + title + "</h4>");
        out.print("<div>");
        boolean color = true;
        for (List<String> l1 : l) {
            if (l1 != null && l1.size() > 0) {
                out.print("<div>"); //format to columns!
                out.print("<small>");
                if (cdata) {
                    out.print("<![CDATA[");
                }
                for (String l11 : l1) {
                    color = !color;
                    if (color) {
                        out.print("<i>");
                    }
                    out.print(l11);
                    if (color) {
                        out.print("</i>");
                    }
                    out.println(" , ");
                }
                if (cdata) {
                    out.print("]]>");
                }
                out.print("</small>");
                out.print("</div>");
                out.println("");
            }
        }
        out.print("</div>");
        out.print("</div>");
        out.print("<br/>");
    }

    @Override
    public void resulsSummary(String foundTitle, int totalHits, String timeTitle, long time, String units) {
        out.println(foundTitle + " " + "<b>" + totalHits + "</b>");
        out.println(timeTitle + " " + "<b>" + time + "</b>" + units);
    }

    @Override
    public void resultsIn(String title, long l, String units) {
        out.println(title + " " + "<b>" + l + "</b>" + units);
    }

    @Override
    public void summary(String path, String queryString, int infoBefore, int infoAfter) {
        String sumamry;
        out.println("<div>");
        try {
            sumamry = InfoExtractor.extract(path, queryString, this, infoBefore, infoAfter);

        } catch (Exception ex) {
            ex.printStackTrace();
            sumamry = ex.toString();
        }
        out.print("<small>");
        if (cdata) {
            out.print("<![CDATA[");
        }
        out.println(sumamry);
        if (cdata) {
            out.print("]]>");
        }
        out.print("</small>");
        out.println("</div>");
        out.println("<br/>");
    }

    @Override
    public void initializationFailed(String s) {
        out.println("<div>");
        out.println(s.replace("\n", "<br/>\n"));
        out.println("</div>");
    }

}

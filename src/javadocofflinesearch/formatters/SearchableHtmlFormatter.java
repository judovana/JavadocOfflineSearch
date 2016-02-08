/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.formatters;

import java.io.PrintStream;
import javadocofflinesearch.server.WebParams;

/**
 *
 * @author jvanek
 */
public class SearchableHtmlFormatter extends StaticHtmlFormatter {

    private final WebParams defaults;
    private static final String CHECKED = "checked";

    public SearchableHtmlFormatter(PrintStream out) {
        super(out);
        this.defaults = null;
    }

    public SearchableHtmlFormatter(PrintStream out, WebParams defaults) {
        super(out);
        this.defaults = defaults;
    }

    @Override
    public void haders() {
        super.haders();
        out.println("<small>Yes, this is not an google, so there is few more sttings...</small><br>");
        out.println("<h2 align=\"center\">JavaDoc Offline Search</h2>");
        out.println("<form action='search'>");
        out.println("<span  style=\"text-align: center\">");
        out.println("<input type=\"radio\" name=\"search-type\" value=\"page-index\" "+getCheckedPage()+"> use page index (it learns!)");
        out.println("<input type=\"radio\" name=\"search-type\" value=\"lucene-index\" " + getCheckedLucene() + "> use lucene index");
        out.println("<input type=\"checkbox\" name=\"merge\" value=\"true\"  " + getCheckedMerge() + ">merge both indexes<br>");
        out.println("</span>");
        out.println("<span  style=\"text-align: center\">");
        out.println("  <input type=\"text\" name=\"query\" value=\""+getQueryValue()+"\"   style=\"width:80%;\"/><br/>");
        out.println("</span>");
        out.println("</form>");
    }

    private String getCheckedMerge() {
        if (defaults != null) {
            if (defaults.isMergeWonted()) {
                return CHECKED;
            }
        }
        return "";
    }

    private String getCheckedLucene() {
        if (defaults != null) {
            if (!defaults.isPage()) {
                return CHECKED;
            }
        }
        return "";
    }

    private String getCheckedPage() {
         if (defaults != null) {
            if (defaults.isPage()) {
                return CHECKED;
            }
        }
        return "";
    }

    private String getQueryValue() {
        if (defaults != null) {
            if (defaults.getQuery()!=null) {
                return defaults.getQuery();
            }
        }
        return "";
    }

}

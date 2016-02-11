/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.formatters;

import java.io.PrintStream;
import java.util.Date;
import javadocofflinesearch.server.WebParams;
import javadocofflinesearch.tools.Commandline;
import javadocofflinesearch.tools.LibrarySetup;

/**
 *
 * @author jvanek
 */
public class SearchableHtmlFormatter extends StaticHtmlFormatter {

    private final WebParams defaults;
    private static final String CHECKED = "checked";
    public static final String searchType = "search-type";
    public static final String previewMaxShow = "previewMaxShow";
    public static final String previewMaxLoad = "previewMaxLoad";
    public static final String merge = Commandline.MERGE_COMAPRATORS;
    public static final String query = Commandline.QUERY;
    public static final String library = Commandline.LIBRARY;
    public static final String noInfo = Commandline.NO_INFO;
    public static final String showAlsoPdfInfo = "xnot-" + Commandline.NO_PDF_INFO;

    public static final String infoBefore = Commandline.INFO_BEFORE;
    public static final String infoAfter = Commandline.INFO_AFTER;

    public static final String ddmDeadline = Commandline.DEAD_LINE;
    public static final String ddmCount = Commandline.DID_COUNT;
    public static final String startAt = Commandline.START_AT;
    public static final String records = Commandline.RECORDS;

    //special field to reset item to start form when clicekd via "pages link"
    public static final String bypage = "bypage";

    public SearchableHtmlFormatter(PrintStream out, LibrarySetup setup) {
        super(out, setup);
        this.defaults = null;
    }

    public SearchableHtmlFormatter(PrintStream out, WebParams defaults) {
        super(out, defaults.getSetup());
        this.defaults = defaults;
    }

    @Override
    public void file(final String url, int page, float score) {
        String href = url.substring(url.indexOf("///") + 2);
        if (url.startsWith("jar")) {
            //appearently ! in middle is enugh :)
        }
        out.println("<big><b><a href='" + href + "'>" + url + "</a>: </b></big>" + page + "/" + score + "<br/>");
    }

    @Override
    public void pages(int from, int to, int total) {
        int recordsPerPage = defaults.getRecords();
        if (recordsPerPage == 0) {
            return;
        }
        if (recordsPerPage < total) {
            out.println("<br/>");
            String s = defaults.getOrigQuery();
            int pages = (total / recordsPerPage) + 1;
            for (int i = 0; i < pages; i++) {
                String nwq = "search?" + s.replaceAll(startAt + "=\\d*", startAt + "=" + (i * (defaults.getRecords()))) + "&" + bypage + "=true";
                if (i * (defaults.getRecords()) >= from && i * (defaults.getRecords()) < to) {
                    out.println("<b> " + (i + 1) + " </b>");
                } else {
                    out.println("<a href='" + nwq + "' > " + (i + 1) + " </a>");
                }
            }
            out.println("<br/>");
        }
    }

    @Override
    public void haders() {
        super.haders();
        out.println("<small>Yes, this is not an google, so there is few more sttings...</small><br>");
        out.println("<h2 align=\"center\">JavaDoc Offline Search</h2>");
        out.println("<h6 align=\"center\">" + new Date() + "</h6>");
        out.println("<form action='search'>");
        out.println("<span>");
        out.println("<input type=\"radio\" name=\"" + searchType + "\" value=\"page-index\" " + getCheckedPage() + "> use page index (it learns!)");
        out.println("<input type=\"radio\" name=\"" + searchType + "\" value=\"lucene-index\" " + getCheckedLucene() + "> use lucene index");
        out.println("<input type=\"checkbox\" name=\"" + merge + "\" value=\"true\"  " + getCheckedMerge() + ">" + getMergeText());
        out.println("<a href=\"https://lucene.apache.org/core/2_9_4/queryparsersyntax.html\" >basic lucene query sintax</a> see indexes: file://path page/lucene<br/>");
        out.println("</span>");
        out.println("<span>");
        out.println("  <input type=\"text\" id='t1' name=\"" + query + "\" value=\"" + getQueryValue() + "\"   style=\"width:99%;\"/><br/>");
        out.println("</span>");
        out.println("<span style='float: right;'>");
        out.println("<span onclick=\"document.getElementById('advanced').style.display = 'block'\" ondblclick=\"document.getElementById('advanced').style.display = 'none'\">Click for more settings:</span><br/>");
        out.println("<span id='advanced' style='display:none;float: right; border:dotted; margin: 5px'>");
        out.println("  <input type=\"checkbox\" name=\"" + noInfo + "\" value=\"true\" " + getWasInfoSelcted() + "/> " + getInfoText() + "<br/>");
        out.println("  <input type=\"checkbox\" name=\"" + showAlsoPdfInfo + "\" value=\"true\" " + getWasshowAlsoPdfInfoSeelcted() + "/> " + getPdfText() + "<br/>");
        out.println("  <input type=\"text\"  id='t2' name=\"" + infoBefore + "\" value=\"" + wasInfoB() + "\"/> how much text to show before highlighted info<br/>");
        out.println("  <input type=\"text\"  id='t3' name=\"" + infoAfter + "\" value=\"" + wasInfoA() + "\"/> how much text to show after highlighted info<br/>");
        out.println("  <input type=\"text\"  id='t4' name=\"" + ddmDeadline + "\" value=\"" + wasDDD() + "\"/>if total-found is bigger then this number, `do you mean ` do not appear<br/>");
        out.println("  <input type=\"text\"  id='t5' name=\"" + ddmCount + "\" value=\"" + wasDDC() + "\"/> number of suggested typo-fixing items<br/>");
        out.println("  <input type=\"text\"  id='t6' name=\"" + startAt + "\" value=\"" + wasStartAt() + "\"/> item to start showing on<br/>");
        out.println("  <input type=\"text\"  id='t7' name=\"" + records + "\" value=\"" + wasRecords() + "\"/> items per page<br/>");
        out.println("  <input type=\"text\"  id='t8' name=\"" + previewMaxShow + "\" value=\"" + wasMS() + "\"/> items to show in preview<br/>");
        out.println("  <input type=\"text\"  id='t9' name=\"" + previewMaxLoad + "\" value=\"" + wasML() + "\"/> items to load to show in preview<br/>");
        out.println("</span>");
        out.println("</span>");
        //without submitbutton, the enter key do not work when more then one input type 'text' is presented
        out.println("  <input type=\"submit\"><br/>");
        out.println("</form>");
    }

    private String getCheckedLucene() {
        if (defaults != null) {
            if (!defaults.isPage()) {
                return CHECKED;
            } else {
                return "";
            }
        }
        if (setup.isLucenePreffered()) {
            return CHECKED;
        }
        return "";
    }

    private String getCheckedPage() {
        if (defaults != null) {
            if (defaults.isPage()) {
                return CHECKED;
            } else {
                return "";
            }
        }
        if (!setup.isLucenePreffered()) {
            return CHECKED;
        }
        return "";
    }

    private String getQueryValue() {
        if (defaults != null) {
            if (defaults.getQuery() != null) {
                return defaults.getQuery();
            }
        }
        return "";
    }

    private String wasDDD() {
        if (defaults != null) {
            return defaults.getDidYouMeantDeadLine() + "";
        }
        return setup.getDidYouMeantDeadLine() + "";
    }

    private String wasDDC() {
        if (defaults != null) {
            return defaults.getDidYouMeantCount() + "";
        }
        return setup.getDidYouMeantCount() + "";
    }

    private String wasRecords() {
        if (defaults != null) {
            return defaults.getRecords() + "";
        }
        return setup.getShowRecords() + "";
    }

    private String wasStartAt() {
        if (defaults != null && !defaults.isWasFromPage()) {
            return defaults.getstartAt() + "";
        }
        return "";
    }

    private String wasInfoA() {
        if (defaults != null) {
            return defaults.getInfoAfter() + "";
        }
        return setup.getShowAfter() + "";
    }

    private String wasInfoB() {
        if (defaults != null) {
            return defaults.getInfoBefore() + "";
        }
        return setup.getShowBefore() + "";
    }

    private String wasMS() {
        if (defaults != null) {
            return defaults.getInfoShow() + "";
        }
        return setup.getMaxShow() + "";
    }

    private String wasML() {
        if (defaults != null) {
            return defaults.getInfoLoad() + "";
        }
        return setup.getMaxLoad() + "";
    }

    public WebParams getDefaults() {
        return defaults;
    }

    private String getMergeText() {
        if (setup.isMergeResults()) {
            return "don't merge results";
        } else {
            return "merge both indexes";

        }
    }

    private String getInfoText() {
        if (setup.isNoInfo()) {
            return "show text-out info";
        } else {
            return "hide text-out info";
        }
    }

    private String getPdfText() {
        if (setup.isNoPdfInfo()) {
            return "load also info from pdfs";
        } else {
            return "dont load info from pdfs";
        }
    }

    private String getWasInfoSelcted() {
        if (defaults != null) {
            if (defaults.isNegateInfo()) {
                return CHECKED;
            } else {
                return "";
            }
        }
        if (setup.isNoInfo()) {
            return "";
        }
        return CHECKED;
    }

    private String getCheckedMerge() {
        if (defaults != null) {
            if (defaults.isNegateMerge()) {
                return CHECKED;
            } else {
                return "";
            }
        }
        if (setup.isMergeResults()) {
            return CHECKED;
        }
        return "";
    }

    private String getWasshowAlsoPdfInfoSeelcted() {
        if (defaults != null) {
            if (defaults.isNegatePdf()) {
                return CHECKED;
            } else {
                return "";
            }
        }
        if (!setup.isNoPdfInfo()) {
            return CHECKED;
        }
        return "";
    }

}

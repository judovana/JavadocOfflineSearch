/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.formatters;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javadocofflinesearch.server.WebParams;
import javadocofflinesearch.tools.Commandline;
import javadocofflinesearch.tools.LibraryManager;
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
    public static final String higlight = Commandline.HIGHLIGHT;
    public static final String jump = "jump";
    public static final String pdf2txt = "pdf2txt";
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
    public static final String jumpPrefix = "JDoS";

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
        out.println("<big><b><a href='" + href + createCommand() + "'>" + url + "</a>: </b></big>" + page + "/" + score + "<br/>");
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
        out.println("<a href=\"https://lucene.apache.org/core/2_9_4/queryparsersyntax.html\" >basic lucene query sintax</a> see indexes: file://path page/lucene. Library:");
        out.println("<select name='" + library + "'>");
        Set<String> l = javadocofflinesearch.JavadocOfflineSearch.listLibraries();
        for (String l1 : l) {
            out.println("<option value='" + l1 + "' " + selected(l1) + " >" + l1 + "</option>");
        }
        out.println("</select>");
        out.println("<br/>");
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
        out.println("  <u>Both "+higlight+" and "+jump+" are transforming output text without any futher checks. May break a lot (broken links...)!</u><br/>");
        out.println("  <input type=\"checkbox\" name=\"" + higlight + "\" value=\"true\" " + getCheckedHighlight() + "/> " + " Highlight. You must <b>rerun</b> the search(html only)" + "<br/>");
        out.println("  <u>"+jump+" may lead to invisible part. Try adjust the number behind anchor <b>#</b>. You can check also individual tokens.</u><br/>");
        out.println("  <input type=\"checkbox\" name=\"" + jump + "\" value=\"true\" " + getCheckedJump() + "/> " + " Jump to (first of) matches. You must <b>rerun</b> the search(html only)" + "<br/>");
        out.println("  <input type=\"checkbox\" name=\"" + pdf2txt + "\" value=\"true\" " + getCheckedPdf2Text() + "/> " + " Allow jump and highlight in pdfs. You must <b>rerun</b> the search(html only)" + "<br/>");
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

    private String getCheckedHighlight() {
        if (defaults != null) {
            if (defaults.isHighlight()) {
                return CHECKED;
            } else {
                return "";
            }
        }
        return "";
    }

    private String getCheckedJump() {
        if (defaults != null) {
            if (defaults.isJump()) {
                return CHECKED;
            } else {
                return "";
            }
        }
        return "";
    }

    private String getCheckedPdf2Text() {
        if (defaults != null) {
            if (defaults.isPdf2txt()) {
                return CHECKED;
            } else {
                return "";
            }
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

    private String selected(String l1) {
        if (defaults != null) {
            if (defaults.getLibrary() != null) {
                if (l1.equals(defaults.getLibrary())) {
                    return "selected";
                } else {
                    return "";
                }
            } else {
                if (l1.equals(LibraryManager.getDefaultLIbrary())) {
                    return "selected";
                } else {
                    return "";
                }
            }
        } else {
            if (l1.equals(LibraryManager.getDefaultLIbrary())) {
                return "selected";
            } else {
                return "";
            }
        }
    }

    private String createCommand() {
        if (defaults != null) {
            if (defaults.isHighlight() || defaults.isInfo() || defaults.isPdf2txt()) {

                List<String> chunks = new ArrayList<>(4);
                chunks.add(query + "=" + defaults.getQuery());
                if (defaults.isHighlight()) {
                    chunks.add(higlight + "=true");
                }
                if (defaults.isJump()) {
                    chunks.add(jump + "=true");
                }
                if (defaults.isPdf2txt()) {
                    chunks.add(pdf2txt + "=true");
                }
                StringBuilder sb = new StringBuilder("?");
                for (int i = 0; i < chunks.size(); i++) {
                    String get = chunks.get(i);
                    sb.append(get);
                    if (i < chunks.size() - 1) {
                        sb.append("&");
                    }
                }
                if (defaults.isJump()) {
                    sb.append("#" + jumpPrefix + "-all-1");
                }
                return sb.toString();
            }
        }
        return "";
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.server;

import java.io.PrintStream;
import java.net.URLDecoder;
import javadocofflinesearch.SearchSettings;
import javadocofflinesearch.formatters.Formatter;
import javadocofflinesearch.formatters.SearchableHtmlFormatter;
import javadocofflinesearch.tools.Commandline;

/**
 *
 * @author jvanek
 */
public class WebParams implements SearchSettings {

    private String query;

    private boolean lucene = false;
    private boolean merge = false;
    private boolean noInfo = false;
    private boolean showPdfINfo = false;

    private boolean wasFromPage = false;

    private final String origQuery;

    private Integer infoBefore;
    private Integer infoAfter;

    private Integer ddmDeadline;
    private Integer ddmCount;

    private Integer startAt;
    private Integer records;

    public String getOrigQuery() {
        return origQuery;
    }

    public WebParams(String query) {
        this.origQuery = query;
        //URLDecoder.decode(origQuery, "utf-8");
        String[] items = origQuery.split("&");
        for (String item : items) {
            String[] pair = item.split("=");
            if (pair.length != 2) {
                System.err.println("Invalid query for " + item);
            } else {
                if (pair[0].equalsIgnoreCase(SearchableHtmlFormatter.query)) {
                    this.query = decode(pair[1]);
                } else if (pair[0].equalsIgnoreCase(SearchableHtmlFormatter.searchType)) {
                    if (pair[1].equalsIgnoreCase("lucene-index")) {
                        lucene = true;
                    }
                } else if (pair[0].equalsIgnoreCase(SearchableHtmlFormatter.merge)) {
                    if (pair[1].equalsIgnoreCase("true")) {
                        merge = true;
                    }
                } else if (pair[0].equalsIgnoreCase(SearchableHtmlFormatter.noInfo)) {
                    if (pair[1].equalsIgnoreCase("true")) {
                        noInfo = true;
                    }
                } else if (pair[0].equalsIgnoreCase(SearchableHtmlFormatter.showAlsoPdfInfo)) {
                    if (pair[1].equalsIgnoreCase("true")) {
                        showPdfINfo = true;
                    }
                }//now less usefull stuff 
                else if (pair[0].equalsIgnoreCase(SearchableHtmlFormatter.infoBefore)) {
                    if (pair[1].trim().length() > 0) {
                        infoBefore = Integer.valueOf(pair[1].trim());
                    } else {
                        infoBefore = null;
                    }
                } else if (pair[0].equalsIgnoreCase(SearchableHtmlFormatter.infoAfter)) {
                    if (pair[1].trim().length() > 0) {
                        infoAfter = Integer.valueOf(pair[1].trim());
                    } else {
                        infoAfter = null;
                    }
                } else if (pair[0].equalsIgnoreCase(SearchableHtmlFormatter.ddmDeadline)) {
                    if (pair[1].trim().length() > 0) {
                        ddmDeadline = Integer.valueOf(pair[1].trim());
                    } else {
                        ddmDeadline = null;
                    }
                } else if (pair[0].equalsIgnoreCase(SearchableHtmlFormatter.ddmCount)) {
                    if (pair[1].trim().length() > 0) {
                        ddmCount = Integer.valueOf(pair[1].trim());
                    } else {
                        ddmCount = null;
                    }
                } else if (pair[0].equalsIgnoreCase(SearchableHtmlFormatter.startAt)) {
                    if (pair[1].trim().length() > 0) {
                        startAt = Integer.valueOf(pair[1].trim());
                    } else {
                        startAt = null;
                    }
                } else if (pair[0].equalsIgnoreCase(SearchableHtmlFormatter.records)) {
                    if (pair[1].trim().length() > 0) {
                        records = Integer.valueOf(pair[1].trim());
                    } else {
                        records = null;
                    }
                } else if (pair[0].equalsIgnoreCase(SearchableHtmlFormatter.bypage)) {
                    if (pair[1].trim().equals("true")) {
                        wasFromPage = true;
                    }
                }

            }
        }

    }

    public String getQuery() {
        return query;
    }

    @Override
    public boolean isInfo() {
        return !noInfo;
    }

    @Override
    public boolean isPage() {
        return !lucene;
    }

    /**
     * for sake of record.
     *
     * @return no longer of course override
     * @Overrode
     */
    public boolean isFileForced() {
        //http://kb.mozillazine.org/Links_to_local_pages_do_not_work
        return false;
    }

    @Override
    public boolean isOmitArchives() {
        //no need to epxose this via web
        return false;
    }
    
    @Override
    public boolean isOmitPdfInfo() {
        return !showPdfINfo;
    }
    

    @Override
    public boolean isMergeWonted() {
        return merge;
    }

    @Override
    public int getInfoBefore() {
        if (infoBefore == null) {
            return -Math.abs(Commandline.defaultBefore);
        } else {
            return -Math.abs(infoBefore);
        }
    }

    @Override
    public int getInfoAfter() {
        if (infoAfter == null) {
            return Commandline.defaultAfter;
        } else {
            return infoAfter;
        }
    }

    @Override
    public int getDidYouMeantDeadLine() {
        if (ddmDeadline == null) {
            return Commandline.didYouMeantDeadLine;
        } else {
            return ddmDeadline;
        }
    }

    @Override
    public int getDidYouMeantCount() {
        if (ddmCount == null) {
            return Commandline.didYouMeantCount;
        } else {
            return ddmCount;
        }
    }

    @Override
    public int getstartAt() {
        if (startAt == null) {
            return 0;
        } else {
            return startAt;
        }
    }

    @Override
    public int getRecords() {
        if (records == null) {
            return Integer.MAX_VALUE;
        } else {
            return records;
        }
    }

    public boolean isWasFromPage() {
        return wasFromPage;
    }

    @Override
    public Formatter createFormatter(PrintStream out) {
        return new SearchableHtmlFormatter(out, this);
    }

    private String decode(String pair) {
        try {
            return URLDecoder.decode(pair, "utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return pair;
        }
    }

}

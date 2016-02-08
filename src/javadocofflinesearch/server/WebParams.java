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

/**
 *
 * @author jvanek
 */
public class WebParams implements SearchSettings {

    private String query;
    private boolean lucene = false;
    private boolean merge = false;
    private final String origQuery;

    public WebParams(String query) {
        this.origQuery = query;
        //URLDecoder.decode(origQuery, "utf-8");
        String[] items = origQuery.split("&");
        for (String item : items) {
            String[] pair = item.split("=");
            if (pair.length != 2) {
                System.err.println("Invalid query for " + item);
            } else {
                if (pair[0].equalsIgnoreCase("query")) {
                    this.query = decode(pair[1]);
                } else if (pair[0].equalsIgnoreCase("search-type")) {
                    if (pair[1].equalsIgnoreCase("lucene-index")) {
                        lucene = true;
                    }
                }else if (pair[0].equalsIgnoreCase("merge")) {
                    if (pair[1].equalsIgnoreCase("true")) {
                        merge = true;
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
        return true;
    }

    @Override
    public boolean isPage() {
        return !lucene;
    }

    @Override
    public boolean isFileForced() {
        //http://kb.mozillazine.org/Links_to_local_pages_do_not_work
        return false;
    }

    @Override
    public boolean isMergeWonted() {
        return merge;
    }

    @Override
    public int getInfoBefore() {
        return -40;
    }

    @Override
    public int getInfoAfter() {
        return 40;
    }

    @Override
    public int getDidYouMeantDeadLine() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getDidYouMeantCount() {
        return 10;
    }

    @Override
    public Formatter createFormatter(PrintStream out) {
        return new SearchableHtmlFormatter(out, this);
    }

    @Override
    public int getstartAt() {
        return 0;
    }

    @Override
    public int getRecords() {
        return Integer.MAX_VALUE;
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

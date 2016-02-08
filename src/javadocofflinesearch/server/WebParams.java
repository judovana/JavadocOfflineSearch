/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.server;

import java.io.PrintStream;
import javadocofflinesearch.SearchSettings;
import javadocofflinesearch.formatters.Formatter;
import javadocofflinesearch.formatters.StaticHtmlFormatter;

/**
 *
 * @author jvanek
 */
public class WebParams implements SearchSettings {

    private final String query;

    public WebParams(String query) {
        this.query = query;
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
        return true;
    }

    @Override
    public boolean isFileForced() {
        return true;
    }

    @Override
    public boolean isMergeWonted() {
        return true;
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
        return new StaticHtmlFormatter(out);
    }

    @Override
    public int getstartAt() {
        return 0;
    }

    @Override
    public int getRecords() {
        return Integer.MAX_VALUE;
    }

}

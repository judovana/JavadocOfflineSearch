/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.formatters;

import java.util.List;

/**
 *
 * @author jvanek
 */
public interface Formatter {

    public String highlitStart();

    public String highlightEnd();
    
    public void haders();
    public void tail();

    public void title(int i, int totalHits, String title);

    public void file(String string, int page, float score);

    public void searchStarted(String info, String what);

    public void couldYouMeant(String title, List<String>... l);

    public void resulsSummary(String foundTitle, int totalHits, String timeTitle, long time, String units);

    public void summary(String path, String queryString, int infoBefore, int infoAfter);

    public void initializationFailed(String s);

    public void resultsIn(String title, long l, String unit);
    
    public void pages(int from, int to, int total);
    
}

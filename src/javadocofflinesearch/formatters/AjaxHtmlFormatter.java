/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.formatters;

import java.io.PrintStream;


/**
 *Was intended to dont include the extracted testxts, but without content length browsers do ojust ok. so no need to implement this ever.
 * 
 * @author jvanek
 */
public class AjaxHtmlFormatter extends SearchableHtmlFormatter{

    public AjaxHtmlFormatter(PrintStream out) {
        super(out);
    }
    
     @Override
    public void summary(String path, String queryString, int infoBefore, int infoAfter) {
        out.println("<div>");
        out.println("strange query to server");
        out.println("</div>");
    }
 
    
}

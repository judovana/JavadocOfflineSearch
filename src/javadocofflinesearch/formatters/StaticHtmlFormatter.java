/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.formatters;

/**
 *
 * @author jvanek
 */
public class StaticHtmlFormatter implements Formatter{

    @Override
    public Object highlitStart() {
        return "<b>";
    }

    @Override
    public Object highlightEnd() {
        return "</b>";
    }
 
    
}

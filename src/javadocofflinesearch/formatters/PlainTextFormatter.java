/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.formatters;

import java.io.PrintStream;

/**
 *
 * @author jvanek
 */
public class PlainTextFormatter implements Formatter{
    private final PrintStream out;

    public PlainTextFormatter(PrintStream out) {
        this.out = out;
    }

    @Override
    public Object highlitStart() {
        return "*";
    }

    @Override
    public Object highlightEnd() {
        return "*";
    }
 
    
}

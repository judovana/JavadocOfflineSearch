/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch;

import java.io.PrintStream;
import javadocofflinesearch.formatters.Formatter;

/**
 *
 * @author jvanek
 */
public interface SearchSettings {

    public boolean isInfo();

    public boolean isPage();

    public boolean isMergeWonted();

    public int getInfoBefore();

    public int getInfoAfter();

    public int getDidYouMeantDeadLine();

    public int getDidYouMeantCount();

    public Formatter createFormatter(PrintStream out);

    public int getstartAt();

    public int getRecords();

    public boolean isOmitArchives();
    
    public boolean isOmitPdfInfo();
}

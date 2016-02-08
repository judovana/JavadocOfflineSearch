/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.lucene;

import java.io.File;
import java.nio.file.Path;

/**
 *
 * @author jvanek
 */
public interface IndexerSettings {

    public Path[] getDirs();

    public boolean isFilenameCaseInsensitiveIncluded(String fileName);

    public boolean isPathCaseInsensitiveIncluded(File path);

    public boolean isExcldedFileIncludedInRanks();

    public boolean isSuffixCaseInsensitiveIncluded(String f);
    
}

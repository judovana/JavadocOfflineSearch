/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Properties;
import javadocofflinesearch.lucene.IndexerSettings;

/**
 *
 * @author jvanek
 */
public class Setup implements IndexerSettings {

    private static final String DIRS = "docs.dirs";
    public static final String VALUE = "/usr/share/javadoc/java";

    private static final String NLE = "ignore.name.lower.equals";
    private static final String NLE_VALUE = "allclasses-noframe.html:object.html:deprecated-list.html:index-1.html:overview-summary.html:package-frame.html";
    private static final String PLC = "ignore.path.lower.contains";
    private static final String PLC_VALUE = "/class-use/";
    private static final String INCLUDE = "ignore.include.forpageandvoc";
    private static final boolean INCLUDE_VAL = false;
    private static final String SUFFIXES="ignore.lower.endwith";
    private static final String SUFFIXES_VAl=".jpeg:.jpg:.png:.gif";
    
    private final File MAIN_CONFIG;
    Properties p = new Properties();

    public File getMAIN_CONFIG() {
        return MAIN_CONFIG;
    }

    public Setup(File CONFIG) {
        MAIN_CONFIG = new File(CONFIG, "javadocOfflineSearch.properties");
        p.setProperty(DIRS, VALUE);
    }

    public void load() throws IOException {
        if (MAIN_CONFIG.exists()) {
            p.load(new InputStreamReader(new FileInputStream(MAIN_CONFIG)));
        } else {
            System.out.println(MAIN_CONFIG.getAbsoluteFile() + " dont exists. Creating with defaults.");

            p.setProperty(DIRS, VALUE);
            p.setProperty(NLE, NLE_VALUE);
            p.setProperty(PLC, PLC_VALUE);
            p.setProperty(INCLUDE, String.valueOf(INCLUDE_VAL));
            p.setProperty(SUFFIXES, SUFFIXES_VAl);
            p.store(new FileOutputStream(MAIN_CONFIG), null);
        }
    }

    private String[] getDirsString() {
        String[] dirs = p.getProperty(DIRS).split(File.pathSeparator);
        return dirs;
    }

    private String[] getNLE() {
        String[] ignredNames = p.getProperty(NLE).split(File.pathSeparator);
        return ignredNames;
    }
    
     private String[] getSuffixes() {
        String[] ignredSuffs = p.getProperty(SUFFIXES).split(File.pathSeparator);
        return ignredSuffs;
    }

    private String[] getPLC() {
        String[] ignredPaths = p.getProperty(PLC).split(File.pathSeparator);
        return ignredPaths;
    }

    private Boolean getInclude1() {
        Boolean val = Boolean.valueOf(p.getProperty(INCLUDE));
        return val;
    }

    @Override
    public boolean isFilenameCaseInsensitiveIncluded(String fileName) {
        fileName = fileName.toLowerCase();
        String[] v = getNLE();
        for (String v1 : v) {
            if (v1.equals(fileName)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean isSuffixCaseInsensitiveIncluded(String fileName) {
        fileName = fileName.toLowerCase();
        String[] sfxes = getSuffixes();
        for (String sfx : sfxes) {
            if (fileName.endsWith(sfx)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isPathCaseInsensitiveIncluded(File path) {
        String p = path.getAbsolutePath().toLowerCase();
        String[] v = getPLC();
        for (String v1 : v) {
            if (p.contains(v1)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isExcldedFileIncludedInRanks() {
        return getInclude1();
    }

    @Override
    public Path[] getDirs() {
        String[] dirs = getDirsString();
        Path[] r = new Path[dirs.length];
        for (int i = 0; i < dirs.length; i++) {
            String dir = dirs[i];
            r[i] = new File(dir).toPath();

        }
        return r;
    }

}

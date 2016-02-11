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
public class LibrarySetup implements IndexerSettings {

    private static final String DIRS = "docs.dirs";
    public static final String VALUE = "/usr/share/javadoc/java";
    public static final String SEMICOLON = ";";

    private static final String NLE = "ignore.name.lower.equals";
    private static final String NLE_VALUE = "allclasses-noframe.html" + SEMICOLON
            + "object.html" + SEMICOLON + "deprecated-list.html" + SEMICOLON + "index-1.html" + SEMICOLON + "overview-summary.html" + SEMICOLON + "package-frame.html";
    private static final String PLC = "ignore.path.lower.contains";
    private static final String PLC_VALUE = "/class-use/";
    private static final String INCLUDE = "ignore.include.forpageandvoc";
    private static final boolean INCLUDE_VAL = false;
    private static final String SUFFIXES = "ignore.lower.endwith";
    private static final String SUFFIXES_VAl = ".jpeg" + SEMICOLON + ".jpg" + SEMICOLON + ".png" + SEMICOLON + ".gif";

    private static final String SECURITY = "security";

    private static final String DEF_OW_showBefore = "overwrite." + Commandline.INFO_BEFORE;
    private static final String DEF_OW_showAfter = "overwrite." + Commandline.INFO_AFTER;
    private static final String DEF_OW_deadine = "overwrite." + Commandline.DEAD_LINE;
    private static final String DEF_OW_sugcount = "overwrite." + Commandline.DID_COUNT;
    private static final String DEF_OW_perpage = "overwrite." + Commandline.RECORDS;
    private static final String DEF_OW_lucene = "overwrite." + Commandline.LUCENE_RANK;
    private static final String DEF_OW_noinfo = "overwrite." + Commandline.NO_INFO;
    private static final String DEF_OW_merge = "overwrite." + Commandline.MERGE_COMAPRATORS;
    private static final String DEF_OW_pdfs = "overwrite." + Commandline.NO_PDF_INFO;
    private static final String DEF_OW_previewMaxLoad = "overwrite.previewMaxLoad";
    private static final String DEF_OW_previewMaxShow = "overwrite.previewMaxShow";

    public static String configName = "javadocOfflineSearch.properties";

    private final File MAIN_CONFIG;
    private final Properties p = new Properties();
    private final LibrarySetup parent;

    public void preload() throws IOException {
        loadImpl();
    }

    public boolean isFileValid(String potentionalFile) {
        if (!isSecurity()) {
            return true;
        }
        String[] l = getDirsString();
        for (String s : l) {
            if (potentionalFile.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public File getMAIN_CONFIG() {
        return MAIN_CONFIG;
    }

    LibrarySetup(File CONFIG, LibrarySetup parent) {
        MAIN_CONFIG = new File(CONFIG, configName);
        this.parent = parent;

    }

    private boolean loaded = false;

    private void load() {
        try {
            loadImpl();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadImpl() throws IOException {
        if (loaded) {
            return;
        }
        loaded = true;
        if (MAIN_CONFIG.exists()) {
            p.load(new InputStreamReader(new FileInputStream(MAIN_CONFIG)));
        } else {
            System.out.println(MAIN_CONFIG.getAbsoluteFile() + " dont exists. Creating with defaults.");
            if (parent != null) {
                //to the "son" we put only where to search
                p.setProperty(DIRS, VALUE);
            } else {
                //allother setting is in main setup
                //but is overwritebale from  "son"
                p.setProperty(NLE, NLE_VALUE);
                p.setProperty(PLC, PLC_VALUE);
                p.setProperty(INCLUDE, String.valueOf(INCLUDE_VAL));
                p.setProperty(SUFFIXES, SUFFIXES_VAl);

                p.setProperty(SECURITY, String.valueOf(HardcodedDefaults.security));

                p.setProperty(DEF_OW_showBefore, String.valueOf(HardcodedDefaults.defaultBefore));
                p.setProperty(DEF_OW_showAfter, String.valueOf(HardcodedDefaults.defaultBefore));
                p.setProperty(DEF_OW_deadine, String.valueOf(HardcodedDefaults.didYouMeantDeadLine));
                p.setProperty(DEF_OW_sugcount, String.valueOf(HardcodedDefaults.didYouMeantCount));
                p.setProperty(DEF_OW_perpage, String.valueOf(HardcodedDefaults.showRecordsDefault));
                p.setProperty(DEF_OW_lucene, String.valueOf(HardcodedDefaults.luceneByDefault));
                p.setProperty(DEF_OW_noinfo, String.valueOf(HardcodedDefaults.noInfo));
                p.setProperty(DEF_OW_merge, String.valueOf(HardcodedDefaults.mergeResults));
                p.setProperty(DEF_OW_pdfs, String.valueOf(HardcodedDefaults.noPdfInfo));
                p.setProperty(DEF_OW_previewMaxLoad, String.valueOf(HardcodedDefaults.infoLoad));
                p.setProperty(DEF_OW_previewMaxShow, String.valueOf(HardcodedDefaults.infoShow));
            }
            p.store(new FileOutputStream(MAIN_CONFIG), "All lists are semicolon separated. When security is true, server is not returning not-inidexed files.");
        }
        if (parent != null) {
            parent.loadImpl();
        }
    }

    private String[] getDirsString() {
        String[] dirs = getSafeValue(DIRS).split(SEMICOLON);
        return dirs;
    }

    private String[] getNLE() {
        String[] ignredNames = getSafeValue(NLE).split(SEMICOLON);
        return ignredNames;
    }

    private String[] getSuffixes() {
        String[] ignredSuffs = getSafeValue(SUFFIXES).split(SEMICOLON);
        return ignredSuffs;
    }

    private String[] getPLC() {
        String[] ignredPaths = getSafeValue(PLC).split(SEMICOLON);
        return ignredPaths;
    }

    private Boolean getInclude1() {
        Boolean val = getBoolean(INCLUDE);
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
        String plc = path.getAbsolutePath().toLowerCase();
        String[] v = getPLC();
        for (String v1 : v) {
            if (plc.contains(v1)) {
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

    private String getSafeValue(String s) {
        load();
        String thisOnesValue = p.getProperty(s);
        if (thisOnesValue == null) {
            if (parent != null) {
                return parent.getSafeValue(s);
            }
        }
        return thisOnesValue;
    }

    private Boolean getBoolean(String s) {
        String stringValue = getSafeValue(s);
        if (stringValue == null) {
            return null;
        }
        return Boolean.valueOf(stringValue);
    }

    private Integer getInteger(String s) {
        String stringValue = getSafeValue(s);
        if (stringValue == null) {
            return null;
        }
        return Integer.valueOf(stringValue);
    }

    public Boolean isSecurity() {

        Boolean i = getBoolean(SECURITY);
        if (i != null) {
            return i;
        }
        return HardcodedDefaults.security;

    }

    public Integer getShowBefore() {
        Integer i = getInteger(DEF_OW_showBefore);
        if (i != null) {
            return i;
        }
        return HardcodedDefaults.defaultBefore;
    }

    public Integer getShowAfter() {
        Integer i = getInteger(DEF_OW_showAfter);
        if (i != null) {
            return i;
        }
        return HardcodedDefaults.defaultAfter;
    }

    public Integer getDidYouMeantDeadLine() {
        Integer i = getInteger(DEF_OW_deadine);
        if (i != null) {
            return i;
        }
        return HardcodedDefaults.didYouMeantDeadLine;
    }

    public Integer getDidYouMeantCount() {
        Integer i = getInteger(DEF_OW_sugcount);
        if (i != null) {
            return i;
        }
        return HardcodedDefaults.didYouMeantCount;
    }

    public Integer getShowRecords() {
        Integer i = getInteger(DEF_OW_perpage);
        if (i != null) {
            return i;
        }
        return HardcodedDefaults.showRecordsDefault;
    }

    public Boolean isLucenePreffered() {
        Boolean i = getBoolean(DEF_OW_lucene);
        if (i != null) {
            return i;
        }
        return HardcodedDefaults.luceneByDefault;
    }

    public Boolean isNoInfo() {
        Boolean i = getBoolean(DEF_OW_noinfo);
        if (i != null) {
            return i;
        }
        return !HardcodedDefaults.noInfo;
    }

    public Boolean isMergeResults() {
        Boolean i = getBoolean(DEF_OW_merge);
        if (i != null) {
            return i;
        }
        return HardcodedDefaults.mergeResults;
    }

    public Boolean isNoPdfInfo() {
        Boolean i = getBoolean(DEF_OW_pdfs);
        if (i != null) {
            return i;
        }
        return HardcodedDefaults.noPdfInfo;
    }

    public Integer getMaxLoad() {
        Integer i = getInteger(DEF_OW_previewMaxLoad);
        if (i != null) {
            return i;
        }
        return HardcodedDefaults.infoLoad;
    }

    public Integer getMaxShow() {
        Integer i = getInteger(DEF_OW_previewMaxShow);
        if (i != null) {
            return i;
        }
        return HardcodedDefaults.infoShow;
    }

    /**
     *
     * @author jvanek
     */
    static class HardcodedDefaults {

        //those are pkg public onlyy because of usage in help
        static final int defaultBefore = 40;
        static final int defaultAfter = 40;
        static final int didYouMeantDeadLine = 10;
        static final int didYouMeantCount = 10;
        static final int showRecordsDefault = Integer.MAX_VALUE;

        private static final boolean security = true;
        static final int infoLoad = 60;
        static final int infoShow = 20;

        private static final boolean luceneByDefault = false;
        private static final boolean noInfo = false;
        private static final boolean mergeResults = false;
        static final boolean omitArchives = false; //only commandline switch
        private static final boolean noPdfInfo = false;

    }

}

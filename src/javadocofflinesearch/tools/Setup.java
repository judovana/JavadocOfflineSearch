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

    private final File MAIN_CONFIG;
    Properties p = new Properties();

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

    public void preload() throws IOException {
        loadImpl();
    }

    public boolean isFileValid(String potentionalFile) {
        if (!HardcodedDefaults.isSecurity()) {
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

    private static class SetupHolder {

        private static Setup INSTANCE;

        public static Setup create(File configDir) {
            INSTANCE = new Setup(configDir);
            return INSTANCE;
        }

        public static Setup getInstance() {
            return INSTANCE;
        }
    }

    public static Setup getSetup() {
        return SetupHolder.getInstance();
    }

    public static Setup createSetup(File congifDir) {
        return SetupHolder.create(congifDir);
    }

    public File getMAIN_CONFIG() {
        return MAIN_CONFIG;
    }

    private Setup(File CONFIG) {
        MAIN_CONFIG = new File(CONFIG, "javadocOfflineSearch.properties");
        p.setProperty(DIRS, VALUE);
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

            p.setProperty(DIRS, VALUE);
            p.setProperty(NLE, NLE_VALUE);
            p.setProperty(PLC, PLC_VALUE);
            p.setProperty(INCLUDE, String.valueOf(INCLUDE_VAL));
            p.setProperty(SUFFIXES, SUFFIXES_VAl);

            p.setProperty(SECURITY, String.valueOf(HardcodedDefaults.isSecurity()));

            p.setProperty(DEF_OW_showBefore, String.valueOf(HardcodedDefaults.getDefaultBefore()));
            p.setProperty(DEF_OW_showAfter, String.valueOf(HardcodedDefaults.getDefaultBefore()));
            p.setProperty(DEF_OW_deadine, String.valueOf(HardcodedDefaults.getDidYouMeantDeadLine()));
            p.setProperty(DEF_OW_sugcount, String.valueOf(HardcodedDefaults.getDidYouMeantCount()));
            p.setProperty(DEF_OW_perpage, String.valueOf(HardcodedDefaults.getShowRecordsDefault()));
            p.setProperty(DEF_OW_lucene, String.valueOf(HardcodedDefaults.isLuceneByDefault()));
            p.setProperty(DEF_OW_noinfo, String.valueOf(HardcodedDefaults.isNoInfo()));
            p.setProperty(DEF_OW_merge, String.valueOf(HardcodedDefaults.isMergeResults()));
            p.setProperty(DEF_OW_pdfs, String.valueOf(HardcodedDefaults.isNoPdfInfo()));
            p.setProperty(DEF_OW_previewMaxLoad, String.valueOf(HardcodedDefaults.getInfoLoad()));
            p.setProperty(DEF_OW_previewMaxShow, String.valueOf(HardcodedDefaults.getInfoShow()));

            p.store(new FileOutputStream(MAIN_CONFIG), "All lists are semicolon separated. When security is true, server is not returning not-inidexed files.");
        }
    }

    private String[] getDirsString() {
        load();
        String[] dirs = p.getProperty(DIRS).split(SEMICOLON);
        return dirs;
    }

    private String[] getNLE() {
        load();
        String[] ignredNames = p.getProperty(NLE).split(SEMICOLON);
        return ignredNames;
    }

    private String[] getSuffixes() {
        load();
        String[] ignredSuffs = p.getProperty(SUFFIXES).split(SEMICOLON);
        return ignredSuffs;
    }

    private String[] getPLC() {
        load();
        String[] ignredPaths = p.getProperty(PLC).split(SEMICOLON);
        return ignredPaths;
    }

    private Boolean getInclude1() {
        load();
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
////////////////////////////////hardcodes overwrites

    private static String getSafeValue(String s) {
        Setup setup = getSetup();
        if (setup == null) {
            return null;
        }
        return setup.p.getProperty(s);

    }

    private static Boolean getBoolean(String s) {
        String stringValue = getSafeValue(s);
        if (stringValue == null) {
            return null;
        }
        return Boolean.valueOf(stringValue);
    }

    private static Integer getInteger(String s) {
        String stringValue = getSafeValue(s);
        if (stringValue == null) {
            return null;
        }
        return Integer.valueOf(stringValue);
    }

    public static Boolean isSecurity() {

        return getBoolean(SECURITY);

    }

    public static Integer getShowBefore() {
        return getInteger(DEF_OW_showBefore);
    }

    public static Integer getShowAfter() {
        return getInteger(DEF_OW_showAfter);
    }

    public static Integer getDeadline() {
        return getInteger(DEF_OW_deadine);
    }

    public static Integer getCount() {
        return getInteger(DEF_OW_sugcount);
    }

    public static Integer getPerPage() {
        return getInteger(DEF_OW_perpage);
    }

    public static Boolean isLucenePreffered() {
        return getBoolean(DEF_OW_lucene);
    }

    public static Boolean isnoInfo() {
        return getBoolean(DEF_OW_noinfo);
    }

    public static Boolean isMergeWonted() {
        return getBoolean(DEF_OW_merge);
    }

    public static Boolean isPdfInfoSilenced() {
        return getBoolean(DEF_OW_pdfs);
    }

    public static Integer getMaxLoad() {
        return getInteger(DEF_OW_previewMaxLoad);
    }

    public static Integer getMaxShow() {
        return getInteger(DEF_OW_previewMaxShow);
    }

}

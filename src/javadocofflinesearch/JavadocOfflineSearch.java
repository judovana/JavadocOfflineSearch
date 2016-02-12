package javadocofflinesearch;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javadocofflinesearch.extensions.HrefCounter;
import javadocofflinesearch.extensions.Vocabulary;
import javadocofflinesearch.lucene.MainIndex;
import javadocofflinesearch.tools.Commandline;
import javadocofflinesearch.tools.LibraryManager;
import javadocofflinesearch.tools.LibrarySetup;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author jvanek
 */
public class JavadocOfflineSearch {

    private static final File CONFIG_HOME;
    private static final File CACHE_HOME;
    public static final File FIREFOX_HOME = new File(System.getProperty("user.home") + "/.mozilla/firefox/");
    public static final File CONFIG;
    public static final File CACHE;
    public static final int PORT = 31754;

    static {
        String XCFG = System.getenv("XDG_CONFIG_HOME");
        String XCCH = System.getenv("XDG_CACHE_HOME");
        if (XCFG == null) {
            CONFIG_HOME = new File(System.getProperty("user.home"), ".config");
        } else {
            CONFIG_HOME = new File(XCFG);
        }
        if (XCCH == null) {
            CACHE_HOME = new File(System.getProperty("user.home"), ".cache");
        } else {
            CACHE_HOME = new File(XCCH);
        }
        CONFIG = new File(CONFIG_HOME, "JavadocOfflineSearch");
        CACHE = new File(CACHE_HOME, "JavadocOfflineSearch");
    }

    //info "query" "file" nekolik snippetu kdy se tma to slovo vyslytuje
    //v pripade html to cche sync ajax = laod jeden, pak druhej...
    public static void main(String[] args) throws IOException, ParseException {

        Commandline cmds = new Commandline(args);
        cmds.parse();

        cmds.checkAll();
        CONFIG.mkdirs();
        CACHE.mkdirs();

        SingleSpaceInstance userInstance = new SingleSpaceInstance(CONFIG, CACHE, cmds);
        userInstance.run();

    }

    public static Set<String> listLibraries() {
        List<String> l1 = getConigLIbraries();
        List<String> l2 = getCacheLIbraries();
        Map<String, Integer> merged = new HashMap(l1.size());
        for (String l : l1) {
            Integer i = merged.get(l);
            if (i == null) {
                merged.put(l, 1);
            } else {
                merged.put(l, 1 + 1);
            }
        }
        for (String l : l2) {
            Integer i = merged.get(l);
            if (i == null) {
                merged.put(l, 1);
            } else {
                merged.put(l, 1 + 1);
            }
        }
        return merged.keySet();
    }

    private static List<String> listLibraries(File mainDir, String... excludes) {
        File[] fs = mainDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        List<String> result = new ArrayList<>(fs.length);
        for (File f : fs) {
            boolean found = false;
            for (String exclude : excludes) {
                if (f.getName().equals(exclude)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.add(f.getName());
            }

        }
        return result;
    }

    public static List<String> getConigLIbraries() {
        return listLibraries(CONFIG, LibrarySetup.configName, HrefCounter.customClicksName, LibraryManager.defaultLibDefName);
    }

    public static List<String> getCacheLIbraries() {
        return listLibraries(CACHE, HrefCounter.pageIndexName, Vocabulary.vocName, MainIndex.mainIndexName);
    }

}

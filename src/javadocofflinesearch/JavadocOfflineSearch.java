package javadocofflinesearch;

import java.io.File;
import java.io.IOException;
import javadocofflinesearch.tools.Commandline;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author jvanek
 */
public class JavadocOfflineSearch {

    private static final File CONFIG_HOME;
    private static final File CACHE_HOME;
    private static final File CONFIG;
    private static final File CACHE;
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
        
        SingleSpaceInstance userInstance =  new SingleSpaceInstance(CONFIG, CACHE, cmds);
        userInstance.run();

    }

}

package javadocofflinesearch;

import java.io.File;
import javadocofflinesearch.formatters.Formatter;
import javadocofflinesearch.lucene.InfoExtractor;
import javadocofflinesearch.lucene.MainIndex;
import javadocofflinesearch.tools.Commandline;
import javadocofflinesearch.tools.Setup;

/**
 *
 * @author jvanek
 */
public class SingleSpaceInstance {

    private final File cache;
    private final Commandline cmds;
    private final Setup setup;

    SingleSpaceInstance(File CONFIG, File CACHE, Commandline cmds) {
        cache = CACHE;
        setup = new Setup(CONFIG);
        this.cmds = cmds;
    }

    void run() {
        try {
            setup.load();
            if (cmds.hasServer()) {
                while (true) {
                    Thread.sleep(100);
                }
            } else if (cmds.isExctractInfo()) {
                String s = InfoExtractor.extract(cmds.getExctractInfo(), cmds.getQuery(), cmds.createFormatter(System.out),  cmds.getInfoBefore(), cmds.getInfoAfter());
                System.out.println(s);
            } else {
                MainIndex mainIndex = new MainIndex(cache);
                if (cmds.hasIndex()) {
                    mainIndex.index(setup.getDirs());
                    return;
                }
                if (!mainIndex.checkInitialized()){
                    System.out.println(mainIndex.printInitialized());
                    System.exit(10);
                }
                mainIndex.search(cmds.getQuery(), cmds);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

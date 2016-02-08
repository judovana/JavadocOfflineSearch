package javadocofflinesearch;

import java.io.File;
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
                cmds.createFormatter(System.out).summary(cmds.getExctractInfo(), cmds.getQuery(),   cmds.getInfoBefore(), cmds.getInfoAfter());
            } else {
                MainIndex mainIndex = new MainIndex(cache, setup);
                if (cmds.hasIndex()) {
                    mainIndex.index();
                    return;
                }
                if (!mainIndex.checkInitialized()){
                    cmds.createFormatter(System.out).initializationFailed(mainIndex.printInitialized());
                    System.exit(10);
                }
                mainIndex.search(cmds.getQuery(), cmds);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

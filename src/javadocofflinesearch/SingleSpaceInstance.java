package javadocofflinesearch;

import java.io.File;
import javadocofflinesearch.lucene.MainIndex;
import javadocofflinesearch.server.ServerLauncher;
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
    private final File config;

    SingleSpaceInstance(File CONFIG, File CACHE, Commandline cmds) {
        cache = CACHE;
        config = CONFIG;
        setup = new Setup(CONFIG);
        this.cmds = cmds;
    }

    void run() {
        try {
            setup.load();
            if (cmds.hasServer()) {
                ServerLauncher lServerLuncher = new ServerLauncher(JavadocOfflineSearch.PORT, setup, cache, config);
                Thread r = new Thread(lServerLuncher);
                r.setDaemon(true);
                r.start();
                while (true) {
                    Thread.sleep(100);
                }
            } else if (cmds.isExctractInfo()) {
                cmds.createFormatter(System.out).summary(cmds.getExctractInfo(), cmds.getQuery(), cmds.getInfoBefore(), cmds.getInfoAfter());
            } else {
                MainIndex mainIndex = new MainIndex(cache, config, setup);
                if (cmds.hasIndex()) {
                    mainIndex.index();
                    return;
                }
                if (!mainIndex.checkInitialized()) {
                    cmds.createFormatter(System.out).initializationFailed(mainIndex.printInitialized());
                    System.exit(10);
                }
                mainIndex.search(cmds.getQuery(), cmds, System.out);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

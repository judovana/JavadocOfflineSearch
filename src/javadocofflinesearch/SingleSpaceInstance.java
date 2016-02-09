package javadocofflinesearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
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
    private final File config;

    SingleSpaceInstance(File CONFIG, File CACHE, Commandline cmds) {
        cache = CACHE;
        config = CONFIG;
        Setup.createSetup(CONFIG);
        this.cmds = cmds;
    }

    void run() {
        try {
            Setup.getSetup().preload();
            if (cmds.hasServer()) {
                ServerLauncher lServerLuncher = new ServerLauncher(JavadocOfflineSearch.PORT,  cache, config);
                Thread r = new Thread(lServerLuncher);
                r.setDaemon(true);
                r.start();
                while (true) {
                    Thread.sleep(100);
                }
            } else if (cmds.isExctractInfo()) {
                cmds.createFormatter(System.out).summary(cmds.getExctractInfo(), cmds.getQuery(), cmds.getInfoBefore(), cmds.getInfoAfter());
            } else if (cmds.hasPrintFirefoxEngine()) {
                printFirefox();
            } else {
                MainIndex mainIndex = new MainIndex(cache, config);
                if (cmds.hasIndex()) {
                    mainIndex.index();
                    installFirefox();
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

    private static final String PLUGIN = "javadocOfflineSearch.xml";

    private void printFirefox() throws IOException {
        InputStream in = getClass().getResourceAsStream("/javadocofflinesearch/" + PLUGIN);
        try (BufferedReader input = new BufferedReader(new InputStreamReader(in))) {
            while (true) {
                String l = input.readLine();
                if (l == null) {
                    break;
                }
                System.out.println(l);
            }
        }
        File f = JavadocOfflineSearch.FIREFOX_HOME;
        if (!f.exists()) {
            System.out.println("You dont have mozilla compliant browser installed");
            return;
        }
        File[] ff1 = f.listFiles();
        if (ff1.length <= 2) {
            System.out.println("You probably dont have any valid profile");
            return;
        }
        System.out.println("You can stream the file to one of following dirs. Prefere Good ones over possible and try to avoid suspicious:");
        for (File mainDir : ff1) {
            if (mainDir.isDirectory()) {
                File subdir = new File(mainDir, "searchplugins");
                if (mainDir.getName().endsWith(".default") && subdir.exists()) {
                    System.out.println("  Good location:\n   " + subdir.getAbsolutePath());
                } else if (mainDir.getName().endsWith(".default")) {
                    System.out.println("  Possible location:\n   " + subdir.getAbsolutePath());
                } else {
                    System.out.println("  Suspicious location:\n   " + subdir.getAbsolutePath());
                }
            }
        }
    }

    private void installFirefox() throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream in = getClass().getResourceAsStream("/javadocofflinesearch/" + PLUGIN);
        try (BufferedReader input = new BufferedReader(new InputStreamReader(in))) {
            while (true) {
                String l = input.readLine();
                if (l == null) {
                    break;
                }
                sb.append(l);
            }
        }
        sb.append("<!-- installed:").append(new Date()).append("-->");
        File f = JavadocOfflineSearch.FIREFOX_HOME;
        if (!f.exists()) {
            System.out.println("Not installing search engine - You dont have mozilla compliant browser installed");
            return;
        }
        File[] ff1 = f.listFiles();
        if (ff1.length <= 2) {
            System.out.println("Not installing search engine - You probably dont have any valid profile");
            return;
        }
        System.out.println("Installing firefox search plugin to:");
        for (File mainDir : ff1) {
            if (mainDir.isDirectory()) {
                File subdir = new File(mainDir, "searchplugins");
                File mainFile = new File(subdir, PLUGIN);
                if (mainDir.getName().endsWith(".default") && subdir.exists()) {
                    System.err.println("  installed to:\n   " + subdir.getAbsolutePath());
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mainFile), "UTF-8"))) {
                        writer.write(sb.toString());
                    }
                } else if (mainDir.getName().endsWith(".default")) {
                    System.err.println("  installed to:\n   " + subdir.getAbsolutePath());
                    subdir.mkdirs();
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mainFile), "UTF-8"))) {
                        writer.write(sb.toString());
                    }
                } else {
                    System.err.println("  Skipped:\n   " + subdir.getAbsolutePath());
                }
            }
        }
    }

}

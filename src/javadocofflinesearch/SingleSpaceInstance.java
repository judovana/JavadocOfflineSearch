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
import java.util.List;
import javadocofflinesearch.extensions.Vocabulary;
import javadocofflinesearch.formatters.Formatter;
import javadocofflinesearch.lucene.InfoExtractor;
import javadocofflinesearch.lucene.MainIndex;
import javadocofflinesearch.server.ServerLauncher;
import javadocofflinesearch.tools.Commandline;
import javadocofflinesearch.tools.LibraryManager;

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
        LibraryManager.createtLibraryManager(config, cache);
        this.cmds = cmds;
    }

    void run() {
        try {
            LibraryManager.getLibraryManager().preload();
            if (cmds.hasServer()) {
                ServerLauncher lServerLuncher = new ServerLauncher(cmds.getPort());
                Thread r = new Thread(lServerLuncher);
                r.setDaemon(false);
                r.start();
            } else if (cmds.isExctractInfo()) {
                Formatter f = cmds.createFormatter(System.out);
                String s = f.summary(cmds.getExctractInfo(), cmds.getQuery(), cmds.getInfoBefore(), cmds.getInfoAfter());
                extendedDidYouMeant(s, f);
            } else if (cmds.isHighligt()) {
                Formatter f = cmds.createFormatter(System.out);
                String highlighted = InfoExtractor.highlightInDoc(cmds.getHighligtInfo(), cmds.getQuery(), f, true, false);
                System.out.println(highlighted);
                extendedDidYouMeant(highlighted, f);
            } else if (cmds.hasPrintFirefoxEngine()) {
                printFirefox();
            } else {
                MainIndex mainIndex = new MainIndex(cmds);
                if (cmds.hasIndex()) {
                    mainIndex.index();
                    installFirefox();
                    return;
                }
                if (!mainIndex.checkInitialized()) {
                    cmds.createFormatter(System.out).printLibrary(cmds.getLibrary());
                    cmds.createFormatter(System.out).initializationFailed(mainIndex.printInitialized());
                    System.exit(10);
                }
                mainIndex.search(cmds.getQuery(), cmds, System.out);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void extendedDidYouMeant(String s, Formatter f) {
        if (s.length() < 15) {
            List<Vocabulary> vcs = LibraryManager.getLibraryManager().getAllVocabularies();
            for (Vocabulary vc : vcs) {
                MainIndex.didYouMent(cmds.getQuery(), f, 10, vc);
            }
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

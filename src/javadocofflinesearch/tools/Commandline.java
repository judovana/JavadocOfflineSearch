/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.tools;

import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import javadocofflinesearch.SearchSettings;
import javadocofflinesearch.JavadocOfflineSearch;
import javadocofflinesearch.formatters.AjaxHtmlFormatter;
import javadocofflinesearch.formatters.ColoredPlainTextFormatter;
import javadocofflinesearch.formatters.Formatter;
import javadocofflinesearch.formatters.PlainTextFormatter;
import javadocofflinesearch.formatters.StaticHtmlFormatter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author jvanek
 */
public class Commandline implements SearchSettings {

    private final String[] args;
    private final Options options;
    private CommandLine line;

    private static final String HELP = "help";
    private static final String INDEX = "index";
    private static final String VERSION = "version";
    private static final String PAGE_RANK = "page-rank"; //those two ranks are not hardcoded on one place, because cmd/web is handling them differently
    static final String LUCENE_RANK = "lucene-rank";
    //pacakge private items are setupable in deployment.properties
    //public items are reused also in WebParams 
    public static final String NO_INFO = "no-info";
    private static final String MORE_INFO = "more-info";
    public static final String QUERY = "query";
    private static final String START_SERVER = "start-server";
    public static final String MERGE_COMAPRATORS = "merge-results";
    private static final String EXTRACT_INFO = "exctract-info";
    private static final String OUTPUT_COLOUR = "color";
    private static final String OUTPUT_HTML = "html";
    private static final String OUTPUT_AJAX = "ajax";
    private static final String OUTPUT_PLAIN = "plain";
    public static final String INFO_AFTER = "info-after";
    public static final String INFO_BEFORE = "info-before";
    public static final String DEAD_LINE = "did-you-mean-deadline";
    public static final String DID_COUNT = "did-you-mean-count";
    private static final int startAtDefault = 0;
    public static final String START_AT = "start-at";
    public static final String RECORDS = "records";
    private static final String ARCHIVES = "omit-archives";
    private static final String PRINT_ENGINE = "print-engine";
    public static final String NO_PDF_INFO = "no-pdf-info";
    public static final String LIBRARY = "library";
    public static final String LIBRARIES = "libraries";
    public static final String DEFAULT_LIBRARY = "default";

    public Commandline(String[] args) {
        Option help = new Option("h", HELP, false, "print this message");
        Option index = new Option("i", INDEX, false, "will index directories specified in config file. If config file for given library does not exists, you must specify the files on commandline");
        Option version = new Option("v", VERSION, false, "print the version information and exit");
        Option pagerank = new Option("p", PAGE_RANK, false, "will use page-rank for sorting before lucene one");
        Option lucenerank = new Option("l", LUCENE_RANK, false, "will use default lucene ranking");
        Option noinfo = new Option("n", NO_INFO, false, "will NOT show snippets of string usages under title and link");
        Option moreinfo = new Option("m", MORE_INFO, false, "will (default) show snippets of string usages under title and link");
        Option server = new Option("s", START_SERVER, false, "will start the server on port 31745. You can then search in browser by http://lcoalhost:" + JavadocOfflineSearch.PORT);
        Option merge = new Option("g", MERGE_COMAPRATORS, false, "will use both lucene and page sorting to determine results");
        Option exInfo = new Option("x", EXTRACT_INFO, true, "from given document, extract those ...info...  based on query");
        Option infoBefore = new Option("B", INFO_BEFORE, true, "number of characters between '...' and 'match'. default " + LibrarySetup.HardcodedDefaults.defaultBefore);
        Option infoAfter = new Option("A", INFO_AFTER, true, "number of characters between 'match' and '...'. default " + LibrarySetup.HardcodedDefaults.defaultAfter);
        Option didDead = new Option("d", DEAD_LINE, true, "min. number of result to occure before 'did you ment' is suggested. default " + LibrarySetup.HardcodedDefaults.didYouMeantDeadLine);
        Option didCount = new Option("D", DID_COUNT, true, "how meny 'did you ment' is suggested. default " + LibrarySetup.HardcodedDefaults.didYouMeantCount);
        Option startAtOpt = new Option("R", START_AT, true, "start at record #number. Default  " + startAtDefault);
        Option recordsOpt = new Option("r", RECORDS, true, "show number of recods #number. Default  " + LibrarySetup.HardcodedDefaults.showRecordsDefault);
        Option outputColor = new Option("c", OUTPUT_COLOUR, false, "will use colored shell output (default in terminal)");
        Option outputHtml = new Option("t", OUTPUT_HTML, false, "will force html marked up output");
        Option outputAjax = new Option("a", OUTPUT_AJAX, false, "will force html marked up with ajax info snippets (not finished, and probably never will)");
        Option outputPlain = new Option("y", OUTPUT_PLAIN, false, "will use simple palintext output (default out of terminal)");
        Option archives = new Option("z", ARCHIVES, false, "will ignore items from archvies from search results");
        Option pdfInfo = new Option("P", NO_PDF_INFO, false, "will not print info from pdfs");
        Option library = new Option("L", LIBRARY, true, " will index into /search in  specified library. If not set 'default' is used");
        Option libraries = new Option(LIBRARIES, false, " list avaible libraries");

        Option search = new Option("q", QUERY, true, "is considered default when no argument is given. Search docs. '-' connected wth word is NOT.");
        Option engine = new Option("e", PRINT_ENGINE, false, "will print out firefox's search engine to be used as firefox plugin");

        options = new Options();
        options.addOption(help);
        options.addOption(index);
        options.addOption(version);
        options.addOption(pagerank);
        options.addOption(lucenerank);
        options.addOption(noinfo);
        options.addOption(moreinfo);
        options.addOption(search);
        options.addOption(server);
        options.addOption(merge);
        options.addOption(exInfo);
        options.addOption(outputColor);
        options.addOption(outputHtml);
        options.addOption(outputAjax);
        options.addOption(outputPlain);
        options.addOption(infoAfter);
        options.addOption(infoBefore);
        options.addOption(didDead);
        options.addOption(didCount);
        options.addOption(startAtOpt);
        options.addOption(recordsOpt);
        options.addOption(archives);
        options.addOption(engine);
        options.addOption(pdfInfo);
        options.addOption(library);
        options.addOption(libraries);
        this.args = args;

    }

    public void parse() {
        CommandLineParser parser = new DefaultParser();
        try {
            line = parser.parse(options, args);
        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            System.exit(1);
        }
    }

    public void checkDupes() {
        if (hasMoreInfo() && hasNoInfo()) {
            System.out.println("only one of " + MORE_INFO + " or " + NO_INFO + " may be set.");
            System.exit(1);
        }
        if (hasPageRank() && hasLuceneRank()) {
            System.out.println("only one of " + PAGE_RANK + " or " + LUCENE_RANK + " may be set.");
            System.exit(1);
        }

    }

    private void checkLibraries() {
        if (line.hasOption(LIBRARIES)) {
            Set<String> merged = JavadocOfflineSearch.listLibraries();
            List<String> configList = JavadocOfflineSearch.getConigLIbraries();
            List<String> cacheList = JavadocOfflineSearch.getCacheLIbraries();
            if (merged.isEmpty()) {
                System.out.println("No library found. You did not run -index yet");
            } else {
                //first verify files
                System.out.println("    Files sanity:");
                for (String library : merged) {
                    verify(library);
                }
                //second only invalid libraries
                System.out.println("    Possibly corrupted libraries:");
                for (String library : merged) {
                    if (configList.contains(library) && cacheList.contains(library)) {
                        //System.out.println(library);
                    } else if (!configList.contains(library) && cacheList.contains(library)) {
                        System.out.println("WARNING only in cache folder - " + library);
                    } else if (configList.contains(library) && !cacheList.contains(library)) {
                        System.out.println("WARNING only in config folder - " + library);
                    } else {
                        System.out.println("Error, library from different planet - " + library);
                    }
                }
                //last only valid libraries
                System.out.println("    Libraries:");
                for (String library : merged) {
                    if (configList.contains(library) && cacheList.contains(library)) {
                        System.out.println(library);
                    } else if (!configList.contains(library) && cacheList.contains(library)) {
                        //System.out.println("WARNING only in cache folder - " + library);
                    } else if (configList.contains(library) && !cacheList.contains(library)) {
                        //System.out.println("WARNING only in config folder - " + library);
                    } else {
                        //System.out.println("Error, library from different planet - " + library);
                    }
                }
            }
            System.exit(0);
        }
    }

    public void checkHelp() {
        if (line.hasOption(HELP)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Javadoc offline search", options);
            System.out.println("When used from commandline, it is expected to run with `more`.");
            System.out.println("When run with `more` please set colors (-c) manually.");
            System.out.println("* java -jar JavadocOfflineSearch.jar -index");
            System.out.println("* java -jar JavadocOfflineSearch.jar  -query");
            System.out.println("* index all files in $XDG_CONFIG_DIR/JavadocOfflineSearch/javadocOfflineSearch.properties");
            System.out.println("* by default " + LibrarySetup.VALUE);
            System.out.println("to use firefox search plugin comaptible and/or commandline approach run:");
            System.out.println("* java -jar JavadocOfflineSearch.jar  -start-server & firefox");

            System.exit(0);
        }
    }

    public void checkVersion() {
        if (line.hasOption(HELP)) {
            System.out.println("Javadoc offline search v 1.0");
            System.exit(0);
        }
    }

    public void verifyIndex() {
        //FIXME-include query+configFile verification
        if (line.hasOption(INDEX) && line.hasOption(LIBRARY) && args.length != 3) {
            System.out.println("when " + INDEX + " is used with " + LIBRARY + " then line must have exactly three argments - index, library and name of library");
            System.exit(1);
        }
        if (line.hasOption(INDEX) && !line.hasOption(LIBRARY) && args.length != 1) {
            System.out.println(INDEX + " must be lone item.");
            System.exit(1);
        }
    }

    public void verifyServer() {
        if (line.hasOption(START_SERVER) && args.length != 1) {
            System.out.println(START_SERVER + " must be lone item.");
            System.exit(1);
        }
    }

    public void verifyFirefox() {
        if (line.hasOption(PRINT_ENGINE) && args.length != 1) {
            System.out.println(PRINT_ENGINE + " must be lone item.");
            System.exit(1);
        }
    }

    public boolean hasPrintFirefoxEngine() {
        return line.hasOption(PRINT_ENGINE);
    }

    public boolean hasServer() {
        return (line.hasOption(START_SERVER));
    }

    public void verifyQeury() {
        if (line.hasOption(QUERY) && line.getArgs().length != 0) {
            System.out.println("use " + QUERY + " OR noting. Not both.");
            System.exit(2);
        }
        if (!line.hasOption(QUERY) && line.getArgs().length != 1) {
            System.out.println("Only one argument is expected as query.");
            System.exit(3);
        }
    }

    public String getQuery() {
        if (line.hasOption(QUERY)) {
            return line.getOptionValue(QUERY);
        }
        return line.getArgs()[0];
    }

    public boolean hasIndex() {
        return line.hasOption(INDEX);
    }

    public boolean hasQuery() {
        return line.hasOption(QUERY);
    }

    public boolean hasPageRank() {
        return line.hasOption(PAGE_RANK);
    }

    public boolean hasLuceneRank() {
        return line.hasOption(LUCENE_RANK);
    }

    public boolean hasNoInfo() {
        return line.hasOption(NO_INFO);
    }

    public boolean hasMoreInfo() {
        return line.hasOption(MORE_INFO);
    }

    @Override
    public boolean isPage() {
        if (hasPageRank()) {
            return true;
        }
        if (hasLuceneRank()) {
            return false;
        }
        return !getSetup().isLucenePreffered();
    }

    @Override
    public boolean isInfo() {
        if (hasMoreInfo()) {
            return true;
        }
        if (hasNoInfo()) {
            return false;
        }
        return !getSetup().isNoInfo();
    }

    public boolean hasMergeWonted() {
        return line.hasOption(MERGE_COMAPRATORS);
    }

    @Override
    public boolean isMergeWonted() {
        if (hasMergeWonted()) {
            return true;
        }
        return getSetup().isMergeResults();
    }

    @Override
    public boolean isOmitArchives() {
        if (line.hasOption(ARCHIVES)) {
            return true;
        }
        return LibrarySetup.HardcodedDefaults.omitArchives;
    }

    @Override
    public boolean isOmitPdfInfo() {
        if (line.hasOption(NO_PDF_INFO)) {
            return true;
        }
        return getSetup().isNoPdfInfo();
    }

    public boolean isColoured() {
        return line.hasOption(OUTPUT_COLOUR);
    }

    public boolean isExctractInfo() {
        return line.hasOption(EXTRACT_INFO);
    }

    public String getExctractInfo() {
        return line.getOptionValue(EXTRACT_INFO);
    }

    @Override
    public int getInfoBefore() {
        if (line.hasOption(INFO_BEFORE)) {
            return -Math.abs(Integer.valueOf(line.getOptionValue(INFO_BEFORE)));
        } else {
            return -Math.abs(getSetup().getShowBefore());
        }
    }

    @Override
    public int getInfoAfter() {
        if (line.hasOption(INFO_AFTER)) {
            return Integer.valueOf(line.getOptionValue(INFO_AFTER));
        } else {
            return getSetup().getShowAfter();
        }
    }

    @Override
    public int getDidYouMeantDeadLine() {
        if (line.hasOption(DEAD_LINE)) {
            return Integer.valueOf(line.getOptionValue(DEAD_LINE));
        } else {
            return getSetup().getDidYouMeantDeadLine();
        }
    }

    @Override
    public int getDidYouMeantCount() {
        if (line.hasOption(DID_COUNT)) {
            return Integer.valueOf(line.getOptionValue(DID_COUNT));
        } else {
            return getSetup().getDidYouMeantCount();
        }
    }

    @Override
    public int getstartAt() {
        if (line.hasOption(START_AT)) {
            return Integer.valueOf(line.getOptionValue(START_AT));
        } else {
            return startAtDefault;
        }
    }

    @Override
    public int getRecords() {
        if (line.hasOption(RECORDS)) {
            return Integer.valueOf(line.getOptionValue(RECORDS));
        } else {
            return getSetup().getShowRecords();
        }
    }

    @Override
    public String getLibrary() {
        if (line.hasOption(LIBRARY)) {
            return (line.getOptionValue(LIBRARY));
        } else {
            return DEFAULT_LIBRARY;
        }
    }

    private boolean isHtml() {
        return line.hasOption(OUTPUT_HTML);
    }

    private boolean isAjax() {
        return line.hasOption(OUTPUT_AJAX);
    }

    private boolean isPlain() {
        return line.hasOption(OUTPUT_PLAIN);
    }

    public void checkAll() {
        //FIXME  pouze pri index umoznit neexistujici library
        Commandline cmds = this;
        cmds.checkHelp();
        cmds.checkVersion();
        cmds.checkLibraries();
        cmds.verifyIndex();
        cmds.verifyServer();
        cmds.verifyFirefox();
        if (!(hasIndex() || hasServer() || hasPrintFirefoxEngine())) {
            cmds.checkDupes();
            cmds.verifyQeury();
            cmds.checkFormatters();
            cmds.checkExtractInfo();
        }
    }

    private void checkExtractInfo() {
        if (isExctractInfo()) {
            if (hasQuery()) {

            } else if (line.getArgs().length == 1) {

            } else {
                System.out.println(EXTRACT_INFO + " can be used only with query or with single `query` argument.");
                System.exit(1);
            }
        }
    }

    private void checkFormatters() {
        int formaters = 0;
        if (isPlain()) {
            formaters++;
        }
        if (isAjax()) {
            formaters++;
        }
        if (isHtml()) {
            formaters++;
        }
        if (isColoured()) {
            formaters++;
        }
        if (formaters > 1) {
            System.out.println("You can use only one formatter at time.");
            System.exit(1);
        }
    }

    @Override
    public Formatter createFormatter(PrintStream out) {
        if (isPlain()) {
            return new PlainTextFormatter(out, this.getSetup());
        } else if (isColoured()) {
            return new ColoredPlainTextFormatter(out, this.getSetup());
        } else if (isHtml()) {
            return new StaticHtmlFormatter(out, this.getSetup());
        } else if (isAjax()) {
            return new AjaxHtmlFormatter(out, this.getSetup());
        } else {
            if (System.console() == null || isWindows()) {
                return new PlainTextFormatter(out, this.getSetup());
            } else {
                return new ColoredPlainTextFormatter(out, this.getSetup());
            }

        }

    }

    private static String OS = null;

    public static String getOsName() {
        if (OS == null) {
            OS = System.getProperty("os.name");
        }
        return OS;
    }

    public static boolean isWindows() {
        return getOsName().toLowerCase().startsWith("windows");
    }

    public static boolean isUnix() {
        return !isWindows();
    }

    private static void verify(String library) {
        File ca = new File(JavadocOfflineSearch.CACHE, library);
        File co = new File(JavadocOfflineSearch.CONFIG, library);

        verifySingle(ca);
        verifySingle(co);
    }

    private static void verifySingle(File ca) {
        if (ca.exists()) {
            if (ca.list().length > 0) {
                System.out.println("looks OK: " + ca.getAbsolutePath() + " items: " + ca.list());
            } else {
                System.out.println("Warning!: " + ca.getAbsolutePath() + " is empty!");
            }
        } else {
            System.out.println("Error!: " + ca.getAbsolutePath() + " dont exists!");
        }
    }

    @Override
    public  LibrarySetup getSetup() {
        return LibraryManager.getLibraryManager().getLibrarySetup(getLibrary());
    }

}

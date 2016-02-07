/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.tools;

import java.io.PrintStream;
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
    private static final String PAGE_RANK = "page-rank";
    private static final String LUCENE_RANK = "lucene-rank";
    private static final String NO_INFO = "no-info";
    private static final String MORE_INFO = "more-info";
    private static final String QUERY = "query";
    private static final String START_SERVER = "start-server";
    private static final String FILE = "force-file";
    private static final String MERGE_COMAPRATORS = "merge-results";
    private static final String EXTRACT_INFO = "exctract-info";
    private static final String OUTPUT_COLOUR = "color";
    private static final String OUTPUT_HTML = "html";
    private static final String OUTPUT_AJAX = "ajax";
    private static final String OUTPUT_PLAIN = "plain";
    private static final String INFO_AFTER = "info-after";
    private static final String INFO_BEFORE = "info-before";
    public static final int defaultBefore = 40;
    public static final int defaultAfter = 40;
    public static final int didYouMeantDeadLine = 10;
    public static final int didYouMeantCount = 10;
    private static final String DEAD_LINE = "did-you-mean-deadline";
    private static final String DID_COUNT = "did-you-mean-count";

    public Commandline(String[] args) {
        Option help = new Option("h", HELP, false, "print this message");
        Option index = new Option("i", INDEX, false, "will index directories specified in config file");
        Option version = new Option("v", VERSION, false, "print the version information and exit");
        Option pagerank = new Option("p", PAGE_RANK, false, "will use page-rank for sorting before lucene one");
        Option lucenerank = new Option("l", LUCENE_RANK, false, "will use default lucene ranking");
        Option noinfo = new Option("n", NO_INFO, false, "will NOT show snippets of string usages under title and link");
        Option moreinfo = new Option("m", MORE_INFO, false, "will (default) show snippets of string usages under title and link");
        Option server = new Option("s", START_SERVER, false, "will start the server on port 31745. You can then search in browser by http://lcoalhost:" + JavadocOfflineSearch.PORT);
        Option file = new Option("f", FILE, false, "will prefix all files by file://");
        Option merge = new Option("g", MERGE_COMAPRATORS, false, "will use both lucene and page sorting to determine results");
        Option exInfo = new Option("x", EXTRACT_INFO, true, "from given document, extract those ...info...  based on query");
        Option infoBefore = new Option("B", INFO_BEFORE, true, " number of characters between '...' and 'match'. default " + defaultBefore);
        Option infoAfter = new Option("A", INFO_AFTER, true, " number of characters between 'match' and '...'. default " + defaultAfter);
        Option didDead = new Option("d", DEAD_LINE, true, " min. number of result to occure before 'did you ment' is suggested. default " + didYouMeantDeadLine);
        Option didCount = new Option("D", DID_COUNT, true, " how meny 'did you ment' is suggested. default " + didYouMeantCount);
        Option outputColor = new Option("c", OUTPUT_COLOUR, false, "will use colored shell output");
        Option outputHtml = new Option("t", OUTPUT_HTML, false, "will force html marked up output");
        Option outputAjax = new Option("a", OUTPUT_AJAX, false, "will force html marked up with ajax info snippets");
        Option outputPlain = new Option("y", OUTPUT_PLAIN, false, "will use simple palintext output (default)");

        Option search = new Option("q", QUERY, true, "is considered default when no argument is given. Search docs. '-' connected wth word is NOT.");

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
        options.addOption(file);
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

    public void checkHelp() {
        if (line.hasOption(HELP)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Javadoc offline search", options);
            System.out.println("When used from commandline, it is expected to run with `more`.");
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
        if (line.hasOption(INDEX) && args.length != 1) {
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
        return !hasLuceneRank();
    }

    @Override
    public boolean isInfo() {
        if (hasMoreInfo()) {
            return true;
        }
        return !hasNoInfo();
    }

    @Override
    public boolean isFileForced() {
        return line.hasOption(FILE);
    }

    @Override
    public boolean isMergeWonted() {
        return line.hasOption(MERGE_COMAPRATORS);
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
            return -Integer.valueOf(line.getOptionValue(INFO_BEFORE));
        } else {
            return -defaultBefore;
        }
    }

    @Override
    public int getInfoAfter() {
        if (line.hasOption(INFO_AFTER)) {
            return Integer.valueOf(line.getOptionValue(INFO_AFTER));
        } else {
            return defaultAfter;
        }
    }

    @Override
    public int getDidYouMeantDeadLine() {
        if (line.hasOption(DEAD_LINE)) {
            return Integer.valueOf(line.getOptionValue(DEAD_LINE));
        } else {
            return didYouMeantDeadLine;
        }
    }

    @Override
    public int getDidYouMeantCount() {
        if (line.hasOption(DID_COUNT)) {
            return Integer.valueOf(line.getOptionValue(DID_COUNT));
        } else {
            return didYouMeantCount;
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
        Commandline cmds = this;
        cmds.checkHelp();
        cmds.checkVersion();
        cmds.verifyIndex();
        cmds.verifyServer();
        cmds.checkDupes();
        cmds.verifyQeury();
        cmds.checkFormatters();
        cmds.checkExtractInfo();
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
            return new PlainTextFormatter(out);
        } else if (isColoured()) {
            return new ColoredPlainTextFormatter(out);
        } else if (isHtml()) {
            return new StaticHtmlFormatter();
        } else if (isAjax()) {
            return new AjaxHtmlFormatter();
        } else {
            return new PlainTextFormatter(out);
        }

    }

}

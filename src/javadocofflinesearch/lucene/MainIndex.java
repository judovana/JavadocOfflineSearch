package javadocofflinesearch.lucene;

import javadocofflinesearch.extensions.PagedScoreDocs;
import javadocofflinesearch.extensions.Vocabulary;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javadocofflinesearch.extensions.HrefCounter;
import javadocofflinesearch.SearchSettings;
import javadocofflinesearch.formatters.Formatter;
import javadocofflinesearch.htmlprocessing.StreamCrossroad;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

/**
 *
 * @author jvanek
 */
public class MainIndex {

    private final StreamCrossroad streamizer;
    private final Vocabulary vocabualry;
    private final HrefCounter hc;
    private final File INDEX;
    public static final String mainIndexName = "javadocIndex.index";
    private final SearchSettings settings;

    public MainIndex(SearchSettings settings) throws IOException {
        INDEX = new File(settings.getSetup().getCacheHome(), mainIndexName);
        this.hc = new HrefCounter(settings.getSetup().getCacheHome());
        this.vocabualry = new Vocabulary(settings.getSetup().getCacheHome());
        this.streamizer = new StreamCrossroad(hc, vocabualry);
        this.settings = settings;
    }

    public boolean checkInitialized() throws IOException {
        try {
            hc.loadHrefs();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            vocabualry.laodVocs();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return (INDEX.exists() && !hc.isEmpty() && !vocabualry.isEmpty());
    }

    public String printInitialized() throws IOException {
        String s = "";
        s += "Your documents were probably not yet indexed or are corrupted\n";
        s += "Run `java -jar JavadocOfflineSearch.jar - index` to fix: \n";
        s += INDEX.getAbsolutePath() + " - " + INDEX.exists() + "\n";
        s += hc.getFile().getAbsolutePath() + " records: " + hc.size() + "\n";
        s += vocabualry.getFile().getAbsolutePath() + " records: " + vocabualry.size() + "\n";
        s += "Your may also check content of:\n";
        s += javadocofflinesearch.JavadocOfflineSearch.CONFIG + "\n";
        return s;
    }

    public void index() throws IOException {
        index(settings.getSetup().getDirs());
    }

    private void index(Path... sources) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        Directory index = new NIOFSDirectory(INDEX.toPath());
        //iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        try (IndexWriter writer = new IndexWriter(index, iwc)) {
            SingleIndexer si = new SingleIndexer(writer, streamizer, settings);
            si.run();
        }
    }

    public void search(String queryString, SearchSettings settings, PrintStream out) throws IOException, ParseException {
        Formatter f = settings.createFormatter(out);
        f.haders();
        queryString = queryString.trim();
        if (queryString.length() == 0) {
            return;
        }
        Directory index = new NIOFSDirectory(INDEX.toPath());
        try (IndexReader reader = DirectoryReader.open(index)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();

            String field = "contents";
            QueryParser parser = new QueryParser(field, analyzer);

            Query query = parser.parse(queryString);

            f.searchStarted("Searching for: ", query.toString(field));
            f.printLibrary(settings.getLibrary());

            Date start = new Date();
            TopDocs found = searcher.search(query, Integer.MAX_VALUE);
            if (found.totalHits.value < settings.getDidYouMeantDeadLine()) {
                didYouMent(settings, queryString, f);

            }
            Date end = new Date();
            f.resulsSummary("Found:", found.totalHits.value, "Time:", (end.getTime() - start.getTime()), "ms");
            PagedScoreDocs[] wrapped = new PagedScoreDocs[found.scoreDocs.length];
            for (int i = 0; i < found.scoreDocs.length; i++) {
                ScoreDoc arr = found.scoreDocs[i];
                wrapped[i] = new PagedScoreDocs(searcher.doc(arr.doc), arr, hc);
            }
            Comparator<PagedScoreDocs> comp = PagedScoreDocs.getComparator(settings.isPage());
            Arrays.sort(wrapped, comp);
            if (settings.isMergeWonted()) {
                PagedScoreDocs[] wrapped2 = new PagedScoreDocs[found.scoreDocs.length];
                System.arraycopy(wrapped, 0, wrapped2, 0, found.scoreDocs.length);
                Comparator<PagedScoreDocs> comp2 = PagedScoreDocs.getComparator(!settings.isPage());
                Arrays.sort(wrapped2, comp2);
                for (int i = 0; i < found.scoreDocs.length; i++) {
                    wrapped[i].incSortedIndex(i);
                    wrapped2[i].incSortedIndex(i);
                }
                //now sot again the first sorted array.It should persists original position in csae it is equal
                Arrays.sort(wrapped, PagedScoreDocs.mergedComp);
            }
            int from = Math.max(0, settings.getstartAt());
            int to = Math.min(wrapped.length - 1, settings.getstartAt() + settings.getRecords());
            f.pages(from, to, wrapped.length);
            for (int i = from; i <= to; i++) {
                PagedScoreDocs doc = wrapped[i];
                if (settings.isOmitArchives() && doc.getPath().startsWith("jar")) {

                } else {
                    f.title((i + 1), found.totalHits.value, doc.getTitle());
                    f.file(doc.getPath(), doc.getPage(), doc.getArr().score);
                    if (settings.isInfo()) {
                        //notpdf OR
                        //pdf+preview for pd allowed
                        if ((!doc.getPath().toLowerCase().endsWith(".pdf")) || (doc.getPath().toLowerCase().endsWith(".pdf") && !settings.isOmitPdfInfo())) {
                            f.summary(doc.getPath(), queryString, settings.getInfoBefore(), settings.getInfoAfter());
                        }
                    }
                }
            }
            f.pages(from, to, wrapped.length);
            Date end2 = new Date();
            f.resultsIn("Results in:", (end2.getTime() - start.getTime()), "ms");
            f.tail();

        }
    }

    public static void didYouMent(String queryString, Formatter f, int count, Vocabulary v) {
        List<String>[] r = couldYouMean(v, count, queryString);
        f.couldYouMeant("Could you meant:", r);
    }

    private static List<String>[] couldYouMean(Vocabulary vocabualry, int didYouMeantCount, String queryString) {
        List<String> l1 = vocabualry.didYouMean(didYouMeantCount, queryString);
        List<String> l2 = new ArrayList<>(0);
        List<String> l3 = new ArrayList<>(0);

        String[] sps = queryString.split("\\s+");
        if (sps.length > 1) {
            l2 = vocabualry.didYouMean(didYouMeantCount, sps);
        }
        String[] ws = queryString.split("[\\W]");
        if (ws.length > sps.length) {
            l3 = vocabualry.didYouMean(didYouMeantCount, ws);
        }

        return new List[]{l1, l2, l3};
    }

    private void didYouMent(SearchSettings settings, String queryString, Formatter f) {
        didYouMent(queryString, f, settings.getDidYouMeantCount(), vocabualry);
    }

}

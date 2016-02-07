package javadocofflinesearch.lucene;

import javadocofflinesearch.htmlprocessing.XmledHtmlToText;
import javadocofflinesearch.extensions.PagedScoreDocs;
import javadocofflinesearch.extensions.Vocabulary;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javadocofflinesearch.JavadocOfflineSearch;
import javadocofflinesearch.extensions.HrefCounter;
import javadocofflinesearch.SearchSettings;
import javadocofflinesearch.formatters.Formatter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
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
import org.apache.lucene.store.SimpleFSDirectory;

/**
 *
 * @author jvanek
 */
public class MainIndex {

    private final Directory index;
    private final XmledHtmlToText htmlizer;
    private final Vocabulary vocabualry;
    private final HrefCounter hc;
    private final File INDEX;

    public MainIndex(File cache) throws IOException {
        INDEX = new File(cache, "javadocIndex.index");
        this.index = new SimpleFSDirectory(INDEX.toPath());
        this.hc = new HrefCounter(cache);
        this.vocabualry = new Vocabulary(cache);
        this.htmlizer = new XmledHtmlToText(hc, vocabualry);
    }

    public boolean checkInitialized() throws IOException {
        hc.loadHrefs();
        vocabualry.laodVocs();
        return (INDEX.exists() && !hc.isEmpty() && !vocabualry.isEmpty());
    }
    
    public String printInitialized() throws IOException {
        String s = INDEX.getAbsolutePath()+" - "+INDEX.exists()+"\n";
        s += hc.getFile().getAbsolutePath()+" records: "+hc.size()+"\n";
        s += vocabualry.getFile().getAbsolutePath()+" records: "+vocabualry.size()+"\n";
        return s;
    }

    public void index(Path... sources) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        //iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        try (IndexWriter writer = new IndexWriter(index, iwc)) {
            SingleIndexer si = new SingleIndexer(writer, htmlizer, sources);
            si.run();
        }
    }

    public void search(String queryString, SearchSettings settings) throws IOException, ParseException {
        Formatter f = settings.createFormatter(System.out);
        queryString = queryString.trim();
        if (queryString.length() == 0) {
            return;
        }
        try (IndexReader reader = DirectoryReader.open(index)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();

            String field = "contents";
            QueryParser parser = new QueryParser(field, analyzer);

            Query query = parser.parse(queryString);
            System.out.println("Searching for: " + query.toString(field));

            Date start = new Date();
            TopDocs found = searcher.search(query, Integer.MAX_VALUE);
            if (found.totalHits < 10) {
                List<String> l = new ArrayList<>();
                List<String> l1 = vocabualry.didYouMean(queryString);
                l.addAll(l1);
                String[] sps = queryString.split("\\s+");
                if (sps.length > 1) {
                    List<String> l2 = vocabualry.didYouMean(sps);
                    l.addAll(l2);
                }
                String[] ws = queryString.split("[\\W]");
                if (ws.length > sps.length) {
                    List<String> l3 = vocabualry.didYouMean(ws);
                    l.addAll(l3);
                }
                System.out.println("Could you meant: ");
                for (String ll : l) {
                    System.out.println(ll);
                }

            }
            System.out.println("Found: " + found.totalHits);
            Date end = new Date();
            System.out.println("Time: " + (end.getTime() - start.getTime()) + "ms");
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
            int count = 0;
            for (PagedScoreDocs doc : wrapped) {
                count++;
                System.out.println(count + "/" + found.totalHits + ") " + doc.getTitle());
                System.out.println(prefix(settings.isFileForced()) + doc.getPath() + ": " + doc.getPage() + "/" + doc.getArr().score);
                if (settings.isInfo()) {
                    try {
                        String sumamry = InfoExtractor.extract(doc.getPath(), queryString, f, settings.getInfoBefore(), settings.getInfoAfter());
                        System.out.println(sumamry);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                System.out.println("\n");
            }
            Date end2 = new Date();
            System.out.println("Results in: " + (end2.getTime() - start.getTime()) + "ms");

            //doPagingSearch(in, searcher, query, hitsPerPage, raw, queries == null && queryString == null);
        }
    }

    /**
     * This demonstrates a typical paging search scenario, where the search
     * engine presents pages of size n to the user. The user can then go to the
     * next page if interested in the next hits.
     *
     * When the query is executed for the first time, then only enough results
     * are collected to fill 5 result pages. If the user wants to page beyond
     * this limit, then the query is executed another time and all hits are
     * collected.
     *
     */
    public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query,
            int hitsPerPage, boolean raw, boolean interactive) throws IOException {

        // Collect enough docs to show 5 pages
        TopDocs results = searcher.search(query, 5 * hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;

        int numTotalHits = results.totalHits;
        System.out.println(numTotalHits + " total matching documents");
        int start = 0;
        int end = Math.min(numTotalHits, hitsPerPage);

        while (true) {
            if (end > hits.length) {
                System.out.println("Only results 1 - " + hits.length + " of " + numTotalHits + " total matching documents collected.");
                System.out.println("Collect more (y/n) ?");
                String line = in.readLine();
                if (line.length() == 0 || line.charAt(0) == 'n') {
                    break;
                }

                hits = searcher.search(query, numTotalHits).scoreDocs;
            }

            end = Math.min(hits.length, start + hitsPerPage);

            for (int i = start; i < end; i++) {
                if (raw) {                              // output raw format
                    System.out.println("doc=" + hits[i].doc + " score=" + hits[i].score);
                    continue;
                }
                Document doc = searcher.doc(hits[i].doc);
                String path = doc.get("path");
                if (path != null) {
                    System.out.println((i + 1) + ". " + path);
                    String title = doc.get("title");
                    if (title != null) {
                        System.out.println("   Title: " + doc.get("title"));
                    }
                } else {
                    System.out.println((i + 1) + ". " + "No path for this document");
                }

            }
            if (!interactive || end == 0) {
                break;
            }
            if (numTotalHits >= end) {
                boolean quit = false;
                while (true) {
                    System.out.print("Press ");
                    if (start - hitsPerPage >= 0) {
                        System.out.print("(p)revious page, ");
                    }
                    if (start + hitsPerPage < numTotalHits) {
                        System.out.print("(n)ext page, ");
                    }
                    System.out.println("(q)uit or enter number to jump to a page.");

                    String line = in.readLine();
                    if (line.length() == 0 || line.charAt(0) == 'q') {
                        quit = true;
                        break;
                    }
                    if (line.charAt(0) == 'p') {
                        start = Math.max(0, start - hitsPerPage);
                        break;
                    } else if (line.charAt(0) == 'n') {
                        if (start + hitsPerPage < numTotalHits) {
                            start += hitsPerPage;
                        }
                        break;
                    } else {
                        int page = Integer.parseInt(line);
                        if ((page - 1) * hitsPerPage < numTotalHits) {
                            start = (page - 1) * hitsPerPage;
                            break;
                        } else {
                            System.out.println("No such page");
                        }
                    }
                }
                if (quit) {
                    break;
                }
                end = Math.min(numTotalHits, start + hitsPerPage);
            }
        }
    }

    private String prefix(boolean prefix) {
        if (prefix) {
            return "file://";
        } else {
            return "";
        }
    }

}

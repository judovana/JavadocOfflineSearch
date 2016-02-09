package javadocofflinesearch.lucene;

import javadocofflinesearch.htmlprocessing.MalformedXmlParser;
import javadocofflinesearch.htmlprocessing.XmledHtmlToText;
import javadocofflinesearch.tools.TitledByteArrayInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javadocofflinesearch.tools.LevenshteinDistance;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;

/**
 *
 * @author jvanek
 */
public class SingleIndexer implements Runnable {

    private final IndexWriter writer;
    private final IndexerSettings setup;
    private final XmledHtmlToText htmlizer;
    private int files = 0;

    SingleIndexer(IndexWriter writer, XmledHtmlToText htmlizer, IndexerSettings setup) {
        this.writer = writer;
        this.setup = setup;
        this.htmlizer = htmlizer;
    }

    @Override
    public void run() {
        Date start = new Date();
        try {
            Path[] sources = setup.getDirs();
            indexDocs(writer, sources);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Indexed " + files + " files");
            Date end = new Date();
            System.out.println("Time: " + (end.getTime() - start.getTime()) + "ms");
            System.out.println("Saving priorities and vocabulary");
            start = new Date();
            try {
                htmlizer.getHc().saveHrefs();
                htmlizer.getVc().saveVocs();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                end = new Date();
                System.out.println("done in " + (end.getTime() - start.getTime()) + "ms");
            }
        }
    }

    private void indexDocs(final IndexWriter writer, final Path... paths) throws IOException {
        for (Path path : paths) {
            indexDocs(writer, path.toFile());
        }
    }

    private void indexDocs(final IndexWriter writer, final File... paths) throws IOException {
        if (paths == null) {
            return;
        }
        for (File path1 : paths) {

            if (path1.isDirectory()) {
                indexDocs(writer, path1.listFiles());
            } else {
                if (!setup.isSuffixCaseInsensitiveIncluded(path1.getName())) {
                    System.out.println("Skipped (non indexable)" + path1);
                    continue;
                }
                //other may be included at least in vocebularry or pageindex
                if (!setup.isFilenameCaseInsensitiveIncluded(path1.getName()) && !setup.isExcldedFileIncludedInRanks()) {
                    System.out.println("Skipped " + path1);
                    continue;
                }
                if (!setup.isPathCaseInsensitiveIncluded(path1) && !setup.isExcldedFileIncludedInRanks()) {
                    System.out.println("Skipped " + path1);
                    continue;
                }
                String name = path1.getName().toLowerCase();
                if (name.endsWith(".zip") || name.endsWith(".jar")) {
                    indexZip(writer, path1);
                } else {
                    indexDoc(writer, path1);
                }

            }
        }
    }

    /**
     * Indexes a single document
     */
    private void indexDoc(IndexWriter writer, URL file) throws IOException {
        files++;
        try (InputStream stream = htmlizer.parseAnother(MalformedXmlParser.xmlizeInputStream(file.openStream()), file)) {
            // make a new, empty document
            Document doc = new Document();

            // Add the path of the file as a field named "path".  Use a
            // field that is indexed (i.e. searchable), but don't tokenize 
            // the field into separate words and don't index term frequency
            // or positional information:
            Field pathField = new StringField("path", LevenshteinDistance.sanitizeFileUrl(file.toExternalForm()), Field.Store.YES);
            doc.add(pathField);

            // Add the last modified date of the file a field named "modified".
            // Use a LongField that is indexed (i.e. efficiently filterable with
            // NumericRangeFilter).  This indexes to milli-second resolution, which
            // is often too fine.  You could instead create a number based on
            // year/month/day/hour/minutes/seconds, down the resolution you require.
            // For example the long value  would mean
            // February 17, 2011, 2- PM.
            //doc.add(new LongField("modified", lastModified, Field.Store.NO));
            if (stream instanceof TitledByteArrayInputStream) {
                doc.add(new TextField("title", ((TitledByteArrayInputStream) stream).getTitle(), Field.Store.YES));
            }
            // Add the contents of the file to a field named "contents".  Specify a Reader,
            // so that the text of the file is tokenized and indexed, but not stored.
            // Note that FileReader expects the file to be in UTF- encoding.
            // If that's not the case searching for special characters will fail.
            doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

            if (!(setup.isFilenameCaseInsensitiveIncluded(new File(file.getFile()).getName()) && setup.isPathCaseInsensitiveIncluded(new File(file.getFile())))) {
                System.out.println("Processed, but NOT added: " + file);
                return;
            }
            if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                System.out.println("adding " + file);
                writer.addDocument(doc);
            } else {
                // Existing index (an old copy of this document may have been indexed) so 
                // we use updateDocument instead to replace the old one matching the exact 
                // path, if present:
                System.out.println("updating " + file);
                writer.updateDocument(new Term("path", file.toString()), doc);
            }
        }
    }

    private void indexDoc(IndexWriter writer, File path1) throws IOException {
        indexDoc(writer, path1.toURI().toURL());
    }

    private void indexZip(IndexWriter writer, File path1) throws IOException {
        ZipInputStream stream = new ZipInputStream(new FileInputStream(path1));
        URL zipUrl = path1.toURI().toURL();
        try {
            ZipEntry entry;
            while ((entry = stream.getNextEntry()) != null) {
                URL entryUrl = new URL("jar:" + LevenshteinDistance.sanitizeFileUrl(zipUrl) + "!/" + entry.getName());
                indexDoc(writer, entryUrl);
            }
        } finally {
            // we must always close the zip file.
            stream.close();
        }
    }
}

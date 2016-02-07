package javadocofflinesearch.lucene;

import javadocofflinesearch.htmlprocessing.MalformedXmlParser;
import javadocofflinesearch.htmlprocessing.XmledHtmlToText;
import javadocofflinesearch.extensions.Vocabulary;
import javadocofflinesearch.tools.TitledByteArrayInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
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
    private final Path[] sources;
    private final XmledHtmlToText htmlizer;
    private int files = 0;

    SingleIndexer(IndexWriter writer, XmledHtmlToText htmlizer, Path... sources) {
        this.writer = writer;
        this.sources = sources;
        this.htmlizer = htmlizer;
    }

    @Override
    public void run() {
        Date start = new Date();
        try {
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
            String n = path1.getName().toLowerCase();
            if (n.equals("allclasses-noframe.html")) {
                continue;
            }
            if (n.equals("object.html")) {
                continue;
            }
            if (n.equals("deprecated-list.html")) {
                continue;
            }
            if (n.equals("index-1.html")) {
                continue;
            }
            if (n.equals("overview-summary.html")) {
                continue;
            }
            if (n.equals("package-frame.html")) {
                continue;
            }
            if (path1.isDirectory()) {
                indexDocs(writer, path1.listFiles());
            } else {
                indexDoc(writer, path1);
            }
        }
    }

    /**
     * Indexes a single document
     */
    private void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
        files++;
        String name = file.getFileName().toString().toLowerCase();
        if (name.endsWith(".jpeg") || name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif")) {
            return;
        }
        try (InputStream stream = htmlizer.parseAnother(MalformedXmlParser.xmlizeInputStream(Files.newInputStream(file)), file)) {
            // make a new, empty document
            Document doc = new Document();

            // Add the path of the file as a field named "path".  Use a
            // field that is indexed (i.e. searchable), but don't tokenize 
            // the field into separate words and don't index term frequency
            // or positional information:
            Field pathField = new StringField("path", file.toString(), Field.Store.YES);
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
        indexDoc(writer, path1.toPath(), Files.getLastModifiedTime(path1.toPath()).toMillis());
    }
}

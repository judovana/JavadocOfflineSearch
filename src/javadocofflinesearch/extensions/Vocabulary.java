package javadocofflinesearch.extensions;

import javadocofflinesearch.tools.LevenshteinDistance;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jvanek
 */
public class Vocabulary {

    Set<String> voc = new HashSet<>();
    private final File file;
    public static final String vocName = "vocabulary";

    public File getFile() {
        return file;
    }

    public Vocabulary(File cache) {
        this.file = new File(cache, vocName);
    }

    public void add(String[] nwv) {
        for (String s : nwv) {
            if (s.length() > 2) {
                voc.add(s);
            }
        }
    }

    public void saveVocs() throws IOException {
        saveVocs(file);
    }

    void saveVocs(File f) throws IOException {
        try (BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"))) {
            for (String value : voc) {
                br.write(value);
                br.write('\n');
            }
        }
    }

    public void laodVocs() throws IOException {
        laodVocs(file);
    }

    private void laodVocs(File parent) throws IOException {
        laodVocs(parent, true);
    }

    private void laodVocs(File f, boolean clear) throws IOException {
        if (clear) {
            voc.clear();
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
            while (true) {
                String s = br.readLine();
                if (s == null) {
                    return;
                }
                voc.add(s);
            }
        }
    }

    /**
     * this one takes ALL querySAtrings, put them to ONE list, then sort and
     * take best of ALL
     *
     * @param count
     * @param queryStrings
     * @return
     */
    public List<String> didYouMeanORIG(int count, String... queryStrings) {
        List<ResultWithDistance> r = new ArrayList<>();
        for (String queryString : queryStrings) {
            for (String voc1 : voc) {
                int l = LevenshteinDistance.levenshteinDistance(voc1, queryString);
                r.add(new ResultWithDistance(l, voc1));
            }
        }
        Collections.sort(r);
        List<String> result = new ArrayList<>(count);
        int i = 0;
        for (ResultWithDistance r1 : r) {
            i++;
            if (i >= count) {
                break;
            }
            result.add(r1.word);
        }
        return result;
    }

    /**
     * this one takes ALL querySAtrings, put them to ALL lists, then sort and
     * take best of EACH
     *
     * @param count
     * @param queryStrings
     * @return
     */
    public List<String> didYouMean(int count, String... queryStrings) {
        List<String> result = new ArrayList<>(count * queryStrings.length);
        for (String queryString : queryStrings) {
            result.addAll(didYouMean(count, queryString));
        }
        return result;
    }

    public List<String> didYouMean(int count, String queryString) {
        return didYouMean(count, voc, queryString);
    }

    public static List<String> didYouMean(int count, Iterable<String> posibilities, String queryString) {
        List<ResultWithDistance> r = new ArrayList<>();
        for (String voc1 : posibilities) {
            int l = LevenshteinDistance.levenshteinDistance(voc1, queryString);
            r.add(new ResultWithDistance(l, voc1));
        }
        Collections.sort(r);
        List<String> result = new ArrayList<>(count);
        int i = 0;
        for (ResultWithDistance r1 : r) {
            i++;
            if (i >= count) {
                break;
            }
            result.add(r1.word);
        }
        return result;
    }

    public boolean isEmpty() {
        return voc.isEmpty();
    }

    public int size() {
        return voc.size();
    }

    public void addAll(String s) {
        String[] vocabulary1 = s.split("\\s+"); //java.security
        String[] vocabulary2 = s.split("[\\W]"); //java security
        this.add(vocabulary1);
        this.add(vocabulary2);
    }

}

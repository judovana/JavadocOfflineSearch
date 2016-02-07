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
import java.util.Arrays;
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

    public File getFile() {
        return file;
    }

    
    public Vocabulary(File cache) {
        this.file = new File(cache, "vocabulary");
    }

    public void add(String[] nwv) {
        voc.addAll(Arrays.asList(nwv));
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

    public List<String> didYouMean(String... queryString) {
        return didYouMean(10, queryString);
    }

    public List<String> didYouMean(int count, String... queryStrings) {
        List<String> result = new ArrayList<>(count);
        List<ResultWithDistance> r = new ArrayList<>();
        for (String queryString : queryStrings) {
            for (String voc1 : voc) {
                int l = LevenshteinDistance.levenshteinDistance(voc1, queryString);
                r.add(new ResultWithDistance(l, voc1));
            }
        }
        Collections.sort(r);
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

}

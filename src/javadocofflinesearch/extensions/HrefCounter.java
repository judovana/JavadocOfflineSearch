/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.extensions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javadocofflinesearch.tools.LevenshteinDistance;

/**
 *
 * @author jvanek
 */
public class HrefCounter {

    private final Map<String, Integer> priorities = new HashMap<>();
    private final Map<String, Integer> customClicks = new HashMap<>();

    private final File file1;
    private final File file2;
    public static final String customClicksName = "customClicks";
    public static final String pageIndexName = "pageIndex";

    public File getFile1() {
        return file1;
    }

    public File getFile2() {
        return file2;
    }

    public HrefCounter(File cache, File config) {
        this.file1 = new File(cache, pageIndexName);
        this.file2 = new File(config, customClicksName);
    }

    public void countPoints(String s) {
        countPoints(s, 1, priorities);
    }

    public void customClick(String s) {
        countPoints(s, 10, customClicks);
    }

    private static void countPoints(String s, int val, Map<String, Integer> where) {
        if (s.length() > 2) {
            Integer i = where.get(s);
            if (i == null) {
                where.put(s, val);
            } else {
                where.put(s, i + val);
            }
        }
    }

    public void saveHrefs() throws IOException {
        saveHrefs(file1, priorities);
    }

    public void saveCustom() throws IOException {
        saveHrefs(file2, customClicks);
    }

    private static void saveHrefs(File f, Map<String, Integer> where) throws IOException {
        Set<Map.Entry<String, Integer>> values = where.entrySet();
        try (BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"))) {
            for (Map.Entry<String, Integer> value : values) {
                br.write(value.getKey());
                br.write(' ');
                br.write(value.getValue().toString());
                br.write('\n');
            }
        }
    }

    public void loadHrefs() throws IOException {
        loadHrefs(file1, true, priorities);
        loadHrefs(file2, true, customClicks);
    }

    private static void loadHrefs(File f, boolean clear, Map<String, Integer> where) throws IOException {
        if (!f.exists()) {
            return;
        }
        if (clear) {
            where.clear();
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
            while (true) {
                String s = br.readLine();
                if (s == null) {
                    return;
                }
                int i = s.lastIndexOf(" ");
                where.put(s.substring(0,i).trim(), Integer.valueOf(s.substring(i).trim()));
            }
        }
    }

    public Integer getPageIndex(String path) {
        Integer p1 = priorities.get(path);
        if (p1 == null) {
            p1 = 0;
        }
        Integer p = customClicks.get(path);
        if (p == null) {
            p = 0;
        }
        return p1 + p;
    }

    public void addLink(String s, URL current) {
        addLink(s, current, false);
    }

    public void addLink(String s, URL current, boolean customClick) {

        int ii = s.lastIndexOf("?");
        if (ii >= 0) {
            s = s.substring(0, ii);
        }
        ii = s.lastIndexOf("#");
        if (ii >= 0) {
            s = s.substring(0, ii);
        }
        if (!s.isEmpty()) {
            if (!customClick) {
                if (s.startsWith("/")) {
                    s = s.substring(1);
                }
                s = absolutizeLink(s, current);
            }
        }
        if (customClick) {
            customClick(s);
        } else {
            countPoints(s);
        }
    }

    private static String absolutizeLink(String s, URL current) {
        String parents = LevenshteinDistance.sanitizeFileUrl(current);
        parents = getParent(parents);
        while (s.startsWith("../")) {
            s = s.substring(3);
            parents = getParent(parents);
        }
        return parents + "/" + s;
    }

    private static String getParent(String parents) {
        //lets prettend windows support
        return parents.substring(0, Math.max(parents.lastIndexOf("/"), parents.lastIndexOf("\\")));
    }

    public boolean isEmpty() {
        return priorities.isEmpty();
    }

    public int size() {
        return priorities.size();
    }
}

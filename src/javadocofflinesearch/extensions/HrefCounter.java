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
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jvanek
 */
public class HrefCounter {

    private final Map<String, Integer> priorities = new HashMap<>();
    private final File file;

    public File getFile() {
        return file;
    }

    
    public HrefCounter(File cache) {
        this.file = new File(cache, "pageIndex");
    }

    public void countPoints(String s) {
        Integer i = priorities.get(s);
        if (i == null) {
            priorities.put(s, 1);
        } else {
            priorities.put(s, i + 1);
        }
    }

    public void saveHrefs() throws IOException {
        saveHrefs(file);
    }

    private void saveHrefs(File f) throws IOException {
        Set<Map.Entry<String, Integer>> values = priorities.entrySet();
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
        loadHrefs(file);
    }
    private void loadHrefs(File f) throws IOException {
        loadHrefs(f, true);
    }

    private void loadHrefs(File f, boolean clear) throws IOException {
        if (clear) {
            priorities.clear();
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
            while (true) {
                String s = br.readLine();
                if (s == null) {
                    return;
                }
                String[] ss = s.split("\\s+");
                priorities.put(ss[0], Integer.valueOf(ss[1]));
            }
        }
    }

    public Integer getPageIndex(String path) {
        return priorities.get(path);
    }

    public void addLink(String s, Path current) {

        if (!s.contains("://")) {
            int ii = s.lastIndexOf("?");
            if (ii >= 0) {
                s = s.substring(0, ii);
            }
            ii = s.lastIndexOf("#");
            if (ii >= 0) {
                s = s.substring(0, ii);
            }
            if (!s.isEmpty()) {
                if (s.startsWith("/")) {
                    s = s.substring(1);
                }
                s = absolutizeLink(s, current.getParent()/*parent, as we need direcotry, not file*/);
                countPoints(s);
            }
        }
    }

    private static String absolutizeLink(String s, Path current) {
        while (s.startsWith("../")) {
            s = s.substring(3);
            current = current.getParent();
        }
        return current.toString() + "/" + s;
    }

    public boolean isEmpty() {
        return priorities.isEmpty();
    }

    public int size() {
        return priorities.size();
    }
}

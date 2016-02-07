/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Properties;

/**
 *
 * @author jvanek
 */
public class Setup {

    private static final String DIRS = "docs.dirs";
    private static final String VALUE = "/usr/share/javadoc/java";
    private final File MAIN_CONFIG;
    Properties p = new Properties();

    public Setup(File CONFIG) {
        MAIN_CONFIG = new File(CONFIG, "javadocOfflineSearch.properties");
        p.setProperty(DIRS, VALUE);
    }

    public void load() throws IOException {
        if (MAIN_CONFIG.exists()) {
            p.load(new InputStreamReader(new FileInputStream(MAIN_CONFIG)));
        } else {
            System.out.println(MAIN_CONFIG.getAbsoluteFile()+" dont exists. Creating with defaults.");
            p.setProperty(DIRS, VALUE);
            p.store(new FileOutputStream(MAIN_CONFIG), null);
        }
    }

    public Path[] getDirs() {
        String[] dirs = p.getProperty(DIRS).split(File.pathSeparator);
        Path[] r = new Path[dirs.length];
        for (int i = 0; i < dirs.length; i++) {
            String dir = dirs[i];
            r[i] = new File(dir).toPath();

        }
        return r;
    }

}

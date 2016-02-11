/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.tools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jvanek
 */
public class LibraryManager {

    private final File config;
    private final File cache;
    private final LibrarySetup mainSetup;
    private final Map<String, LibrarySetup> librares = new HashMap<>();

    private LibraryManager(File config, File cache) {
        this.config = config;
        this.cache = cache;
        mainSetup = new LibrarySetup(cache, null);
        Set<String> l = javadocofflinesearch.JavadocOfflineSearch.listLibraries();
        if (l.isEmpty()) {
            l = new HashSet<>(1);
            l.add(Commandline.DEFAULT_LIBRARY);
        }
        for (String library : l) {
            File setupf = new File(config, library);
            LibrarySetup setup = new LibrarySetup(setupf, mainSetup);
            librares.put(library, setup);
        }
    }

    public void preload() throws IOException {
        mainSetup.preload();
        Collection<LibrarySetup> q = librares.values();
        for (LibrarySetup setup : q) {
            setup.preload();
        }

    }

    public LibrarySetup getLibrarySetup(String name) {
        LibrarySetup q = librares.get(name);
        if (q == null) {
            throw new RuntimeException("Library " + name + " dont exists!");
        }
        return q;
    }

    private static class LibraryManagerHolder {

        private static LibraryManager instance;

        public static LibraryManager getInstance() {
            return instance;
        }

        public static LibraryManager createInstance(File config, File cache) {
            instance = new LibraryManager(config, cache);
            return instance;
        }

    }

    public static LibraryManager getLibraryManager() {
        return LibraryManagerHolder.getInstance();
    }

    public static LibraryManager createtLibraryManager(File config, File cache) {
        return LibraryManagerHolder.createInstance(config, cache);
    }
}

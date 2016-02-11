/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadocofflinesearch.tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javadocofflinesearch.extensions.HrefCounter;

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
        mainSetup = new LibrarySetup(config, cache, null);
        Set<String> l = javadocofflinesearch.JavadocOfflineSearch.listLibraries();
        if (!l.contains(Commandline.DEFAULT_LIBRARY)) {
            l = new HashSet<>(l);
            l.add(Commandline.DEFAULT_LIBRARY);
        }
        for (String library : l) {
            initLIbrary(config, library, cache);
        }
    }

    private LibrarySetup initLIbrary(File config1, String library, File cache1) {
        File setupf1 = new File(config1, library);
        File setupf2 = new File(cache1, library);
        LibrarySetup setup = new LibrarySetup(setupf1, setupf2, mainSetup);
        librares.put(library, setup);
        return setup;
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
            q = initLIbrary(config, name, cache);
        }
        return q;
    }

    public boolean isFileValid(String potentionalFile) {
        if (!mainSetup.isSecurity()) {
            return true;
        }
//        files in main config are newer scaned. Have it sense to use them there?
//        if (mainSetup.isFileValid(potentionalFile)){
//            return true;
//        }
        Collection<LibrarySetup> setups = librares.values();
        for (LibrarySetup setup : setups) {
            if (setup.isFileValid(potentionalFile)) {
                return true;
            }
        }
        return false;
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

    private static class CustomClicksHolder {

        private static HrefCounter customClicks;

        public static HrefCounter getCustomClicks(File config) throws IOException {
            if (customClicks == null) {
                customClicks = new HrefCounter(config);
                customClicks.loadHrefs();
            }
            return customClicks;
        }

    }

    public synchronized HrefCounter getCustomClicks() {
        try {
            return CustomClicksHolder.getCustomClicks(config);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public synchronized void clickedHrefTo(String absolutePath) {
        try {
            //why did I added this?
            //if (absolutePath.toLowerCase().endsWith(".html") && hc.size() > 0) {
            getCustomClicks().addLink(absolutePath, new URL(absolutePath), true);
            getCustomClicks().saveHrefs();
            //}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

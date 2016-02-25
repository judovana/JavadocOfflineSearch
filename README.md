# JavadocOfflineSearch
```
The goal of this project was to make searching in your (java)docs as easy and comfortable as when you are browsing them online.
So main access to them is via comfort of your browser on  http://localhost:31754
And of course it have also commandline interface server-free, but not so powerful server-based  one (You never needed javadoc in headless system? Yaaah.. lucky one :) ).

run
 * java -jar JavadocOfflineSearch.jar  -h
for help

run
 * java -jar JavadocOfflineSearch.jar  -index <file(s)>/<dir(s)>
create default library (if not exists) (also file:dir... notation is supported)
 * java -jar JavadocOfflineSearch.jar  -index 
reindex default library
 * java -jar JavadocOfflineSearch.jar  -index -library <library>  <file(s)>/<dir(s)>
index  files to new library
 * java -jar JavadocOfflineSearch.jar  -index -library <library>
reindex all files in $XDG_CONFIG_DIR/JavadocOfflineSearch/LIBRARY/javadocOfflineSearch.properties

run
 * java -jar JavadocOfflineSearch.jar  <query>
to search from commandline in defualt library
 * java -jar JavadocOfflineSearch.jar  -library <library> <query>
to search from commandline in librarylibrary

** you can set default library as file $XDG_CONFIG_DIR/JavadocOfflineSearch/defaultLib (put inside name, spaces inside not allowed)**

run
 * java -jar JavadocOfflineSearch.jar  -start-server & your_browser "http://localhost:31754"
to use browser to browse yours docs. Indexing installs firefox plugin (if it is installed)

Is capable to index htmldocs, plaintexts and pdfs. Is capable to index/access archived (zip/jar) docs (like javadoc mostly is)
Uses lucene index and page-rank index. (Or mixture of both if you wish)
page-rank is original google's algorithm. More pages is pointing to the target, more prefered it is.
More times you click to any resource, more page-rank it gets too.
If you wont to ignore pdfs and are using assembled version, then you can put .pdf to list of ignored suffixes in your javadocOfflineSearch.properties

Of course plain text/pdfs files are very hardly to be sorted by page-rank (unless some htmls are pointing to those plain files)
```
# Requirements
```
Requires: tagsoup, lucene-core, lucene-analyzers-common, lucene-queries, lucene-queryparser, (appache)commons-cli
If you wont to index also pdfs, you need also: (apache)pdfbox, appache-commons-logging-api, appache-commons-logging, (apache)fontbox
```
# WARNING
```
Index what YOU need and run it when YOU need.
Security attribute in javadocOfflineSearch.properties checks if accessed resource was actually indexed, but still, at least dont run it as root.
Rather never run this as public service. Well yes ti is works, and yes, security is doing its job. But once off, it is exposing all yor local files and content of archives when used online!
I'm not going to fixing security bugs when used like this (but go on on your own :P)
```


# defaultLib
```
If you are reindexing often, or you have everything in some dev library or so on. Yu really *should* use $XDG_CONFIG_DIR/JavadocOfflineSearch/defaultLib to set your default library
Otherwise you will be unhappy to keep specifying  -library in commandline or keep clicking on "library" in main search form.
```


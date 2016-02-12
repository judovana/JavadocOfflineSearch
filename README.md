# JavadocOfflineSearch
```
The goal of this project was to make searching in your (java)docs as easy and comfortable as when you are browsing them online.
So main access to them is via comfort of your browser on  http://localhost:31754
And of course it have also commandline interface server-free, but not so powerful server-based  one (You never needed javadoc in headless system? Yaaah.. lucky one :) ).

run
 * java -jar JavadocOfflineSearch.jar  -h
for help
run
 * java -jar JavadocOfflineSearch.jar  -index 
 * java -jar JavadocOfflineSearch.jar  -index <file(s)>/<dir(s)>
 * java -jar JavadocOfflineSearch.jar  -index -library-library  <file(s)>/<dir(s)>
to index index all files in $XDG_CONFIG_DIR/JavadocOfflineSearch/LIBRARY/javadocOfflineSearch.properties
 * If this file not yet exists, you must specify those dir(s)/file(s) on commandline
run
 * java -jar JavadocOfflineSearch.jar  <arg>
to search from commandline
run
 * java -jar JavadocOfflineSearch.jar  -start-server & firefox
to use firefox search plugin comaptible and/or commandline approach run:

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
Never ever run this as public service. This is exposing all yor local files and content of archives when used online!
Index what YOU need and run it when YOU need.
security attribute in javadocOfflineSearch.properties checks if accesed resource was actually indexed, but still, at least dont run it as root.
```


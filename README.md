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
to index index all files in $XDG_CONFIG_DIR/JavadocOfflineSearch/javadocOfflineSearch.properties
 * by default /usr/share/javadoc/java
run
 * java -jar JavadocOfflineSearch.jar  <arg>
to search from commandline
run
 * java -jar JavadocOfflineSearch.jar  -start-server & firefox
to use firefox search plugin comaptible and/or commandline approach run:

Is capable to index htmldocs and plaintexts. Is capable to index/access archived (zip/jar) docs
Uses lucene index and page-rank index. (Or mixture f both if you wish)
page-rank is original google's algorithm. More pages is pointing to the target, more prefered it is.
More times you click to any resource, more page-rank it gets too.

Of course plain text files are very hardly to be sorted by page-rank (unless some htmls are pointing to those plain files)
```
# Requirements
```
Requires: tagsoup, lucene-core, lucene-analyzers-common, lucene-queries, lucene-queryparser, (appache)commons-cli
```
# WARNING
```
Never ever run this as public service. This is exposing all yor local files and content of archives when used online!
Index what YOU need and run it when YOU need.
```

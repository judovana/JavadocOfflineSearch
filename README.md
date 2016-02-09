# JavadocOfflineSearch

The goal of this project was to make searching in your (java)docs as easy and comfortable as when you are browsing them online.
So main access to them is cia comfort of your browser on  http://localhost:31754
And of course it have also commandline interface server-free, but not os powerfull server-based  one (You never needed javadoc in headless system? Yaaah.. lucky one :) ).

 * java -jar JavadocOfflineSearch.jar  -h
 * java -jar JavadocOfflineSearch.jar  -index
 * java -jar JavadocOfflineSearch.jar  <arg>
 to use firefox search plugin comaptible and/or commandline approach run:
 * java -jar JavadocOfflineSearch.jar  -start-server & firefox
 * index all files in $XDG_CONFIG_DIR/JavadocOfflineSearch/javadocOfflineSearch.properties
 * by default /usr/share/javadoc/java

Requires: tagsoup, lucene-core, lucene-analyzers-common, lucene-queries, lucene-queryparser, (appache)commons-cli


Is capable to index htmldocs and plaintexts. Is capable to index/access archived (zip/jar) docs
Uses lucene index and page-rank index. (Or mixture f both if you wish)
page-rank is original google's algorithm. More pages is pointing to the target, more preffered it is.
More times you click to any resource, more page-rank it gets too.

Of course plain text files are very hardly to be sorted by page-rank (unles some htmls are pointing to those palin files)

# WARNING
Never ever run this as public service. This is exposing all yor local files and content of archives when used online!
Index what YOU need and run it when YOU need.

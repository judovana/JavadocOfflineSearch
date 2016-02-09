package javadocofflinesearch.extensions;

import java.util.Comparator;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;

/**
 *
 * @author jvanek
 */
public class PagedScoreDocs {

    public static Comparator<PagedScoreDocs> getComparator(boolean page) {
        if (page) {
            return pageFirst;
        } else {
            return luceneFirst;
        }
    }

    private final Document doc;
    private final ScoreDoc arr;
    private final String path;
    private final String title;
    private final int page;
    private int sortedIndex = 0;

    @Override
    public String toString() {
        return path + ": " + page + "/" + arr.score + "(" + sortedIndex + ")";
    }

    public PagedScoreDocs(Document doc, ScoreDoc arr, HrefCounter hc) {
        this.doc = doc;
        this.arr = arr;
        this.path = doc.get("path");
        this.title = doc.get("title");
        this.page = hc.getPageIndex(path);

    }

    public Document getDoc() {
        return doc;
    }

    public int getPage() {
        return page;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public ScoreDoc getArr() {
        return arr;
    }

    public void incSortedIndex(int add) {
        this.sortedIndex += add;
    }

    public int getSortedIndex() {
        return sortedIndex;
    }

    public static final Comparator<PagedScoreDocs> pageFirst = new Comparator<PagedScoreDocs>() {

        @Override
        public int compare(PagedScoreDocs o1, PagedScoreDocs o2) {
            //page first lucene later
            //uzitecny muze byt i obracene
            int r = o2.getPage() - o1.getPage();
            if (r == 0) {
                float f = o1.getArr().score - o2.getArr().score;
                return (int) (f * 100000000);
            } else {
                return r;
            }

        }
    };
    public static final Comparator<PagedScoreDocs> luceneFirst = new Comparator<PagedScoreDocs>() {

        @Override
        public int compare(PagedScoreDocs o1, PagedScoreDocs o2) {
            float f = -o1.getArr().score + o2.getArr().score;
            int r = (int) (f * 100000000);

            if (r == 0) {
                return -o2.getPage() + o1.getPage();
            } else {
                return r;
            }

        }
    };

    //we expec we ot presorted array, and java is clever enough to not reorder ordered suu
    public static final Comparator<PagedScoreDocs> mergedComp = new Comparator<PagedScoreDocs>() {

        @Override
        public int compare(PagedScoreDocs o1, PagedScoreDocs o2) {
            int i = o1.getSortedIndex() - o2.getSortedIndex();
            return i;

        }
    };

}

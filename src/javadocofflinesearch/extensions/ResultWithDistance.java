
package javadocofflinesearch.extensions;

/**
 *
 * @author jvanek
 */
class ResultWithDistance implements Comparable<ResultWithDistance> {

     final int dist;
    final String word;

    public ResultWithDistance(int dist, String word) {
        this.dist = dist;
        this.word = word;
    }

    @Override
    public int compareTo(ResultWithDistance o) {
        return this.dist - o.dist;
    }

}

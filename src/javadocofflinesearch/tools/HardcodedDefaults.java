package javadocofflinesearch.tools;

/**
 *
 * @author jvanek
 */
public class HardcodedDefaults {

    private static final int defaultBefore = 40;
    private static final int defaultAfter = 40;
    private static final int didYouMeantDeadLine = 10;
    private static final int didYouMeantCount = 10;
    private static final int showRecordsDefault = Integer.MAX_VALUE;

    private static final boolean security = true;
    private static final int infoLoad = 60;
    private static final int infoShow = 20;

    private static final boolean luceneByDefault = false;
    private static final boolean noInfo = false;
    private static final boolean mergeResults = false;
    private static final boolean omitArchives = false;
    private static final boolean noPdfInfo = false;

    /**
     * @return the defaultBefore
     */
    public static int getDefaultBefore() {
        Integer i = Setup.getShowBefore();
        if (i != null) {
            return i;
        }
        return defaultBefore;
    }

    /**
     * @return the defaultAfter
     */
    public static int getDefaultAfter() {
        Integer i = Setup.getShowAfter();
        if (i != null) {
            return i;
        }
        return defaultAfter;
    }

    /**
     * @return the didYouMeantDeadLine
     */
    public static int getDidYouMeantDeadLine() {
        Integer i = Setup.getDeadline();
        if (i != null) {
            return i;
        }
        return didYouMeantDeadLine;
    }

    /**
     * @return the didYouMeantCount
     */
    public static int getDidYouMeantCount() {
        Integer i = Setup.getCount();
        if (i != null) {
            return i;
        }
        return didYouMeantCount;
    }

    /**
     * @return the showRecordsDefault
     */
    public static int getShowRecordsDefault() {
        Integer i = Setup.getPerPage();
        if (i != null) {
            return i;
        }
        return showRecordsDefault;
    }

    /**
     * @return the security
     */
    public static boolean isSecurity() {
        Boolean i = Setup.isSecurity();
        if (i != null) {
            return i;
        }
        return security;
    }

    /**
     * @return the luceneByDefault
     */
    public static boolean isLuceneByDefault() {
        Boolean i = Setup.isLucenePreffered();
        if (i != null) {
            return i;
        }
        return luceneByDefault;
    }

    /**
     * @return the noInfo
     */
    public static boolean isNoInfo() {
        Boolean i = Setup.isnoInfo();
        if (i != null) {
            return i;
        }
        return noInfo;
    }

    /**
     * @return the mergeResults
     */
    public static boolean isMergeResults() {
        Boolean i = Setup.isMergeWonted();
        if (i != null) {
            return i;
        }
        return mergeResults;
    }

    /**
     * @return the omitArchives
     */
    public static boolean isOmitArchives() {
        return omitArchives;
    }

    public static boolean isNoPdfInfo() {
        Boolean i = Setup.isPdfInfoSilenced();
        if (i != null) {
            return i;
        }
        return noPdfInfo;
    }

    public static int getInfoLoad() {
        Integer i = Setup.getMaxLoad();
        if (i != null) {
            return i;
        }
        return infoLoad;
    }

    public static int getInfoShow() {
        Integer i = Setup.getMaxShow();
        if (i != null) {
            return i;
        }
        return infoShow;
    }

}

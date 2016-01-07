package org.aksw.limes.core.measures.measure.string;

import org.aksw.limes.core.io.cache.Instance;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.LinkedList;

public class RatcliffObershelpMeasure extends StringMeasure implements TrieFilterableStringMeasure {

    /**
     * the current score will be stored here.
     */
    private int score;

    @Override
    public double characterFrequencyUpperBound(int l1, int l2, int m) {
        return (2*(double)m)/((double)l1+(double)l2);
    }

    @Override
    public int characterMatchLowerBound(int l1, int l2, double threshold) {
        return (int) Math.round(Math.ceil(threshold*(double)(l1+l2)/2.0d));
    }

    @Override
    public int lengthLowerBound(int l1, double threshold) {
        return (int) Math.round(Math.ceil((threshold)/(2-threshold)*(double)l1));
    }

    @Override
    public int lengthUpperBound(int l1, double threshold) {
        return (int) Math.round(Math.floor((2-threshold)/(threshold)*(double)l1));
    }

    @Override
    public LinkedList<ImmutableTriple<Integer, Integer, Integer>> getPartitionBounds(int maxSize, double threshold) {
        LinkedList<ImmutableTriple<Integer, Integer, Integer>> sliceBoundaries = new LinkedList<>();
        for (int t = 1; t <= maxSize; t++) {
            sliceBoundaries.add(new ImmutableTriple<>(t, lengthLowerBound(t, threshold), lengthUpperBound(t, threshold)));
        }
        return sliceBoundaries;
    }

    /**
     * returns the score calculated by the algorithm
     *
     * @param s1
     * @param s2
     * @return double
     */
    public double proximity(String s1, String s2) {
        score = 0;

        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        processRatcliffAlgorithm(s1, s2);

        return (double) score / (s1.length() + s2.length());
    }

    /**
     * processes the ratcliff/obershelp-algorithm recursivcely. this method is
     * faster than the iterative option (ca. 5-10%).
     *
     * @param s1
     * @param s2
     */
    private void processRatcliffAlgorithm(String s1, String s2) {

        String substring = findLongestSubstring(s1, s2);
        if (substring == null || substring.isEmpty())
            return;
        score += 2 * substring.length();
        int index1 = s1.indexOf(substring);
        int index2 = s2.indexOf(substring);
        String rightPartS1 = s1.substring(index1 + substring.length());
        String leftPartS1 = s1.substring(0, index1);
        String rightPartS2 = s2.substring(index2 + substring.length());
        String leftPartS2 = s2.substring(0, index2);
        if (rightPartS1 != null && !rightPartS1.isEmpty()
                && rightPartS2 != null && !rightPartS2.isEmpty()) {
            processRatcliffAlgorithm(rightPartS1, rightPartS2);
        }
        if (leftPartS1 != null && !leftPartS1.isEmpty() && leftPartS2 != null
                && !leftPartS2.isEmpty()) {
            processRatcliffAlgorithm(leftPartS1, leftPartS2);
        }
    }

    /**
     * finds the largest common group of two strings.
     *
     * @param s
     * @param t
     * @return String
     */
    private String findLongestSubstring(String s, String t) {

        StringBuilder sb = new StringBuilder();
        if (s == null || s.isEmpty() || t == null || t.isEmpty())
            return "";

        // ignore case
        s = s.toLowerCase();
        t = t.toLowerCase();

        // java initializes them already with 0
        int[][] num = new int[s.length()][t.length()];
        int maxlen = 0;
        int lastSubsBegin = 0;

        for (int i = 0; i < s.length(); i++) {
            for (int j = 0; j < t.length(); j++) {
                if (s.charAt(i) == t.charAt(j)) {
                    if ((i == 0) || (j == 0))
                        num[i][j] = 1;
                    else
                        num[i][j] = 1 + num[i - 1][j - 1];

                    if (num[i][j] > maxlen) {
                        maxlen = num[i][j];
                        // generate substring from str1 => i
                        int thisSubsBegin = i - num[i][j] + 1;
                        if (lastSubsBegin == thisSubsBegin) {
                            // if the current LCS is the same as the last time
                            // this block ran
                            sb.append(s.charAt(i));
                        } else {
                            // this block resets the string builder if a
                            // different LCS is found
                            sb.setLength(0);
                            sb.append(s.substring(thisSubsBegin, i + 1));
                            lastSubsBegin = thisSubsBegin;
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

    @Override
    public int getPrefixLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMidLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getSizeFilteringThreshold(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAlpha(int xTokensNumber, int yTokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getSimilarity(int overlap, int lengthA, int lengthB) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean computableViaOverlap() {
        return false;
    }

    @Override
    public double getSimilarity(Object a, Object b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getType() {
        return "string";
    }

    @Override
    public double getSimilarity(Instance a, Instance b, String property1, String property2) {
        double value = 0;
        double sim = 0;
        for (String source : a.getProperty(property1)) {
            for (String target : b.getProperty(property2)) {
                sim = proximity(source, target);
                if (sim > value) {
                    value = sim;
                }
            }
        }
        return sim;
    }

    @Override
    public String getName() {
        return "ratcliff-obershelp";
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return -1d;
    }
}

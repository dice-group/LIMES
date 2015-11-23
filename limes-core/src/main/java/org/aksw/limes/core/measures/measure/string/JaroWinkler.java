package org.aksw.limes.core.measures.measure.string;


import org.aksw.limes.core.io.cache.Instance;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.LinkedList;

/**
 * This class implements the Jaro-Winkler algorithm that was designed as
 * a string subsequence alignment method for matching names in the US Census.
 * It is thus optimized for relatively small sized strings of latin letters only.
 * It provides all the features of the original C implementation by William E. Winkler,
 * although the features that made it specific for name matching may be disabled.
 *
 * To overcome the complexity O(n*m) for non matching cases a filter is added.
 * Given a threshold it can identify pairs whose Jaro-Winkler proximity
 * is confidently less than or equal to that threshold.
 *
 * @author Kevin Dreßler
 * https://github.com/kvndrsslr/SemanticWeb-QuickJaroWinkler
 */

public class JaroWinkler extends StringMeasure implements TrieFilterableStringMeasure  {

    private static char[][] sp =
            {{'A','E'},{'A','I'},{'A','O'},{'A','U'},{'B','V'},{'E','I'},{'E','O'},{'E','U'},
                    {'I','O'},{'I','U'},{'O','U'},{'I','Y'},{'E','Y'},{'C','G'},{'E','F'},
                    {'W','U'},{'W','V'},{'X','K'},{'S','Z'},{'X','S'},{'Q','C'},{'U','V'},
                    {'M','N'},{'L','I'},{'Q','O'},{'P','R'},{'I','J'},{'2','Z'},{'5','S'},
                    {'8','B'},{'1','I'},{'1','L'},{'0','O'},{'0','Q'},{'C','K'},{'G','J'},
                    {'E',' '},{'Y',' '},{'S',' '}};

    public static double winklerBoostThreshold = 0.7d;

    private int[][] adjwt;

    private boolean simOn;

    private boolean uppercase;

    private boolean longStrings;

    public JaroWinkler() {
        this(true, false, false);
    }

    public JaroWinkler(boolean uppercaseOn, boolean longStringsOn, boolean characterSimilarityOn) {
        super();
        // initialize options
        this.uppercase = uppercaseOn;
        this.longStrings = longStringsOn;
        this.simOn = characterSimilarityOn;
        int i, j;
        if (characterSimilarityOn) {
            adjwt = new int[91][91];
            for (i = 0; i < 91; i++) for (j = 0; j < 91; j++) adjwt[i][j] = 0;
            for (i = 0; i < 36; i++) {
                adjwt[sp[i][0]][sp[i][1]] = 3;
                adjwt[sp[i][1]][sp[i][0]] = 3;
            }
        }
    }

    /**
     * Is character not numeric?
     * @param c character
     * @return true if not numeric, false otherwise
     */
    private boolean notNum (char c) {
        return (c > 57) || (c < 48);
    }

    /**
     * Is Character alphanumeric?
     * @param c character
     * @return true if alphanumeric, false otherwise
     */
    private boolean inRange (char c) {
        return (c > 0) && (c < 91);
    }


    /**
     * Clone method for parallel execution
     * @return
     */
    public JaroWinkler clone() {
        return new JaroWinkler(uppercase,longStrings,simOn);
    }

    /**
     * Calculate the proximity of two input strings if
     * proximity is assured to be over given threshold threshold.
     *
     * @param yang string to align on
     * @return similarity score (proximity)
     */
    public double proximity (char[] yin, char[] yang) {
        boolean[] yinFlags = new boolean[yin.length];
        boolean[] yangFlags = new boolean[yang.length];
        int i;
        for (i = 0; i < yin.length; i++) yinFlags[i] = false;
        for (i = 0; i < yang.length; i++) yangFlags[i] = false;
        boolean matriarch = yin.length > yang.length;
        int len = matriarch ? yang.length : yin.length;
        int range = matriarch ? yin.length : yang.length;
        range = range / 2 - 1;
        if (range < 0)
            range = 0;
        int k;
        int t;
        int m = t = k = 0;
        int j;
        for (i = 0; i < yin.length; i++) {
            int low = (i >= range) ? i - range : 0;
            int high = (i + range + 1 <= yang.length) ? i + range : yang.length - 1;
            for (j = low; j <= high; j++) {
                if (!yangFlags[j] && (yang[j]) == (yin[i])) {
                    yinFlags[i] = yangFlags[j] = true;
                    m++;
                    break;
                }
            }
        }
        if (m == 0)
            return 0.0d;

        for (i = 0; i < yin.length; i++) {
            if (yinFlags[i]) {
                for (j = k; j < yang.length; j++) {
                    if (yangFlags[j]) {
                        k = j + 1;
                        break;
                    }
                }
                if (yin[i] != yang[j])
                    t++;
            }
        }
        t /= 2;
        double sim;
        if (len > m && simOn) {
            sim = 0.0d;
            for (i = 0; i < yin.length; i++) {
                if (!yinFlags[i] && inRange(yin[i])) {
                    for (j = 0; j < yang.length; j++) {
                        if (!yangFlags[j] && inRange(yang[j])) {
                            if (adjwt[yin[i]][yang[j]] > 0) {
                                sim += adjwt[yin[i]][yang[j]];
                                yangFlags[j] = true;
                                break;
                            }
                        }
                    }
                }
            }
            sim = sim / 10.0d + m;
        } else {
            sim = (double) m;
        }
        double weight = sim / ((double) yin.length) + sim / ((double) yang.length)
                + ((double) (m - t)) / ((double) m);
        weight /= 3.0d;
        if (weight > winklerBoostThreshold) {
            k = (len >= 4) ? 4 : len;
            for (i = 0; ((i < k) && (yin[i] == yang[i]) && notNum(yin[i])); i++);
            if (i > 0)
                weight += i * 0.1d * (1.0d - weight);
            if (longStrings && len > 4 && m > i + 1 && 2 * m >= len + i && notNum(yin[0]))
                weight += (1.0d - weight) *
                        ((double) (m - i - 1)) / ((yin.length + yang.length - i * 2.0d + 2.0d));
        }
        return weight;
    }

    /**
     * Calculate the proximity of two input strings if
     * proximity is assured to be over given threshold threshold.
     *
     * @param yi string to be aligned
     * @param ya string to align on
     * @return similarity score (proximity)
     */
    public double proximity (String yi, String ya) {
        return proximity(getArrayRepresentation(yi), getArrayRepresentation(ya));
    }

    public char[] getArrayRepresentation (String s) {
        s = s.trim();
        if (uppercase)
            s = s.toUpperCase();
        return s.toCharArray();
    }

    @Override
    public double characterFrequencyUpperBound(int l1, int l2, int m) {
        double theta = (((double)m/(double)l1) + ((double)m/(double)l2) + 1.0d) / 3.0d;
        if (theta > winklerBoostThreshold)
            theta = theta + 0.4d * (1.0d-theta);
        return theta;
    }

    @Override
    public int characterMatchLowerBound(int l1, int l2, double threshold) {
        return (int) Math.round(Math.ceil((threshold - 0.6d)*((3*(double)l2*(double)l1)/(0.6d*(((double)l2+(double)l1))))));
    }

    public int lengthUpperBound(int l1, double threshold) {
        // when threshold is not over 0.8 then a delta can approach infinity
        // infinity is not available for integers in java, so this uses -1
        if (threshold <= 0.8d)
            return -1;
        else
            return (int) Math.round(Math.ceil(
                    (0.6d * (double) l1) / (3.0d * threshold - 2.4d)
            ));
    }

    public int lengthLowerBound(int l1, double threshold) {
        return (int) Math.round(Math.floor(
                (l1 * (3.0d * threshold - 2.4d)) / 0.6d
        ));
    }

    public LinkedList<ImmutableTriple<Integer, Integer, Integer>> getPartitionBounds(int maxSize, double threshold) {
        LinkedList<ImmutableTriple<Integer, Integer, Integer>> sliceBoundaries = new LinkedList<>();
        for (int t = 1; t <= maxSize; t++) {
            sliceBoundaries.add(new ImmutableTriple<>(t, lengthLowerBound(t, threshold), lengthUpperBound(t, threshold)));
        }
        return sliceBoundaries;
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
                char[] cSource = getArrayRepresentation(source);
            for (String target : b.getProperty(property2)) {
                char[] cTarget = getArrayRepresentation(target);
                sim = proximity(cSource, cTarget);
                if (sim > value) {
                    value = sim;
                }
            }
        }
        return sim;
    }

    @Override
    public String getName() {
        return "jaro-winkler";
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return -1d;
    }
}
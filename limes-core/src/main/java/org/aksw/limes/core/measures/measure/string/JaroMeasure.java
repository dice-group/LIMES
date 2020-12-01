/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.string;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.io.cache.Instance;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class JaroMeasure extends StringMeasure {
    public static int getTranspositions(List<Character> source, List<Character> target) {
        if (source.isEmpty() || target.isEmpty() || (source.size() != target.size())) {
            return -1;
        }
        int transpositions = 0;
        for (int i = 0; i < source.size(); i++) {
            if (!source.get(i).equals(target.get(i)))
                transpositions++;
        }
        transpositions /= 2.0f;
        return transpositions;
    }

    /**
     * returns a string buffer of characters from string1 within string2 if they
     * are of a given distance seperation from the position in string1.
     *
     * @param string1 String 
     * @param string2 String
     * @param distanceSep distance separator
     * @return a string buffer of characters from string1 within string2 if they
     *         are of a given distance seperation from the position in string1
     */
    public static List<Character> getCommonCharacters(final String string1, final String string2,
            final int distanceSep) {
        // create a return buffer of characters
        List<Character> returnCommons = new ArrayList<Character>();
        // create a copy of string2 for processing
        char[] copy = string2.toCharArray();
        // iterate over string1
        int n = string1.length();
        int m = string2.length();
        for (int i = 0; i < n; i++) {
            char ch = string1.charAt(i);
            // set boolean for quick loop exit if found
            boolean foundIt = false;
            // compare char with range of characters to either side

            for (int j = Math.max(0, i - distanceSep); !foundIt && j < Math.min(i + distanceSep, m); j++) {
                // check if found
                if (copy[j] == ch) {
                    foundIt = true;
                    // append character found
                    returnCommons.add(ch);
                    // alter copied string2 for processing
                    copy[j] = (char) 0;
                }
            }
        }
        return returnCommons;
    }

    /**
     * gets the similarity of the two strings using Jaro distance.
     *
     * @param string1
     *            the first input string
     * @param string2
     *            the second input string
     * @return a value between 0-1 of the similarity
     */

    public float getSimilarity(String string1, String string2) {

        // get half the length of the string rounded up - (this is the distance
        // used for acceptable transpositions)
        int halflen = ((int) (Math.min(string1.length(), string2.length()))) / 2;

        // get common characters
        List<Character> common1 = getCommonCharacters(string1, string2, halflen);
        List<Character> common2 = getCommonCharacters(string2, string1, halflen);

        // check for zero in common

        // get the number of transpositions
        int transpositions = getTranspositions(common1, common2);
        if (transpositions == -1)
            return 0f;

        // calculate jaro metric
        return (common1.size() / ((float) string1.length()) + common2.size() / ((float) string2.length())
                + (common1.size() - transpositions) / ((float) common1.size())) / 3.0f;
    }

    public double getSimilarity(Object object1, Object object2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getType() {
        return "string";
    }

    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        double max = 0;
        double sim = 0;
        for (String source : instance1.getProperty(property1)) {
            for (String target : instance2.getProperty(property2)) {
                sim = getSimilarity(source, target);
                if (sim > max) {
                    max = sim;
                }
            }
        }
        return max;
    }

    public String getName() {
        return "jaro";
    }

    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }

    @Override
    public int getPrefixLength(int tokensNumber, double threshold) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMidLength(int tokensNumber, double threshold) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getSizeFilteringThreshold(int tokensNumber, double threshold) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getAlpha(int xTokensNumber, int yTokensNumber, double threshold) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getSimilarity(int overlap, int lengthA, int lengthB) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean computableViaOverlap() {
        // TODO Auto-generated method stub
        return false;
    }

}
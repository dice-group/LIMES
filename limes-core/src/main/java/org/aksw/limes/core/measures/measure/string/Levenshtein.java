/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.aksw.limes.core.measures.measure.string;


import org.aksw.limes.core.io.cache.Instance;
/**
 *
 * @author ngonga
 */
public class Levenshtein extends StringMeasure {

    public int getPrefixLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMidLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getSizeFilteringThreshold(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getAlpha(int xTokensNumber, int yTokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getSimilarity(int overlap, int lengthA, int lengthB) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getSimilarity(Object a, Object b) {
        return (new uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein()).getSimilarity(a+"", b+"");
    }

    public String getType() {
        return "string";
    }

    public double getSimilarity(Instance a, Instance b, String property1, String property2) {
        double sim = 0;
        double max = 0;
        for (String p1 : a.getProperty(property1)) {
            for (String p2 : b.getProperty(property2)) {
                sim = getSimilarity(p1, p2);
                if (max < sim) {
                    max = sim;
                }
            }
        }
        return max;
    }

    public String getName() {
        return "levenshtein";
    }

    public boolean computableViaOverlap() {
        return false;
    }

    //fake value
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize/5000d;
    }

}

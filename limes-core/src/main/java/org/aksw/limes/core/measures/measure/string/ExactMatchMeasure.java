/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.string;

import org.aksw.limes.core.io.cache.Instance;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class ExactMatchMeasure extends StringMeasure {

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
        if (overlap == lengthA && lengthA == lengthB)
            return 1d;
        return 0d;
    }

    public boolean computableViaOverlap() {
        return true;
    }

    public double getSimilarity(Object object1, Object object2) {
        if ((object1 + "").equals(object2 + ""))
            return 1d;
        return 0d;
    }

    public String getType() {
        return "string";
    }

    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        for (String source : instance1.getProperty(property1)) {
            for (String target : instance2.getProperty(property2)) {
                if (source.equals(target))
                    return 1d;
            }

        }
        return 0d;
    }

    public String getName() {
        return "exactMatch";
    }

    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }


}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.string;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Instance;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class TrigramMeasure extends StringMeasure {

    public TrigramMeasure() {
    }


    public double getSimilarity(int overlap, int lengthA, int lengthB) {
        return ((double) 2 * overlap) / (double) (lengthA + lengthB);
    }

    public String getType() {
        return "string";
    }

    // need to set back p1 to p2 and p2 to p1
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        double sim = 0;
        double max = 0;

        for (String p1 : instance1.getProperty(property1)) {
            for (String p2 : instance2.getProperty(property2)) {
                sim = getSimilarity(p1, p2);
                if (max < sim) {
                    max = sim;
                }
            }
        }
        return max;
    }

    public String getName() {
        return "Trigram";
    }

    public double getSimilarity(Object object1, Object object2) {
        String p1 = "  " + object1 + "  ";
        String p2 = "  " + object2 + "  ";

        if (p1.length() == 4 && p2.length() == 4)
            return 1.0;
        if ((p1.length() == 4 && p2.length() > 4) || (p2.length() == 4 && p1.length() > 4))
            return 0.0;
        Set<String> t1 = getTrigrams(p1);
        Set<String> t2 = getTrigrams(p2);
        double counter = 0;
        for (String s : t1) {
            if (t2.contains(s))
                counter++;
        }
        return counter / (t1.size() + t2.size() - counter);
    }

    public Set<String> getTrigrams(String a) {
        Set<String> result = new HashSet<>();
        String copy = a;

        for (int i = 3; i <= copy.length(); i++) {
            result.add(copy.substring(i - 3, i));
        }
        return result;
    }

    public int getPrefixLength(int tokensNumber, double threshold) {
        int k = 1;
        if (threshold == 0)
            k = 0;

        return (tokensNumber - (int) Math.ceil((float) (tokensNumber * threshold / (2 - threshold))) + k);
    }

    public int getMidLength(int tokensNumber, double threshold) {
        int k = 1;
        if (threshold == 0)
            k = 0;

        return (tokensNumber - (int) Math.ceil((float) (tokensNumber * threshold)) + k);
    }

    public double getSizeFilteringThreshold(int tokensNumber, double threshold) {
        return (double) (threshold / (2 - threshold) * tokensNumber);
    }

    public int getAlpha(int xTokensNumber, int yTokensNumber, double threshold) {
        return (int) Math.ceil((float) (threshold / 2 * (xTokensNumber + yTokensNumber)));
    }

    public boolean computableViaOverlap() {
        return true;
    }

    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }
}

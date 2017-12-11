package org.aksw.limes.core.measures.measure.string;

import org.aksw.limes.core.io.cache.Instance;

public class DoubleMetaphoneMeasure extends StringMeasure {

    public static final int codeLength = 4;

    public static String getCode(String string) {
    	EDoubleMetaphone ephone = new EDoubleMetaphone(string);
        return ephone.getDoubleMetaphoneRepresentation();
    }

    public double proximity(String s1, String s2) {
        char[] c1, c2;
        c1 = DoubleMetaphoneMeasure.getCode(s1).toCharArray();
        c2 = DoubleMetaphoneMeasure.getCode(s2).toCharArray();
        double distance = 0d;
        for (int i = 0; i < c1.length; i++)
            if (c1[i] != c2[i])
                distance += 1d;
        return (1.0d - (distance / (double) DoubleMetaphoneMeasure.codeLength));
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
    public double getSimilarity(Object object1, Object object2) {
        return proximity(object1.toString(), object2.toString());
    }

    @Override
    public String getType() {
        return "string";
    }

    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        double value = 0;
        double sim = 0;
        for (String source : instance1.getProperty(property1)) {
            for (String target : instance2.getProperty(property2)) {
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
        return "doublemeta";
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }

}

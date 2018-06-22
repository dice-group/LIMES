package org.aksw.limes.core.measures.measure.graphs.gouping.labels;

public class NumberSimilarity implements ILabelSimilarity {
    @Override
    public double score(String s1, String s2) {
        double d1 = Double.parseDouble(s1);
        double d2 = Double.parseDouble(s2);
        return 1/(1+(Math.abs(d1 - d2)/Math.max(Math.abs(d1), Math.abs(d2))));
    }
}

package org.aksw.limes.core.measures.measure.graphs.gouping.labels;

import org.aksw.limes.core.measures.measure.string.JaccardMeasure;

public class JaccardSimilarity implements ILabelSimilarity {
    @Override
    public double score(String s1, String s2) {
        return new JaccardMeasure().getSimilarity(s1, s2);
    }
}

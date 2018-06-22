package org.aksw.limes.core.measures.measure.graphs.gouping.labels;

import org.aksw.limes.core.measures.measure.string.CosineMeasure;

public class CosineSimilarity implements ILabelSimilarity {
    @Override
    public double score(String s1, String s2) {
        return new CosineMeasure().getSimilarity(s1, s2);
    }
}

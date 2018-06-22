package org.aksw.limes.core.measures.measure.graphs.gouping.labels;

import org.aksw.limes.core.measures.measure.string.LevenshteinMeasure;

public class LevenshteinSimilarity implements ILabelSimilarity {
    @Override
    public double score(String s1, String s2) {
        return new LevenshteinMeasure().getSimilarity(s1, s2);
    }
}

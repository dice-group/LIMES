package org.aksw.limes.core.measures.measure.graphs.gouping.labels;

public class SimDefinition {

    private ILabelSimilarity similarity;
    private double threshold;

    public SimDefinition(ILabelSimilarity similarity, double threshold) {
        this.similarity = similarity;
        this.threshold = threshold;
    }

    public ILabelSimilarity getSimilarity() {
        return similarity;
    }

    public double getThreshold() {
        return threshold;
    }

}

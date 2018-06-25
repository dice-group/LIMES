package org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster;

import org.aksw.limes.core.measures.measure.MeasureType;

public class SimilarityFilter {

    private MeasureType similarityType;
    private double threshold;

    public SimilarityFilter(MeasureType similarityType, double threshold) {
        this.similarityType = similarityType;
        this.threshold = threshold;
    }

    public MeasureType getSimilarityType() {
        return similarityType;
    }

    public double getThreshold() {
        return threshold;
    }
}

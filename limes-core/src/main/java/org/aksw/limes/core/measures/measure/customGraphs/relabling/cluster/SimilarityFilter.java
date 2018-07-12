package org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster;

import org.aksw.limes.core.measures.measure.MeasureType;

/**
 * A filter for mapping objects
 *
 * @author Cedric Richter
 */

public class SimilarityFilter {

    private MeasureType similarityType;
    private double threshold;

    public SimilarityFilter(MeasureType similarityType, double threshold) {
        this.similarityType = similarityType;
        this.threshold = threshold;
    }

    /**
     *
     * @return the measure to apply for filtering
     */
    public MeasureType getSimilarityType() {
        return similarityType;
    }

    /**
     *
     * @return threshold for the given measure
     */
    public double getThreshold() {
        return threshold;
    }
}

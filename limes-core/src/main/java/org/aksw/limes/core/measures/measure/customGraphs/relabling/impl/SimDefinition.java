package org.aksw.limes.core.measures.measure.customGraphs.relabling.impl;

import org.aksw.limes.core.measures.measure.string.IStringMeasure;

/**
 * @author Cedric Richter
 */
public class SimDefinition {

    private IStringMeasure measure;
    private double threshold;

    public SimDefinition(IStringMeasure measure, double threshold) {
        this.measure = measure;
        this.threshold = threshold;
    }

    public IStringMeasure getMeasure() {
        return measure;
    }

    public double getThreshold() {
        return threshold;
    }

}

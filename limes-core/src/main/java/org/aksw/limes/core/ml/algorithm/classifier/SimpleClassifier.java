/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.classifier;

import java.util.Locale;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 *
 */
public class SimpleClassifier implements Comparable<SimpleClassifier> {
    protected String measure = null;
    protected String sourceProperty = null;
    protected String targetProperty = null;
    protected double threshold = 1.0;
    protected double weight = 1.0;
    protected double fMeasure = 0.0;

    public SimpleClassifier(String measure, double threshold) {
        this.measure = measure;
        this.threshold = threshold;
    }

    public SimpleClassifier(String measure, double threshold, String sourceProperty, String targetProperty) {
        this.measure = measure;
        this.threshold = threshold;
        this.sourceProperty = sourceProperty;
        this.targetProperty = targetProperty;
    }

    public SimpleClassifier clone() {
        SimpleClassifier copy = new SimpleClassifier(measure, threshold);
        copy.setfMeasure(fMeasure);
        copy.sourceProperty = sourceProperty;
        copy.targetProperty = targetProperty;
        copy.weight = weight;
        return copy;
    }

    public double getfMeasure() {
        return fMeasure;
    }

    public void setfMeasure(double fMeasure) {
        this.fMeasure = fMeasure;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    /**
     * @return MetricExpression
     * @author sherif
     */
    public String getMetricExpression() {
//        return measure + "(x." + sourceProperty + ",y." + targetProperty + ")|" + String.format(Locale.ENGLISH, "%.2f", threshold);
        return measure + "(x." + sourceProperty + ",y." + targetProperty + ")|" +  threshold;
    }

    public String getSourceProperty() {
        return sourceProperty;
    }

    public void setSourceProperty(String sourceProperty) {
        this.sourceProperty = sourceProperty;
    }

    public String getTargetProperty() {
        return targetProperty;
    }

    public void setTargetProperty(String targetProperty) {
        this.targetProperty = targetProperty;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "(Source: " + sourceProperty + 
                " Target: " + targetProperty + 
                " Measure: " + measure + 
                " Theta = " + threshold + 
                " FMeasure = " + getfMeasure() + 
                " Weight = " + weight + ")";
    }

    /**
     * Shorter toString().
     *
     * @return m(p1, p2) theta=t, weight=w.
     */
    public String toLinkSpecString() {
        return "" + measure + "(" + sourceProperty + " , " + targetProperty + "):Theta = " + threshold + ", Weight = " + weight;
    }

    @Override
    public int compareTo(SimpleClassifier o) {
        if (this.getfMeasure() > o.getfMeasure()) {
            return 1;
        }
        if (this.getfMeasure() < o.getfMeasure()) {
            return -1;
        }
        return 0;
    }
}


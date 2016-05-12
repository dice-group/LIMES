/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.euclid;

/**
 *
 * @author ngonga
 */
public class SimpleClassifier {
    public String measure = null;
    public double threshold = 1.0;
    public double fMeasure = 0.0;    
    public String sourceProperty = null;    
    public String targetProperty = null;  
    public double weight = 1.0;
    public SimpleClassifier(String measure, double threshold)
    {
        this.measure = measure;
        this.threshold = threshold;
    }
    
    public SimpleClassifier(String measure, double threshold, String sourceProperty, String targetProperty)
    {
        this.measure = measure;
        this.threshold = threshold;
        this.sourceProperty = sourceProperty;
        this.targetProperty = targetProperty;
    }
    
    @Override
    public String toString()
    {
        return "(Source: "+sourceProperty+" Target: "+targetProperty+" Measure: "+measure+" Theta = "+threshold+" FMeasure = "+fMeasure+" Weight = "+weight+")";
    }
    /**
     * Shorter toString().
     * @return m(p1,p2) theta=t, weight=w.
     */
    public String toString2() {
    	return ""+measure+"("+sourceProperty+" , "+targetProperty+"):Theta = "+threshold+", Weight = "+weight;
    }

    /**
     * @return MetricExpression
     * @author sherif
     */
    public String getMetricExpression() {
    	return measure + "(x." + sourceProperty+",y." + targetProperty+")|" + String.format("%.2f", threshold);
    }
    public SimpleClassifier clone() {
        SimpleClassifier copy = new SimpleClassifier(measure, threshold);
        copy.fMeasure = fMeasure;
        copy.sourceProperty = sourceProperty;
        copy.targetProperty = targetProperty;
        copy.weight = weight;
        return copy;
    }
}


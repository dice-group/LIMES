package org.aksw.limes.core.measures.measure.metricfactory;

import org.aksw.limes.core.io.cache.Instance;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Interface for metric factories. Technically not really factories as they do
 * not return objects but rather parametrized the metrics.
 * @author ngonga
 */
public interface MetricFactory {
    /**
     * Transforms the input expression into a string that can be evaluated.
     * 
     *
     * @param expression
     *            Input expression
     */
    public void setExpression(String expression);
    /**
     * Compute similarity between two instances.
     *
     * @param a
     *            source instance
     * @param b
     * 		  target instance
     */
    public float getSimilarity(Instance a, Instance b);
    public String foldExpression(String expression, String var1, String var2);
}

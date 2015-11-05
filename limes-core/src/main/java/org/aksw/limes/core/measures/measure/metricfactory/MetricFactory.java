package org.aksw.limes.core.measures.measure.metricfactory;

import org.aksw.limes.core.data.Instance;

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
    public void setExpression(String expression);
    public float getSimilarity(Instance a, Instance b);
    public String foldExpression(String expression, String var1, String var2);
}

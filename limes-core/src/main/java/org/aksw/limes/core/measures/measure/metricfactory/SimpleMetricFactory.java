package org.aksw.limes.core.measures.measure.metricfactory;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import uk.ac.shef.wit.simmetrics.similaritymetrics.*;

import org.aksw.limes.core.io.cache.Instance;
import org.apache.log4j.*;

/**
 * Implements a simple metric factory that allows for linking on single properties.
 * @author ngonga
 */
public class SimpleMetricFactory implements MetricFactory {

    AbstractStringMetric metric;
    String property1;
    String property2;
    String sourceVar, targetVar;
    Logger logger;
    int counter;

    public SimpleMetricFactory(String sourceVariable, String targetVariable) {
        metric = null;
        logger = Logger.getLogger("LIMES");
        counter = 0;
        sourceVar = sourceVariable;
        targetVar = targetVariable;
    }

    /**
     * Sets the metric to the metric whose name is expression. If expression does
     * not correspond to any known metric, the metric is set to Levenshtein
     * @param expression The name of the metric
     */
    public void setExpression(String expression) {
        //get metric name

        metric = new Levenshtein();
        if (expression.toLowerCase().startsWith("blockdistance")) {
            metric = new BlockDistance();
        } else if (expression.toLowerCase().startsWith("euclidean")) {
            metric = new EuclideanDistance();
        } else if (expression.toLowerCase().startsWith("qgrams")) {
            metric = new QGramsDistance();
        } else if (expression.toLowerCase().startsWith("levenshtein")) {
            metric = new Levenshtein();
        } 

        //get properties
        logger.info("Expression = "+expression);
        expression = expression.substring(expression.indexOf("(")+1, expression.indexOf(")"));
        logger.info("Variables = "+expression);
        String split[] = expression.split(",");
        if (split[0].contains(sourceVar.replaceAll("\\?", ""))) {
            property1 = split[0].substring(split[0].indexOf(".") + 1);
            property2 = split[1].substring(split[1].indexOf(".") + 1);
        }
        else
        {
            property2 = split[0].substring(split[0].indexOf(".") + 1);
            property1 = split[1].substring(split[1].indexOf(".") + 1);
        }

         //logger.info("Source property = "+property1 +"; Target property = "+property2);
    }

    /** Compute the similarity of two property values
     *
     * @param a Property value 1
     * @param b Property value 1
     * @return Similarity
     */
    public float getSimilarity(String a, String b)
    {
        return metric.getSimilarity(a, b);
    }
    /** Computes the similarity between a and b
     *
     * @param a First instance
     * @param b Second instance
     * @return Similarty of a and b
     */
    public float getSimilarity(Instance a, Instance b) {
        //counter++;
        float max = 0;
        float metricValue = 0;

        for (String value1 : a.getProperty(property1)) {
            for (String value2 : b.getProperty(property2)) {
                metricValue = metric.getSimilarity(value1, value2);
                counter++;
                if (metricValue > max) {
                    max = metricValue;
                }
            }
        }

        /*
        String value1, value2;
        Iterator<String> aIter = a.getProperty(property1).iterator();
        Iterator<String> bIter = b.getProperty(property2).iterator();
        while (aIter.hasNext()) {
        value1 = aIter.next();
        while (bIter.hasNext()) {
        value2 = bIter.next();
        metricValue = metric.getSimilarity(value1, value2);
        if (metricValue > max) {
        max = metricValue;
        }
        }
        } */
        //logger.info("Compared "+a.getUri()+" and "+b.getUri()+" and got "+max);
        return max;
    }

    /**
     * Returns the number of calls of this factory
     * @return Number of calls of this factor
     */
    public int getComparisons() {
        return counter;
    }

    /**
     * Folds the bi-space metric given in into a metric within the target source
     * @param expression
     * @param var1
     * @param var2
     * @return The folded expression for the metric.
     */
    public String foldExpression(String expression, String var1, String var2) {
        String copy = expression + "";
        String props = expression.substring(expression.indexOf("(") + 1, expression.indexOf(")"));
        String split[] = props.split(",");
        split[0] = split[0].trim();
        split[1] = split[1].trim();
        if (split[0].startsWith(var1)) {
            copy = copy.replaceAll(split[1], split[0]);
        } else {
            copy = copy.replaceAll(split[0], split[1]);
        }
        return copy;
    }
}

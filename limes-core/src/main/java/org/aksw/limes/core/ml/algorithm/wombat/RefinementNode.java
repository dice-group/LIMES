package org.aksw.limes.core.ml.algorithm.wombat;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

public class RefinementNode implements Comparable<RefinementNode> {

    private double fMeasure = -Double.MAX_VALUE;
    protected AMapping map = MappingFactory.createDefaultMapping();
    protected String metricExpression = "";

    public RefinementNode(double fMeasure, AMapping map, String metricExpression) {
        this.setfMeasure(fMeasure);
        this.setMap(map);
        this.setMetricExpression(metricExpression);
    }

    @Override
    public int compareTo(RefinementNode o) {
        return (int) (fMeasure - o.getFMeasure());
    }

    public double getFMeasure() {
        return fMeasure;
    }

    public AMapping getMapping() {
        return map;
    }

    public String getMetricExpression() {
        return metricExpression;
    }

    public void setMetricExpression(String metricExpression) {
        this.metricExpression = metricExpression;
    }

    public void setfMeasure(double fMeasure) {
        this.fMeasure = fMeasure;
    }

    public void setMap(AMapping map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return getMetricExpression() + " (F = " + getFMeasure() + ")";
    }
}

package org.aksw.limes.core.ml.algorithm.wombat;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;

/**
 *
 */
public class ExtendedRefinementNode extends RefinementNode {

    private double maxFMeasure = 1d;

    public ExtendedRefinementNode() {
        super(-Double.MAX_VALUE, MappingFactory.createDefaultMapping(), "");
    }


    public ExtendedRefinementNode(double fMeasure, AMapping map, String metricExpression) {
        super(fMeasure, map , metricExpression);
    }

    public ExtendedRefinementNode(double fMeasure, AMapping map, String metricExpression, AMapping refMap, double beta, double rMax) {
        super(fMeasure, map , metricExpression);
        double pMax = computeMaxPrecision(map, refMap);
        maxFMeasure = (1+Math.pow(beta,2)) * pMax * rMax / (Math.pow(beta,2) * pMax + rMax);
    }

    private double computeMaxPrecision(AMapping map, AMapping refMap) {
        AMapping falsePos = MappingFactory.createDefaultMapping();
        for (String key : map.getMap().keySet()) {
            for (String value : map.getMap().get(key).keySet()) {
                if (refMap.getMap().containsKey(key) || refMap.getReversedMap().containsKey(value)) {
                    falsePos.add(key, value, map.getMap().get(key).get(value));
                }
            }
        }
        AMapping m = MappingOperations.difference(falsePos, refMap);
        return (double) refMap.size() / (double) (refMap.size() + m.size());
    }

    /**
     * @return max F-Score
     * @author sherif
     */
    public double getMaxFMeasure() {
        return maxFMeasure;
    }

    public void setMaxFMeasure(double maxFMeasure) {
        this.maxFMeasure = maxFMeasure;
    }


}

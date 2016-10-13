/**
 *
 */
package org.aksw.limes.core.ml.algorithm.wombat;


import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Precision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;


/**
 * @author sherif
 */
public class RefinementNode implements Comparable<RefinementNode> {

    protected static double rMax = -Double.MAX_VALUE;
    protected static boolean saveMapping = true;
    protected double precision = -Double.MAX_VALUE;
    protected double recall = -Double.MAX_VALUE;
    protected double fMeasure = -Double.MAX_VALUE;
    protected double maxFMeasure = 1d;
    protected AMapping map = MappingFactory.createDefaultMapping();
    protected String metricExpression = new String();

    /**
     * Constructor
     *
     * @author sherif
     */
    public RefinementNode() {
    }


    /**
     * Constructor
     *
     * @param fMeasure
     * @param map
     * @param metricExpression
     * @author sherif
     */
    public RefinementNode(double fMeasure, AMapping map, String metricExpression) {
        super();
        this.setfMeasure(fMeasure);
        this.setMap(map);
        this.setMetricExpression(metricExpression);
    }


    /**
     * Note: basically used for unsupervised version of WOMBAT
     *
     * @param map
     * @param metricExpression
     * @param fMeasure
     */
    public RefinementNode(AMapping map, String metricExpression, double fMeasure) {
        super();
        this.setfMeasure(fMeasure);
        this.setMap(saveMapping ? map : null);
        this.setMetricExpression(metricExpression);

    }


    /**
     * Constructor
     *
     * @param map
     * @param metricExpression
     * @param refMap
     * @author sherif
     */
    public RefinementNode(AMapping map, String metricExpression, AMapping refMap) {
        super();
        this.setPrecision(new Precision().calculate(map, new GoldStandard(refMap)));
        this.setRecall(new Recall().calculate(map, new GoldStandard(refMap)));
        this.setfMeasure((precision == 0 && recall == 0) ? 0 : 2 * precision * recall / (precision + recall));
        double pMax = computeMaxPrecision(map, refMap);
        this.setMaxFMeasure(2 * pMax * rMax / (pMax + rMax));
        this.setMap(saveMapping ? map : null);
        this.setMetricExpression(metricExpression);
    }

    public static double getrMax() {
        return rMax;
    }

    public static void setrMax(double rMax) {
        RefinementNode.rMax = rMax;
    }

    public static boolean isSaveMapping() {
        return saveMapping;
    }

    public static void setSaveMapping(boolean saveMapping) {
        RefinementNode.saveMapping = saveMapping;
    }

    /* (non-Javadoc)
     * Compare RefinementNodes based on fitness
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
    @Override
    public int compareTo(RefinementNode o) {
        return (int) (fMeasure - o.getFMeasure());

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

    public double getFMeasure() {
        return fMeasure;
    }

    public AMapping getMapping() {
        return map;
    }

    /**
     * @return max F-Score
     * @author sherif
     */
    public double getMaxFMeasure() {
        return 0;
    }

    public void setMaxFMeasure(double maxFMeasure) {
        this.maxFMeasure = maxFMeasure;
    }

    public String getMetricExpression() {
        return metricExpression;
    }

    public void setMetricExpression(String metricExpression) {
        this.metricExpression = metricExpression;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public void setfMeasure(double fMeasure) {
        this.fMeasure = fMeasure;
    }

    public void setMap(AMapping map) {
        this.map = map;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
        return
                getMetricExpression() +
                        //				this.hashCode()+
                        //				" (P = " + precision + ", " + "R = " + recall + ", " + "F = " + fMeasure + ")";
                        " (F = " + getFMeasure() + ")";
    }
}

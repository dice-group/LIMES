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

    protected static double maxRecall = -Double.MAX_VALUE;
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
     * @param map to include in the RefinementNode
     * @param metricExpression to include in the RefinementNode
     * @param fMeasure to include in the RefinementNode
     *
     * @author sherif
     */
    public RefinementNode(AMapping map, String metricExpression, double fMeasure) {
        super();
        this.setfMeasure(fMeasure);
        this.setMap(map);
        this.setMetricExpression(metricExpression);
    }

    public RefinementNode(AMapping map, String metricExpression, double fMeasure, 
            double precesion, double recall, double maxFMeasure) {
        this(map, metricExpression, fMeasure);
        this.setfMeasure(fMeasure);
        this.setPrecision(precesion);
        this.setRecall(recall);
        this.setMaxFMeasure(maxFMeasure);
    }


    public static double getMaxRecall() {
        return maxRecall;
    }

    public static void setMaxRecall(double maxRecall) {
        RefinementNode.maxRecall = maxRecall;
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
        double diff = fMeasure - o.getFMeasure();
        if(diff < 0) {
            return -1;
        } else if (diff > 0) {
            return 1;
        }
        return 0;

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
        return maxFMeasure;
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
        this.map = saveMapping ? map : null;
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

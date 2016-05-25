/**
 * 
 */
package org.aksw.limes.core.ml.algorithm.wombat;



import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Precision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations;



/**
 * @author sherif
 *
 */
public class RefinementNode implements Comparable<RefinementNode> {

    protected double precision             = -Double.MAX_VALUE;
    protected double recall                = -Double.MAX_VALUE;
	protected double fMeasure              = -Double.MAX_VALUE;
	protected double maxFMeasure           = 1d;
	protected Mapping map                  = new MemoryMapping();
	protected String metricExpression 	   = new String();
	protected static double rMax 		   = -Double.MAX_VALUE;
	protected static boolean saveMapping   = true;

	/**
	 * Constructor
	 * 
	 *@author sherif
	 */
	public RefinementNode() {
	}

	/**
	 * Constructor
	 * 
	 * @param fMeasure
	 * @param map
	 * @param metricExpression
	 *@author sherif
	 */
	public RefinementNode(double fMeasure, Mapping map, String metricExpression) {
		super();
		this.setfMeasure(fMeasure);
		this.setMap(map);
		this.setMetricExpression(metricExpression);
	}

	/**
	 * Constructor
	 * 
	 * @param PRECISION
	 * @param RECALL
	 * @param map
	 * @param metricExpression
	 *@author sherif
	 */
	public RefinementNode(Mapping map, String metricExpression, Mapping refMap) {
		super();
		this.setPrecision(new Precision().calculate(map, new GoldStandard(refMap)));
		this.setRecall(new Recall().calculate(map, new GoldStandard(refMap)));
		this.setfMeasure((precision == 0 && recall == 0) ? 0 : 2 * precision * recall / (precision + recall));
		double pMax = computeMaxPrecision(map, refMap);
		this.setMaxFMeasure(2 * pMax * rMax / (pMax + rMax));
		this.setMap(saveMapping ? map : null);
		this.setMetricExpression(metricExpression);
	}


	/**
	 * Note: basically used for unsupervised version of WOMBAT 
	 * @param map
	 * @param metricExpression
	 * @param fMeasure
	 */
	public RefinementNode(Mapping map, String metricExpression, double fMeasure) {
		super();
		this.setfMeasure(fMeasure);
		this.setMap(saveMapping ? map : null);
		this.setMetricExpression(metricExpression);

	}

	private double computeMaxPrecision(Mapping map, Mapping refMap) {
		Mapping falsePos = new MemoryMapping();
		for(String key: map.getMap().keySet()){
			for(String value : map.getMap().get(key).keySet()){
				if(refMap.getMap().containsKey(key) || refMap.getReversedMap().containsKey(value)){
					falsePos.add(key, value, map.getMap().get(key).get(value));
				}
			}
		}
		Mapping m = MappingOperations.difference(falsePos, refMap);
		return (double)refMap.size()/(double)(refMap.size() + m.size());
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
				" (F = " + getfMeasure() + ")";
	}


	/* (non-Javadoc)
	 * Compare RefinementNodes based on fitness
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RefinementNode o) {
		return (int) (fMeasure - o.getfMeasure());

	}




	/**
	 * @return max F-Score
	 * @author sherif
	 */
	public double getMaxFMeasure() {
		return 0;
	}

    public double getfMeasure() {
        return fMeasure;
    }

    public void setfMeasure(double fMeasure) {
        this.fMeasure = fMeasure;
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

    public void setMaxFMeasure(double maxFMeasure) {
        this.maxFMeasure = maxFMeasure;
    }

    public Mapping getMap() {
        return map;
    }

    public void setMap(Mapping map) {
        this.map = map;
    }

    public String getMetricExpression() {
        return metricExpression;
    }

    public void setMetricExpression(String metricExpression) {
        this.metricExpression = metricExpression;
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
}

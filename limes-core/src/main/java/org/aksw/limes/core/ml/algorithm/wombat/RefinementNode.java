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

	public double precision 		= -Double.MAX_VALUE;
	public double recall 			= -Double.MAX_VALUE;
	public double fMeasure 			= -Double.MAX_VALUE;
	public double maxFMeasure 		= 1d;
	public Mapping map 				= new MemoryMapping();
	public String metricExpression 	= new String();
	public static double rMax 		= -Double.MAX_VALUE;

	public static boolean saveMapping = true;

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
		this.fMeasure = fMeasure;
		this.map = map;
		this.metricExpression = metricExpression;
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
		this.precision 	= new Precision().calculate(map, new GoldStandard(refMap));
		this.recall 	= new Recall().calculate(map, new GoldStandard(refMap));
		this.fMeasure = (this.precision == 0 && this.recall == 0) ? 0 : 2 * precision * recall / (precision + recall);
		double pMax = computeMaxPrecision(map, refMap);
		this.maxFMeasure = 2 * pMax * rMax / (pMax + rMax);
		this.map = saveMapping ? map : null;
		this.metricExpression = metricExpression;
	}


	/**
	 * Note: basically used for unsupervised version of WOMBAT 
	 * @param map
	 * @param metricExpression
	 * @param fMeasure
	 */
	public RefinementNode(Mapping map, String metricExpression, double fMeasure) {
		super();
		this.fMeasure = fMeasure;
		this.map = saveMapping ? map : null;
		this.metricExpression = metricExpression;

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
				metricExpression + 
				//				this.hashCode()+
				//				" (P = " + precision + ", " + "R = " + recall + ", " + "F = " + fMeasure + ")";
				" (F = " + fMeasure + ")";
	}


	/* (non-Javadoc)
	 * Compare RefinementNodes based on fitness
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RefinementNode o) {
		return (int) (fMeasure - o.fMeasure);

	}




	/**
	 * @return max F-Score
	 * @author sherif
	 */
	public double getMaxFMeasure() {
		return 0;
	}
}

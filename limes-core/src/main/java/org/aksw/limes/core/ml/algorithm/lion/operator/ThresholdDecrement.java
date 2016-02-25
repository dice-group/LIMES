package org.aksw.limes.core.ml.algorithm.lion.operator;

import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.io.ls.LinkSpecification;

/**
 * Default implementation of a threshold decreaser: multiplies
 * @author Klaus Lyko
 *
 */
public class ThresholdDecrement implements ThresholdDecreaser {

	double decreaseFactorHigh = 0.850d;
	double decreaseLow = 0.3d;
	double decrease = 0.925d;
//	double decrement = 0.05;
	
	
	
//	public Set<Double> boldDecrease(double threshold) {
//		HashSet<Double> decr = new HashSet<Double>();
////		decr.add(threshold*decreaseFactorHigh);
////		decr.add(threshold*decreaseLow);
//		return decr;
//	}
	
	@Override
	public Set<Double> decrease(LinkSpecification spec) {
		String metric = spec.getMeasure().substring(0, spec.getMeasure().indexOf("("));
		double threshold = spec.getThreshold();
		Set<Double> thresholdNew = decreaseMetricSpecific(metric, threshold);
		
		Set<Double> set = new HashSet<Double>();
//		set.add(decreaseIntelligent(metric, threshold));
//		set.add(spec.threshold*decreaseLow);
//		if(thresholdNew != -1) {
			set.addAll(thresholdNew);
//		}
//		for(Double d : boldDecrease(threshold)) {
//			if(d >= 0.15)
//				set.add(d);
//		}
		return set;
	}
	
	public Set<Double> decreaseMetricSpecific(String metric, double threshold)
	{
		Set<Double> set = new HashSet<Double>();
//		System.out.println("Decrement on "+metric);
//		if(metric.toLowerCase().equals("jaccard"))
//			threshold=threshold*0.9;
//		else 
//		} else if(metric.toLowerCase().equals("trigrams")||metric.equalsIgnoreCase("Trigram"))
//		threshold-=0.3;
//		String measure = metric.substring(0, metric.indexOf("("));
		if(metric.toLowerCase().equals("levenshtein"))
		{	
//			System.out.println("Using levensthein decrements");
			//according to theta = 1/(1+d)
			
			if(threshold > 0.951) {
				set.add(0.95);
//				System.out.println("Threshold levenstein set to 0.9");
			}
			else {
				double d = (1/threshold)-1; // 1 => 0.9 => 0.47 => 0.32 => 0.2 ...
				d++;
				threshold=1/(1+d);// it goes 1,1/2,1/3,..etc
				set.add(threshold);
			}
		// general case
		} else {
			set.add(threshold*decrease);
		}
		
		// we do not want to go below 0.2 (too dissimilar)
		if(threshold<0.15) {
			return set;
		}
		return set;
	}
}

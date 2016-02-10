package org.aksw.limes.core.ml.algorithm.lion.operator;

import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.io.ls.LinkSpecification;

public class ThresholdDecrementSimple implements ThresholdDecreaser {

	double decreaseFactorHigh = 0.850d;
	double decreaseLow = 0.75d;
	
	@Override
	public Set<Double> decrease(LinkSpecification spec) {
		Set<Double> set = new HashSet<Double>();
		set.add(spec.getThreshold()*decreaseFactorHigh);
//		set.add(spec.threshold*decreaseLow);
		return set;
	}
	
}

package org.aksw.limes.core.ml.algorithm.dragon.Utils;

import java.util.List;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.dragon.TrainingInstance;

public class InstanceCalculator {

	/**
	 * 
	 * @param instanceList
	 * @return [positive][negative]
	 */
	public static double[] getNumberOfPositiveNegativeInstances(List<TrainingInstance> instanceList) {
		double[] posNegNumber = { 0.0, 0.0 };
		for (TrainingInstance t : instanceList) {
			if (t.getClassLabel() > 0.9) {
				posNegNumber[0]++;
			} else {
				posNegNumber[1]++;
			}
		}
		return posNegNumber;
	}

	/**
	 * 
	 * @param m
	 * @return [positive][negative]
	 */
	public static double[] getNumberOfPositiveNegativeInstances(AMapping m) {
		double[] posNegNumber = { 0.0, 0.0 };
		for (String s : m.getMap().keySet()) {
			for (String t : m.getMap().get(s).keySet()) {
				double value = m.getMap().get(s).get(t);
				if (value > 0.9) {
					posNegNumber[0]++;
				} else {
					posNegNumber[1]++;
				}
			}
		}
		return posNegNumber;
	}
}

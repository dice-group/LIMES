package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * This method calculates the accuracy of the mapping. It is defined as the proportion of true results (positive or negative) to the total number
 * of the population. It can be calculated using the following formula:
 * (T+) + (T-)/(+) + (-)),  T+: true positive, T-:True negative(mxn-goldstandard-F+), +: all postitive (gold standard), -: all possible links out of gold standard(mxn-gold) 
 * 
 * @author Mofeed Hassan <mounir@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 1.0
 */
public class Accuracy extends PRF implements IQualitativeMeasure {

	@Override
	public double calculate(Mapping predictions, GoldStandard goldStandard) {
		double truePositiveValue = trueFalsePositive(predictions, goldStandard.goldStandard, true);
		long allPositiveValue = goldStandard.goldStandard.size();
		//double falsePositiveValue = trueFalsePositive(predictions, goldStandard, false);
		double trueNegativeValue = trueNegative(allPositiveValue, goldStandard.targetUris.size() , goldStandard.sourceUris.size());
		return (truePositiveValue + trueNegativeValue)/(goldStandard.targetUris.size() + goldStandard.sourceUris.size());
	}
}

package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.evaluation.evaluator.GoldStandard;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * (T+) + (T-)/(+) + (-)),  T+: true positive, T-:True negative(mxn-goldstandard-F+), +: all postitive (gold standard), -: all possible links out of gold standard(mxn-gold) 
 * 
 * @author Mofeed Hassan <mounir@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-03
 */
public class Accuracy extends PRF implements QualitativeMeasure {

	@Override
	public double calculate(Mapping predictions, GoldStandard goldStandard) {
		double truePositiveValue = trueFalsePositive(predictions, goldStandard.goldStandard, true);
		long allPositiveValue = goldStandard.goldStandard.size();
		//double falsePositiveValue = trueFalsePositive(predictions, goldStandard, false);
		double trueNegativeValue = trueNegative(allPositiveValue, goldStandard.targetUris.size() , goldStandard.sourceUris.size());
		return (truePositiveValue + trueNegativeValue)/(goldStandard.targetUris.size() + goldStandard.sourceUris.size());
	}
}

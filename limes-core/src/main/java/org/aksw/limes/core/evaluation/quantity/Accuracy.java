package org.aksw.limes.core.evaluation.quantity;

import org.aksw.limes.core.io.mapping.Mapping;

/**
 * (T+) + (T-)/(+) + (-)),  T+: true positive, T-:True negative(mxn-goldstandard-F+), +: all postitive (gold standard), -: all possible links out of gold standard(mxn-gold) 
 * 
 * @author Mofeed Hassan <mounir@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-03
 */
public class Accuracy extends PRF implements QuantitativeMeasure {

	@Override
	public double calculate(Mapping predictions, Mapping goldStandard) {
/*		double truePositiveValue = trueFalsePositive(predictions, goldStandard, true);
		double allPositiveValue = goldStandard.size();
		//double falsePositiveValue = trueFalsePositive(predictions, goldStandard, false);
		double trueNegativeValue = trueNegative(allPositiveValue, sourceDatasetSize, targetDatasetSize);*/
		return 0;
	}
	
	public double calculate(Mapping predictions, Mapping goldStandard, long sourceDatasetSize, long targetDatasetSize) {
		double truePositiveValue = trueFalsePositive(predictions, goldStandard, true);
		long allPositiveValue = goldStandard.size();
		//double falsePositiveValue = trueFalsePositive(predictions, goldStandard, false);
		double trueNegativeValue = trueNegative(allPositiveValue, sourceDatasetSize, targetDatasetSize);
		return (truePositiveValue + trueNegativeValue)/(sourceDatasetSize + targetDatasetSize);
	}

}

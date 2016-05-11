package org.aksw.limes.core.evaluation.evaluator;

import org.aksw.limes.core.evaluation.qualititativeMeasures.Accuracy;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Precision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoPrecision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoRecall;
import org.aksw.limes.core.evaluation.qualititativeMeasures.QualitativeMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;

public class EvaluatorFactory {

	public static QualitativeMeasure getQualitativeMeasure(MeasureType measure)
	{
		if(measure.equals(MeasureType.precision))
			return new Precision();
		else if(measure.equals(MeasureType.recall))
			return new Recall();
		else if(measure.equals(MeasureType.fmeasure))
			return new FMeasure();
		else if(measure.equals(MeasureType.pseuPrecision))
			return new PseudoPrecision();
		else if(measure.equals(MeasureType.PseuRecall))
			return new PseudoRecall();
		else if(measure.equals(MeasureType.pseuFMeasure))
			return new PseudoFMeasure();
		else if(measure.equals(MeasureType.accuracy))
			return new Accuracy();
		return null;
	}
}

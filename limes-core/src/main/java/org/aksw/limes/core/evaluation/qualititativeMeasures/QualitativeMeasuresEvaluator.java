package org.aksw.limes.core.evaluation.qualititativeMeasures;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorFactory;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * This class's function is to evaluate mappings against several qaulitative measures
 * @author mofeed
 * @version 1.0
 */
public class QualitativeMeasuresEvaluator {

    Map<EvaluatorType,Double> evaluations = new HashMap<EvaluatorType,Double>();


    /**
     * @param prediction: the results predicted to represent mappings between two datasets
     * @param goldStandard: It is an object that contains {Mapping-> gold standard, List of source URIs, List of target URIs}
     * @param evaluationMeasures: Set of Measures to evaluate the resulted mappings against
     * @return a Map contains the measure name and the corresponding calculated value
     */
    public Map<EvaluatorType,Double> evaluate (Mapping predictions, GoldStandard goldStandard ,Set<EvaluatorType> evaluationMeasures)
    {
        for (EvaluatorType measureType : evaluationMeasures) {

            IQualitativeMeasure measure = EvaluatorFactory.create(measureType);
            double evaluationValue = measure.calculate(predictions, goldStandard);
            evaluations.put(measureType, evaluationValue);

            /*		if(measureType.equals(MeasureType.precision))
				evaluatePrecision(predictions,goldStandard);
			else if (measureType.equals(MeasureType.recall))
				evaluateRecall(predictions,goldStandard);
			else if (measureType.equals(MeasureType.fmeasure))
				evaluateFMeasure(predictions,goldStandard);
			else if (measureType.equals(MeasureType.pseuFMeasure))
				evaluatePFMeasure(predictions,goldStandard);
			else if (measureType.equals(MeasureType.pseuPrecision))
				evaluatePPrecision(predictions,goldStandard);
			else if (measureType.equals(MeasureType.PseuRecall))
				evaluatePRecall(predictions,goldStandard);
			else if (measureType.equals(MeasureType.accuracy))
				evaluateAccuracy(predictions,goldStandard);
			else if (measureType.equals(MeasureType.auc))
				evaluateAUC(predictions,goldStandard);
			else System.out.println("Error: unrecognized evaluation measure");*/
        }

        return evaluations;
    }
    /*private void evaluatePrecision(Mapping predictions, GoldStandard goldStandard)
	{
		double precision = new Precision().calculate(predictions, goldStandard);
		evaluations.put(MeasureType.precision, precision);
	}
	private void evaluateRecall(Mapping predictions, GoldStandard goldStandard)
	{
		double recall = new Recall().calculate(predictions, goldStandard);
		evaluations.put(MeasureType.recall, recall);
	}
	private void evaluateFMeasure(Mapping predictions, GoldStandard goldStandard)
	{
		double fmeasure = new FMeasure().calculate(predictions, goldStandard);
		evaluations.put(MeasureType.fmeasure, fmeasure);
	}
	private void evaluatePPrecision(Mapping predictions,GoldStandard goldStandard)
	{
		double pPrecision = new PseudoPrecision().calculate(predictions, goldStandard) ;
		evaluations.put(MeasureType.pseuPrecision, pPrecision);
	}
	private void evaluatePRecall(Mapping predictions,GoldStandard goldStandard)
	{
		double pRecall = new PseudoRecall().calculate(predictions, goldStandard);
		evaluations.put(MeasureType.PseuRecall, pRecall);
	}
	private void evaluatePFMeasure(Mapping predictions,GoldStandard goldStandard)
	{
		double pfmeasure = new PseudoFMeasure().calculate(predictions, goldStandard);
		evaluations.put(MeasureType.pseuFMeasure, pfmeasure);
	}

	private void evaluateAccuracy(Mapping predictions, GoldStandard goldStandard)
	{
		double accuracy = new Accuracy().calculate(predictions, goldStandard);
		evaluations.put(MeasureType.accuracy, accuracy);
	}
	private void evaluateAUC(Mapping predictions, GoldStandard goldStandard)
	{
		double auc = new AUC().calculate(predictions, goldStandard);
		evaluations.put(MeasureType.auc, auc);
	}
	@SuppressWarnings("unused")
	private void evaluateAll(Mapping predictions, GoldStandard goldStandard)
	{
		evaluatePrecision(predictions,goldStandard);
		evaluateRecall(predictions,goldStandard);
		evaluateFMeasure(predictions,goldStandard);
		evaluatePPrecision(predictions,goldStandard);
		evaluatePRecall(predictions,goldStandard);
		evaluatePFMeasure(predictions,goldStandard);
		evaluateAccuracy(predictions,goldStandard);
		evaluateAUC(predictions,goldStandard);
	}*/
}

/*public class Evaluate {

	Map<EvalFunc,Double> evaluations = new HashMap<EvalFunc,Double>();
	//long sourceDatasetSize, long targetDatasetSize
	public Map<EvalFunc,Double> evaluate (Mapping predictions, Mapping goldStandard,List<String> sourceUris, List<String> targetUris ,EvalFunc evaluationFunc)
	{

		if(evaluationFunc.equals(EvalFunc.precision))
			evaluatePrecision(predictions,goldStandard);
		else if (evaluationFunc.equals(EvalFunc.recall))
			evaluateRecall(predictions,goldStandard);
		else if (evaluationFunc.equals(EvalFunc.fmeasure))
			evaluateFMeasure(predictions,goldStandard);
		else if (evaluationFunc.equals(EvalFunc.pseuFMeasure))
			evaluatePFMeasure(predictions,sourceUris,targetUris);
		else if (evaluationFunc.equals(EvalFunc.pseuPrecision))
			evaluatePPrecision(predictions,sourceUris,targetUris);
		else if (evaluationFunc.equals(EvalFunc.PseuRecall))
			evaluatePRecall(predictions,sourceUris,targetUris);
		else if (evaluationFunc.equals(EvalFunc.accuracy))
			evaluateAccuracy(predictions,goldStandard,sourceUris.size(),targetUris.size());
		else if (evaluationFunc.equals(EvalFunc.auc))
			evaluateAUC(predictions,goldStandard);
		else if (evaluationFunc.equals(EvalFunc.all))
			evaluateAll(predictions,goldStandard,sourceUris,targetUris);
		return evaluations;
	}
	private void evaluatePrecision(Mapping predictions, Mapping goldStandard)
	{
		double precision = new Precision().calculate(predictions, goldStandard);
		evaluations.put(EvalFunc.precision, precision);
	}
	private void evaluateRecall(Mapping predictions, Mapping goldStandard)
	{
		double recall = new Recall().calculate(predictions, goldStandard);
		evaluations.put(EvalFunc.recall, recall);
	}
	private void evaluateFMeasure(Mapping predictions, Mapping goldStandard)
	{
		double fmeasure = new FMeasure().calculate(predictions, goldStandard);
		evaluations.put(EvalFunc.fmeasure, fmeasure);
	}
	private void evaluatePPrecision(Mapping predictions,List<String> sourceUris, List<String> targetUris)
	{
		double pPrecision = new PseudoFMeasure().getPseudoPrecision(sourceUris,targetUris,predictions);
		evaluations.put(EvalFunc.pseuPrecision, pPrecision);
	}
	private void evaluatePRecall(Mapping predictions,List<String> sourceUris, List<String> targetUris)
	{
		double pRecall = new PseudoFMeasure().getPseudoRecall(sourceUris,targetUris,predictions);
		evaluations.put(EvalFunc.PseuRecall, pRecall);
	}
	private void evaluatePFMeasure(Mapping predictions,List<String> sourceUris, List<String> targetUris)
	{
		double pfmeasure = new PseudoFMeasure().getPseudoFMeasure(sourceUris,targetUris,predictions);
		evaluations.put(EvalFunc.pseuFMeasure, pfmeasure);
	}

	private void evaluateAccuracy(Mapping predictions, Mapping goldStandard,long sourceUrisSize, long targetUrisSize)
	{
		double accuracy = new Accuracy().calculate(predictions, goldStandard,sourceUrisSize,targetUrisSize);
		evaluations.put(EvalFunc.accuracy, accuracy);
	}
	private void evaluateAUC(Mapping predictions, Mapping goldStandard)
	{
		double auc = new AUC().calculate(predictions, goldStandard);
		evaluations.put(EvalFunc.auc, auc);
	}
	private void evaluateAll(Mapping predictions, Mapping goldStandard,List<String> sourceUris, List<String> targetUris)
	{
		evaluatePrecision(predictions,goldStandard);
		evaluateRecall(predictions,goldStandard);
		evaluateFMeasure(predictions,goldStandard);
		evaluatePPrecision(predictions,sourceUris,targetUris);
		evaluatePRecall(predictions,sourceUris,targetUris);
		evaluatePFMeasure(predictions,sourceUris,targetUris);
		evaluateAccuracy(predictions,goldStandard,sourceUris.size(),targetUris.size());
		evaluateAUC(predictions,goldStandard);
	}
}*/

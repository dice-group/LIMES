package org.aksw.limes.core.evaluation.quantity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.evaluation.MeasureType;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * @author mofeed
 *
 */
public class QuantitativeMeasuresEvaluator {
	
	Map<MeasureType,Double> evaluations = new HashMap<MeasureType,Double>();
	
	
	//long sourceDatasetSize, long targetDatasetSize
	public Map<MeasureType,Double> evaluate (Mapping predictions, Mapping goldStandard,List<String> sourceUris, List<String> targetUris ,Set<MeasureType> evaluationMeasures)
	{
		for (MeasureType measureType : evaluationMeasures) {
			
			if(measureType.equals(MeasureType.precision))
				evaluatePrecision(predictions,goldStandard);
			else if (measureType.equals(MeasureType.recall))
				evaluateRecall(predictions,goldStandard);
			else if (measureType.equals(MeasureType.fmeasure))
				evaluateFMeasure(predictions,goldStandard);
			else if (measureType.equals(MeasureType.pseuFMeasure))
				evaluatePFMeasure(predictions,sourceUris,targetUris);
			else if (measureType.equals(MeasureType.pseuPrecision))
				evaluatePPrecision(predictions,sourceUris,targetUris);
			else if (measureType.equals(MeasureType.PseuRecall))
				evaluatePRecall(predictions,sourceUris,targetUris);
			else if (measureType.equals(MeasureType.accuracy))
				evaluateAccuracy(predictions,goldStandard,sourceUris.size(),targetUris.size());
			else if (measureType.equals(MeasureType.auc))
				evaluateAUC(predictions,goldStandard);
			else System.out.println("Error: unrecognized evaluation measure");
		}
		
		return evaluations;
	}
	private void evaluatePrecision(Mapping predictions, Mapping goldStandard)
	{
		double precision = new Precision().calculate(predictions, goldStandard);
		evaluations.put(MeasureType.precision, precision);
	}
	private void evaluateRecall(Mapping predictions, Mapping goldStandard)
	{
		double recall = new Recall().calculate(predictions, goldStandard);
		evaluations.put(MeasureType.recall, recall);
	}
	private void evaluateFMeasure(Mapping predictions, Mapping goldStandard)
	{
		double fmeasure = new FMeasure().calculate(predictions, goldStandard);
		evaluations.put(MeasureType.fmeasure, fmeasure);
	}
	private void evaluatePPrecision(Mapping predictions,List<String> sourceUris, List<String> targetUris)
	{
		double pPrecision = new PseudoFMeasure().getPseudoPrecision(sourceUris,targetUris,predictions);
		evaluations.put(MeasureType.pseuPrecision, pPrecision);
	}
	private void evaluatePRecall(Mapping predictions,List<String> sourceUris, List<String> targetUris)
	{
		double pRecall = new PseudoFMeasure().getPseudoRecall(sourceUris,targetUris,predictions);
		evaluations.put(MeasureType.PseuRecall, pRecall);
	}
	private void evaluatePFMeasure(Mapping predictions,List<String> sourceUris, List<String> targetUris)
	{
		double pfmeasure = new PseudoFMeasure().getPseudoFMeasure(sourceUris,targetUris,predictions);
		evaluations.put(MeasureType.pseuFMeasure, pfmeasure);
	}

	private void evaluateAccuracy(Mapping predictions, Mapping goldStandard,long sourceUrisSize, long targetUrisSize)
	{
		double accuracy = new Accuracy().calculate(predictions, goldStandard,sourceUrisSize,targetUrisSize);
		evaluations.put(MeasureType.accuracy, accuracy);
	}
	private void evaluateAUC(Mapping predictions, Mapping goldStandard)
	{
		double auc = new AUC().calculate(predictions, goldStandard);
		evaluations.put(MeasureType.auc, auc);
	}
	@SuppressWarnings("unused")
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

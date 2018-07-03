package org.aksw.limes.core.evaluation;

import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.datastrutures.TaskData;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.evaluator.Evaluator;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.WombatSimple;

public class ExtendedLSEvaluation {

	public static void main(String[] args) throws UnsupportedMLImplementationException {
		EvaluationData eval = DataSetChooser.getData("person1");
		TaskData td = new TaskData(new GoldStandard(eval.getReferenceMapping(), eval.getSourceCache().getAllUris(),
				eval.getTargetCache().getAllUris()), eval.getSourceCache(), eval.getTargetCache(), eval);
		td.dataName = eval.getName();
		Set<TaskData> data = new HashSet<>();
		Set<EvaluatorType> measures = new HashSet<>();
		data.add(td);
		measures.add(EvaluatorType.F_MEASURE);
		AMLAlgorithm wombat = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
				MLImplementationType.SUPERVISED_BATCH);
		LogicOperator[] operators = { LogicOperator.ALGEBRAICT, LogicOperator.ALGEBRAICTCO, LogicOperator.ALGEBRAICDIFF,
				LogicOperator.EINSTEINT, LogicOperator.EINSTEINTCO, LogicOperator.EINSTEINDIFF, LogicOperator.AND,
				LogicOperator.OR, LogicOperator.MINUS };
		((WombatSimple) wombat.getMl()).setAvailableOperators(operators);
		new Evaluator().crossValidate(wombat, null, data, 10, measures, null);
	}
}

package org.aksw.limes.core.evaluation;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.datastrutures.TaskAlgorithm;
import org.aksw.limes.core.datastrutures.TaskData;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.evaluator.Evaluator;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.evaluator.Summary;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.WombatSimple;

public class ExtendedLSEvaluation {

	public static void main(String[] args) throws UnsupportedMLImplementationException, FileNotFoundException {
		Set<TaskData> data = new HashSet<>();
		List<TaskAlgorithm> algos = new ArrayList<>();
		String[] dataSetNames = new String[] { "restaurants", "drugs" };
		for (String d : dataSetNames) {
			EvaluationData eval = DataSetChooser.getData(d);
			TaskData td = new TaskData(new GoldStandard(eval.getReferenceMapping(), eval.getSourceCache().getAllUris(),
					eval.getTargetCache().getAllUris()), eval.getSourceCache(), eval.getTargetCache(), eval);
			td.dataName = eval.getName();
			data.add(td);
		}
		Set<EvaluatorType> measures = new HashSet<>();
		measures.add(EvaluatorType.F_MEASURE);
		measures.add(EvaluatorType.PRECISION);
		measures.add(EvaluatorType.RECALL);

		LogicOperator[] crispOps = { LogicOperator.AND, LogicOperator.OR, LogicOperator.MINUS };
		LogicOperator[] fuzzyOps = { LogicOperator.ALGEBRAICT, LogicOperator.ALGEBRAICTCO, LogicOperator.ALGEBRAICDIFF,
				LogicOperator.EINSTEINT, LogicOperator.EINSTEINTCO, LogicOperator.EINSTEINDIFF };
		LogicOperator[] hamacherOps = { LogicOperator.HAMACHERT, LogicOperator.HAMACHERTCO,
				LogicOperator.HAMACHERDIFF };
		LogicOperator[] allOps = { LogicOperator.AND, LogicOperator.OR, LogicOperator.MINUS, LogicOperator.ALGEBRAICT,
				LogicOperator.ALGEBRAICTCO, LogicOperator.ALGEBRAICDIFF, LogicOperator.EINSTEINT,
				LogicOperator.EINSTEINTCO, LogicOperator.EINSTEINDIFF, LogicOperator.HAMACHERT,
				LogicOperator.HAMACHERTCO, LogicOperator.HAMACHERDIFF };
		TaskAlgorithm crisp = createWombatWithOperators(crispOps);
		crisp.setName("crisp");
		TaskAlgorithm fuzzy = createWombatWithOperators(fuzzyOps);
		fuzzy.setName("fuzzy");
		TaskAlgorithm hamacher = createWombatWithOperators(hamacherOps);
		hamacher.setName("hamacher");
		TaskAlgorithm all = createWombatWithOperators(allOps);
		all.setName("all");
		algos.add(crisp);
		algos.add(fuzzy);
		algos.add(hamacher);
		algos.add(all);
		Summary s = new Evaluator().crossValidateWithTuningAndStatisticalTest(algos, data, measures, 10);
		System.out.println(s);
		s.printToFiles("/tmp/");
	}

	public static TaskAlgorithm createWombatWithOperators(LogicOperator[] operators)
			throws UnsupportedMLImplementationException {
		AMLAlgorithm algo = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
				MLImplementationType.SUPERVISED_BATCH);
		((WombatSimple) algo.getMl()).setAvailableOperators(operators);
		TaskAlgorithm tAlgo = new TaskAlgorithm(MLImplementationType.SUPERVISED_BATCH, algo, null, null);
		return tAlgo;
	}
}

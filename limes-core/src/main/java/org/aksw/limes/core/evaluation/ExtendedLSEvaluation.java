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
import org.aksw.limes.core.ml.algorithm.fptld.FPTLD;

public class ExtendedLSEvaluation {

	public static void main(String[] args) throws UnsupportedMLImplementationException, FileNotFoundException {
		// EvaluationData eval = DataSetChooser.getData("PERSON2");
		// LinkSpecification ls = new LinkSpecification(
		// "MINUS(qgrams(x.http://www.okkam.org/ontology_person1.owl#has_address,y.http://www.okkam.org/ontology_person2.owl#has_address)|0.81,qgrams(x.http://www.okkam.org/ontology_person1.owl#surname,y.http://www.okkam.org/ontology_person2.owl#surname)|0.5904900000000002)",
		// 0.0);
		// CanonicalPlanner dp = new CanonicalPlanner();
		// SimpleExecutionEngine ee = new SimpleExecutionEngine(eval.getSourceCache(),
		// eval.getTargetCache(), "?x", "?y");
		// AMapping prediction = ee.execute(ls, dp);
		//
		// LinkSpecification ls2 = new LinkSpecification(
		// "PLUKDIFF_1.0(qgrams(x.http://www.okkam.org/ontology_person1.owl#has_address,y.http://www.okkam.org/ontology_person2.owl#has_address)|0.81,qgrams(x.http://www.okkam.org/ontology_person1.owl#surname,y.http://www.okkam.org/ontology_person2.owl#surname)|0.5904900000000002)",
		// 0.0);
		// AMapping prediction2 = ee.execute(ls, dp);
		// System.out.println(prediction.size() + " vs. " + prediction2.size());
		// for (String s : prediction2.getMap().keySet()) {
		// for (String t : prediction2.getMap().get(s).keySet()) {
		// if (prediction.getMap().get(s) == null || prediction.getMap().get(s).get(t)
		// == null) {
		// System.out.println(s + t + prediction2.getMap().get(s).get(t));
		// }
		// }
		// }
		Set<TaskData> data = new HashSet<>();
		List<TaskAlgorithm> algos = new ArrayList<>();
		// String[] dataSetNames = new String[] {
		// "restaurantsfixed", "person1",
		// "person2",
		// "DBLPACM", "ABTBUY", "DBLPSCHOLAR", "AMAZONGOOGLEPRODUCTS", "DBPLINKEDMDB"
		// };
		// for (String d : dataSetNames) {
		String d = args[1];
			EvaluationData eval = DataSetChooser.getData(d);
			TaskData td = new TaskData(new GoldStandard(eval.getReferenceMapping(), eval.getSourceCache().getAllUris(),
					eval.getTargetCache().getAllUris()), eval.getSourceCache(), eval.getTargetCache(), eval);
			td.dataName = eval.getName();
			data.add(td);
		// }

		Set<EvaluatorType> measures = new HashSet<>();
		measures.add(EvaluatorType.F_MEASURE);
		measures.add(EvaluatorType.PRECISION);
		measures.add(EvaluatorType.RECALL);

		LogicOperator[] crispOps = { LogicOperator.AND, LogicOperator.OR, LogicOperator.MINUS };
		LogicOperator[] algebraic = { LogicOperator.ALGEBRAICT, LogicOperator.ALGEBRAICTCO,
				LogicOperator.ALGEBRAICDIFF };
		LogicOperator[] lukasiewicz = { LogicOperator.LUKASIEWICZT, LogicOperator.LUKASIEWICZTCO,
				LogicOperator.LUKASIEWICZDIFF };
		// LogicOperator[] lukOps = { LogicOperator.LUKASIEWICZT,
		// LogicOperator.LUKASIEWICZTCO,
		// LogicOperator.LUKASIEWICZDIFF };
		// LogicOperator[] hamacherOps = { LogicOperator.HAMACHERT,
		// LogicOperator.HAMACHERTCO,
		// LogicOperator.HAMACHERDIFF };
		// LogicOperator[] allOps = { LogicOperator.AND, LogicOperator.OR,
		// LogicOperator.MINUS, LogicOperator.ALGEBRAICT,
		// LogicOperator.ALGEBRAICTCO, LogicOperator.ALGEBRAICDIFF,
		// LogicOperator.EINSTEINT,
		// LogicOperator.EINSTEINTCO, LogicOperator.EINSTEINDIFF,
		// LogicOperator.LUKASIEWICZT,
		// LogicOperator.LUKASIEWICZTCO, LogicOperator.LUKASIEWICZDIFF,
		// LogicOperator.HAMACHERT,
		// LogicOperator.HAMACHERTCO, LogicOperator.HAMACHERDIFF };
		TaskAlgorithm crispWombat = createWombatWithOperators(crispOps);
		crispWombat.setName("crispWombat");
		TaskAlgorithm algebraicWombat = createWombatWithOperators(algebraic);
		algebraicWombat.setName("algWom");
		TaskAlgorithm lukasiewiczWombat = createWombatWithOperators(lukasiewicz);
		lukasiewiczWombat.setName("lukasiewiczWombat");
		TaskAlgorithm fptldalg = createFPTLDWithOperators(algebraic);
		fptldalg.setName("algfptld");
		TaskAlgorithm fptldluk = createFPTLDWithOperators(lukasiewicz);
		fptldluk.setName("lukfptld");
		// TaskAlgorithm fptldparaluk = createFPTLDWithOperators(pluk);
		// fptldparaluk.setName("paralukfptld");
		// TaskAlgorithm luk = createWombatWithOperators(plukOps);
		// luk.setName("pluk");
		// TaskAlgorithm fuzzy = createWombatWithOperators(fuzzyOps);
		// fuzzy.setName("fuzzy");
		// TaskAlgorithm hamacher = createWombatWithOperators(hamacherOps);
		// hamacher.setName("hamacher");
		// TaskAlgorithm all = createWombatWithOperators(allOps);
		// all.setName("all");
		// algos.add(crisp);
		// algos.add(fptld);
		// algos.add(fptld2);
		algos.add(crispWombat);
		algos.add(algebraicWombat);
		algos.add(lukasiewiczWombat);
		algos.add(fptldalg);
		algos.add(fptldluk);
		// algos.add(fptldparaluk);
		// algos.add(luk);
		// algos.add(fuzzy);
		// algos.add(hamacher);
		// algos.add(all);
		Summary s = new Evaluator().crossValidateWithTuningAndStatisticalTest(algos, data, measures, 10);
		s.printToFiles(args[0]);
	}

	public static TaskAlgorithm createFPTLDWithOperators(LogicOperator[] operators)
			throws UnsupportedMLImplementationException {
		AMLAlgorithm algo = MLAlgorithmFactory.createMLAlgorithm(FPTLD.class, MLImplementationType.SUPERVISED_BATCH);
		((FPTLD) algo.getMl()).setOperators(operators);
		TaskAlgorithm tAlgo = new TaskAlgorithm(MLImplementationType.SUPERVISED_BATCH, algo, null, null);
		return tAlgo;
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

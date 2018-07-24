package org.aksw.limes.core.evaluation;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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
		// String[] dataSetNames = new String[] { "restaurantsfixed", "person1",
		// "person2", "DBLPACM", "ABTBUY",
		// "DBLPSCHOLAR", "AMAZONGOOGLEPRODUCTS", "DBPLINKEDMDB" };
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
		LogicOperator[] einsteinOps = { LogicOperator.EINSTEINT, LogicOperator.EINSTEINTCO,
				LogicOperator.EINSTEINDIFF };
		LogicOperator[] algebraic = { LogicOperator.ALGEBRAICT, LogicOperator.ALGEBRAICTCO,
				LogicOperator.ALGEBRAICDIFF };
		LogicOperator[] lukasiewicz = { LogicOperator.LUKASIEWICZT, LogicOperator.LUKASIEWICZTCO,
				LogicOperator.LUKASIEWICZDIFF };
		LogicOperator[] yager = { LogicOperator.YAGERT, LogicOperator.YAGERTCO, LogicOperator.YAGERDIFF };
		LogicOperator[] hamacherOps = { LogicOperator.HAMACHERT, LogicOperator.HAMACHERTCO,
				LogicOperator.HAMACHERDIFF };
		LogicOperator[] all = Stream.of(crispOps, einsteinOps, algebraic, lukasiewicz, yager, hamacherOps)
				.flatMap(Stream::of).toArray(LogicOperator[]::new);

		TaskAlgorithm crispWombat = createWombatWithOperators(crispOps);
		crispWombat.setName("crispWom");
		TaskAlgorithm einsteinWombat = createWombatWithOperators(einsteinOps);
		einsteinWombat.setName("einstWom");
		TaskAlgorithm algebraicWombat = createWombatWithOperators(algebraic);
		algebraicWombat.setName("algWom");
		TaskAlgorithm lukasiewiczWombat = createWombatWithOperators(lukasiewicz);
		lukasiewiczWombat.setName("lukWom");
		TaskAlgorithm yagerWombat = createWombatWithOperators(yager);
		yagerWombat.setName("yagerWom");
		TaskAlgorithm hamacherWombat = createWombatWithOperators(hamacherOps);
		hamacherWombat.setName("hamacherWom");
		TaskAlgorithm allWombat = createWombatWithOperators(all);
		allWombat.setName("allWom");

		TaskAlgorithm crispFPTLD = createFPTLDWithOperators(crispOps);
		crispFPTLD.setName("crispfptld");
		TaskAlgorithm einsteinFPTLD = createFPTLDWithOperators(einsteinOps);
		einsteinFPTLD.setName("einstfptld");
		TaskAlgorithm algebraicFPTLD = createFPTLDWithOperators(algebraic);
		algebraicFPTLD.setName("algfptld");
		TaskAlgorithm lukasiewiczFPTLD = createFPTLDWithOperators(lukasiewicz);
		lukasiewiczFPTLD.setName("lukfptld");
		TaskAlgorithm yagerFPTLD = createFPTLDWithOperators(yager);
		yagerFPTLD.setName("yagerfptld");
		TaskAlgorithm hamacherFPTLD = createFPTLDWithOperators(hamacherOps);
		hamacherFPTLD.setName("hamacherfptld");
		TaskAlgorithm allFPTLD = createFPTLDWithOperators(all);
		allFPTLD.setName("allfptld");
		algos.add(crispWombat);
		algos.add(crispFPTLD);
		algos.add(einsteinWombat);
		algos.add(einsteinFPTLD);
		algos.add(algebraicWombat);
		algos.add(algebraicFPTLD);
		algos.add(lukasiewiczWombat);
		algos.add(lukasiewiczFPTLD);
		algos.add(yagerWombat);
		algos.add(yagerFPTLD);
		algos.add(hamacherWombat);
		algos.add(hamacherFPTLD);
		algos.add(allWombat);
		algos.add(allFPTLD);
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

package org.aksw.limes.core.ml.algorithm.NLGLS;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.DataSets;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.WombatSimple;

public class NLGLSRun {


	public static void main(String[] args) throws UnsupportedMLImplementationException {

		EvaluationData eval = DataSetChooser.getData(DataSets.AMAZONGOOGLEPRODUCTS);

		AMLAlgorithm wombat = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
				MLImplementationType.SUPERVISED_BATCH);
		//		//Especially the source and target caches
		wombat.init(null, eval.getSourceCache(), eval.getTargetCache());
		//		//And the training data 
		MLResults learn = wombat.getMl().learn(eval.getReferenceMapping());
		LinkSpecification linkSpecification = learn.getLinkSpecification();
		//	
		//		System.out.println(" low threshold "+linkSpecification.toString());
		//		fullMeasureNLG(linkSpecification);
		SimpleNLGTemplate.introduction(linkSpecification);
		System.out.println("the introduction ");
		System.out.println("-----------------------------");
		System.out.println(SimpleNLGTemplate.realisation);

	} 
}





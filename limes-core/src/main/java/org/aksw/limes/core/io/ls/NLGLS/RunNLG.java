//package org.aksw.limes.core.io.ls.NLGLS;
//
//import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
//import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.DataSets;
//import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
//import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
//import org.aksw.limes.core.io.ls.LinkSpecification;
//import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
//import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
//import org.aksw.limes.core.ml.algorithm.MLImplementationType;
//import org.aksw.limes.core.ml.algorithm.MLResults;
//import org.aksw.limes.core.ml.algorithm.WombatSimple;
//
//public class RunNLG {
//
//	public static void main(String[] args) throws UnsupportedMLImplementationException {
//		String str="OR(OR(OR(jaccard(x.date_of_birth,y.date_of_birth)|1.0,"
//				+ "jaccard(x.date_of_birth,y.has_address)|1.0)|0.6,"
//				+ "AND(jaccard(x.date_of_birth,y.date_of_birth)|1.0,jaccard(x.date_of_birth,y.has_address)|1.0)|0.5)|0.9,"
//				+ "AND(AND(jaccard(x.date_of_birth,y.date_of_birth)|1.0,jaccard(x.date_of_birth,y.has_address)|1.0)|0.6,"
//				+ "OR(jaccard(x.date_of_birth,y.date_of_birth)|1.0,jaccard(x.date_of_birth,y.has_address)|1.0)|0.5)|0.8)";
//		String str1="OR(OR(jaccard(x.http://www.okkam.org/ontology_person1.owl#date_of_birth,y.http://www.okkam.org/ontology_person2.owl#date_of_birth)|1.0,qgrams(x.http://www.okkam.org/ontology_person1.owl#date_of_birth,y.http://www.okkam.org/ontology_person2.owl#has_address)|1.0)|0.0,cosine(x.http://www.okkam.org/ontology_person1.owl#soc_sec_id,y.http://www.okkam.org/ontology_person2.owl#soc_sec_id)|1.0)";
//		LinkSpecification link=new LinkSpecification();
//		link.readSpec(str, 0.6);
//		EvaluationData eval = DataSetChooser.getData(DataSets.PERSON1);
//		AMLAlgorithm wombat = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
//				MLImplementationType.SUPERVISED_BATCH);
//
//		wombat.init(null, eval.getSourceCache(), eval.getTargetCache());
//		//        //And the training data
//		MLResults learn = wombat.getMl().learn(eval.getReferenceMapping());
//		LinkSpecification linkSpec = learn.getLinkSpecification();
//		//LinkSpecSummary.summary(linkSpec, eval.getSourceCache(), eval.getTargetCache(),94.0);
//		//System.out.println(" the description is: "+linkSpec.getFullExpression());
//		int x = 0;
//		String prevNumber = "";	
//		String  str4="2";
//		//LinkSpecSummary.summary(linkSpec, eval.getSourceCache(), eval.getTargetCache(), 0.94, "");
//		//LinkSpecSummary.fullMeasureNLG(link,x,prevNumber);
//		LinkSpecSummary.sumerazation(link, eval.getSourceCache(), eval.getTargetCache(), 100.0,0,"");
//		//System.out.println(" the description " +fullMeasureNLG.toString());
//		// TODO Auto-generated method stub
//
//	}
//
//}

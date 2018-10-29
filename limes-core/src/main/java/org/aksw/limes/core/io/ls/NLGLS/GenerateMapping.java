package org.aksw.limes.core.io.ls.NLGLS;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.WombatSimple;

public class GenerateMapping {


	static AMapping slection(LinkSpecification linkSpec, ACache source, ACache target) throws UnsupportedMLImplementationException {

		AMLAlgorithm wombat = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
				MLImplementationType.SUPERVISED_BATCH);
		//        //Especially the source and target caches
		wombat.init(null,source, target);
		//        //And the training data
		MLResults mlModel=new MLResults();
		mlModel.setLinkSpecification(linkSpec);
		AMapping mapping = wombat.predict(source, target, mlModel);
		return mapping;
	}


//
//	public static void main(String[] args) throws UnsupportedMLImplementationException {
//		String ls1="OR(OR(OR(OR(qgrams(x.name,y.name)|0.4135,jaccard(x.name,y.name)|0.3637)|0.1136,OR(qgrams(x.name,y.name)|0.4135,qgrams(x.name,y.name)|0.4135)|0.1136)|0.1136,qgrams(x.name,y.name)|0.4135)|0.2861,jaccard(x.name,y.name)|0.3637)";
//		String ls2="OR(OR(trigrams(x.name,y.name)|0.6355,OR(trigrams(x.description,y.description)|0.4965,OR(trigrams(x.name,y.name)|0.4965,levenshtein(x.name,y.name)|0.7808)|0.6127)|0.6127)|0.6127,qgrams(x.name,y.name)|0.6127)";
//		String ls3="AND(AND(trigrams(x.title,y.title)|0.3882,trigrams(x.title,y.title)|0.3882)|0.6803,OR(trigrams(x.authors,y.authors)|0.4420,jaccard(x.title,y.title)|0.6803)|0.6803)";
//		String ls4="AND(AND(AND(trigrams(x.title,y.title)|0.6233,AND(AND(overlap(x.title,y.title)|0.5124,cosine(x.authors,y.authors)|0.5254)|0.5124,overlap(x.title,y.title)|0.5124)|0.5124)|0.5124,cosine(x.authors,y.authors)|0.5254)|0.5124,overlap(x.title,y.title)|0.5124)";
//		String ls5="AND(AND(cosine(x.title,y.title)|0.5266,AND(AND(qgrams(x.title,y.title)|0.3679,euclidean(x.year,y.year)|0.8331)|0.5325,euclidean(x.year,y.year)|0.8331)|0.5325)|0.5325,euclidean(x.year,y.year)|0.8331)";
//		EvaluationData eval = DataSetChooser.getData(DataSets.DBLPSCHOLAR);
//		LinkSpecification link=new LinkSpecification();
//
//		link.readSpec(ls5, 0.1);
//		AMapping mapping =slection(link, eval.getSourceCache(),eval.getTargetCache());
//
//		System.out.println("the mapping is "+ mapping);
//	}

}

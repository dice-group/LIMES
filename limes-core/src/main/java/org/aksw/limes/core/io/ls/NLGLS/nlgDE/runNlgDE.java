package org.aksw.limes.core.io.ls.NLGLS.nlgDE;


import java.io.IOException;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.DataSets;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;

public class runNlgDE {

	//protected static Lexicon lexicon = new XMLLexicon(); 
	public static void main(String[] args) throws UnsupportedMLImplementationException, IOException {

		LsPostProcessorDE lsPostProcessor=new LsPostProcessorDE() ;
		LinkSpecification linkSpec=new LinkSpecification();
		EvaluationData eval = DataSetChooser.getData(DataSets.DBLPACM);

		String ls1="OR(OR(OR(OR(qgrams(x.name,y.description)|0.41,jaccard(x.name,y.name)|0.36)|0.11,OR(euclidean(x.price,y.price)|0.41,cosine(x.name,y.name)|0.41)|0.11)|0.11,qgrams(x.description,y.description)|0.41)|0.2861,jaccard(x.name,y.name)|0.36)";
		String ls2="OR(OR(trigrams(x.description,y.description)|0.63,OR(trigrams(x.description,y.description)|0.7,OR(euclidean(x.price,y.price)|0.49,levenshtein(x.name,y.name)|0.78)|0.61)|0.61)|0.61,qgrams(x.name,y.name)|0.61)";
		String ls3="AND(AND(trigrams(x.title,y.title)|0.38,trigrams(x.title,y.title)|0.38)|0.68,OR(trigrams(x.authors,y.authors)|0.44,jaccard(x.title,y.title)|0.68)|0.68)";
		String ls4="AND(AND(AND(trigrams(x.title,y.title)|0.62,AND(AND(overlap(x.title,y.title)|0.51,cosine(x.authors,y.authors)|0.52)|0.51,overlap(x.title,y.title)|0.51)|0.51)|0.51,cosine(x.authors,y.authors)|0.52)|0.51,overlap(x.venue,y.venue)|0.51)";
		String ls5="AND(AND(cosine(x.title,z.title)|0.52,AND(AND(qgrams(x.title,z.title)|0.36,euclidean(x.authors,z.authors)|0.83)|0.53,cosine(x.year,z.year)|0.83)|0.53)|0.53,euclidean(x.year,z.year)|0.83)";
		String ls6="or(qgrams(x.title,y.name)|1,trigrams(x.authors,y.authors)|1)";
		linkSpec.readSpec(ls6, 1);
		//AMapping slection = lsPostProcessor.selection(linkSpec,eval.getSourceCache(),eval.getTargetCache());

		
		lsPostProcessor.postProcessor(linkSpec);//summarization(linkSpec,eval.getSourceCache(),eval.getTargetCache(), slection,0.2);
      
	
	
	
	}



}

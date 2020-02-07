package org.aksw.limes.core.io.ls.NLGLS.nlgDE;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.DataSets;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;

public class runNlgDE {

	//protected static Lexicon lexicon = new XMLLexicon(); 
	public static void main(String[] args) throws UnsupportedMLImplementationException, IOException {
		List<String> allLS=new ArrayList<String>();

		//EvaluationData eval = DataSetChooser.getData(DataSets.DBLPACM);

		String ls1 ="OR(OR(OR(OR(qgrams(x.name,y.description)|0.41,jaccard(x.name,y.name)|0.36)|0.11,OR(euclidean(x.price,y.price)|0.41,cosine(x.name,y.name)|0.41)|0.11)|0.11,qgrams(x.description,y.description)|0.41)|0.2861,jaccard(x.name,y.name)|0.36)";
		String ls1b="OR(OR(OR(OR(qgrams(x.name,y.description)|0.41, jaccard(x.name,y.name)|0.36),OR(euclidean(x.price,y.price)|0.41,cosine(x.name,y.name)|0.41), qgrams(x.description,y.description)|0.41),jaccard(x.name,y.name)|0.36)";

		String ls2   ="OR(OR(trigrams(x.description,y.description)|0.63,OR(trigrams(x.description,y.description)|0.7,OR(euclidean(x.price,y.price)|0.49,levenshtein(x.name,y.name)|0.78)|0.61)|0.61)|0.61,qgrams(x.name,y.name)|0.61)";
		String ls2_b ="OR(OR(trigrams(x.description,y.description)|0.63,OR(trigrams(x.description,y.description)|0.49,OR(euclidean(x.price,y.price)|0.49, levenshtein(x.name,y.name)0.78 ))),qgrams(x.name,y.name)|0.61)";

		String ls3   ="AND(AND(trigrams(x.title,y.title)|0.38,jaccard(x.title,y.title)|0.38)|0.68,OR(trigrams(x.authors,y.authors)|0.44,jaccard(x.title,y.title)|0.68)|0.68)";
		String ls3_b ="AND(AND(trigrams(x.title,y.title)|0.38,jaccard(x.title,y.title)|0.48),OR(trigrams(x.authors,y.authors)|0.44,jaccard(x.title,y.title)|0.68))";

		String ls4   ="AND(AND(AND(trigrams(x.title,y.title)|0.62,AND(AND(overlap(x.title,y.title)|0.51,cosine(x.authors,y.authors)|0.52)|0.51,overlap(x.title,y.title)|0.51)|0.51)|0.51,cosine(x.authors,y.authors)|0.52)|0.51,overlap(x.venue,y.venue)|0.51)";
		String ls4_b ="AND(AND(AND(trigrams(x.title,y.title)|0.62,AND(AND(overlap(x.title,y.title)|0.51,cosine(x.authors,y.authors)|0.52),overlap(x.title,y.title)|0.51)),cosine(x.authors,y.authors)|0.52),overlap(x.venue,y.venue)|0.51)";

		String ls5   ="AND(AND(cosine(x.title,y.title)|0.52,AND(AND(qgrams(x.title,y.title)|0.36,jaccard(x.authors,y.authors)|0.83)|0.53,cosine(x.year,y.year)|0.83)|0.53)|0.53,euclidean(x.year,y.year)|0.83)";
		String ls5_b ="AND(AND(jaccard(x.title,y.title)|0.52,AND(AND(qgrams(x.title,y.title)|0.36,jaccard(x.authors,y.authors)|0.83),cosine(x.year,y.year)|0.83) ),euclidean(x.year,y.year)|0.83)" ;

		String ls6="or(qgrams(x.name,y.title)|1,qgrams(x.name,y.name)|1)";
		String ls7="qgrams(x.title,y.name)";
		String ls8="OR(jaccard(x.name,y.name)|1,trigrams(x.name,y.description)|0.61)";

		String scholar= "OR(OR(cosine(x.title,y.title)|0.66,jaccard(x.title,y.authors)|0.43)|0.0,trigram(x.authors,y.title)|0.43)";
		String amazon="OR(OR(cosine(x.title,y.name)|0.48,cosine(x.description,y.description)|0.43)|0.0,jaccard(x.title,y.description)|0.43)";
		//allLS.add(ls1);
		//allLS.add(ls2);
		//allLS.add(ls3);
		//allLS.add(ls4);
		allLS.add(amazon);
		int counter=1;
		for(String str: allLS) {
			LsPostProcessorDE lsPostProcessorDe=new LsPostProcessorDE() ;
			LinkSpecification linkSpec=new LinkSpecification();
			linkSpec.readSpec(str, 1.0);
			//AMapping slection = lsPostProcessorDe.selection(linkSpec,eval.getSourceCache(),eval.getTargetCache());

			//lsPostProcessorDe.summarization(linkSpec,eval.getSourceCache(),eval.getTargetCache(), slection,0.2);

			lsPostProcessorDe.postProcessor(linkSpec);//summarization(linkSpec,eval.getSourceCache(),eval.getTargetCache(), slection,0.2);
			System.out.println("----------------------------------LINK SPECIFICATION " + counter++);
			System.out.println(str);
			System.out.println("----------------------------------LINK SPECIFICATION VERBALIZATION START------------------\n");
			lsPostProcessorDe.realisng();

			System.out.println("----------------------------------LINK SPECIFICATION VERBALIZATION END------------------\n");
		}
	}



}

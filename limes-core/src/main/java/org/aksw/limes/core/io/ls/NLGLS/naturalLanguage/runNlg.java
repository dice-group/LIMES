package org.aksw.limes.core.io.ls.NLGLS.naturalLanguage;


import java.io.IOException;
import java.util.List;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.ls.LinkSpecification;

import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class runNlg {
	
	
	 static void postProcessor(String str) throws UnsupportedMLImplementationException {

		LinkSpecification link=new LinkSpecification();
		Lexicon lexicon = new XMLLexicon();                      
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		SPhraseSpec clause=nlgFactory.createClause();
		SPhraseSpec clause1=nlgFactory.createClause();
		link.readSpec(str, 0.6);
		//System.out.println(ls5);

		List<NLGElement> allNLGElement = LinkSpecSummery.fullMeasureNLG(link);
		clause1.setObject("the" +" link");
		clause1.setVerb("generate");
		clause1.setFeature(Feature.TENSE,Tense.FUTURE);
		clause1.setFeature(Feature.PASSIVE, true);
		Realiser clause1Realiser = new Realiser(lexicon);
		NLGElement clause1Realised = clause1Realiser.realise(clause1);
		
		System.out.println(clause1Realised);
		
		clause.setSubject(LinkSpecSummery.previousSubject);
		clause.setVerb("have");
		clause.setObject(LinkSpecSummery.objCollection);

		//the clause
		Realiser clauseRealiser = new Realiser(lexicon);
		NLGElement clauseRealised = clauseRealiser.realise(clause);

		//System.out.println("aggregation "+clauseRealised);
		allNLGElement.add(clauseRealised);
		for(NLGElement ele:allNLGElement) {
			// Realiser finalRealiser = new Realiser(lexicon);
			// NLGElement finalRealised = finalRealiser.realise(ele);
			System.out.println(ele);
		}

	}


	public static void main(String[] args) throws UnsupportedMLImplementationException, IOException {


		String ls1="OR(OR(OR(AND(qgrams(x.author,y.author)|0.4135,jaccard(x.name,y.name)|0.3637)|0.1136,OR(trigrams(x.name,y.name)|0.4135,cosine(x.name,y.name)|0.4135)|0.1136)|0.1136,qgrams(x.name,y.name)|0.4135)|0.2861,jaccard(x.name,y.name)|0.3637)";
		String ls2="AND(OR(trigrams(x.name,y.name)|0.6355,OR(trigrams(x.description,y.description)|0.4965,OR(trigrams(x.name,y.name)|0.4965,levenshtein(x.name,y.name)|0.4965)|0.6127)|0.6127)|0.6127,qgrams(x.name,y.name)|0.4965)";
		String ls3="AND(AND(trigrams(x.title,y.title)|0.3882,trigrams(x.title,y.title)|0.3882)|0.6803,OR(trigrams(x.authors,y.authors)|0.4420,jaccard(x.title,y.title)|0.6803)|0.6803)";
		String ls4="AND(AND(AND(trigrams(x.title,y.title)|0.6233,AND(AND(overlap(x.title,y.title)|0.5124,cosine(x.authors,y.authors)|0.5254)|0.5124,overlap(x.title,y.title)|0.5124)|0.5124)|0.5124,cosine(x.authors,y.authors)|0.5254)|0.5124,overlap(x.title,y.title)|0.5124)";
		String ls5="AND(AND(cosine(x.title,z.title)|0.5266,AND(AND(qgrams(x.title,z.title)|0.3679,euclidean(x.year,z.year)|0.8331)|0.5325,euclidean(x.year,z.year)|0.8331)|0.5325)|0.5325,euclidean(x.year,z.year)|0.8331)";

		
		String test= "OR(OR(OR(jaccard(x.date_of_birth,y.date_of_birth)|1.0,"
				+ "jaccard(x.date_of_birth,y.has_address)|0.0)|0.6,"
				+ "AND(jaccard(x.date_of_birth,y.date_of_birth)|1.0,jaccard(x.date_of_birth,y.has_address)|0.9)|0.5)|0.9,"
				+ "AND(AND(jaccard(x.date_of_birth,y.date_of_birth)|1.0,jaccard(x.date_of_birth,y.has_address)|1.0)|0.6,"
				+ "OR(jaccard(x.date_of_birth,y.date_of_birth)|1.0,jaccard(x.date_of_birth,y.has_address)|1.0)|0.5)|0.8)";

		
		postProcessor( ls1);


	}

	//}

}

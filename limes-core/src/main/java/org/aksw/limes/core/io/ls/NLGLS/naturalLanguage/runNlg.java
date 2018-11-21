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

		String ls1="OR(OR(OR(OR(qgrams(x.name,y.description)|0.41,jaccard(x.name,y.name)|0.36)|0.11,OR(euclidean(x.price,y.price)|0.41,cosine(x.name,y.name)|0.41)|0.11)|0.11,qgrams(x.description,y.description)|0.41)|0.2861,jaccard(x.name,y.name)|0.36)";
		String ls2="OR(OR(trigrams(x.description,y.description)|0.63,OR(trigrams(x.description,y.description)|0.49,OR(trigrams(x.price,y.price)|0.49,levenshtein(x.name,y.name)|0.78)|0.61)|0.61)|0.61,qgrams(x.name,y.name)|0.61)";
		String ls3="AND(AND(trigrams(x.title,y.title)|0.38,trigrams(x.title,y.title)|0.38)|0.68,OR(trigrams(x.authors,y.authors)|0.44,jaccard(x.title,y.title)|0.68)|0.68)";
		String ls4="AND(AND(AND(trigrams(x.title,y.title)|0.62,AND(AND(overlap(x.title,y.title)|0.51,cosine(x.authors,y.authors)|0.52)|0.51,overlap(x.title,y.title)|0.51)|0.51)|0.51,cosine(x.authors,y.authors)|0.52)|0.51,overlap(x.venue,y.venue)|0.51)";
		String ls5="AND(AND(cosine(x.title,z.title)|0.52,AND(AND(qgrams(x.title,z.title)|0.36,euclidean(x.authors,z.authors)|0.83)|0.53,cosine(x.year,z.year)|0.83)|0.53)|0.53,euclidean(x.year,z.year)|0.83)";
		

		String test= "OR(OR(OR(jaccard(x.date_of_birth,y.date_of_birth)|1.0,"
				+ "jaccard(x.date_of_birth,y.has_address)|0.0)|0.6,"
				+ "AND(jaccard(x.date_of_birth,y.date_of_birth)|1.0,jaccard(x.date_of_birth,y.has_address)|0.9)|0.5)|0.9,"
				+ "AND(AND(jaccard(x.date_of_birth,y.date_of_birth)|1.0,jaccard(x.date_of_birth,y.has_address)|1.0)|0.6,"
				+ "OR(jaccard(x.date_of_birth,y.date_of_birth)|1.0,jaccard(x.date_of_birth,y.has_address)|1.0)|0.5)|0.8)";

		System.out.println("ABT-BUY_LS1: "+ls1);
		postProcessor( ls1);
		
		//System.out.println("*******************************************");
		//System.out.println("*******************************************");
		//System.out.println("ABT-BUY_LS2: "+ls2);
		//postProcessor( ls2);
		
		//System.out.println("*******************************************");
		//System.out.println("*******************************************");
	//	System.out.println("ACM_DBLP_LS1: "+ls3);
		//postProcessor( ls3);
		
	//	System.out.println("*******************************************");
	//	System.out.println("*******************************************");
		//System.out.println("SCHOLAR_DBLP_LS1: "+ls5);
		//postProcessor( ls5);
		
		System.out.println("*******************************************");
		System.out.println("*******************************************");

	}

	//}

}

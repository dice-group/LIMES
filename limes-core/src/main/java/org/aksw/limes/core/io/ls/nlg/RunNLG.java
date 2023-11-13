package org.aksw.limes.core.io.ls.nlg;

import java.io.IOException;
import java.util.ArrayList;
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

public class RunNLG {



	public static void main(String[] args) throws UnsupportedMLImplementationException, IOException {

		List<NLGElement> result = new ArrayList<>();
		List<String> finalResult = new ArrayList<>();

		Lexicon lexicon = new XMLLexicon();                       
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		Realiser realiser_4 = new Realiser(lexicon);
		SPhraseSpec    clause_1=nlgFactory.createClause();
		clause_1.setObject(WORDS.THE +" tow resources");
		clause_1.setVerb("link");
		clause_1.setFeature(Feature.TENSE,Tense.FUTURE);
		clause_1.setFeature(Feature.PASSIVE, true);


		String ls1="OR(OR(OR(OR(qgrams(x.name,y.name)|0.4135,jaccard(x.name,y.name)|0.3637)|0.1136,OR(trigrams(x.name,y.name)|0.4135,cosine(x.name,y.name)|0.4135)|0.1136)|0.1136,qgrams(x.name,y.name)|0.4135)|0.2861,jaccard(x.name,y.name)|0.3637)";
		String ls2="AND(OR(trigrams(x.name,y.name)|0.6355,OR(trigrams(x.description,y.description)|0.4965,OR(trigrams(x.name,y.name)|0.4965,levenshtein(x.name,y.name)|0.4965)|0.6127)|0.6127)|0.6127,qgrams(x.name,y.name)|0.4965)";
		String ls3="AND(AND(trigrams(x.title,y.title)|0.3882,trigrams(x.title,y.title)|0.3882)|0.6803,OR(trigrams(x.authors,y.authors)|0.4420,jaccard(x.title,y.title)|0.6803)|0.6803)";
		String ls4="AND(AND(AND(trigrams(x.title,y.title)|0.6233,AND(AND(overlap(x.title,y.title)|0.5124,cosine(x.authors,y.authors)|0.5254)|0.5124,overlap(x.title,y.title)|0.5124)|0.5124)|0.5124,cosine(x.authors,y.authors)|0.5254)|0.5124,overlap(x.title,y.title)|0.5124)";
		String ls5="AND(AND(cosine(x.title,z.title)|0.5266,AND(AND(qgrams(x.title,z.title)|0.3679,euclidean(x.year,z.year)|0.8331)|0.5325,euclidean(x.year,z.year)|0.8331)|0.5325)|0.5325,euclidean(x.year,z.year)|0.8331)";
		
		
		String test= "OR(OR(OR(jaccard(x.date_of_birth,y.date_of_birth)|1.0,"
	            + "jaccard(x.date_of_birth,y.has_address)|0.0)|0.6,"
	            + "AND(jaccard(x.date_of_birth,y.date_of_birth)|1.0,jaccard(x.date_of_birth,y.has_address)|0.9)|0.5)|0.9,"
	            + "AND(AND(jaccard(x.date_of_birth,y.date_of_birth)|1.0,jaccard(x.date_of_birth,y.has_address)|1.0)|0.6,"
	            + "OR(jaccard(x.date_of_birth,y.date_of_birth)|1.0,jaccard(x.date_of_birth,y.has_address)|1.0)|0.5)|0.8)";
		
		LinkSpecification link=new LinkSpecification();

		link.readSpec(test, 0.6);
		//System.out.println(ls5);
		List<SPhraseSpec> allNLGElement=LinkSpecSummary.fullMeasureNLG(link);
		for(SPhraseSpec ele:allNLGElement) {
		
		Realiser finalRealiser = new Realiser(lexicon);
		NLGElement finalRealised = finalRealiser.realise(ele);
		System.out.println(finalRealised.toString());}

		String previous = "";
		//String previous1 = "";
		/*for (NLGElement el: allNLGElement) {
			//System.out.println(el);
			//if (!previous.equals(el.toString()) ) {               
				//previous = el.toString();
				result.add(el);               
			//}               
		}*/
		String temp="";
		String prefix ="";
		for (int i=0;i<result.size();i++) {
			Realiser realiser_1 = new Realiser(lexicon);
			NLGElement realised = realiser_1.realise(result.get(i));
			String el = realised.getRealisation();
			if (el.length()==2||el.length()==3) {
				temp = el;
			}else {
				//treatment for the last element
				if (i == result.size()-1) {
					//System.out.println("the last operator");
					finalResult.add(temp);                                       
				}
				//treatment for the first element
				if (i == 0) {
					if (el.startsWith("or") || el.startsWith("and") || el.startsWith("not")|| el.startsWith("xor")){
						if (el.startsWith("or")) {
							prefix = "or";
						}else if (el.startsWith("and")) {
							prefix = "and";
						}else if (el.startsWith("not")) {
							prefix = "not";
						}
						else if (el.startsWith("xor")) {
							prefix = "xor";
						}
						//System.out.println(prefix);
						el = el.substring(el.lastIndexOf(prefix)+3);
					}
				}

				finalResult.add(el);
			}               
		}
		//finalResult=new ArrayList<String>();
		for(String strEle:finalResult) {



			//System.out.println(strEle);

		}
		//System.out.println("************************************************");
		//System.out.println("************************************************");
		//}




	}

}

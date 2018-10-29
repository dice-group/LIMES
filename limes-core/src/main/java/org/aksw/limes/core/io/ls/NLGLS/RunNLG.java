package org.aksw.limes.core.io.ls.NLGLS;

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


		String ls1="OR(OR(OR(OR(qgrams(x.name,y.name)|0.4135,jaccard(x.name,y.name)|0.3637)|0.1136,OR(qgrams(x.name,y.name)|0.4135,qgrams(x.name,y.name)|0.4135)|0.1136)|0.1136,qgrams(x.name,y.name)|0.4135)|0.2861,jaccard(x.name,y.name)|0.3637)";
		String ls2="OR(OR(trigrams(x.name,y.name)|0.6355,OR(trigrams(x.description,y.description)|0.4965,OR(trigrams(x.name,y.name)|0.4965,levenshtein(x.name,y.name)|0.7808)|0.6127)|0.6127)|0.6127,qgrams(x.name,y.name)|0.6127)";
		String ls3="XOR(XOR(XOR(XOR(XOR(levenshtein(x.title,y.name)|0.6527,levenshtein(x.title,y.name)|0.3701)|0.3668,cosine(x.title,y.name)|0.6835)|0.3668,levenshtein(x.title,y.name)|0.3701)|0.3668,levenshtein(x.title,y.name)|0.3701)|0.3668,trigrams(x.title,y.name)|0.6527)";
		String ls4="OR(OR(OR(jaccard(x.title,y.name)|0.8520,overlap(x.title,y.name)|0.8444)|0.8444,cosine(x.description,y.description)|0.8444)|0.7744,jaccard(x.title,y.name)|0.6032)";
		String ls5="XOR(XOR(jaccard(x.description,y.description)|0.5122,cosine(x.title,y.name)|0.4551)|0.5122,XOR(cosine(x.title,y.name)|0.4776,XOR(jaccard(x.description,y.description)|0.5122,cosine(x.title,y.name)|0.5348)|0.5122)|0.2181)";
		String ls6="AND(AND(trigrams(x.title,y.title)|0.3882,trigrams(x.title,y.title)|0.3882)|0.6803,OR(trigrams(x.authors,y.authors)|0.4420,jaccard(x.title,y.title)|0.6803)|0.6803)";
		String ls7="AND(AND(AND(trigrams(x.title,y.title)|0.6233,AND(AND(overlap(x.title,y.title)|0.5124,cosine(x.authors,y.authors)|0.5254)|0.5124,overlap(x.title,y.title)|0.5124)|0.5124)|0.5124,cosine(x.authors,y.authors)|0.5254)|0.5124,overlap(x.title,y.title)|0.5124)";
		String ls8="AND(jaccard(x.title,y.title)|0.4521,AND(qgrams(x.authors,y.authors)|0.3352,AND(AND(cosine(x.venue,y.venue)|0.1580,cosine(x.venue,y.venue)|0.1580)|0.1580,cosine(x.venue,y.venue)|0.1580)|0.1580)|0.1580)";
		String ls9="AND(AND(cosine(x.title,y.title)|0.5266,AND(AND(qgrams(x.title,y.title)|0.3679,euclidean(x.year,y.year)|0.8331)|0.5325,euclidean(x.year,y.year)|0.8331)|0.5325)|0.5325,euclidean(x.year,y.year)|0.8331)";
		LinkSpecification link=new LinkSpecification();

		link.readSpec(ls9, 0.6);
		System.out.println(ls9);
		List<NLGElement>allNLGElement=LinkSpecSummary.fullMeasureNLG(link);


		String previous = "";
		//String previous1 = "";
		for (NLGElement el: allNLGElement) {
			if (!previous.equals(el.toString()) ) {               
				previous = el.toString();
				result.add(el);               
			}               
		}
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



			System.out.println(strEle);

		}
		System.out.println("************************************************");
		System.out.println("************************************************");
		//}




	}

}

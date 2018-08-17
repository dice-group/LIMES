package org.aksw.limes.core.ml.algorithm.NLGLS;
import java.util.Locale;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.ls.LinkSpecification;

import com.ibm.icu.text.RuleBasedNumberFormat;

import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.StringElement;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class SimpleNLGTemplate {
	
	public static String realisation;
	public static String realisation1;
	public static String realisation2;
	public static String realisation3;
	public static String realisation4;

	public String getIntroductionNLG() {
		return realisation;	
	}
	
	public String getFullMeasureNLG() {
		return realisation1+ realisation2+realisation3;}
	
	public String getAtomicMeasureNLG() {
		return realisation4;
	}
	public static void introduction(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {

		Lexicon lexicon = new XMLLexicon();                         
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		// the first sentence 
		DocumentElement sentence0 = nlgFactory.createSentence();
		StringElement theLinkDiscovey = new StringElement(WORDS.THE+" "+WORDS.LINKDISCOVERY);
		PPPhraseSpec over = nlgFactory.createPrepositionPhrase(WORDS.OVER);
		StringElement theKnowledgeBase = new StringElement(WORDS.KNOWLEDGEBASES);	

		sentence0.addComponent(theLinkDiscovey);
		sentence0.addComponent(over);
		sentence0.addComponent(theKnowledgeBase);
		VPPhraseSpec v = nlgFactory.createVerbPhrase(WORDS.GET);
		v.setFeature(Feature.TENSE, Tense.PRESENT);
		v.setFeature(Feature.PROGRESSIVE, true);
		sentence0.addComponent(v);
		WordElement popular = new WordElement(WORDS.POPULAR);
		sentence0.addComponent(popular);

		SPhraseSpec s0 = nlgFactory.createClause();

		SPhraseSpec s1 = nlgFactory.createClause();
		s1.setSubject(WORDS.IT);
		s1.setVerb(WORDS.PLAY);
		s1.setFeature(Feature.TENSE, Tense.PRESENT);
		s1.addComplement(WORDS.ACENTRAL);
		s1.addComplement(WORDS.ROLE);
		s1.addComplement(WORDS.INLINKEDDATA);

		SPhraseSpec s2 = nlgFactory.createClause();
		s2.setFeature(Feature.CUE_PHRASE, WORDS.THEREFORE);
		NPPhraseSpec thisYoungField = nlgFactory.createNounPhrase(WORDS.THIS, WORDS.YOUNGFIELD);
		s2.setObject(thisYoungField);
		s2.setVerb(WORDS.ATTRACT);
		s2.setFeature(Feature.PASSIVE,true);
		s2.setFeature(Feature.PERFECT, true);
		s2.setSubject(WORDS.MANYRESEARCHERS);


		CoordinatedPhraseElement c0 = nlgFactory.createCoordinatedPhrase();
		c0.addCoordinate(s0);
		c0.addCoordinate(s1);
		c0.addCoordinate(s2);
		sentence0.addComponent(c0);
		// the end of the first sentence 

		// the second sentence
		DocumentElement sentence1 = nlgFactory.createSentence();
		SPhraseSpec s4 = nlgFactory.createClause();
		s4.addFrontModifier(WORDS.MANY);

		NPPhraseSpec algorithm = nlgFactory.createNounPhrase(WORDS.ALGORITHM);
		algorithm.setPlural(true);
		s4.setObject(algorithm);
		s4.setVerb(WORDS.PROPOSE);
		s4.setFeature(Feature.PASSIVE,true);
		s4.setFeature(Feature.PERFECT, true);

		SPhraseSpec s5 = nlgFactory.createClause();
		s5.addComplement(WORDS.TO);
		s5.addComplement(WORDS.IMPROVE);
		s5.addComplement(WORDS.THE +" "+ WORDS.PERFORMANCE);
		s5.addComplement(WORDS.OF);
		s5.addComplement(WORDS.LINKDISCOVERYFRAMEWORKS);
		s5.addComplement(WORDS.BYMEANSOF);
		CoordinatedPhraseElement c1 = nlgFactory.createCoordinatedPhrase();
		c1.addCoordinate(s4);
		c1.addComplement(s5);
		sentence1.addComponent(c1);
		// the end of the second sentence 

		// the third sentence
		DocumentElement sentence2 = nlgFactory.createSentence();
		SPhraseSpec s6 = nlgFactory.createClause();
		s6.addFrontModifier(WORDS.RECENTLY);
		NPPhraseSpec researcher = nlgFactory.createNounPhrase(WORDS.THE, WORDS.RESEARCHER);
		researcher.setPlural(true);
		s6.setSubject(researcher);
		s6.setVerb(WORDS.PAY);
		s6.setFeature(Feature.TENSE,Tense.PRESENT);
		s6.setFeature(Feature.PERFECT, true);
		s6.setFeature(Feature.PROGRESSIVE, true);

		NPPhraseSpec attention = nlgFactory.createNounPhrase(WORDS.ATTENTION);
		attention.addPreModifier(WORDS.MORE);
		s6.addComplement(attention);
		NPPhraseSpec linkSpecification = nlgFactory.createNounPhrase(WORDS.THE,WORDS.LINKSPECIFICATION);
		PPPhraseSpec ofLinkSpecification = nlgFactory.createPrepositionPhrase(WORDS.TO);
		ofLinkSpecification.setObject(linkSpecification);
		s6.addComplement(ofLinkSpecification);
		SPhraseSpec s7 = nlgFactory.createClause();
		s7.addComplement(WORDS.TO);
		s7.addComplement(WORDS.ACHIEVE);
		SPhraseSpec s8 = nlgFactory.createClause();
		NPPhraseSpec accuracy = nlgFactory.createNounPhrase(WORDS.ACCURACY);
		accuracy.addPreModifier(WORDS.HIGH);

		NPPhraseSpec runTime = nlgFactory.createNounPhrase(WORDS.RUNTIME);
		runTime.addPreModifier(WORDS.LESS);
		s8.addComplement(accuracy);
		s8.addComplement(runTime);
		CoordinatedPhraseElement c2 = nlgFactory.createCoordinatedPhrase();
		c2.addCoordinate(s6);
		c2.addComplement(s7);
		c2.addComplement(s8);
		sentence2.addComponent(c2);
		// the end of the third sentence 

		//the the fourth sentence
		DocumentElement sentence3 = nlgFactory.createSentence();
		SPhraseSpec s9 = nlgFactory.createClause();
		s9.addComplement(WORDS.INDICE);
		SPhraseSpec s10 = nlgFactory.createClause();
		s10.setSubject(WORDS.WE);
		s10.setVerb(WORDS.USE);
		NPPhraseSpec machineLearning = nlgFactory.createNounPhrase(WORDS.ALGORITHM);
		machineLearning.setPlural(true);
		machineLearning.addPreModifier(WORDS.MACHINELEARNING);
		s10.setObject(machineLearning);
		s10.setFeature(Feature.CUE_PHRASE,WORDS.FOREXAMPLE);
		SPhraseSpec s11 = nlgFactory.createClause();
		s11.addComplement(WORDS.SUCHAS);
		s11.addComplement("WOMBAT, EAGLE");
		sentence3.addComponent(s9);
		sentence3.addComponent(s10);
		sentence3.addComponent(s11);
		//the end of fourth sentence

		//the fifth sentence
		DocumentElement sentence4 = nlgFactory.createSentence();
		SPhraseSpec s12 = nlgFactory.createClause();
		s12.setObject(WORDS.WOMBATALGORITHM);
		s12.setVerb(WORDS.USE);
		s12.setFeature(Feature.TENSE, Tense.PRESENT);
		s12.setFeature(Feature.PASSIVE, true);
		SPhraseSpec s13 = nlgFactory.createClause();
		s13.addComplement(WORDS.TO);
		s13.addComplement(WORDS.OPTIMIZE);
		SPhraseSpec s14 = nlgFactory.createClause();
		NPPhraseSpec linkSpecifications = nlgFactory.createNounPhrase(WORDS.THE,WORDS.LINKSPECIFICATION);
		s14.addComplement(linkSpecifications);
		SPhraseSpec s15 = nlgFactory.createClause();
		s15.setFeature(Feature.COMPLEMENTISER, WORDS.WHICH);
		s15.setObject(WORDS.IT);
		s15.setVerb(WORDS.DETERMINE);
		s15.setFeature(Feature.TENSE, Tense.PRESENT);
		s15.setFeature(Feature.PASSIVE, true);
		s14.addComplement(s15);
		NPPhraseSpec similarityMeasure = nlgFactory.createNounPhrase(WORDS.THE,WORDS.SIMILARITYMEASURE);
		s15.setSubject(similarityMeasure);
		sentence4.addComponent(s12);
		sentence4.addComponent(s13);
		sentence4.addComponent(s14);
		// the end of the fifth sentence

		// the sixth sentence
		DocumentElement sentence5 = nlgFactory.createSentence();
		SPhraseSpec s16 = nlgFactory.createClause();
		s16.setSubject(WORDS.WE);
		s16.setVerb(WORDS.USE);
		s16.setObject(WORDS.SIMILARITYMEASURE);
		s16.addComplement(WORDS.SUCHAS);
		s16.addComplement(WORDS.MEASURES);
		s16.addComplement(WORDS.TO);
		s16.addComplement(WORDS.COMPUTE);
		s16.addComplement(WORDS.THE+" "+WORDS.SIMILARTY);
		s16.addComplement(WORDS.BETWEEN);
		s16.addComplement(WORDS.PROPERTYOFSOURCE);
		s16.addComplement(WORDS.AND);
		s16.addComplement(WORDS.PROPERTYOFTARGET);
		sentence5.addComponent(s16);
		// the end of the sixth sentence

		// the seventh sentence 
		DocumentElement sentence6 = nlgFactory.createSentence();
		SPhraseSpec s17 = nlgFactory.createClause();
		s17.setSubject(WORDS.WE);
		s17.setFeature(Feature.CUE_PHRASE, WORDS.THEREFORE);
		s17.setVerb(WORDS.NEED);
		s17.addComplement(WORDS.TO);
		s17.addComplement(WORDS.SET);
		s17.addComplement(WORDS.THRESHOLDVALUE);
		s17.addComplement(WORDS.TO);
		s17.addComplement(WORDS.ELIMINATE);
		s17.addComplement(WORDS.RESOURCES);
		s17.addComplement(WORDS.SIMILARITYVALUE);
		s17.addComplement(WORDS.UNDER);
		s17.addComplement(WORDS.THRESHOLD);
		sentence6.addComponent(s17);
		// the end of the seventh sentence

		//the eighth sentence
		DocumentElement sentence7 = nlgFactory.createSentence();
		SPhraseSpec s18 = nlgFactory.createClause();
		s18.setFeature(Feature.CUE_PHRASE, WORDS.ONCE);
		s18.setSubject(WORDS.THE+" "+WORDS.SIMILARITYMEASURE);
		s18.setVerb(WORDS.IS);
		s18.addComplement(WORDS.ABOVE);
		s18.addComplement(WORDS.THRESHOLD);
		SPhraseSpec s19 = nlgFactory.createClause();
		s19.setObject(WORDS.LINK);
		s19.setVerb(WORDS.GENERATE);
		s19.setFeature(Feature.TENSE, Tense.FUTURE);
		s19.setFeature(Feature.PASSIVE, true);
		sentence7.addComponent(s18);
		sentence7.addComponent(s19);
		//the end of the eighth sentence

		//the ninth sentence
		DocumentElement sentence8 = nlgFactory.createSentence();
		SPhraseSpec s20 = nlgFactory.createClause();
		s20.setFeature(Feature.CUE_PHRASE, WORDS.AFTERTHAT);
		s20.setSubject(WORDS.THE+" "+WORDS.MACHINELEARNING+" "+WORDS.ALGORITHM+" (e.g. WOMBAT Simple)");
		s20.setVerb(WORDS.OPTIMIZE);
		s20.setFeature(Feature.TENSE, Tense.PRESENT);
		s20.setObject(WORDS.THE+" "+WORDS.LINKSPECIFICATION);
		s20.addComplement(WORDS.THAT);
		s20.addComplement(WORDS.RETURN);
		s20.addComplement(WORDS.BEST);
		s20.addComplement(WORDS.FMEASURE);
		sentence8.addComponent(s20);
		// the end of the ninth sentence

		// the tenth sentence
		DocumentElement sentence9 = nlgFactory.createSentence();
		SPhraseSpec s21 = nlgFactory.createClause();
		s21.setFeature(Feature.CUE_PHRASE, WORDS.PARAGRAPH);
		s21.setSubject(WORDS.WE);
		s21.setVerb(WORDS.EXPLAIN);
		s21.setObject(WORDS.PROCESS);
		s21.addComplement(WORDS.DETAILS);
		sentence9.addComponent(s21);
		DocumentElement paragraph = nlgFactory.createParagraph();

		paragraph.addComponent(sentence0);
		paragraph.addComponent(sentence1);
		paragraph.addComponent(sentence2);
		paragraph.addComponent(sentence3);
		paragraph.addComponent(sentence4);
		paragraph.addComponent(sentence5);
		paragraph.addComponent(sentence6);
		paragraph.addComponent(sentence7);
		paragraph.addComponent(sentence8);
		paragraph.addComponent(sentence9);
		Realiser realiser = new Realiser(lexicon);
		//realiser.setDebugMode(true);

		NLGElement realised = realiser.realise(paragraph);
		//realiser.setCommaSepCuephrase(true);
		realisation = realised.getRealisation();
		//System.out.println(realisation);
		fullMeasureNLG(linkSpec);
	}

	private static void fullMeasureNLG(LinkSpecification linkSpecification) throws UnsupportedMLImplementationException {

		Lexicon lexicon = new XMLLexicon();                         
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		// create nouns
		NPPhraseSpec theLink = nlgFactory.createNounPhrase(WORDS.THE, WORDS.LINKSPECIFICATION);
		NPPhraseSpec complexSpec = nlgFactory.createNounPhrase(WORDS.COMPLEXSPECIFICATION);
		// create preposition
		PPPhraseSpec onTheLink = nlgFactory.createPrepositionPhrase(WORDS.ON); 
		//set the nouns to objects
		onTheLink.setObject(theLink);

		// create clauses
		SPhraseSpec s0 = nlgFactory.createClause(WORDS.THE +" "+ WORDS.MAPPING,WORDS.BASE, onTheLink);
		s0.setFeature(Feature.TENSE,Tense.PAST);
		s0.addPreModifier(WORDS.IS);

		SPhraseSpec s1 = nlgFactory.createClause();
		s1.setObject(WORDS.WHERE+" "+WORDS.THE +" "+ WORDS.LINKSPECIFICATION);
		s1.setSubject(complexSpec);
		s1.setVerb(WORDS.PRODUCE);
		s1.setFeature(Feature.PASSIVE,true);
		s1.setFeature(Feature.PERFECT, true);

		SPhraseSpec s2 = nlgFactory.createClause();
		s2.setSubject(WORDS.IT);
		s1.setSubject(complexSpec);
		s2.setVerb(WORDS.CONSIST + " "+ WORDS.OF);

		RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.UK, RuleBasedNumberFormat.SPELLOUT);
		s2.setObject(nf.format(linkSpecification.getChildren().size(), 
				WORDS.SPELLOUT_NUMBERING)+" "+ WORDS.CHILDREN+ " "+ WORDS.AND+
				" "+nf.format(linkSpecification.getAllLeaves().size(),
						WORDS.SPELLOUT_NUMBERING) +" "+WORDS.LEAVES );

		CoordinatedPhraseElement c = nlgFactory.createCoordinatedPhrase();

		c.addCoordinate(s0);
		c.addCoordinate(s1);
		c.addCoordinate(s2);

		DocumentElement sentence = nlgFactory.createSentence(c);
		//DocumentElement sentence3 = nlgFactory.createSentence();

		SPhraseSpec s3 = nlgFactory.createClause();
		s3.setFeature(Feature.CUE_PHRASE, WORDS.ALSO);
		s3.setSubject(WORDS.THE+ " "+ WORDS.LINKSPECIFICATION);
		s3.setVerb(WORDS.HAS);
		s3.setObject(WORDS.OPERATOR+ " "+linkSpecification.getOperator());

		DocumentElement paragraph = nlgFactory.createParagraph();
		DocumentElement sentence3 = nlgFactory.createSentence(s3);
		paragraph.addComponent(sentence);
		paragraph.addComponent(sentence3);
		Realiser realiser = new Realiser(lexicon);
		NLGElement realised = realiser.realise(paragraph);
		realisation1 = realised.getRealisation();
		//System.out.println(realisation1);

		for (int i=0;i< linkSpecification.getChildren().size();i++) {
			String format = nf.format(i+1, WORDS.SPELLOUT_ORDINAL);
			String format1 = nf.format(linkSpecification.getChildren().get(i).getAllLeaves().size(),
					WORDS.SPELLOUT_NUMBERING);
			String temp;
			if(linkSpecification.getChildren().get(i).getAllLeaves().size()<=1) 
				temp=" "+ WORDS.LEAVE;
			else temp=" "+ WORDS.LEAVES;

			SPhraseSpec s5 = nlgFactory.createClause(WORDS.THE+" "+ format + " "+WORDS.CHILD,WORDS.HAS, format1+temp);
			s5.setFeature(Feature.TENSE,Tense.PRESENT);

			SPhraseSpec s6 = nlgFactory.createClause();
			LogicOperator operator = linkSpecification.getChildren().get(i).getOperator();
			//	System.out.println("STRING: "+string);
			if(operator!=null) {
				//s6.setFeature(Feature.CONJUNCTION, WORDS.AND);
				s6.addComplement(WORDS.OPERATOR);}
			else {
				s6.addComplement(WORDS.NOOPERATOR);	
				s6.addComplement(WORDS.ONLYATOMICMEASURE);	}
			LogicOperator operator1 = linkSpecification.getChildren().get(i).getOperator();
			//	System.out.println("STRING: "+string);
			if(operator1!=null) {

				s6.addComplement(operator1.toString());
				s6.addComplement(WORDS.THAT);
				s6.addComplement(WORDS.COMBINES);
				s6.addComplement(WORDS.TWOATOMICMEASURE);
			}
			CoordinatedPhraseElement c1 = nlgFactory.createCoordinatedPhrase();
			c1.addCoordinate(s5);
			c1.addCoordinate(s6);

			DocumentElement sentence1 = nlgFactory.createSentence(c1);
			DocumentElement paragraph1 = nlgFactory.createParagraph();
			paragraph1.addComponent(sentence1);

			Realiser realiser1 = new Realiser(lexicon);
			NLGElement realised1 = realiser1.realise(paragraph1);
			realisation2 = realised1.getRealisation();
		//	System.out.println(realisation2);

			if(!linkSpecification.getChildren().get(i).isAtomic()) {
				SPhraseSpec s7 = nlgFactory.createClause();
				s7.addComplement(WORDS.FORTHEFIRSTLEAVE);

				DocumentElement sentence2 = nlgFactory.createSentence(s7);

				Realiser realiser2 = new Realiser(lexicon);
				NLGElement realised2 = realiser2.realise(sentence2);
				realisation3 = realised2.getRealisation();

			//	System.out.println(realisation3);

				atomicMeasureNLG(linkSpecification.getChildren().get(i).getAllLeaves().get(0));

				SPhraseSpec s8 = nlgFactory.createClause();
				s8.addComplement(WORDS.FORTHESECONDTLEAVE);

				DocumentElement sentence4 = nlgFactory.createSentence(s8);
				Realiser realiser3 = new Realiser(lexicon);
				NLGElement realised3 = realiser3.realise(sentence4);
				String realisation4 = realised3.getRealisation();
				System.out.println(realisation4);
				WORDS.GENERATE="creat";
				WORDS.MAPPING="linking";
				atomicMeasureNLG(linkSpecification.getChildren().get(i).getAllLeaves().get(1));
			}
			else {
				WORDS.MAPPING="link generating";
				WORDS.GENERATE="make";
				WORDS.MOREVER="furthermore";
				atomicMeasureNLG(linkSpecification.getChildren().get(i).getAllLeaves().get(0));}
		}

	}
	private static void atomicMeasureNLG(LinkSpecification learn) throws UnsupportedMLImplementationException {

		String atomicMeasureString = learn.getAtomicMeasure();
		String threshold = Double.toString(learn.getThreshold());
		String str;
		if(learn.isAtomic())
			str = WORDS.ATOMICMEASURE;
		else  
			str="";

		Lexicon lexicon = new XMLLexicon();                          
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		// create nouns
		NPPhraseSpec theLink = nlgFactory.createNounPhrase(WORDS.THE, WORDS.LINKSPECIFICATION);
		NPPhraseSpec atomicMeasure = nlgFactory.createNounPhrase(str);
		// create preposition
		PPPhraseSpec onTheLink = nlgFactory.createPrepositionPhrase(WORDS.ON);  
		//set the nouns to objects
		onTheLink.setObject(theLink); 

		// create clauses
		SPhraseSpec s0 = nlgFactory.createClause(WORDS.THE +" "+ WORDS.MAPPING,WORDS.BASE, onTheLink);
		s0.setFeature(Feature.TENSE,Tense.PAST); 
		s0.addPreModifier(WORDS.IS);

		SPhraseSpec s1 = nlgFactory.createClause();
		s1.setObject(WORDS.IT);
		s1.setSubject(atomicMeasure);
		s1.setVerb(WORDS.GENERATE);
		s1.setFeature(Feature.PASSIVE,true);
		s1.setFeature(Feature.PERFECT, true);

		//coordinate the clauses
		CoordinatedPhraseElement c = nlgFactory.createCoordinatedPhrase();
		c.addCoordinate(s0);
		c.addCoordinate(s1);

		// create a sentence
		DocumentElement sentence = nlgFactory.createSentence(c);
		StringElement theatomicmeasure = new StringElement(WORDS.THE+" "+str);        
		StringElement verbIs = new StringElement(WORDS.IS);
		WordElement measureFunction = new WordElement(atomicMeasureString);

		// create a sentence
		DocumentElement sentence2 = nlgFactory.createSentence();
		sentence2.addComponent(theatomicmeasure);
		sentence2.addComponent(verbIs);
		sentence2.addComponent(measureFunction);

		DocumentElement sentence3 = nlgFactory.createSentence();
		StringElement hasSimilarty = new StringElement(WORDS.SIMILARTY);
		WordElement similiarty = new WordElement(threshold);
		PPPhraseSpec ofSimilarty = nlgFactory.createPrepositionPhrase(WORDS.OF);

		sentence3.addComponent(hasSimilarty);
		sentence3.addComponent(ofSimilarty);
		sentence3.addComponent(similiarty);

		SPhraseSpec s3 = nlgFactory.createClause(WORDS.THE+" "+str, WORDS.IS,measureFunction);
		s3.setFeature(Feature.CUE_PHRASE, WORDS.MOREVER);

		SPhraseSpec s4 = nlgFactory.createClause(WORDS.IT,WORDS.HAS,sentence3);

		CoordinatedPhraseElement c1 = nlgFactory.createCoordinatedPhrase();
		c1.addCoordinate(s3);
		c1.addCoordinate(s4);

		DocumentElement sentence4 = nlgFactory.createSentence();
		sentence4.addComponent(c1);
		//create paragraph
		DocumentElement paragraph = nlgFactory.createParagraph();
		paragraph.addComponent(sentence);
		paragraph.addComponent(sentence4);
		Realiser realiser = new Realiser(lexicon);
		NLGElement realised = realiser.realise(paragraph);
		realisation4 = realised.getRealisation();
		System.out.println(realisation4);

	}




}

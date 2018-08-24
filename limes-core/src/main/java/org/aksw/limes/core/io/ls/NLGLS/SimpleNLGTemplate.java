package  org.aksw.limes.core.io.ls.NLGLS;
import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.ls.LinkSpecification;

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
	public static String realisation5;
	
	public String getIntroductionNLG() {
		return realisation;	
	}

	public String getFullMeasureNLG() {
		return realisation1+realisation2+realisation3+realisation4;}

	public String getAtomicMeasureNLG() {
		return realisation5;
	}

	public static void descriptor(LinkSpecification linkSpec, boolean verbos) throws UnsupportedMLImplementationException {


		//verbos=false;
		if (verbos==true)
			introduction(linkSpec);
		if(!linkSpec.isAtomic())
			fullMeasureNLG(linkSpec);
		else
			atomicMeasureNLG(linkSpec);
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
		String realisation = realised.getRealisation();
		System.out.println(realisation);
		fullMeasureNLG(linkSpec);
	}

	public static void fullMeasureNLG(LinkSpecification linkSpecification) throws UnsupportedMLImplementationException {
		System.out.println("the link spec is: "+ linkSpecification.getFullExpression());
		Lexicon lexicon = new XMLLexicon();                         
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		// create nouns
		NPPhraseSpec theLink = nlgFactory.createNounPhrase(WORDS.THE, WORDS.LINKSPECIFICATION);
		//NPPhraseSpec complexSpec = nlgFactory.createNounPhrase(WORDS.COMPLEXSPECIFICATION);
		// create preposition
		PPPhraseSpec onTheLink = nlgFactory.createPrepositionPhrase(WORDS.ON); 
		//set the nouns to objects
		onTheLink.setObject(theLink);

		SPhraseSpec clause_1 = nlgFactory.createClause();
		SPhraseSpec clause_2 = nlgFactory.createClause();
		SPhraseSpec clause_3 = nlgFactory.createClause();
		SPhraseSpec clause_4 = nlgFactory.createClause();
		SPhraseSpec clause_5 = nlgFactory.createClause();
		SPhraseSpec clause_6 = nlgFactory.createClause();
		SPhraseSpec clause_7 = nlgFactory.createClause();
		SPhraseSpec clause_8 = nlgFactory.createClause();
		SPhraseSpec clause_9 = nlgFactory.createClause();
		DocumentElement sentence_1 = nlgFactory.createSentence();
		DocumentElement sentence_2 = nlgFactory.createSentence();
		DocumentElement sentence_3 = nlgFactory.createSentence();
		DocumentElement sentence_4 = nlgFactory.createSentence();
		DocumentElement sentence_5 = nlgFactory.createSentence();
		CoordinatedPhraseElement coordinate_1 = nlgFactory.createCoordinatedPhrase();
		CoordinatedPhraseElement coordinate_2 = nlgFactory.createCoordinatedPhrase();
		CoordinatedPhraseElement coordinate_3 = nlgFactory.createCoordinatedPhrase();
		CoordinatedPhraseElement coordinate_4 = nlgFactory.createCoordinatedPhrase();
		CoordinatedPhraseElement coordinate_5 = nlgFactory.createCoordinatedPhrase();
		clause_1.setObject(WORDS.THE +" tow resources");
		//s1.addPostModifier("as follow");

		clause_1.setVerb("link");//s1.setVerb(WORDS.PRODUCE);
		clause_1.setFeature(Feature.TENSE,Tense.FUTURE);
		clause_1.setFeature(Feature.PASSIVE, true);

		//		RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.UK, RuleBasedNumberFormat.SPELLOUT);
		//c.addCoordinate(s1);
		if(linkSpecification.getOperator().toString()=="OR")
			clause_2.addComplement(" if any of the following conditions holds: ");
		if(linkSpecification.getOperator().toString()=="AND")
			clause_2.addComplement(" if both of the following conditions hold: ");
		if(linkSpecification.getOperator().toString()=="NOT")
			clause_2.addComplement(" if both of the following conditions not hold: ");

		coordinate_1.addComplement(clause_1);
		coordinate_1.addComplement(clause_2);
		sentence_1.addComponent(coordinate_1);
		DocumentElement paragraph = nlgFactory.createParagraph();

		paragraph.addComponent(sentence_1);
		//paragraph.addComponent(sentence3);
		Realiser realiser = new Realiser(lexicon);
		NLGElement realised = realiser.realise(coordinate_1);
		String realisation1 = realised.getRealisation();
		System.out.println(realisation1);

		for (int i=0;i< linkSpecification.getChildren().size();i++) {
			int size = linkSpecification.getChildren().get(i).size();
			if(!linkSpecification.getChildren().get(i).isAtomic()) {
				LogicOperator operator22 = linkSpecification.getChildren().get(i).getOperator();
				//	System.out.println("operator 2: "+operator22);
				clause_3.setObject("1: the tow resources");
				clause_3.setVerb("link");
				clause_3.setFeature(Feature.TENSE, Tense.FUTURE);
				clause_3.setFeature(Feature.PASSIVE, true);
				if(operator22.toString()=="OR")
					clause_4.addComplement(" if any of the following conditions holds: ");
				if(operator22.toString()=="AND")
					clause_4.addComplement(" if both of the following conditions hold: ");
				if(operator22.toString()=="NOT")
					clause_4.addComplement(" if both of the following conditions not hold: ");
				coordinate_2.addComplement(clause_3);
				coordinate_2.addComplement(clause_4);
				sentence_2.addComponent(coordinate_2);
				DocumentElement paragraph1 = nlgFactory.createParagraph();
				paragraph1.addComponent(sentence_2);

				Realiser realiser1 = new Realiser(lexicon);
				NLGElement realised1 = realiser1.realise(coordinate_2);
				String realisation2 = realised1.getRealisation();
				System.out.println(realisation2);
				if(size>1) {

					LinkSpecification fullExpression = linkSpecification.getChildren().get(i).getAllLeaves().get(0);
					//s111111.addComplement("1.1: ");
					//String format=nf.format(i+1);
					//System.out.println("format :"+ format);
					NLGElement atomicMeasureNLG1 = atomicMeasureNLG1(fullExpression);
					int number=i+1;
					String str=number+".1: ";
					clause_6.addComplement(str+atomicMeasureNLG1+",");
					//System.out.println("sentence: "+ atomicMeasureNLG1);
					LinkSpecification linkSpecification2 = linkSpecification.getChildren().
							get(i).getAllLeaves().get(1);
					//s1111111.addComplement("1.2:");
					NLGElement atomicMeasureNLG2=atomicMeasureNLG1(linkSpecification2);
					str=number+".2: ";
					clause_7.addComplement("\n"+str+atomicMeasureNLG2);
					coordinate_3.addComplement(clause_6);
					coordinate_3.addComplement(clause_7);
					sentence_3.addComponent(coordinate_3);


					Realiser realiser11 = new Realiser(lexicon);
					NLGElement realised11 = realiser11.realise(sentence_3);
					String realisation22 = realised11.getRealisation();
					System.out.println(realisation22);
				}
				else {
					LinkSpecification linkSpecification33 = linkSpecification.getChildren().get(i).getAllLeaves().get(0);
					NLGElement atomicMeasureNLG3=atomicMeasureNLG1(linkSpecification33);

				}


				clause_5.setObject("2: the two resources");
				clause_5.setVerb("link");
				clause_5.setFeature(Feature.TENSE, Tense.FUTURE);
				clause_5.setFeature(Feature.PASSIVE, true);

			} else {
				if(i==0) {
					NLGElement atomicMeasureNLG4 = atomicMeasureNLG1(linkSpecification.getChildren().get(0).getAllLeaves().get(0));
					clause_8.addComplement("if "+atomicMeasureNLG4+",");
					coordinate_4.addComplement(clause_5);
					coordinate_4.addComplement(clause_8);
					//coordinate_4.addComplement(clause_9);
					sentence_4.addComponent(coordinate_4);
					Realiser realiser11 = new Realiser(lexicon);
					NLGElement realised11 = realiser11.realise(coordinate_4);

					String realisation11 = realised11.getRealisation();
					System.out.println(realisation11);

				}
				if(i==1) {
					NLGElement atomicMeasureNLG5 = atomicMeasureNLG1(linkSpecification.getChildren().get(1).getAllLeaves().get(0));
					clause_9.addComplement("if "+atomicMeasureNLG5);
					coordinate_5.addComplement(clause_5);
					coordinate_5.addComplement(clause_9);
					//coordinate_4.addComplement(clause_9);
					sentence_5.addComponent(coordinate_5);
					Realiser realiser111 = new Realiser(lexicon);
					NLGElement realised111 = realiser111.realise(coordinate_5);

					String realisation111 = realised111.getRealisation();
					System.out.println(realisation111);


				}

				//				System.out.println("iii: "+i);//}
				//				//clause_8.addComplement("if "+atomicMeasureNLG4);
				//				
				//				//atomicMeasureNLG4=new NLGElement();
				//				coordinate_4.addComplement(clause_5);
				//				coordinate_4.addComplement(clause_8);
				//				coordinate_4.addComplement(clause_9);
				//				sentence_4.addComponent(coordinate_4);
				//				Realiser realiser11 = new Realiser(lexicon);
				//				NLGElement realised11 = realiser11.realise(sentence_4);
				//			
				//				String realisation11 = realised11.getRealisation();
				//				System.out.println(realisation11);
			}

		}

	}
	public static void atomicMeasureNLG(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {

		String atomicMeasureString = linkSpec.getAtomicMeasure();
		String fullExpression = linkSpec.getFullExpression();
		String leftProp;
		String rightProp;
		leftProp = linkSpec.getMeasure().substring(fullExpression.indexOf("x"),
				fullExpression.indexOf(","));
		if(leftProp.contains("#")) {
			leftProp=leftProp.substring(leftProp.indexOf("#")+1);
		}
		rightProp = linkSpec.getMeasure().substring(fullExpression.indexOf("y"),
				fullExpression.indexOf(")"));
		if(rightProp.contains("#")) {
			rightProp=rightProp.substring(rightProp.indexOf("#")+1);
			rightProp = rightProp.substring(rightProp.indexOf("#")+1);
		}

		Lexicon lexicon = new XMLLexicon();                          
		NLGFactory nlgFactory = new NLGFactory(lexicon);

		SPhraseSpec s1 = nlgFactory.createClause();
		s1.setObject(WORDS.THE+"  link ");
		//s1.setSubject(atomicMeasure);
		//s1.addPostModifier("as follow");
		s1.setVerb("link");
		s1.setFeature(Feature.PASSIVE,true);
		s1.setFeature(Feature.PERFECT, true);
		//s1.addComplement("as follow");
		//coordinate the clauses
		CoordinatedPhraseElement c = nlgFactory.createCoordinatedPhrase();
		c.addCoordinate(s1);
		DocumentElement sentence = nlgFactory.createSentence();
		sentence.addComponent(c);
		DocumentElement sentence1 = nlgFactory.createSentence();
		SPhraseSpec s2 = nlgFactory.createClause();
		SPhraseSpec s3 = nlgFactory.createClause();
		s2.addFrontModifier("if");
		s3.addComplement("the "+ atomicMeasureString+ " similarity");
		s3.addComplement("between");
		s3.addComplement("the property "+"("+leftProp+")");
		s3.addComplement("and");
		s3.addComplement("the property"+"("+rightProp+")");
		s3.addComplement("of the two resources");
		s2.setSubject(s3);
		s2.setVerb("is");
		if(linkSpec.getThreshold()==1.0)
			s2.addComplement("equal "+linkSpec.getThreshold()*100 +" %");
		else
			s2.addComplement("greater or equal than "+linkSpec.getThreshold()*100 +" %");
		CoordinatedPhraseElement cc = nlgFactory.createCoordinatedPhrase();
		cc.addCoordinate(s2);
		//cc.addCoordinate(s12);
		sentence1.addComponent(cc);

		DocumentElement paragraph = nlgFactory.createParagraph();
		paragraph.addComponent(sentence);
		sentence.addComponent(sentence1);
		//paragraph.addComponent(sentence4);
		Realiser realiser = new Realiser(lexicon);
		NLGElement realised = realiser.realise(sentence);
		String realisation5 = realised.getRealisation();
		System.out.println(realisation5);

	}

	public static NLGElement atomicMeasureNLG1(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {

		String atomicMeasureString = linkSpec.getAtomicMeasure();
		String fullExpression = linkSpec.getFullExpression();
		String leftProp;
		String rightProp;
		leftProp = linkSpec.getMeasure().substring(fullExpression.indexOf("x"),
				fullExpression.indexOf(","));
		if(leftProp.contains("#")) {
			leftProp=leftProp.substring(leftProp.indexOf("#")+1);
		}
		rightProp = linkSpec.getMeasure().substring(fullExpression.indexOf("y"),
				fullExpression.indexOf(")"));
		if(rightProp.contains("#")) {
			rightProp=rightProp.substring(rightProp.indexOf("#")+1);
			rightProp = rightProp.substring(rightProp.indexOf("#")+1);
		}

		Lexicon lexicon = new XMLLexicon();                          
		NLGFactory nlgFactory = new NLGFactory(lexicon);

		DocumentElement sentence1 = nlgFactory.createSentence();
		SPhraseSpec s2 = nlgFactory.createClause();
		SPhraseSpec s3 = nlgFactory.createClause();
		//s2.addFrontModifier("if");
		s3.addComplement("the "+ atomicMeasureString+ " similarity");
		s3.addComplement("between");
		s3.addComplement("the property "+"("+leftProp+")");
		s3.addComplement("and");
		s3.addComplement("the property"+"("+rightProp+")");
		s3.addComplement("of the two resources");
		s2.setSubject(s3);
		s2.setVerb("is");
		if(linkSpec.getThreshold()==1.0)
			s2.addComplement("equal "+linkSpec.getThreshold()*100 +"%");
		else
			s2.addComplement("greater or equal than "+linkSpec.getThreshold()*100 +"%");
		CoordinatedPhraseElement cc = nlgFactory.createCoordinatedPhrase();
		cc.addCoordinate(s2);
		sentence1.addComponent(cc);
		Realiser realiser = new Realiser(lexicon);
		NLGElement realised = realiser.realise(s2);
		//String realisation5 = realised.getRealisation();
		//System.out.println(realisation5);

		return realised;
	}
}

package  org.aksw.limes.core.io.ls.NLGLS;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * @author Abdullah Ahmed
 *
 */
public class LinkSpecSummery {

	protected static Logger logger = LoggerFactory.getLogger(LinkSpecSummery.class);

	/**
	 * @param linkSpec
	 * @param verbos
	 * @throws UnsupportedMLImplementationException
	 */
	public static void descriptor(LinkSpecification linkSpec, boolean verbos) throws UnsupportedMLImplementationException {


		//verbos=false;
		if (verbos==true)
			introduction(linkSpec);

		fullMeasureNLG(linkSpec);

	}

	/**
	 * @param linkSpec
	 * @throws UnsupportedMLImplementationException
	 */
	public static void introduction(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {

		Lexicon lexicon = new XMLLexicon();                         
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		// the first sentence 


		DocumentElement sentence_1 = nlgFactory.createSentence();
		DocumentElement sentence_2 = nlgFactory.createSentence();
		DocumentElement sentence_3 = nlgFactory.createSentence();
		DocumentElement sentence_4 = nlgFactory.createSentence();
		DocumentElement sentence_5 = nlgFactory.createSentence();
		DocumentElement sentence_6 = nlgFactory.createSentence();
		DocumentElement sentence_7 = nlgFactory.createSentence();
		DocumentElement sentence_8 = nlgFactory.createSentence();
		DocumentElement sentence_9 = nlgFactory.createSentence();
		DocumentElement sentence_10 = nlgFactory.createSentence();

		SPhraseSpec clause_1 = nlgFactory.createClause();
		SPhraseSpec clause_2 = nlgFactory.createClause();
		SPhraseSpec clause_3 = nlgFactory.createClause();
		SPhraseSpec clause_4 = nlgFactory.createClause();
		SPhraseSpec clause_5 = nlgFactory.createClause();
		SPhraseSpec clause_6 = nlgFactory.createClause();
		SPhraseSpec clause_7 = nlgFactory.createClause();
		SPhraseSpec clause_8 = nlgFactory.createClause();
		SPhraseSpec clause_9 = nlgFactory.createClause();
		SPhraseSpec clause_10 = nlgFactory.createClause();
		SPhraseSpec clause_11= nlgFactory.createClause();
		SPhraseSpec clause_12 = nlgFactory.createClause();
		SPhraseSpec clause_13 = nlgFactory.createClause();
		SPhraseSpec clause_14 = nlgFactory.createClause();
		SPhraseSpec clause_15 = nlgFactory.createClause();
		SPhraseSpec clause_16 = nlgFactory.createClause();
		SPhraseSpec clause_17 = nlgFactory.createClause();
		SPhraseSpec clause_18 = nlgFactory.createClause();
		SPhraseSpec clause_19 = nlgFactory.createClause();
		SPhraseSpec clause_20 = nlgFactory.createClause();
		SPhraseSpec clause_21 = nlgFactory.createClause();

		CoordinatedPhraseElement coordinate_1 = nlgFactory.createCoordinatedPhrase();
		CoordinatedPhraseElement coordinate_2 = nlgFactory.createCoordinatedPhrase();
		CoordinatedPhraseElement coordinate_3 = nlgFactory.createCoordinatedPhrase();

		StringElement theLinkDiscovey = new StringElement(WORDS.THE+" "+WORDS.LINKDISCOVERY);
		PPPhraseSpec over = nlgFactory.createPrepositionPhrase(WORDS.OVER);
		StringElement theKnowledgeBase = new StringElement(WORDS.KNOWLEDGEBASES);	

		sentence_1.addComponent(theLinkDiscovey);
		sentence_1.addComponent(over);
		sentence_1.addComponent(theKnowledgeBase);
		VPPhraseSpec v = nlgFactory.createVerbPhrase(WORDS.GET);
		v.setFeature(Feature.TENSE, Tense.PRESENT);
		v.setFeature(Feature.PROGRESSIVE, true);
		sentence_1.addComponent(v);
		WordElement popular = new WordElement(WORDS.POPULAR);
		sentence_1.addComponent(popular);


		clause_2.setSubject(WORDS.IT);
		clause_2.setVerb(WORDS.PLAY);
		clause_2.setFeature(Feature.TENSE, Tense.PRESENT);
		clause_2.addComplement(WORDS.ACENTRAL);
		clause_2.addComplement(WORDS.ROLE);
		clause_2.addComplement(WORDS.INLINKEDDATA);

		clause_3.setFeature(Feature.CUE_PHRASE, WORDS.THEREFORE);
		NPPhraseSpec thisYoungField = nlgFactory.createNounPhrase(WORDS.THIS, WORDS.YOUNGFIELD);
		clause_3.setObject(thisYoungField);
		clause_3.setVerb(WORDS.ATTRACT);
		clause_3.setFeature(Feature.PASSIVE,true);
		clause_3.setFeature(Feature.PERFECT, true);
		clause_3.setSubject(WORDS.MANYRESEARCHERS);


		coordinate_1.addCoordinate(clause_1);
		coordinate_1.addCoordinate(clause_2);
		coordinate_1.addCoordinate(clause_3);
		sentence_1.addComponent(coordinate_1);
		// the end of the first sentence 
		// the second sentence
		clause_4.addFrontModifier(WORDS.MANY);

		NPPhraseSpec algorithm = nlgFactory.createNounPhrase(WORDS.ALGORITHM);
		algorithm.setPlural(true);
		clause_4.setObject(algorithm);
		clause_4.setVerb(WORDS.PROPOSE);
		clause_4.setFeature(Feature.PASSIVE,true);
		clause_4.setFeature(Feature.PERFECT, true);

		clause_5.addComplement(WORDS.TO);
		clause_5.addComplement(WORDS.IMPROVE);
		clause_5.addComplement(WORDS.THE +" "+ WORDS.PERFORMANCE);
		clause_5.addComplement(WORDS.OF);
		clause_5.addComplement(WORDS.LINKDISCOVERYFRAMEWORKS);
		clause_5.addComplement(WORDS.BYMEANSOF);

		coordinate_2.addCoordinate(clause_4);
		coordinate_2.addComplement(clause_5);
		sentence_2.addComponent(coordinate_2);
		// the end of the second sentence 

		// the third sentence
		clause_6.addFrontModifier(WORDS.RECENTLY);
		NPPhraseSpec researcher = nlgFactory.createNounPhrase(WORDS.THE, WORDS.RESEARCHER);
		researcher.setPlural(true);
		clause_6.setSubject(researcher);
		clause_6.setVerb(WORDS.PAY);
		clause_6.setFeature(Feature.TENSE,Tense.PRESENT);
		clause_6.setFeature(Feature.PERFECT, true);
		clause_6.setFeature(Feature.PROGRESSIVE, true);

		NPPhraseSpec attention = nlgFactory.createNounPhrase(WORDS.ATTENTION);
		attention.addPreModifier(WORDS.MORE);
		clause_6.addComplement(attention);
		NPPhraseSpec linkSpecification = nlgFactory.createNounPhrase(WORDS.THE,WORDS.LINKSPECIFICATION);
		PPPhraseSpec ofLinkSpecification = nlgFactory.createPrepositionPhrase(WORDS.TO);
		ofLinkSpecification.setObject(linkSpecification);
		clause_6.addComplement(ofLinkSpecification);
		clause_7.addComplement(WORDS.TO);
		clause_7.addComplement(WORDS.ACHIEVE);

		NPPhraseSpec accuracy = nlgFactory.createNounPhrase(WORDS.ACCURACY);
		accuracy.addPreModifier(WORDS.HIGH);

		NPPhraseSpec runTime = nlgFactory.createNounPhrase(WORDS.RUNTIME);
		runTime.addPreModifier(WORDS.LESS);
		clause_8.addComplement(accuracy);
		clause_8.addComplement(runTime);
		coordinate_3.addCoordinate(clause_6);
		coordinate_3.addComplement(clause_7);
		coordinate_3.addComplement(clause_8);
		sentence_3.addComponent(coordinate_3);
		// the end of the third sentence 

		//the the fourth sentence
		clause_9.addComplement(WORDS.INDICE);

		clause_10.setSubject(WORDS.WE);
		clause_10.setVerb(WORDS.USE);
		NPPhraseSpec machineLearning = nlgFactory.createNounPhrase(WORDS.ALGORITHM);
		machineLearning.setPlural(true);
		machineLearning.addPreModifier(WORDS.MACHINELEARNING);
		clause_10.setObject(machineLearning);
		clause_10.setFeature(Feature.CUE_PHRASE,WORDS.FOREXAMPLE);

		clause_11.addComplement(WORDS.SUCHAS);
		clause_11.addComplement("WOMBAT, EAGLE");
		sentence_4.addComponent(clause_9);
		sentence_4.addComponent(clause_10);
		sentence_4.addComponent(clause_11);
		//the end of fourth sentence

		//the fifth sentence

		clause_12.setObject(WORDS.WOMBATALGORITHM);
		clause_12.setVerb(WORDS.USE);
		clause_12.setFeature(Feature.TENSE, Tense.PRESENT);
		clause_12.setFeature(Feature.PASSIVE, true);

		clause_13.addComplement(WORDS.TO);
		clause_13.addComplement(WORDS.OPTIMIZE);

		NPPhraseSpec linkSpecifications = nlgFactory.createNounPhrase(WORDS.THE,WORDS.LINKSPECIFICATION);
		clause_14.addComplement(linkSpecifications);

		clause_15.setFeature(Feature.COMPLEMENTISER, WORDS.WHICH);
		clause_15.setObject(WORDS.IT);
		clause_15.setVerb(WORDS.DETERMINE);
		clause_15.setFeature(Feature.TENSE, Tense.PRESENT);
		clause_15.setFeature(Feature.PASSIVE, true);
		clause_14.addComplement(clause_15);
		NPPhraseSpec similarityMeasure = nlgFactory.createNounPhrase(WORDS.THE,WORDS.SIMILARITYMEASURE);
		clause_15.setSubject(similarityMeasure);
		sentence_5.addComponent(clause_12);
		sentence_5.addComponent(clause_13);
		sentence_5.addComponent(clause_14);
		// the end of the fifth sentence

		// the sixth sentence
		clause_16.setSubject(WORDS.WE);
		clause_16.setVerb(WORDS.USE);
		clause_16.setObject(WORDS.SIMILARITYMEASURE);
		clause_16.addComplement(WORDS.SUCHAS);
		clause_16.addComplement(WORDS.MEASURES);
		clause_16.addComplement(WORDS.TO);
		clause_16.addComplement(WORDS.COMPUTE);
		clause_16.addComplement(WORDS.THE+" "+WORDS.SIMILARTY);
		clause_16.addComplement(WORDS.BETWEEN);
		clause_16.addComplement(WORDS.PROPERTYOFSOURCE);
		clause_16.addComplement(WORDS.AND);
		clause_16.addComplement(WORDS.PROPERTYOFTARGET);
		sentence_6.addComponent(clause_16);
		// the end of the sixth sentence

		// the seventh sentence 
		clause_17.setSubject(WORDS.WE);
		clause_17.setFeature(Feature.CUE_PHRASE, WORDS.THEREFORE);
		clause_17.setVerb(WORDS.NEED);
		clause_17.addComplement(WORDS.TO);
		clause_17.addComplement(WORDS.SET);
		clause_17.addComplement(WORDS.THRESHOLDVALUE);
		clause_17.addComplement(WORDS.TO);
		clause_17.addComplement(WORDS.ELIMINATE);
		clause_17.addComplement(WORDS.RESOURCES);
		clause_17.addComplement(WORDS.SIMILARITYVALUE);
		clause_17.addComplement(WORDS.UNDER);
		clause_17.addComplement(WORDS.THRESHOLD);
		sentence_7.addComponent(clause_17);
		// the end of the seventh sentence

		//the eighth sentence
		clause_18.setFeature(Feature.CUE_PHRASE, WORDS.ONCE);
		clause_18.setSubject(WORDS.THE+" "+WORDS.SIMILARITYMEASURE);
		clause_18.setVerb(WORDS.IS);
		clause_18.addComplement(WORDS.ABOVE);
		clause_18.addComplement(WORDS.THRESHOLD);

		clause_19.setObject(WORDS.LINK);
		clause_19.setVerb(WORDS.GENERATE);
		clause_19.setFeature(Feature.TENSE, Tense.FUTURE);
		clause_19.setFeature(Feature.PASSIVE, true);
		sentence_8.addComponent(clause_18);
		sentence_8.addComponent(clause_19);
		//the end of the eighth sentence

		//the ninth sentence
		clause_20.setFeature(Feature.CUE_PHRASE, WORDS.AFTERTHAT);
		clause_20.setSubject(WORDS.THE+" "+WORDS.MACHINELEARNING+" "+WORDS.ALGORITHM+" (e.g. WOMBAT Simple)");
		clause_20.setVerb(WORDS.OPTIMIZE);
		clause_20.setFeature(Feature.TENSE, Tense.PRESENT);
		clause_20.setObject(WORDS.THE+" "+WORDS.LINKSPECIFICATION);
		clause_20.addComplement(WORDS.THAT);
		clause_20.addComplement(WORDS.RETURN);
		clause_20.addComplement(WORDS.BEST);
		clause_20.addComplement(WORDS.FMEASURE);
		sentence_9.addComponent(clause_20);
		// the end of the ninth sentence

		// the tenth sentence
		clause_21.setFeature(Feature.CUE_PHRASE, WORDS.PARAGRAPH);
		clause_21.setSubject(WORDS.WE);
		clause_21.setVerb(WORDS.EXPLAIN);
		clause_21.setObject(WORDS.PROCESS);
		clause_21.addComplement(WORDS.DETAILS);
		sentence_10.addComponent(clause_21);
		DocumentElement paragraph = nlgFactory.createParagraph();

		paragraph.addComponent(sentence_1);
		paragraph.addComponent(sentence_2);
		paragraph.addComponent(sentence_3);
		paragraph.addComponent(sentence_4);
		paragraph.addComponent(sentence_5);
		paragraph.addComponent(sentence_6);
		paragraph.addComponent(sentence_7);
		paragraph.addComponent(sentence_8);
		paragraph.addComponent(sentence_9);
		paragraph.addComponent(sentence_10);
		Realiser realiser = new Realiser(lexicon);
		//realiser.setDebugMode(true);

		NLGElement realised = realiser.realise(paragraph);
		//realiser.setCommaSepCuephrase(true);
		String realisation = realised.getRealisation();
		System.out.println(realisation);
		fullMeasureNLG(linkSpec);
	}


	/**
	 * @param linkspec
	 * @param source
	 * @param target
	 * @param threshold
	 * @throws UnsupportedMLImplementationException
	 */
	static void summery(LinkSpecification linkspec, ACache source, ACache target, double threshold) throws UnsupportedMLImplementationException {

		int allMapZise;
		allMapZise=slection(linkspec, source, target).getSize();
		if(!linkspec.isAtomic())
		{


			for( int i=0;i<linkspec.getChildren().size();i++) {

				int number=i+1;
				String str= number +": ";

				if(!linkspec.getChildren().get(i).isAtomic()) 

				{
					int	map1=slection(linkspec.getChildren().get(i), source, target).getSize();
					double percentage1=((double) map1/(double) allMapZise)*100.d;
					double roundPercentage1 = Math.round(percentage1*100.0/100.0);
					String percentage1AsStreing=Double.toString(roundPercentage1);
					if(roundPercentage1>=threshold)
						fullMeasureNLG(linkspec.getChildren().get(i));
					for(int j=0;j<linkspec.getChildren().get(i).getAllLeaves().size();j++) 
					{
						int x=i+1;
						int y=j+1;
						String str1=x+"."+y+": ";
						int map2=slection(linkspec.getChildren().get(i).getAllLeaves().get(j), source, target).getSize();
						double percentage2=((double) map2/(double) allMapZise)*100.d;
						//    allLinkSpecs.add( linkSpec_1);
						double roundPercentage2 = Math.round(percentage2*100.0/100.0);
						String percentage1AsStreing1=Double.toString(roundPercentage2);
						if(roundPercentage2>=threshold)
							fullMeasureNLG(linkspec.getChildren().get(i).getAllLeaves().get(j));

					}

				}
				else 
				{
					linkspec.getChildren().get(i);
					int map3=slection(linkspec.getChildren().get(i), source, target).getSize();
					double percentage3=((double) map3/(double) allMapZise)*100.d;
					//    allLinkSpecs.add( linkSpec_1);
					double roundPercentage3 = Math.round(percentage3*100.0/100.0);
					String percentage1AsStreing3=Double.toString(roundPercentage3);
					if(roundPercentage3>=threshold)
						fullMeasureNLG(linkspec.getChildren().get(i));

				}
			}
		}
		else
		{
			int map4=slection(linkspec, source, target).getSize();
			double percentage4=((double) map4/(double) allMapZise)*100.d;
			//    allLinkSpecs.add( linkSpec_1);
			double roundPercentage4 = Math.round(percentage4*100.0/100.0);
			String percentage1AsStreing4=Double.toString(roundPercentage4);
			if(roundPercentage4>=threshold)
				fullMeasureNLG(linkspec);
		}


	}

	/**
	 * @param linkSpec
	 * @param source
	 * @param target
	 * @return
	 * @throws UnsupportedMLImplementationException
	 */
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


	static void fullMeasureNLG(LinkSpecification linkspec) throws UnsupportedMLImplementationException {
		Lexicon lexicon = new XMLLexicon();                         
		NLGFactory nlgFactory = new NLGFactory(lexicon);

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

		clause_1.setVerb("link");
		clause_1.setFeature(Feature.TENSE,Tense.FUTURE);
		clause_1.setFeature(Feature.PASSIVE, true);

		if(!linkspec.isAtomic())
		{
			if(linkspec.getOperator().toString()=="OR")
				clause_2.addComplement(" if any of the following conditions holds: ");
			if(linkspec.getOperator().toString()=="AND")
				clause_2.addComplement(" if both of the following conditions hold: ");
			if(linkspec.getOperator().toString()=="NOT")
				clause_2.addComplement(" if both of the following conditions not hold: ");
			coordinate_2.addComplement(clause_1);
			coordinate_2.addComplement(clause_2);
			Realiser realiser_2 = new Realiser(lexicon);
			NLGElement realised_2 = realiser_2.realise(coordinate_2);
			String realisation_2 = realised_2.getRealisation();
			System.out.println(realisation_2);

			for( int i=0;i<linkspec.getChildren().size();i++) {
				int number=i+1;
				String str= number +": ";
				clause_1.setObject(str+WORDS.THE +" tow resources");
				clause_1.setVerb("link");
				clause_1.setFeature(Feature.TENSE,Tense.FUTURE);
				clause_1.setFeature(Feature.PASSIVE, true);

				if(!linkspec.getChildren().get(i).isAtomic()) 
				{
					if(linkspec.getChildren().get(i).getOperator().toString()=="OR")
						clause_3.addComplement(" if any of the following conditions holds: ");
					if(linkspec.getChildren().get(i).getOperator().toString()=="AND")
						clause_3.addComplement(" if both of the following conditions hold: ");
					if(linkspec.getChildren().get(i).getOperator().toString()=="NOT")
						clause_3.addComplement(" if both of the following conditions not hold: ");

					coordinate_1.addComplement(clause_1);
					coordinate_1.addComplement(clause_3);
					clause_3=new SPhraseSpec(nlgFactory);
					Realiser realiser_1 = new Realiser(lexicon);
					NLGElement realised_1 = realiser_1.realise(coordinate_1);
					String realisation_1 = realised_1.getRealisation();
					System.out.println(realisation_1);
					coordinate_1=new CoordinatedPhraseElement();

					for(int j=0;j<linkspec.getChildren().get(i).getAllLeaves().size();j++) {
						int x=i+1;
						int y=j+1;
						String str1=x+"."+y+": ";
						clause_4.setObject(str1+WORDS.THE +" tow resources");
						clause_4.setVerb("link");
						clause_4.setFeature(Feature.TENSE,Tense.FUTURE);
						clause_4.setFeature(Feature.PASSIVE, true);
						clause_4.addPostModifier("if");

						NLGElement atomicMeasureNLG1 = atomicMeasureNLG(linkspec.getChildren().get(i).getAllLeaves().get(j));
						clause_5.addComplement(atomicMeasureNLG1);
						coordinate_3.addComplement(clause_4);
						coordinate_3.addComplement(clause_5);
						clause_4=new SPhraseSpec(nlgFactory);
						clause_5=new SPhraseSpec(nlgFactory);
						Realiser realiser_3 = new Realiser(lexicon);
						NLGElement realised_3 = realiser_3.realise(coordinate_3);
						String realisation_3= realised_3.getRealisation();
						System.out.println(realisation_3);
						coordinate_3=new CoordinatedPhraseElement();

					}

				}
				else {
					clause_6.setObject(str+WORDS.THE +" tow resources");
					clause_6.setVerb("link");;
					clause_6.setFeature(Feature.TENSE,Tense.FUTURE);
					clause_6.setFeature(Feature.PASSIVE, true);
					clause_6.addPostModifier("if");
					NLGElement atomicMeasureNLG2 = atomicMeasureNLG(linkspec.getChildren().get(i));
					clause_7.addComplement(atomicMeasureNLG2);
					coordinate_4.addComplement(clause_6);
					coordinate_4.addComplement(clause_7);

					clause_6=new SPhraseSpec(nlgFactory);
					clause_7=new SPhraseSpec(nlgFactory);
					Realiser realiser_4 = new Realiser(lexicon);
					NLGElement realised_4 = realiser_4.realise(coordinate_4);
					String realisation_4= realised_4.getRealisation();
					System.out.println(realisation_4);
					coordinate_4=new CoordinatedPhraseElement();
				}
			}
		}
		else {
			clause_8.setObject(WORDS.THE +" tow resources");
			clause_8.setVerb("link");
			clause_8.setFeature(Feature.TENSE,Tense.FUTURE);
			clause_8.setFeature(Feature.PASSIVE, true);
			clause_8.addPostModifier("if");
			NLGElement atomicMeasureNLG3 = atomicMeasureNLG(linkspec);
			clause_9.addComplement(atomicMeasureNLG3);
			coordinate_5.addComplement(clause_8);
			coordinate_5.addComplement(clause_9);

			clause_8=new SPhraseSpec(nlgFactory);
			clause_9=new SPhraseSpec(nlgFactory);
			Realiser realiser_5 = new Realiser(lexicon);
			NLGElement realised_5 = realiser_5.realise(coordinate_5);
			String realisation_5= realised_5.getRealisation();
			System.out.println(realisation_5);
			coordinate_5=new CoordinatedPhraseElement();
		}

	}

	//	public static void atomicMeasureNLG(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {
	//
	//		String atomicMeasureString = linkSpec.getAtomicMeasure();
	//		String fullExpression = linkSpec.getFullExpression();
	//		String leftProp;
	//		String rightProp;
	//		leftProp = linkSpec.getMeasure().substring(fullExpression.indexOf("x"),
	//				fullExpression.indexOf(","));
	//		if(leftProp.contains("#")) {
	//			leftProp=leftProp.substring(leftProp.indexOf("#")+1);
	//		}
	//		rightProp = linkSpec.getMeasure().substring(fullExpression.indexOf("y"),
	//				fullExpression.indexOf(")"));
	//		if(rightProp.contains("#")) {
	//			rightProp=rightProp.substring(rightProp.indexOf("#")+1);
	//			rightProp = rightProp.substring(rightProp.indexOf("#")+1);
	//		}
	//
	//		Lexicon lexicon = new XMLLexicon();                          
	//		NLGFactory nlgFactory = new NLGFactory(lexicon);
	//
	//		SPhraseSpec clause_1 = nlgFactory.createClause();
	//		SPhraseSpec clause_2 = nlgFactory.createClause();
	//		SPhraseSpec clause_3 = nlgFactory.createClause();
	//		DocumentElement sentence_1 = nlgFactory.createSentence();
	//		DocumentElement sentence_2 = nlgFactory.createSentence();
	//		CoordinatedPhraseElement coordinate_1 = nlgFactory.createCoordinatedPhrase();
	//		CoordinatedPhraseElement coordinate_2 = nlgFactory.createCoordinatedPhrase();
	//
	//		clause_1.setObject(WORDS.THE+"  link ");
	//		clause_1.setVerb("link");
	//		clause_1.setFeature(Feature.PASSIVE,true);
	//		clause_1.setFeature(Feature.PERFECT, true);
	//
	//		coordinate_1.addCoordinate(clause_1);
	//		sentence_1.addComponent(coordinate_1);
	//
	//		clause_2.addFrontModifier("if");
	//		clause_3.addComplement("the "+ atomicMeasureString+ " similarity");
	//		clause_3.addComplement("between");
	//		clause_3.addComplement("the property "+"("+leftProp+")");
	//		clause_3.addComplement("and");
	//		clause_3.addComplement("the property"+"("+rightProp+")");
	//		clause_3.addComplement("of the two resources");
	//		clause_2.setSubject(clause_3);
	//		clause_2.setVerb("is");
	//		if(linkSpec.getThreshold()==1.0)
	//			clause_2.addComplement("equal "+linkSpec.getThreshold()*100 +" %");
	//		else
	//			clause_2.addComplement("greater or equal than "+linkSpec.getThreshold()*100 +" %");
	//
	//		coordinate_2.addCoordinate(clause_2);
	//		sentence_2.addComponent(coordinate_2);
	//
	//		DocumentElement paragraph = nlgFactory.createParagraph();
	//		paragraph.addComponent(sentence_1);
	//		sentence_1.addComponent(sentence_2);
	//		Realiser realiser_1 = new Realiser(lexicon);
	//		NLGElement realised_1 = realiser_1.realise(sentence_1);
	//		String realisation_1 = realised_1.getRealisation();
	//		System.out.println(realisation_1);
	//
	//	}

	/**
	 * @param linkSpec
	 * @return
	 * @throws UnsupportedMLImplementationException
	 */
	public static NLGElement atomicMeasureNLG(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {

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

		SPhraseSpec clause_1 = nlgFactory.createClause();
		SPhraseSpec clause_2 = nlgFactory.createClause();
		CoordinatedPhraseElement coordinate_1 = nlgFactory.createCoordinatedPhrase();
		DocumentElement sentence_1 = nlgFactory.createSentence();

		clause_2.addComplement("the "+ atomicMeasureString+ " similarity");
		clause_2.addComplement("between");
		clause_2.addComplement("the property "+"("+leftProp+")");
		clause_2.addComplement("and");
		clause_2.addComplement("the property"+"("+rightProp+")");
		clause_2.addComplement("of the two resources");

		clause_1.setSubject(clause_2);
		clause_1.setVerb("is");

		if(linkSpec.getThreshold()==1.0)
			clause_1.addComplement("equal "+linkSpec.getThreshold()*100 +"%");
		else
			clause_1.addComplement("greater or equal than "+linkSpec.getThreshold()*100 +"%");
		coordinate_1.addCoordinate(clause_1);
		sentence_1.addComponent(coordinate_1);
		Realiser realiser_1 = new Realiser(lexicon);
		NLGElement realised_1 = realiser_1.realise(clause_1);
		return realised_1;
	}
}

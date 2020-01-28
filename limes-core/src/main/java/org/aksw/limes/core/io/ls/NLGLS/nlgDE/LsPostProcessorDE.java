package org.aksw.limes.core.io.ls.NLGLS.nlgDE;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.WombatSimple;

import simplenlgde.features.DiscourseFunction;
import simplenlgde.features.Feature;
import simplenlgde.features.InternalFeature;
import simplenlgde.features.NumberAgreement;
import simplenlgde.features.Tense;
import simplenlgde.framework.CoordinatedPhraseElement;
import simplenlgde.framework.DocumentElement;
import simplenlgde.framework.NLGElement;
import simplenlgde.framework.NLGFactory;
import simplenlgde.framework.PhraseElement;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.lexicon.XMLLexicon;
import simplenlgde.phrasespec.AdjPhraseSpec;
import simplenlgde.phrasespec.NPPhraseSpec;
import simplenlgde.phrasespec.SPhraseSpec;
import simplenlgde.realiser.Realiser;

public class LsPostProcessorDE {
	private static Lexicon lexicon=Lexicon.getDefaultLexicon();
	List<NLGElement> allNLGElement;
	void postProcessor(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {
		List<NLGElement> allNLGElement=new ArrayList<NLGElement>();
		LinkSpecSummeryDE linkSpecsSummery= new LinkSpecSummeryDE();
		LsPreProcessorDE lsPreProcessor=new LsPreProcessorDE();
		//Lexicon lexicon ;//= new XMLLexicon();                      
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		SPhraseSpec clause=nlgFactory.createClause();
		SPhraseSpec clause1=nlgFactory.createClause();
		SPhraseSpec clause2=nlgFactory.createClause();
		//List<NLGElement> allNLGElement=new ArrayList<NLGElement>();
		clause1.setSubject("Der" +" Link");
		clause1.setVerb("generieren");
		//clause1.setFeature(Feature.TENSE,Tense.FUTURE);
		clause1.setFeature(Feature.PASSIVE, true);
		
		Realiser clause1Realiser = new Realiser(lexicon);
		NLGElement clause1Realised = clause1Realiser.realise(clause1);
		allNLGElement = linkSpecsSummery.fullMeasureNLG(linkSpec);
		allNLGElement.add(0, clause1Realised);

		//System.out.println(clause1Realised);
		if(!linkSpec.isAtomic()) {
			clause.setSubject(linkSpecsSummery.previousSubject);
			clause.setVerb("haben");
			clause.setObject(linkSpecsSummery.objCollection);
			//clause.setFeature(Feature.COMPLEMENTISER, "wenn");
			//clause1.addComplement(clause);
			Realiser clauseRealiser = new Realiser(lexicon);
			NLGElement clauseRealised = clauseRealiser.realise(clause);
			clause=new SPhraseSpec(nlgFactory);
			allNLGElement.add(clauseRealised);
			for(NLGElement ele:allNLGElement) {
				DocumentElement s = nlgFactory.createSentence(ele);
				List<NLGElement> components = s.getComponents();
				System.out.println(components);
			}
		}

		else {

			NPPhraseSpec name = lsPreProcessor.atomicSimilarity(linkSpec);
			PhraseElement resourceValue = lsPreProcessor.resourceValue(linkSpec);
			NPPhraseSpec theta = lsPreProcessor.Theta(linkSpec);
			CoordinatedPhraseElement coordinate = lsPreProcessor.coordinate(linkSpec);
			String rightProp2 = lsPreProcessor.leftProperty(linkSpec);
			String leftProp2 = lsPreProcessor.rightProperty(linkSpec);
			//String rightProp2 = LSPreProcessor.leftProperty(linkSpecification);
			//String leftProp2 = LSPreProcessor.rightProperty(linkSpecification);
			double d=linkSpec.getThreshold();
			NPPhraseSpec firstSubject = LinkSpecSummeryDE.subject(coordinate,resourceValue,rightProp2, leftProp2);

			String stringTheta="";

			NPPhraseSpec subject = nlgFactory.createNounPhrase();
			if(d==1) {
				stringTheta=" Übereinstimmung";
				AdjPhraseSpec adjective = nlgFactory.createAdjectivePhrase("genau");
				adjective.setFeature(Feature.IS_COMPARATIVE, true);
				subject = nlgFactory.createNounPhrase(stringTheta);
				subject.addModifier(adjective);
				name.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
				subject.addComplement(name);//.addPreModifier(stringTheta);

			}
			if(d==0) {
				stringTheta =    "Nichtübereinstimmung";

				AdjPhraseSpec adjective = nlgFactory.createAdjectivePhrase("vollständig");
				adjective.setFeature(Feature.IS_COMPARATIVE, true);
				subject = nlgFactory.createNounPhrase(stringTheta);
				subject.addModifier(adjective);
				//subject = nlgFactory.createNounPhrase(stringTheta);
				name.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
				subject.addComplement(name);
			}
			if(d>0&& d<1) {
				Realiser clause2Realiser = new Realiser(lexicon);
				NLGElement thetaRealised = clause2Realiser.realise(theta);

				String	thetaAString=thetaRealised.toString();
				subject = nlgFactory.createNounPhrase(thetaAString);
				name.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
				subject.addComplement(name);
			}
			if(!rightProp2.equals(leftProp2))
				firstSubject.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
			clause2.setSubject(firstSubject);
			clause2.setVerb("haben");
			clause2.setObject(subject);
			Realiser clauseRealiser = new Realiser(lexicon);
			NLGElement clauseRealised = clauseRealiser.realise(clause2);
			System.out.println(clauseRealised);

		}
	}
	// ACache source, ACache target
	void summarization(LinkSpecification linkSpec,ACache source, ACache target,
			AMapping referenceMapping, double userScore)
					throws UnsupportedMLImplementationException {
		FMeasure fMeasure=new FMeasure();
		if(linkSpec.isAtomic()) 
		{
			AMapping slection = selection(linkSpec,source, target);
			double f=fMeasure.calculate(slection, new GoldStandard(referenceMapping));
			double r=fMeasure.recall(slection, new GoldStandard(referenceMapping));
			double p=fMeasure.precision(slection, new GoldStandard(referenceMapping));
			if(f>=userScore) {
				System.out.println("|| F_Meausre ||  Recall    ||  Precision ||");
				System.out.println(" ||  "+ f+ "  ||  " +r+ " || "+p+" ||");
				postProcessor(linkSpec);
			}
		}
		else {
			AMapping slection = selection(linkSpec,source, target);
			//if(userScore==roundPercentage1) {
			double f=fMeasure.calculate(slection, new GoldStandard(referenceMapping));
			double r=fMeasure.recall(slection, new GoldStandard(referenceMapping));
			double p=fMeasure.precision(slection, new GoldStandard(referenceMapping));

			if(f>=userScore) {
				System.out.println("|| F_Meausre ||  Recall    ||  Precision ||");
				System.out.println(" ||  "+ f+ "  ||  " +r+ " || "+p+" ||");
				postProcessor(linkSpec);}
			for (int i=0;i<linkSpec.getChildren().size();i++) {
				LinkSpecification linkSpecification = linkSpec.getChildren().get(i);
				if(linkSpecification.isAtomic())
				{
					AMapping slection1 = selection(linkSpecification,source, target);
					double f1=fMeasure.calculate(slection1, new GoldStandard(referenceMapping));
					double r1=fMeasure.recall(slection1, new GoldStandard(referenceMapping));
					double p1=fMeasure.precision(slection1, new GoldStandard(referenceMapping));
					if(f1>=userScore) {
						System.out.println(" || F_Meausre  ||  Recall    ||  Precision ||");
						System.out.println(" ||  "+ f1+ "  ||  " +r1+ "  ||     "+p1+" ||");
						postProcessor(linkSpecification);
					}
				}
				else 
					summarization(linkSpecification,source,target,referenceMapping, userScore);
			}
		}

	}
	public  AMapping selection(LinkSpecification linkSpec, ACache source, ACache target) throws UnsupportedMLImplementationException {

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


}

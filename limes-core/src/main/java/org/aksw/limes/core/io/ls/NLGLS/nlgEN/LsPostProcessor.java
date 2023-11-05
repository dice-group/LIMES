package org.aksw.limes.core.io.ls.NLGLS.nlgEN;

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

import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class LsPostProcessor {

	public String postProcessor(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {
		LinkSpecSummery linkSpecsSummery= new LinkSpecSummery();
		LsPreProcessor lsPreProcessor=new LsPreProcessor();
		Lexicon lexicon = new XMLLexicon();                      
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		SPhraseSpec clause=nlgFactory.createClause();
		SPhraseSpec clause1=nlgFactory.createClause();
		SPhraseSpec clause2=nlgFactory.createClause();
		String finlalStr="";
		List<NLGElement> allNLGElement = linkSpecsSummery.fullMeasureNLG(linkSpec);
		clause1.setObject("The" +" link");
		clause1.setVerb("generate");
		clause1.setFeature(Feature.TENSE,Tense.FUTURE);
		clause1.setFeature(Feature.PASSIVE, true);
		clause1.addPostModifier("if");
		Realiser clause1Realiser = new Realiser(lexicon);
		NLGElement clause1Realised = clause1Realiser.realise(clause1);
		String intro=clause1Realised.toString();
		//System.out.println(clause1Realised);
		if(!linkSpec.isAtomic()) {
			clause.setSubject(linkSpecsSummery.previousSubject);
			clause.setVerb("have");
			clause.setObject(linkSpecsSummery.objCollection);
			Realiser clauseRealiser = new Realiser(lexicon);
			NLGElement clauseRealised = clauseRealiser.realise(clause);
			clause=new SPhraseSpec(nlgFactory);
			allNLGElement.add(clauseRealised);

			List<String> lsVerbalization=new ArrayList<String>();

			String str="";
			for(NLGElement ele:allNLGElement) {
				str+=" " +ele;
				//System.out.println(ele);
			}

			 finlalStr="\""+linkSpec.getFullExpression() +"\""+","+"\""+intro +str+"\"";
			//System.out.println(finlalStr);
			//return finlalStr;
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
			NPPhraseSpec firstSubject = LinkSpecSummery.subject(coordinate,resourceValue,rightProp2, leftProp2);

			String stringTheta="";

			if(d==1) 
			{
				stringTheta=" exact match of";
				name.addPreModifier(stringTheta);
				//name.addPostModifier(stringTheta);
			}
			if(d==0) 
			{
				stringTheta=    "complete mismatch of";
				name.addPreModifier(stringTheta);
			}
			if(d>0&& d<1)
			{
				Realiser clause2Realiser = new Realiser(lexicon);
				NLGElement thetaRealised = clause2Realiser.realise(theta);
				String	thetaAString=thetaRealised.toString();
				stringTheta=thetaAString +" of ";
				name.addPreModifier(stringTheta);
			}
			clause2.setSubject(firstSubject);
			clause2.setVerb("have");
			clause2.setObject(name);
			Realiser clauseRealiser = new Realiser(lexicon);
			NLGElement clauseRealised = clauseRealiser.realise(clause2);
			System.out.println(clauseRealised);
			finlalStr = clauseRealised.toString();

		}
		return finlalStr;
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

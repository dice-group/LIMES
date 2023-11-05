package org.aksw.limes.core.io.ls.NLGLS.nlgDE;
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
import simplenlgde.framework.CoordinatedPhraseElement;
import simplenlgde.framework.NLGElement;
import simplenlgde.framework.NLGFactory;
import simplenlgde.framework.PhraseElement;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.phrasespec.AdjPhraseSpec;
import simplenlgde.phrasespec.NPPhraseSpec;
import simplenlgde.phrasespec.SPhraseSpec;
import simplenlgde.phrasespec.VPPhraseSpec;
import simplenlgde.realiser.Realiser;

public class LsPostProcessorDE {

	private static Lexicon lexicon=Lexicon.getDefaultLexicon();
	public static List<NLGElement> allNLGElement;

	public String postProcessor(LinkSpecification linkSpec) throws UnsupportedMLImplementationException {

		LinkSpecSummeryDE linkSpecsSummery= new LinkSpecSummeryDE();
		LsPreProcessorDE lsPreProcessor=new LsPreProcessorDE();                     
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		SPhraseSpec clause=nlgFactory.createClause();
		SPhraseSpec clause2=nlgFactory.createClause();
		allNLGElement = linkSpecsSummery.fullMeasureNLG(linkSpec);

		if(!linkSpec.isAtomic()) 
		{
			NPPhraseSpec subject= nlgFactory.createNounPhrase(linkSpecsSummery.previousSubject);
			clause.setSubject(subject);
			VPPhraseSpec verb = nlgFactory.createVerbPhrase("haben");
			clause.setVerb(verb);
			clause.setObject(linkSpecsSummery.objCollection);
			Realiser clauseRealiser = new Realiser(lexicon);
			NLGElement clauseRealised = clauseRealiser.realise(clause);
			clause=new SPhraseSpec(nlgFactory);
			allNLGElement.add(clauseRealised);

		}

		else
		{

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

			if(d==1)
			{
				stringTheta=" Übereinstimmung";
				AdjPhraseSpec adjective = nlgFactory.createAdjectivePhrase("genau");
				adjective.setFeature(Feature.IS_COMPARATIVE, true);
				NPPhraseSpec subject = nlgFactory.createNounPhrase(stringTheta);
				subject.addModifier(adjective);
				name.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
				subject.addComplement(name);

				clause2.setSubject(firstSubject);
				VPPhraseSpec verb = nlgFactory.createVerbPhrase("haben");
				clause2.setVerb(verb);
				clause2.setObject(subject);
				Realiser clauseRealiser = new Realiser(lexicon);
				NLGElement clauseRealised = clauseRealiser.realise(clause2);
				System.out.println(clauseRealised);
				clause2=new SPhraseSpec(nlgFactory);
				return clauseRealised.toString();

			}
			if(d==0) 
			{
				stringTheta =    "Nichtübereinstimmung";
				AdjPhraseSpec adjective = nlgFactory.createAdjectivePhrase("vollständig");
				adjective.setFeature(Feature.IS_COMPARATIVE, true);
				NPPhraseSpec subject = nlgFactory.createNounPhrase(stringTheta);
				subject.addModifier(adjective);
				name.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);

				clause2.setSubject(firstSubject);
				VPPhraseSpec verb = nlgFactory.createVerbPhrase("haben");
				clause2.setVerb(verb);
				clause2.setObject(subject);
				Realiser clauseRealiser = new Realiser(lexicon);
				NLGElement clauseRealised = clauseRealiser.realise(clause2);
				System.out.println(clauseRealised);
				clause2=new SPhraseSpec(nlgFactory);
				subject.addComplement(name);
				return clauseRealised.toString();
			}
			if(d>0&& d<1) 
			{

				Realiser clause2Realiser = new Realiser(lexicon);
				NLGElement thetaRealised = clause2Realiser.realise(theta);

				String	thetaAString=thetaRealised.toString();
				NPPhraseSpec subject  = nlgFactory.createNounPhrase(thetaAString);
				name.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
				subject.addComplement(name);

				clause2.setSubject(firstSubject);
				VPPhraseSpec verb = nlgFactory.createVerbPhrase("haben");
				clause2.setVerb(verb);
				clause2.setObject(subject);
				Realiser clauseRealiser = new Realiser(lexicon);
				NLGElement clauseRealised = clauseRealiser.realise(clause2);
				System.out.println(clauseRealised);
				clause2=new SPhraseSpec(nlgFactory);
				return clauseRealised.toString();
			}


		}
		return "";
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

				//realisng();
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
				postProcessor(linkSpec);
				realisng();
				System.out.println(" I am here");
			}
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
						//realisng();
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

	public String realisng() {
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		SPhraseSpec clause=nlgFactory.createClause();
		SPhraseSpec clause1=nlgFactory.createClause();
		SPhraseSpec clause2=nlgFactory.createClause();
		SPhraseSpec clause3=nlgFactory.createClause();
		Realiser clause1Realiser = new Realiser(lexicon);
		String op="";
		String verbalzation="";
		for(int i=0;i<allNLGElement.size();i++)
		{
			
			String temp1="";
			String temp2="";
			String str=allNLGElement.get(i).getRealisation();
			//System.out.println("string: "+i+"   "+str);
			if(str.length()>2&&str.contains("haben")) {
				VPPhraseSpec AuxVerb = nlgFactory.createVerbPhrase("passieren");
				//AuxVerb.setFeature(Feature.PASSIVE, true);
				clause.setSubject("Der Link");
				clause.setVerb(AuxVerb);
				//clause.setFeature(Feature.PASSIVE, true);
				temp1=str.substring( 0,str.lastIndexOf("haben"));
				NPPhraseSpec subject = nlgFactory.createNounPhrase(temp1);
				subject.setPlural(true);
				clause1.setSubject(subject);
				VPPhraseSpec verb = nlgFactory.createVerbPhrase("haben");
				clause1.setVerb(verb);
				temp1=str.substring( str.lastIndexOf("haben")+5,str.length());
				clause1.setObject(temp1);
				clause1.setFeature(Feature.COMPLEMENTISER, "wenn");
				clause.addComplement(clause1);
				NLGElement clause1Realised = clause1Realiser.realise(clause);
				clause=new SPhraseSpec(nlgFactory);
				//System.out.println("yy "+clause1Realised);
				verbalzation= clause1Realised.getRealisation();
				//System.out.println("verbal: "+verbalzation);
			}

			if(str.equals("und")||str.equals("oder")) {
				op=str;
				//System.out.println(op);
			}
			//verbalzation=verbalzation1+" "+str+" ";
			if (str.length()>2&&str.contains("hat ")) {
				VPPhraseSpec AuxVerb = nlgFactory.createVerbPhrase("passieren");
				//SPhraseSpec clause3=nlgFactory.createClause();
				clause3.setSubject("Der Link");
				clause3.setVerb(AuxVerb);
				//clause3.setFeature(Feature.PASSIVE, true);
				temp2=str.substring( 0,str.lastIndexOf("hat"));
				NPPhraseSpec subject1 = nlgFactory.createNounPhrase(temp2);
				subject1.setFeature(Feature.NUMBER, NumberAgreement.SINGULAR) ;
				clause2.setSubject(subject1);
				VPPhraseSpec verb = nlgFactory.createVerbPhrase("haben");
				clause2.setVerb(verb);
				temp2=str.substring( str.lastIndexOf("hat")+3,str.length());
				clause2.setObject(temp2);
				clause2.setFeature(Feature.COMPLEMENTISER, "wenn");
				clause3.addComplement(clause2);
				NLGElement clause1Realised = clause1Realiser.realise(clause3);
				clause3=new SPhraseSpec(nlgFactory);
				//System.out.println("xxx "+clause1Realised);
				verbalzation=verbalzation+" "+op+" "+ clause1Realised.getRealisation();
				


			}
			//System.out.println(str);
			//return verbalzation;
		}
		//System.out.println("verbalzation: "+verbalzation);
		return verbalzation;

	}


}

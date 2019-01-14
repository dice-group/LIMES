package org.aksw.limes.core.io.ls.NLGLS.naturalLanguage;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simplenlg.features.Feature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

/**
 * @author Abdullah Ahmed
 *
 */
public class LinkSpecSummery {

	private static Logger logger = LoggerFactory.getLogger(LinkSpecSummery.class);
	private static List<NLGElement> allOperator=new ArrayList<NLGElement>();
	private static String AggregationResult = "";
	private static Lexicon lexicon = new XMLLexicon();                 
	protected static NLGFactory nlgFactory =new NLGFactory(lexicon);
	private static NPPhraseSpec name;
	private static NPPhraseSpec measureName;
	private static NPPhraseSpec theta;
	protected  String previousSubject="";
	private  String previousStringTheta = "";
	private  List<NLGElement> result =new ArrayList<NLGElement>();
	protected  CoordinatedPhraseElement objCollection= nlgFactory.createCoordinatedPhrase() ;

	/**
	 * @param linkspec
	 * @return
	 * @throws UnsupportedMLImplementationException
	 */
	protected  List<NLGElement> fullMeasureNLG(LinkSpecification linkspec) throws UnsupportedMLImplementationException {

		SPhraseSpec clause = nlgFactory.createClause();
		LsPreProcessor lsPreProcessor=new LsPreProcessor();
		if(linkspec.isAtomic()) {

			NPPhraseSpec name = lsPreProcessor.atomicSimilarity(linkspec);
			PhraseElement resourceValue = lsPreProcessor.resourceValue(linkspec);
			NPPhraseSpec theta = lsPreProcessor.Theta(linkspec);
			CoordinatedPhraseElement coordinate = lsPreProcessor.coordinate(linkspec);
			double d=linkspec.getLowThreshold();

		}else {

			for (int i=0;i<linkspec.getChildren().size();i++) {
				String operatorAsString =linkspec.getOperator().toString().toLowerCase();
				//System.out.println(" the operator is "+operatorAsString);
				NPPhraseSpec operator=new NPPhraseSpec(nlgFactory);
				operator.addComplement(operatorAsString);
				Realiser realiser2 = new Realiser(lexicon);
				NLGElement realised2 = realiser2.realise(operator);
				//    System.out.println("the realizer is "+realised_2.toString());
				if (!allOperator.isEmpty()) {
					AggregationResult = AggregationResult + "," +realised2.toString();
					//System.out.println(realised_2.toString());
					//result.add(realised_2);
				}
				allOperator.add(realised2);
				LinkSpecification linkSpecification = linkspec.getChildren().get(i);
				if(linkSpecification.isAtomic()) {

					name = lsPreProcessor.atomicSimilarity(linkSpecification);
					PhraseElement resourceValue = lsPreProcessor.resourceValue(linkSpecification);
					theta = lsPreProcessor.Theta(linkSpecification);
					CoordinatedPhraseElement coordinate = lsPreProcessor.coordinate(linkSpecification);
					String rightProp2 = lsPreProcessor.leftProperty(linkSpecification);
					String leftProp2 = lsPreProcessor.rightProperty(linkSpecification);
					//String rightProp2 = LSPreProcessor.leftProperty(linkSpecification);
					//String leftProp2 = LSPreProcessor.rightProperty(linkSpecification);

					NPPhraseSpec firstSubject = subject(coordinate,resourceValue,rightProp2, leftProp2);

					double d=linkSpecification.getThreshold();

					String stringTheta="";

					if(d==1) {
						stringTheta=" exact match of";
						name.addPreModifier(stringTheta);
					}
					if(d==0) {
						stringTheta=    "complete mismatch of";
						name.addPreModifier(stringTheta);
					}
					if(d>0&& d<1) {
						Realiser clause2Realiser = new Realiser(lexicon);
						NLGElement thetaRealised = clause2Realiser.realise(theta);

						String	thetaAString=thetaRealised.toString();
						stringTheta=thetaAString +" of ";
						name.addPreModifier(stringTheta);
					}
					measureName = lsPreProcessor.atomicSimilarity(linkSpecification);
					//The Object

					//the subject	
					Realiser firstSubjectRealiser = new Realiser(lexicon);
					NLGElement firstSubjectRealised = firstSubjectRealiser.realise(firstSubject);
					if (previousSubject.equals(firstSubjectRealised.toString())) {
						objCollection.addCoordinate(operator);
						objCollection.setFeature(Feature.CONJUNCTION, "");
						if (previousStringTheta.equals(stringTheta)) {	
							objCollection.setFeature(Feature.CONJUNCTION, "");
							objCollection.addCoordinate(measureName);
							//objCollection.addPostModifier(stringTheta);
						}else {
							objCollection.setFeature(Feature.CONJUNCTION, "");
							objCollection.addCoordinate(name);
							previousStringTheta = stringTheta;
						}

					}else {
						if (!(objCollection.getChildren().isEmpty())) {							
							clause.setSubject(previousSubject);
							clause.setVerb("have");							
							clause.setObject(objCollection);

							//the clause
							Realiser clauseRealiser = new Realiser(lexicon);
							NLGElement clauseRealised = clauseRealiser.realise(clause);
							result.add(clauseRealised);
							//put the second operator
							result.add(realised2);
							objCollection.clearCoordinates();
							previousSubject = firstSubjectRealised.toString();
							objCollection.setFeature(Feature.CONJUNCTION, "");
							objCollection.addCoordinate(name);
						}else {
							previousSubject = firstSubjectRealised.toString();
							objCollection.addCoordinate(name);
							objCollection.setFeature(Feature.CONJUNCTION, "");
						}

					}					
				}else 
				{
					fullMeasureNLG(linkSpecification);
				}

			}

		}

		return result;
	}


	/**
	 * @param coordinate
	 * @param resourceValue
	 * @param leftProp
	 * @param rightProp
	 * @return
	 */
	public static NPPhraseSpec subject(
			CoordinatedPhraseElement coordinate, PhraseElement resourceValue, String leftProp
			, String rightProp) {

		NPPhraseSpec subject1 = nlgFactory.createNounPhrase();
		NPPhraseSpec subject2 = nlgFactory.createNounPhrase();



		Realiser clause22Realiser = new Realiser(lexicon);
		NLGElement sameResource = clause22Realiser.realise(resourceValue);
		String sameResourceAsString=sameResource.toString();
		String p=    "the "+sameResourceAsString+" of "+"the source "+"and "+"the target"+" resources";
		PPPhraseSpec pp = nlgFactory.createPrepositionPhrase(p);
		if(rightProp.equals(leftProp)) {
			subject1.addComplement(pp);
			return subject1;
		}
		else {
			subject2.addComplement(coordinate);
			return subject2;

		}

	}



	/*public void flush() {

		previousSubject="";
		previousStringTheta="";
		objCollection= nlgFactory.createCoordinatedPhrase() ;
		result =new ArrayList<NLGElement>();
	}*/

}
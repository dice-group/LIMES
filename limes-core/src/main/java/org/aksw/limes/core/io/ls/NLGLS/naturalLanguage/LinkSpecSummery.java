package org.aksw.limes.core.io.ls.NLGLS.naturalLanguage;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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



	protected static Logger logger = LoggerFactory.getLogger(LinkSpecSummery.class);
	protected static List<NLGElement> nlgElements=new ArrayList<NLGElement>();
	protected static List<List<NLGElement>> allnlg =new ArrayList<List<NLGElement>>();
	protected static NLGElement atomicMeasureNLG;
	protected static String leftProp;
	protected static String rightProp;
	protected static List<NLGElement> allcoordinate =new ArrayList<NLGElement>();
	protected static List<NLGElement> finalCoordinate =new ArrayList<NLGElement>();
	protected static List<NLGElement> allOperator=new ArrayList<NLGElement>();
	protected static String AggregationResult = "";
	protected static List<String> temp = new ArrayList<String>();
	protected static List<String> tempTheta = new ArrayList<String>();

	protected static Lexicon lexicon = new XMLLexicon();                  
	protected static NLGFactory nlgFactory = new NLGFactory(lexicon);

	protected static NPPhraseSpec name;
	protected static NPPhraseSpec firstSubject;
	protected static NPPhraseSpec theta;
	protected static CoordinatedPhraseElement obj=nlgFactory.createCoordinatedPhrase() ;
	protected static NPPhraseSpec oprtator=new NPPhraseSpec(nlgFactory);
	protected static CoordinatedPhraseElement coordinateClause=nlgFactory.createCoordinatedPhrase() ;
	protected static String previousSubject="";
	protected static List<NLGElement> result =new ArrayList<NLGElement>();
	protected static CoordinatedPhraseElement objCollection= nlgFactory.createCoordinatedPhrase() ;
	protected static LinkSpecification linkspec;
	protected static int x ;



	protected static List<NLGElement> fullMeasureNLG(LinkSpecification linkspec) throws UnsupportedMLImplementationException {

		SPhraseSpec clause_1 = nlgFactory.createClause();
		SPhraseSpec clause_2 = nlgFactory.createClause();
		SPhraseSpec clause_3 = nlgFactory.createClause();
		SPhraseSpec clause_4 = nlgFactory.createClause();
		SPhraseSpec clause = nlgFactory.createClause();
		x=linkspec.getAllLeaves().size();
		CoordinatedPhraseElement coordinate_2 = nlgFactory.createCoordinatedPhrase();
		CoordinatedPhraseElement coordinate_4 = nlgFactory.createCoordinatedPhrase();

		if(linkspec.isAtomic()) {
			String previousEle="";
			NPPhraseSpec name = LsPreProcessor.atomicSimilarity(linkspec);
			PhraseElement resourceValue = LsPreProcessor.resourceValue(linkspec);
			NPPhraseSpec theta = LsPreProcessor.Theta(linkspec);
			CoordinatedPhraseElement coordinate = LsPreProcessor.coordinate(linkspec);
			double d=linkspec.getLowThreshold();

		}else {

			for (int i=0;i<linkspec.getChildren().size();i++) {

				LinkSpecification linkSpecification = linkspec.getChildren().get(i);
				//System.out.println("link is "+linkSpecification.getFullExpression());

				if(linkSpecification.isAtomic()) {

					String operatorAsString =linkspec.getOperator().toString().toLowerCase();

					oprtator.addComplement(operatorAsString);
					Realiser realiser_2 = new Realiser(lexicon);
					NLGElement realised_2 = realiser_2.realise(oprtator);
					//    System.out.println("the realizer is "+realised_2.toString());
					if (!allOperator.isEmpty()) {
						AggregationResult = AggregationResult + "," +realised_2.toString();
						//allcoordinate.add(oprtator);
						//temp.add(realised_2.toString());
					}
					allOperator.add(realised_2);
					nlgElements.add(realised_2);

					name = LsPreProcessor.atomicSimilarity(linkSpecification);
					PhraseElement resourceValue = LsPreProcessor.resourceValue(linkSpecification);
					theta = LsPreProcessor.Theta(linkSpecification);
					CoordinatedPhraseElement coordinate = LsPreProcessor.coordinate(linkSpecification);
					String rightProp2 = LsPreProcessor.leftProperty(linkSpecification);
					String leftProp2 = LsPreProcessor.rightProperty(linkSpecification);

					NPPhraseSpec firstSubject = subject(coordinate,resourceValue,rightProp2, leftProp2);

					clause_3=new SPhraseSpec(nlgFactory);


					double d=linkSpecification.getThreshold();

					String stringTheta="";

					if(d==1.00) {
						stringTheta="exact match";
						name.addPreModifier(stringTheta);

					}
					if(d==0.00) {
						stringTheta=    "complete mismatch";
						name.addPreModifier(stringTheta);

					}
					if(d>0.0&& d<1.0) {

						Realiser clause2Realiser = new Realiser(lexicon);
						NLGElement thetaRealised = clause2Realiser.realise(theta);
						stringTheta=thetaRealised.toString();
						name.addPreModifier(stringTheta);
					}

					//the object
					Realiser objectRealiser = new Realiser(lexicon);
					NLGElement objectRealised = objectRealiser.realise(name);
					//the subject	
					Realiser firstSubjectRealiser = new Realiser(lexicon);
					NLGElement firstSubjectRealised = firstSubjectRealiser.realise(firstSubject);
					/*System.out.println("previous element "+ previousSubject);
					System.out.println("current element "+ firstSubjectRealised.toString());*/
					if (previousSubject.equals(firstSubjectRealised.toString())) {
						//System.out.println("found the same subject");
						objCollection.addCoordinate(name);
						objCollection.addCoordinate(oprtator);
						//System.out.println("number of element inside Obj "+ size);
					}else {
						//System.out.println("found the first or the new different subject element");
						//System.out.println("number of element inside Obj "+objCollection.getChildren().size());
						//System.out.println(objCollection.getChildren().size());
						if (!(objCollection.getChildren().isEmpty())) {
							//System.out.println("Obj is "+objCollection.getChildren().size());
							clause.setSubject(previousSubject);
							clause.setVerb("have");							
							clause.setObject(objCollection);

							//the clause
							Realiser clauseRealiser = new Realiser(lexicon);
							NLGElement clauseRealised = clauseRealiser.realise(clause);
							result.add(clauseRealised);
							/*System.out.println(" aggregation "+clauseRealised);
							System.out.println("\n");*/
							objCollection.clearCoordinates();

							//System.out.println("number of element inside Obj "+ size);
							previousSubject = firstSubjectRealised.toString();
							//System.out.println("previous subject "+previousSubject);
							objCollection.addCoordinate(name);
							objCollection.addCoordinate(oprtator);
						}else {
							previousSubject = firstSubjectRealised.toString();
							//System.out.println("previous subject "+previousSubject);
							objCollection.addCoordinate(name);
							int size = objCollection.getChildren().size();
							//System.out.println("number of element inside Obj "+ size);
						}

					}					
				}else {
					fullMeasureNLG(linkSpecification);
				}

			}

		}

		//return nlgElements;
		//System.out.println(" iteration "+x);

		return result;
		//return temp;

	}




	public static NPPhraseSpec subject(
			CoordinatedPhraseElement coordinate, PhraseElement resourceValue, String leftProp
			, String rightProp) {

		NPPhraseSpec subject1 = nlgFactory.createNounPhrase();
		NPPhraseSpec subject2 = nlgFactory.createNounPhrase();



		Realiser clause22Realiser = new Realiser(lexicon);
		NLGElement sameResource = clause22Realiser.realise(resourceValue);
		String sameResourceAsString=sameResource.toString();
		String p=    "the "+sameResourceAsString+" of "+"the source "+"and "+"the target";
		PPPhraseSpec pp = nlgFactory.createPrepositionPhrase(p);
		if(rightProp.equals(leftProp)) {
			subject1.addComplement(pp);
			//clause1.setVerb("have");
			//    clause1.setObject(resourceValue);
			return subject1;
		}
		else {
			subject2.addComplement(coordinate);
			//clause2.setVerb("have");
			return subject2;

		}

	}


}
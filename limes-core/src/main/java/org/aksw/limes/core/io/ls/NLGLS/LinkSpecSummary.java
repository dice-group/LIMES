package  org.aksw.limes.core.io.ls.NLGLS;
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
import simplenlg.lexicon.english.XMLLexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

/**
 * @author Abdullah Ahmed
 *
 */
public class LinkSpecSummary {

	protected static Logger logger = LoggerFactory.getLogger(LinkSpecSummary.class);
	protected static List<NLGElement> nlgElements=new ArrayList<NLGElement>();
	protected static List<List<NLGElement>> allnlg =new ArrayList<List<NLGElement>>();
	protected static NLGElement atomicMeasureNLG;
	protected static String leftProp;
	protected static String rightProp;
	protected static List<NPPhraseSpec> allName = new ArrayList<NPPhraseSpec>();
	protected static List<PhraseElement> allresourceValue = new ArrayList<PhraseElement>();
	protected static List<NPPhraseSpec> alltheta = new ArrayList<NPPhraseSpec>();
	protected static List<CoordinatedPhraseElement> allSameSubject =new ArrayList<CoordinatedPhraseElement>();
	protected static List<SPhraseSpec> allcoordinate =new ArrayList<SPhraseSpec>();
	protected static List<NLGElement> allOperator=new ArrayList<NLGElement>();
	protected static String AggregationResult = "";
	protected static List<String> temp = new ArrayList<String>();
	protected static List<String> tempTheta = new ArrayList<String>();

	protected static Lexicon lexicon = new XMLLexicon();                    
	protected static NLGFactory nlgFactory = new NLGFactory(lexicon);
	protected static CoordinatedPhraseElement finalCoordinate ;


	protected static List<SPhraseSpec> fullMeasureNLG(LinkSpecification linkspec) throws UnsupportedMLImplementationException {

		SPhraseSpec clause1 = nlgFactory.createClause();
		SPhraseSpec clause2 = nlgFactory.createClause();
		SPhraseSpec clause3 = nlgFactory.createClause();
		SPhraseSpec clause4 = nlgFactory.createClause();
		SPhraseSpec clause =   nlgFactory.createClause();
		CoordinatedPhraseElement coordinate2 = nlgFactory.createCoordinatedPhrase();
		CoordinatedPhraseElement coordinate3 = nlgFactory.createCoordinatedPhrase();


		if(linkspec.isAtomic()) {
			String previousEle="";
			NPPhraseSpec name = LSPreProcessor.atomicSimilarity(linkspec);
			PhraseElement resourceValue = LSPreProcessor.resourceValue(linkspec);
			NPPhraseSpec theta = LSPreProcessor.Theta(linkspec);
			CoordinatedPhraseElement sameSubject = LSPreProcessor.sameSubject();
			CoordinatedPhraseElement coordinate = LSPreProcessor.coordinate(linkspec);
			double d=linkspec.getLowThreshold();

			//NLGElement atomicMeasureNLG = preProcessoring(sameSubject,resourceValue,name, theta, coordinate,d);
			if (!previousEle.equals(atomicMeasureNLG.toString()) ) {           
				previousEle = atomicMeasureNLG.toString();
				nlgElements.add(atomicMeasureNLG);           
			}
		}else {

			for (int i=0;i<linkspec.getChildren().size();i++) {

				LinkSpecification linkSpecification = linkspec.getChildren().get(i);
				if(linkSpecification.isAtomic()) {

					String operatorAsString =linkspec.getOperator().toString().toLowerCase();
					SPhraseSpec oprtator = nlgFactory.createClause();
					oprtator.addComplement(operatorAsString);
					Realiser realiser1 = new Realiser(lexicon);
					NLGElement realised1 = realiser1.realise(oprtator);
					//	System.out.println("the realizer is "+realised_2.toString());
					if (!allOperator.isEmpty()) {
						//AggregationResult = AggregationResult + "," +realised_2.toString();
						allcoordinate.add(oprtator);
						temp.add(oprtator.toString());
					}
					allOperator.add(realised1);
					nlgElements.add(realised1);  

					clause3=new SPhraseSpec(nlgFactory);
					NPPhraseSpec name = LSPreProcessor.atomicSimilarity(linkSpecification);
					PhraseElement resourceValue = LSPreProcessor.resourceValue(linkSpecification);
					NPPhraseSpec theta = LSPreProcessor.Theta(linkSpecification);
					CoordinatedPhraseElement sameSubject = LSPreProcessor.sameSubject();
					CoordinatedPhraseElement coordinate = LSPreProcessor.coordinate(linkSpecification);
					String rightProp2 = LSPreProcessor.leftProperty(linkSpecification);
					String leftProp2 = LSPreProcessor.rightProperty(linkSpecification);

					double d=linkSpecification.getThreshold();

					clause1.setSubject(sameSubject);
					clause1.setVerb("have");
					clause1.setObject(resourceValue);


					if(d==1.00) {

						name.addPreModifier("exact match");
						//clause_2.setObject(name);



					}
					if(d==0.00) {

						name.addPreModifier("complete mismatch");
						clause2.setObject(name);


					}
					if(d>0.0&& d<1.0) {
						name.addPreModifier(theta);
						clause2.setObject(name);


					}

					Realiser clause1Realiser = new Realiser(lexicon);
					NLGElement clause1Realised = clause1Realiser.realise(clause1);

					Realiser clause2Realiser = new Realiser(lexicon);
					NLGElement clause2Realised = clause2Realiser.realise(clause2);


					if(rightProp2.equals(leftProp2)) {

						/*coordinate_2.addCoordinate(clause_1);
						coordinate_2.addCoordinate(clause_2);*/

						//coordinate_2.addCoordinate(clause_3);
						//Realiser realiser_1 = new Realiser(lexicon);
						//	NLGElement realised_1 = realiser_1.realise(coordinate_2);

						//System.out.println(realised_1.toString());						
						Realiser coordinate2Realiser = new Realiser(lexicon);
						NLGElement coordinate2Realised = coordinate2Realiser.realise(coordinate2);

						if (temp.contains(clause1Realised.toString())) {
							//System.out.println(clause1Realised.toString());									

							if (temp.get(temp.size()-2).matches(clause1Realised.toString())) {
								//System.out.println("the subjects are same");

								AggregationResult = AggregationResult + " "+clause2Realised.toString();
							}
							else 
							{								
								AggregationResult = AggregationResult + " "+ coordinate2Realised.toString();
							}
							temp.add(clause1Realised.toString());
						}
						else 
						{
							//System.out.println("it's not seen");
							//AggregationResult = AggregationResult + " " + coordinate2Realised.toString();
							CoordinatedPhraseElement obj = nlgFactory.createCoordinatedPhrase();
							obj.addCoordinate(resourceValue);
							obj.addCoordinate(name);
							clause.setSubject(sameSubject);
							clause.setVerb("have");
							clause.setObject(obj);							
							allcoordinate.add(clause);
							temp.add(clause1Realised.toString());

						}
						//reset clause_1 and clause_2
						clause1=new SPhraseSpec(nlgFactory);
						clause2=new SPhraseSpec(nlgFactory);
						coordinate2=new CoordinatedPhraseElement();
					}else {
						clause4.setSubject(coordinate);
						clause4.setVerb("has");
						clause4.setObject(name);

						coordinate3.addCoordinate(clause4);
						Realiser realiser4= new Realiser(lexicon);
						NLGElement realised_4 = realiser4.realise(coordinate3);

						Realiser coordinateRealiser= new Realiser(lexicon);
						NLGElement coordinateRealised = coordinateRealiser.realise(coordinate);

						Realiser nameRealiser= new Realiser(lexicon);
						NLGElement nameRealised = nameRealiser.realise(name);

						if (temp.contains(coordinateRealised.toString()))
						{
							if (temp.get(temp.size()-2).matches(coordinateRealised.toString())) {
								AggregationResult = AggregationResult + " "+nameRealised.toString();
							}
							else {								
								AggregationResult = AggregationResult + " "+ realised_4.toString();
							}
							temp.add(coordinateRealised.toString());
						}
						else {							
							AggregationResult = AggregationResult + " " + realised_4.toString();
							temp.add(coordinateRealised.toString());							
						}
					}

					coordinate3=new CoordinatedPhraseElement();			         
					clause1 =new SPhraseSpec(nlgFactory);
					clause2 =new SPhraseSpec(nlgFactory);		


					nlgElements.add(atomicMeasureNLG);



				}			

				else {


					fullMeasureNLG(linkSpecification);

				}

			}

		}
		//return nlgElements;
		return allcoordinate;
		//return temp;

	}
	

}
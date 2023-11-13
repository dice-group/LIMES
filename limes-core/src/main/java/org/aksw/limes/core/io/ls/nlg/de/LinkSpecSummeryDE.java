package org.aksw.limes.core.io.ls.nlg.de;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simplenlgde.features.DiscourseFunction;
import simplenlgde.features.Feature;
import simplenlgde.features.InternalFeature;
import simplenlgde.features.NumberAgreement;
import simplenlgde.framework.*;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.phrasespec.AdjPhraseSpec;
import simplenlgde.phrasespec.NPPhraseSpec;
import simplenlgde.phrasespec.SPhraseSpec;
import simplenlgde.realiser.Realiser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Abdullah Ahmed
 */
public class LinkSpecSummeryDE {

    private static Logger logger = LoggerFactory.getLogger(LinkSpecSummeryDE.class);
    private List<NLGElement> allOperator = new ArrayList<NLGElement>();
    private String AggregationResult = "";
    private Lexicon lexicon = Lexicon.getDefaultLexicon();// = new XMLLexicon();
    protected NLGFactory nlgFactory = new NLGFactory(lexicon);
    private NPPhraseSpec name;
    private NPPhraseSpec subject;
    private NPPhraseSpec measureName;
    private NPPhraseSpec theta;
    protected String previousSubject = "";
    private String previousStringTheta = "";
    private List<NLGElement> result = new ArrayList<NLGElement>();
    protected CoordinatedPhraseElement objCollection = nlgFactory.createCoordinatedPhrase();

    private LsPreProcessorDE lsPreProcessor = new LsPreProcessorDE();

    /**
     * @param linkspec
     * @return
     */

    protected List<NLGElement> fullMeasureNLG(LinkSpecification linkspec) {
        SPhraseSpec clause = nlgFactory.createClause();
        if (linkspec.isAtomic()) {

            NPPhraseSpec name = lsPreProcessor.atomicSimilarity(linkspec);
            PhraseElement resourceValue = lsPreProcessor.resourceValue(linkspec);
            NPPhraseSpec theta = lsPreProcessor.theta(linkspec);
            CoordinatedPhraseElement coordinate = lsPreProcessor.coordinate(linkspec);
            double d = linkspec.getLowThreshold();

        } else {

            for (int i = 0; i < linkspec.getChildren().size(); i++) {
                String operatorAsString = linkspec.getOperator().toString().toLowerCase();
                NLGElement complementElement = null;
                if (operatorAsString.equals("and")) {
                    complementElement = new StringElement("und");
                } else if (operatorAsString.equals("or")) {
                    complementElement = new StringElement("oder");
                }
                if (!allOperator.isEmpty()) {
                    AggregationResult = AggregationResult + "," + complementElement.toString();

                }
                allOperator.add(complementElement);
                LinkSpecification linkSpecification = linkspec.getChildren().get(i);
                if (linkSpecification.isAtomic()) {

                    name = lsPreProcessor.atomicSimilarity(linkSpecification);
                    PhraseElement resourceValue = lsPreProcessor.resourceValue(linkSpecification);
                    theta = lsPreProcessor.theta(linkSpecification);
                    CoordinatedPhraseElement coordinate = lsPreProcessor.coordinate(linkSpecification);
                    String rightProp2 = lsPreProcessor.leftProperty(linkSpecification);
                    String leftProp2 = lsPreProcessor.rightProperty(linkSpecification);
                    //String rightProp2 = LSPreProcessor.leftProperty(linkSpecification);
                    //String leftProp2 = LSPreProcessor.rightProperty(linkSpecification);
                    NPPhraseSpec firstSubject = subject(coordinate, rightProp2, leftProp2);
                    double d = linkSpecification.getThreshold();

                    String stringTheta = "";

                    if (d == 1) {
                        stringTheta = " Übereinstimmung";
                        AdjPhraseSpec adjective = nlgFactory.createAdjectivePhrase("genau");
                        adjective.setFeature(Feature.IS_COMPARATIVE, true);
                        subject = nlgFactory.createNounPhrase(stringTheta);
                        subject.addModifier(adjective);
                        name.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
                        subject.addComplement(name);//.addPreModifier(stringTheta);


                    }
                    if (d == 0) {
                        stringTheta = "Nichtübereinstimmung";

                        AdjPhraseSpec adjective = nlgFactory.createAdjectivePhrase("vollständig");
                        adjective.setFeature(Feature.IS_COMPARATIVE, true);
                        subject = nlgFactory.createNounPhrase(stringTheta);
                        subject.addModifier(adjective);
                        //subject = nlgFactory.createNounPhrase(stringTheta);
                        name.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
                        subject.addComplement(name);


                    }
                    if (d > 0 && d < 1) {
                        Realiser clause2Realiser = new Realiser(lexicon);
                        NLGElement thetaRealised = clause2Realiser.realise(theta);

                        String thetaAString = thetaRealised.toString();
                        subject = nlgFactory.createNounPhrase(thetaAString);
                        name.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
                        subject.addComplement(name);
                    }
                    measureName = lsPreProcessor.atomicSimilarity(linkSpecification);
                    //The Object

                    //the subject
                    Realiser firstSubjectRealiser = new Realiser(lexicon);
                    NLGElement firstSubjectRealised = firstSubjectRealiser.realise(firstSubject);
                    if (previousSubject.equals(firstSubjectRealised.toString())) {
                        if (operatorAsString.equals("and")) {
                            objCollection.addCoordinate("und");
                        }
                        if (operatorAsString.equals("or")) {
                            objCollection.addCoordinate("oder");
                        }
                        objCollection.setFeature(Feature.CONJUNCTION, "");
                        if (previousStringTheta.equals(stringTheta)) {
                            objCollection.setFeature(Feature.CONJUNCTION, "");
                            objCollection.addCoordinate(measureName);
                            //objCollection.addPostModifier(stringTheta);
                        } else {
                            objCollection.setFeature(Feature.CONJUNCTION, "");
                            objCollection.addCoordinate(subject);
                            previousStringTheta = stringTheta;
                        }

                    } else {
                        if (!(objCollection.getChildren().isEmpty())) {
                            NPPhraseSpec tempS = nlgFactory.createNounPhrase(previousSubject);
                            if (previousSubject.contains("die Ressource"))
                                tempS.setPlural(true);
                            clause.setSubject(tempS);
                            clause.setVerb("haben");
                            clause.setObject(objCollection);
                            //the clause
                            Realiser clauseRealiser = new Realiser(lexicon);
                            NLGElement clauseRealised = clauseRealiser.realise(clause);
                            result.add(clauseRealised);
                            //put the second operator
                            result.add(complementElement);
                            objCollection.clearCoordinates();
                            previousSubject = firstSubjectRealised.toString();
                            objCollection.setFeature(Feature.CONJUNCTION, "");
                            objCollection.addCoordinate(subject);
                        } else {
                            previousSubject = firstSubjectRealised.toString();
                            objCollection.addCoordinate(subject);
                            objCollection.setFeature(Feature.CONJUNCTION, "");
                        }

                    }
                } else {
                    fullMeasureNLG(linkSpecification);
                }

            }

        }

        return result;
    }


    /**
     * @param coordinate
     * @param leftProp
     * @param rightProp
     * @return
     */
    public NPPhraseSpec subject(CoordinatedPhraseElement coordinate, String leftProp, String rightProp) {

        Realiser clause22Realiser = new Realiser(lexicon);
        String p = leftProp + " von " + "der Datenquelle " + "und " + "dem Datenziel" + " Ressourcen";

        if (rightProp.equals(leftProp)) {
            NPPhraseSpec subject1 = nlgFactory.createNounPhrase(p);
            subject1.setFeature(Feature.NUMBER, NumberAgreement.SINGULAR);
            //subject1.addComplement(pp);
            return subject1;
        } else {
            NLGElement differentResource = clause22Realiser.realise(coordinate);
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase(differentResource.toString());
            subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);//setFeature(Feature.NUMBER, );
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
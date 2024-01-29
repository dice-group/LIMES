package org.aksw.limes.core.io.ls.nlg.en;

import org.aksw.limes.core.io.ls.LinkSpecification;
import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

import java.util.List;

public class LsPostProcessorEN {

    public String postProcessor(LinkSpecification linkSpec) {
        LinkSpecSummeryEN linkSpecsSummery = new LinkSpecSummeryEN();
        LsPreProcessorEN lsPreProcessor = new LsPreProcessorEN();
        Lexicon lexicon = new XMLLexicon();
        NLGFactory nlgFactory = new NLGFactory(lexicon);
        SPhraseSpec clause = nlgFactory.createClause();
        SPhraseSpec clause1 = nlgFactory.createClause();
        SPhraseSpec clause2 = nlgFactory.createClause();
        String result = "";
        List<NLGElement> allNLGElement = linkSpecsSummery.fullMeasureNLG(linkSpec);
        clause1.setObject("The" + " link");
        clause1.setVerb("generate");
        clause1.setFeature(Feature.TENSE, Tense.FUTURE);
        clause1.setFeature(Feature.PASSIVE, true);
        clause1.addPostModifier("if");
        Realiser clause1Realiser = new Realiser(lexicon);
        NLGElement clause1Realised = clause1Realiser.realise(clause1);
        String intro = clause1Realised.toString();
        //System.out.println(clause1Realised);
        if (!linkSpec.isAtomic()) {
            clause.setSubject(linkSpecsSummery.previousSubject);
            clause.setVerb("have");
            clause.setObject(linkSpecsSummery.objCollection);
            Realiser clauseRealiser = new Realiser(lexicon);
            NLGElement clauseRealised = clauseRealiser.realise(clause);
            allNLGElement.add(clauseRealised);

            StringBuilder str = new StringBuilder();
            for (NLGElement ele : allNLGElement) {
                str.append(" ").append(ele);
                //System.out.println(ele);
            }

            result = intro + str;
        } else {

            NPPhraseSpec name = lsPreProcessor.atomicSimilarity(linkSpec);
            NPPhraseSpec theta = lsPreProcessor.theta(linkSpec);
            CoordinatedPhraseElement coordinate = lsPreProcessor.coordinate(linkSpec);
            String leftProp2 = lsPreProcessor.leftProperty(linkSpec);
            String rightProp2 = lsPreProcessor.rightProperty(linkSpec);

            double d = linkSpec.getThreshold();
            NPPhraseSpec firstSubject = linkSpecsSummery.subject(coordinate, leftProp2, rightProp2);

            String stringTheta = "";

            if (d == 1) {
                stringTheta = " exact match of";
                name.addPreModifier(stringTheta);
                //name.addPostModifier(stringTheta);
            }
            if (d == 0) {
                stringTheta = "complete mismatch of";
                name.addPreModifier(stringTheta);
            }
            if (d > 0 && d < 1) {
                Realiser clause2Realiser = new Realiser(lexicon);
                NLGElement thetaRealised = clause2Realiser.realise(theta);
                String thetaAString = thetaRealised.toString();
                stringTheta = thetaAString + " of ";
                name.addPreModifier(stringTheta);
            }
            clause2.setSubject(firstSubject);
            clause2.setVerb("have");
            clause2.setObject(name);
            Realiser clauseRealiser = new Realiser(lexicon);
            NLGElement clauseRealised = clauseRealiser.realise(clause2);
            result = intro + " " + clauseRealised.toString();

        }
        return result;
    }


}

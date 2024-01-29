package org.aksw.limes.core.io.ls.nlg.de;

import org.aksw.limes.core.io.ls.LinkSpecification;
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

import java.util.List;

public class LsPostProcessorDE {

    private Lexicon lexicon = Lexicon.getDefaultLexicon();
    public List<NLGElement> allNLGElement;

    public String postProcessor(LinkSpecification linkSpec) {
        LinkSpecSummeryDE linkSpecsSummery = new LinkSpecSummeryDE();
        LsPreProcessorDE lsPreProcessor = new LsPreProcessorDE();
        NLGFactory nlgFactory = new NLGFactory(lexicon);
        SPhraseSpec clause = nlgFactory.createClause();
        SPhraseSpec clause2 = nlgFactory.createClause();
        allNLGElement = linkSpecsSummery.fullMeasureNLG(linkSpec);

        if (!linkSpec.isAtomic()) {
            NPPhraseSpec subject = nlgFactory.createNounPhrase(linkSpecsSummery.previousSubject);
            clause.setSubject(subject);
            VPPhraseSpec verb = nlgFactory.createVerbPhrase("haben");
            clause.setVerb(verb);
            clause.setObject(linkSpecsSummery.objCollection);
            Realiser clauseRealiser = new Realiser(lexicon);
            NLGElement clauseRealised = clauseRealiser.realise(clause);
            clause = new SPhraseSpec(nlgFactory);
            allNLGElement.add(clauseRealised);

        } else {

            NPPhraseSpec name = lsPreProcessor.atomicSimilarity(linkSpec);
            PhraseElement resourceValue = lsPreProcessor.resourceValue(linkSpec);
            NPPhraseSpec theta = lsPreProcessor.theta(linkSpec);
            CoordinatedPhraseElement coordinate = lsPreProcessor.coordinate(linkSpec);
            String rightProp2 = lsPreProcessor.leftProperty(linkSpec);
            String leftProp2 = lsPreProcessor.rightProperty(linkSpec);
            //String rightProp2 = LSPreProcessor.leftProperty(linkSpecification);
            //String leftProp2 = LSPreProcessor.rightProperty(linkSpecification);
            double d = linkSpec.getThreshold();
            NPPhraseSpec firstSubject = linkSpecsSummery.subject(coordinate, rightProp2, leftProp2);
            String stringTheta = "";

            if (d == 1) {
                stringTheta = " Übereinstimmung";
                AdjPhraseSpec adjective = nlgFactory.createAdjectivePhrase("genaue");
                //adjective.setFeature(Feature.IS_COMPARATIVE, true);

                NPPhraseSpec subject = nlgFactory.createNounPhrase(stringTheta);
                subject.addModifier(adjective);
                String string = getString(nlgFactory, clause2, name, firstSubject, subject);
                allNLGElement.add(new Realiser(lexicon).realise(nlgFactory.createNounPhrase(string)));
                return string;

            }
            if (d == 0) {
                stringTheta = "Nichtübereinstimmung";
                AdjPhraseSpec adjective = nlgFactory.createAdjectivePhrase("vollständig");
                adjective.setFeature(Feature.IS_COMPARATIVE, true);

                NPPhraseSpec subject = nlgFactory.createNounPhrase(stringTheta);
                subject.addModifier(adjective);
                String string = getString(nlgFactory, clause2, name, firstSubject, subject);
                allNLGElement.add(new Realiser(lexicon).realise(nlgFactory.createNounPhrase(string)));
                return string;
            }
            if (d > 0 && d < 1) {
                Realiser clause2Realiser = new Realiser(lexicon);
                NLGElement thetaRealised = clause2Realiser.realise(theta);
                stringTheta = thetaRealised.toString();

                NPPhraseSpec subject = nlgFactory.createNounPhrase(stringTheta);
                String string = getString(nlgFactory, clause2, name, firstSubject, subject);
                allNLGElement.add(new Realiser(lexicon).realise(nlgFactory.createNounPhrase(string)));
                return string;
            }


        }
        return "";
    }

    private String getString(NLGFactory nlgFactory, SPhraseSpec clause2, NPPhraseSpec name, NPPhraseSpec firstSubject, NPPhraseSpec subject) {
        name.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
        subject.addComplement(name);
        clause2.setSubject(firstSubject);
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("haben");
        clause2.setVerb(verb);
        clause2.setObject(subject);
        Realiser clauseRealiser = new Realiser(lexicon);
        NLGElement clauseRealised = clauseRealiser.realise(clause2);
        return clauseRealised.toString();
    }

    public String realisng() {
        NLGFactory nlgFactory = new NLGFactory(lexicon);
        SPhraseSpec clause = nlgFactory.createClause();
        SPhraseSpec clause1 = nlgFactory.createClause();
        SPhraseSpec clause2 = nlgFactory.createClause();
        SPhraseSpec clause3 = nlgFactory.createClause();
        Realiser clause1Realiser = new Realiser(lexicon);
        String op = "";
        String verbalzation = "";
        for (int i = 0; i < allNLGElement.size(); i++) {

            String temp1 = "";
            String temp2 = "";
            String str = allNLGElement.get(i).getRealisation();
            //System.out.println("string: "+i+"   "+str);
            if (str.length() > 2 && str.contains("haben")) {
                VPPhraseSpec AuxVerb = nlgFactory.createVerbPhrase("passieren");
                //AuxVerb.setFeature(Feature.PASSIVE, true);
                clause.setSubject("Der Link");
                clause.setVerb(AuxVerb);
                //clause.setFeature(Feature.PASSIVE, true);
                temp1 = str.substring(0, str.lastIndexOf("haben"));
                NPPhraseSpec subject = nlgFactory.createNounPhrase(temp1);
                subject.setPlural(true);
                clause1.setSubject(subject);
                VPPhraseSpec verb = nlgFactory.createVerbPhrase("haben");
                clause1.setVerb(verb);
                temp1 = str.substring(str.lastIndexOf("haben") + 5, str.length());
                clause1.setObject(temp1);
                clause1.setFeature(Feature.COMPLEMENTISER, "wenn");
                clause.addComplement(clause1);
                NLGElement clause1Realised = clause1Realiser.realise(clause);
                clause = new SPhraseSpec(nlgFactory);
                //System.out.println("yy "+clause1Realised);
                verbalzation = clause1Realised.getRealisation();
                //System.out.println("verbal: "+verbalzation);
            }

            if (str.equals("und") || str.equals("oder")) {
                op = str;
                //System.out.println(op);
            }
            //verbalzation=verbalzation1+" "+str+" ";
            if (str.length() > 2 && str.contains("hat ")) {
                VPPhraseSpec AuxVerb = nlgFactory.createVerbPhrase("passieren");
                //SPhraseSpec clause3=nlgFactory.createClause();
                clause3.setSubject("Der Link");
                clause3.setVerb(AuxVerb);
                //clause3.setFeature(Feature.PASSIVE, true);
                temp2 = str.substring(0, str.lastIndexOf("hat"));
                NPPhraseSpec subject1 = nlgFactory.createNounPhrase(temp2);
                subject1.setFeature(Feature.NUMBER, NumberAgreement.SINGULAR);
                clause2.setSubject(subject1);
                VPPhraseSpec verb = nlgFactory.createVerbPhrase("haben");
                clause2.setVerb(verb);
                temp2 = str.substring(str.lastIndexOf("hat") + 3);
                clause2.setObject(temp2);
                clause2.setFeature(Feature.COMPLEMENTISER, "wenn");
                clause3.addComplement(clause2);
                NLGElement clause1Realised = clause1Realiser.realise(clause3);
                clause3 = new SPhraseSpec(nlgFactory);
                //System.out.println("xxx "+clause1Realised);
                verbalzation = verbalzation + " " + op + " " + clause1Realised.getRealisation();
                verbalzation = verbalzation.trim();
                verbalzation = verbalzation.substring(0, 1).toUpperCase() + verbalzation.substring(1);

            }
            //System.out.println(str);
            //return verbalzation;
        }
        //System.out.println("verbalzation: "+verbalzation);
        return verbalzation;

    }


}

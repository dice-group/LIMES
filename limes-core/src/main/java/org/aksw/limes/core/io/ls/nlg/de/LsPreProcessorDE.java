package org.aksw.limes.core.io.ls.nlg.de;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.ls.nlg.ALSPreprocessor;
import simplenlg.features.Feature;
import simplenlgde.features.DiscourseFunction;
import simplenlgde.features.InternalFeature;
import simplenlgde.framework.CoordinatedPhraseElement;
import simplenlgde.framework.NLGFactory;
import simplenlgde.framework.PhraseElement;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.phrasespec.NPPhraseSpec;
import simplenlgde.phrasespec.PPPhraseSpec;

public class LsPreProcessorDE extends ALSPreprocessor {

    protected Lexicon lexicon = Lexicon.getDefaultLexicon();//= new XMLLexicon();
    protected NLGFactory nlgFactory = new NLGFactory(lexicon);

    public NPPhraseSpec atomicMeasure(LinkSpecification linkSpec) {
        String atomicMeasureString = linkSpec.getAtomicMeasure();
        return nlgFactory.createNounPhrase(atomicMeasureString);
    }

    public NPPhraseSpec atomicSimilarity(LinkSpecification linkSpec) {
        String atomicMeasureString = linkSpec.getAtomicMeasure();
        atomicMeasureString = makeFirstCharUppercaseRestLowercase(atomicMeasureString);

        NPPhraseSpec n2 = nlgFactory.createNounPhrase(atomicMeasureString);
        NPPhraseSpec similarity = nlgFactory.createNounPhrase(" Ähnlichkeit");
        n2.setDeterminer("eine");
        //n2.setFeature(Feature.NUMBER,NumberAgreement.SINGULAR);
        //n2.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
        n2.addPostModifier(similarity);

        return n2;
    }

    public CoordinatedPhraseElement coordinate(LinkSpecification linkSpec) {
        String leftProp = leftProperty(linkSpec);
        String rightProp = rightProperty(linkSpec);

        PhraseElement leftPValue = nlgFactory.createNounPhrase(leftProp);
        PhraseElement leftP = nlgFactory.createNounPhrase("die Ressource");
        leftPValue.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
        leftP.addComplement(leftPValue);
        PhraseElement Datenquelle = nlgFactory.createNounPhrase("die Datenquelle");
        Datenquelle.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
        leftP.addComplement(Datenquelle);

        NPPhraseSpec rightPValue = nlgFactory.createNounPhrase(rightProp);
        PhraseElement rightP = nlgFactory.createNounPhrase("die Ressource");
        rightPValue.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
        rightP.addComplement(rightPValue);
        PhraseElement Datenziel = nlgFactory.createNounPhrase("das Datenziel");
        Datenziel.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
        rightP.addComplement(Datenziel);
        CoordinatedPhraseElement coordinate_1 = nlgFactory.createCoordinatedPhrase();
        coordinate_1.addCoordinate(leftP);
        coordinate_1.addCoordinate(rightP);
        return coordinate_1;
    }

    public NPPhraseSpec theta(LinkSpecification linkSpec) {
        double d = linkSpec.getThreshold() * 100;
        d = Math.round(d * 100 / 100);
        int dAsInteger = (int) d;

        NPPhraseSpec theta = nlgFactory.createNounPhrase(dAsInteger + "%");

        PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
        pp.addComplement(theta);
        pp.setPreposition("von");
        return theta;
    }

    public NPPhraseSpec resourceValue(LinkSpecification linkSpec) {
        String function = linkSpec.getMeasure().substring(0,linkSpec.getMeasure().indexOf("("));
        NPPhraseSpec resource = nlgFactory.createNounPhrase("Ressource");
        resource.setFeature(Feature.POSSESSIVE, true);
        NPPhraseSpec resourceValue = nlgFactory.createNounPhrase(function);
        return resourceValue;
    }


}


package org.aksw.limes.core.io.ls.nlg.en;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.ls.nlg.ALSPreprocessor;
import simplenlg.features.Feature;
import simplenlg.features.InternalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;

public class LsPreProcessorEN extends ALSPreprocessor {

    protected Lexicon lexicon = new XMLLexicon();
    protected NLGFactory nlgFactory = new NLGFactory(lexicon);

    public NPPhraseSpec atomicMeasure(LinkSpecification linkSpec) {
        String atomicMeasureString = linkSpec.getAtomicMeasure();
        NPPhraseSpec n1 = nlgFactory.createNounPhrase(atomicMeasureString);

        return n1;
    }

    public NPPhraseSpec atomicSimilarity(LinkSpecification linkSpec) {

        String atomicMeasureString = linkSpec.getAtomicMeasure();
        atomicMeasureString = makeFirstCharUppercaseRestLowercase(atomicMeasureString);

        NPPhraseSpec n2 = nlgFactory.createNounPhrase(atomicMeasureString);
        NPPhraseSpec similarity = nlgFactory.createNounPhrase(" similarity");
        n2.setDeterminer("a");
        n2.addPostModifier(similarity);

        return n2;
    }


    public CoordinatedPhraseElement coordinate(LinkSpecification linkSpec) {
        String leftProp = leftProperty(linkSpec);
        String rightProp = rightProperty(linkSpec);

        PhraseElement leftP = nlgFactory.createNounPhrase("the", "resource");
        leftP.setFeature(Feature.POSSESSIVE, true);
        PhraseElement leftPValue = nlgFactory.createNounPhrase(leftProp);
        leftPValue.setFeature(InternalFeature.SPECIFIER, leftP);
        leftPValue.addComplement("of the source");
        //PhraseElement leftSource=nlgFactory.createNounPhrase("the","resource");
        PhraseElement rightP = nlgFactory.createNounPhrase("the", "resource");
        rightP.setFeature(Feature.POSSESSIVE, true);
        PhraseElement rightPValue = nlgFactory.createNounPhrase(rightProp);
        rightPValue.setFeature(InternalFeature.SPECIFIER, rightP);
        rightPValue.addComplement("of the target");
        CoordinatedPhraseElement coordinate_1 = nlgFactory.createCoordinatedPhrase(leftPValue, rightPValue);

        return coordinate_1;
    }

    public NPPhraseSpec theta(LinkSpecification linkSpec) {
        double d = linkSpec.getThreshold() * 100;
        d = Math.round(d * 100 / 100);
        int dAsInteger = (int) d;

        NPPhraseSpec theta = nlgFactory.createNounPhrase(dAsInteger + "%");

        PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
        pp.addComplement(theta);
        pp.setPreposition("of");
        return theta;
    }

    public NPPhraseSpec resourceValue(LinkSpecification linkSpec) {
        String function = linkSpec.getMeasure().substring(0,linkSpec.getMeasure().indexOf("("));
        NPPhraseSpec resource = nlgFactory.createNounPhrase("resource");
        resource.setFeature(Feature.POSSESSIVE, true);
        NPPhraseSpec resourceValue = nlgFactory.createNounPhrase(function);
        return resourceValue;
    }

}


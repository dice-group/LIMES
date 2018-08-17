package org.aksw.limes.core.ml.algorithm.NLGLS;

    import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;


 public class NLGTest  {

        public static void main(String[] args) {
                Lexicon lexicon = Lexicon.getDefaultLexicon();
                NLGFactory nlgFactory = new NLGFactory(lexicon);
                Realiser realiser = new Realiser(lexicon);
                NLGElement s1 = nlgFactory.createSentence("my dog is happy");
                String output = realiser.realiseSentence(s1);
                System.out.println(output);
                

        }

}

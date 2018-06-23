package org.aksw.limes.core.measures.measure.string.bilang;

import java.util.ArrayList;
import java.util.HashMap;
import org.aksw.limes.core.measures.measure.AMeasure;
import org.aksw.limes.core.measures.measure.string.AStringMeasure;
import org.aksw.limes.core.measures.measure.string.SimpleEditDistanceMeasure;

/**
 * A very simple syntactical bilingual string measure based on a dictionary:
 */
public class SimpleDictionaryMeasure extends AStringMeasure {
    
    
    private BilangDictionary dictionary;
    private AMeasure innerMeasure = new SimpleEditDistanceMeasure();
    
    public SimpleDictionaryMeasure(BilangDictionary dictionary) {
        this.dictionary = dictionary;
    }
    
    @Override
    public int getPrefixLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public int getMidLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public double getSizeFilteringThreshold(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public int getAlpha(int xTokensNumber, int yTokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public double getSimilarity(int overlap, int lengthA, int lengthB) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public boolean computableViaOverlap() {
        return false;
    }
    
    /**
     * iterates through all possible translations and uses innerMeasure to determine the similarity
     * as the product of the inner similarity from the input word to the e.g. english word,
     * multiplied by the inner similarity of the e.g. german translation of that word to the output word
     *
     * @param object1, the first word (a string as Object)
     * @param object2, the second word (a string as Object)
     * @return best translation similarity
     */
    @Override
    public double getSimilarity(Object object1, Object object2) {
        String input = ("" + object1).toLowerCase();
        String output = ("" + object2).toLowerCase();
        double bestSimilarity = 0.0;
        HashMap<String, ArrayList<String>> map = dictionary.getSource2TargetMap();
        for (String sourceWord : map.keySet()) {
            for (String targetWord : map.get(sourceWord)) {
                double similarity = innerMeasure.getSimilarity(input, sourceWord) *
                    innerMeasure.getSimilarity(targetWord, output);
                if (similarity > bestSimilarity) {
                    bestSimilarity = similarity;
                }
            }
        }
        return bestSimilarity;
    }
    
    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }
    
    @Override
    public String getName() {
        return "simple_dictionary";
    }
}

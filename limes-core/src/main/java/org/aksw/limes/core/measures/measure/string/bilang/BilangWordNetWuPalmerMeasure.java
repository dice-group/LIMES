package org.aksw.limes.core.measures.measure.string.bilang;

import java.util.ArrayList;
import org.aksw.limes.core.measures.measure.string.AStringMeasure;

/**
 * A bilingual semantic string similarity measure based on the Wu-Palmer similarity measure in English:
 * Given any dictionary from English to another language (e.g. German), and two words to compare,
 * this measure looks at all English translations of the word of the other language, computes the Wu-Palmer similarity
 * between them and the given English word, and returns the best similarity value of those comparisons.
 *
 * @author Swante Scholz
 */
public class BilangWordNetWuPalmerMeasure extends AStringMeasure {
    
    private WordNetInterface wnInterface;
    private BilangDictionary englishDictionary;
    
    /**
     * Creates an instance of this measure with the desired WordNetInterface and dictionary.
     *
     * @param wnInterface the WordNetInterface instance, in order to compute Wu-Palmer similarity in English
     * @param englishDictionary a dictionary from english to any other language
     */
    public BilangWordNetWuPalmerMeasure(WordNetInterface wnInterface,
        BilangDictionary englishDictionary) {
        this.wnInterface = wnInterface;
        this.englishDictionary = englishDictionary;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Computes the similarity between the two given words.
     *
     * @param englishWord a word in english
     * @param wordOfOtherLanguageInDictionary a word in the other language of the dictionary
     * @return the maximum Wu-Palmer similarity value between the {@code englishWord} and translations
     * of the other word
     */
    private double getSimilarity(String englishWord, String wordOfOtherLanguageInDictionary) {
        double maxSimilarity = 0.0;
        for (String englishTranslation : englishDictionary.getTarget2SourceMap().getOrDefault(
            wordOfOtherLanguageInDictionary, new ArrayList<>())) {
            double similarity = wnInterface
                .computeWuPalmerSimilarity(englishWord, englishTranslation);
            maxSimilarity = Math.max(maxSimilarity, similarity);
        }
        return maxSimilarity;
    }
    
    /**
     * @param object1, the first word
     * @param object2, the second word
     * @return the similarity between these words, as described above. The order (english and then
     * the other language, or the other way around) is irrelevant. Both possible orders are
     * evaluated and the maximum value is returned.
     */
    @Override
    public double getSimilarity(Object object1, Object object2) {
        String a = ("" + object1).toLowerCase();
        String b = ("" + object2).toLowerCase();
        return Math.max(getSimilarity(a, b), getSimilarity(b, a));
    }
    
    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }
    
    @Override
    public String getName() {
        return "wu_palmer";
    }
}

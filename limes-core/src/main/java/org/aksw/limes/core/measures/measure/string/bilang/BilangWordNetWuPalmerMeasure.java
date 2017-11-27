package org.aksw.limes.core.measures.measure.string.bilang;

import java.util.ArrayList;
import org.aksw.limes.core.measures.measure.string.AStringMeasure;

public class BilangWordNetWuPalmerMeasure extends AStringMeasure {

  private WordNetInterface wnInterface;
  private BilangDictionary englishDictionary;

  public BilangWordNetWuPalmerMeasure(WordNetInterface wnInterface, BilangDictionary englishDictionary) {
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

  private double getSimilarity(String englishWord, String wordOfOtherLanguageInDictionary) {
    double maxSimilarity = 0.0;
    for (String englishTranslation : englishDictionary.getTarget2sourceMap().getOrDefault(
        wordOfOtherLanguageInDictionary, new ArrayList<>())) {
      double similarity = wnInterface.getSimilarity(englishWord, englishTranslation);
      maxSimilarity = Math.max(maxSimilarity, similarity);
    }
    return maxSimilarity;
  }

  @Override
  public double getSimilarity(Object object1, Object object2) {
    String a = ("" + object1).toLowerCase();
    String b = ("" + object2).toLowerCase();
    return Math.max(getSimilarity(a, b), getSimilarity(b, a));
  }

  @Override
  public double getRuntimeApproximation(double mappingSize) {
    return 1000.d;
  }

  @Override
  public String getName() {
    throw new UnsupportedOperationException("wu_palmer");
  }
}

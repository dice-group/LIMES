package org.aksw.limes.core.measures.measure.string.bilang;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.AMeasure;
import org.aksw.limes.core.measures.measure.string.AStringMeasure;

public class SimpleDictionaryMeasure extends AStringMeasure {


  private Dictionary dictionary;

  public SimpleDictionaryMeasure(Dictionary dictionary) {

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

  private double proximity(String source, String target) {
    if (dictionary.key)
  }

  @Override
  public double getSimilarity(Object object1, Object object2) {
    String a = "" + object1;
    String b = "" + object2;
    return Math.min(proximity(a,b), proximity(b,a));
  }

  @Override
  public double getRuntimeApproximation(double mappingSize) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getName() {
    return "simpleDictionary";
  }
}

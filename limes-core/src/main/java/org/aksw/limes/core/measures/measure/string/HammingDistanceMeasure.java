package org.aksw.limes.core.measures.measure.string;

import org.aksw.limes.core.io.cache.Instance;

public class HammingDistanceMeasure extends AStringMeasure {

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
  public double getSimilarity(Object object1, Object object2) {
    String s1 = object1 + "";
    String s2 = object2 + "";
    if (s1.length() != s2.length()) {
      throw new IllegalArgumentException("arguments must be of same length");
    }
    if (s1.length() == 0) {
      return 1.0;
    }
    int numberOfDifferences = 0;
    for (int i = 0; i < s1.length(); i++) {
      if (s1.charAt(i) != s2.charAt(i)) {
        numberOfDifferences++;
      }
    }
    return 1.0 - numberOfDifferences / (double) s1.length();
  }

  @Override
  public String getName() {
    return "hamming";
  }

  @Override
  public boolean computableViaOverlap() {
    return false;
  }

  @Override
  public double getRuntimeApproximation(double mappingSize) {
    return mappingSize / 1000d;
  }

}

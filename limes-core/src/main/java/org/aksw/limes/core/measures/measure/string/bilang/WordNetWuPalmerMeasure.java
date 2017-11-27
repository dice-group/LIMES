package org.aksw.limes.core.measures.measure.string.bilang;

import org.aksw.limes.core.measures.measure.string.AStringMeasure;

public class WordNetWuPalmerMeasure extends AStringMeasure {

  private WordNetInterface wnInterface;

  public WordNetWuPalmerMeasure(WordNetInterface wnInterface) {

    this.wnInterface = wnInterface;
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

  @Override
  public double getSimilarity(Object object1, Object object2) {
    return wnInterface.getSimilarity(""+object1, ""+object2);
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

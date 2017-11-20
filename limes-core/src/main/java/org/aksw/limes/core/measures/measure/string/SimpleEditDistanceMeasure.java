package org.aksw.limes.core.measures.measure.string;

import static org.apache.commons.lang3.math.NumberUtils.min;
import static uk.ac.shef.wit.simmetrics.math.MathFuncs.min3;

import org.aksw.limes.core.io.cache.Instance;
import org.apache.commons.lang3.math.NumberUtils;

public class SimpleEditDistanceMeasure extends AStringMeasure {

  private final int matchingCost;
  private final int insertionCost;
  private final int deletionCost;
  private final int substitutionCost;

  public SimpleEditDistanceMeasure() {
    this(0,1,1,1);
  }

  public SimpleEditDistanceMeasure(int matchingCost, int insertionCost, int deletionCost, int substitutionCost) {
    this.matchingCost = matchingCost;
    this.insertionCost = insertionCost;
    this.deletionCost = deletionCost;
    this.substitutionCost = substitutionCost;
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

  public int getWorstCaseCost(int length1, int length2) {
    int min = Math.min(length1, length2);
    int result = min * Math.min(insertionCost+deletionCost, Math.max(matchingCost, substitutionCost));
    int lengthDifference = length1 - length2;
    if (lengthDifference > 0) {
      result += lengthDifference * deletionCost;
    } else {
      result += -lengthDifference * insertionCost;
    }
    return result;
  }

  @Override
  public double getSimilarity(Object object1, Object object2) {
    String s1 = object1 + "";
    String s2 = object2 + "";
    if (s1.isEmpty() && s2.isEmpty()) {
      return 1.0;
    }
    int length1 = s1.length(), length2 = s2.length();
    int[] previousRow = new int[length1 + 1];
    for (int i = 0; i <= length1; i++) {
      previousRow[i] = i * deletionCost;
    }
    for (int y = 0; y < length2; y++) {
      int[] currentRow = new int[length1+1];
      currentRow[0] = (y + 1) * insertionCost;
      char c2 = s2.charAt(y);
      for (int x = 0; x < length1; x++) {
        char c1 = s1.charAt(x);
        if (c1 == c2) {
          currentRow[x+1] = previousRow[x];
        } else {
          int matchingSubstitutionCost = matchingCost;
          if (s1.charAt(x) != s2.charAt(y)) {
            matchingSubstitutionCost = substitutionCost;
          }
          currentRow[x+1] = NumberUtils.min(previousRow[x] + matchingSubstitutionCost,
              previousRow[x+1] + insertionCost,
              currentRow[x] + deletionCost);
        }
      }
      previousRow = currentRow;
    }
    return 1.0 - previousRow[length1] / (double) getWorstCaseCost(length1, length2);
  }

  @Override
  public String getName() {
    return "simple-edit-distance";
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

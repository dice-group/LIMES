package org.aksw.limes.core.measures.measure.string;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * A rather simple edit distance measure implementation.
 * In contrast to the LevenshteinMeasure, this class allows for different cost for the four
 * possible kinds of operations (match, insert, delete, substitute)
 */
public class SimpleEditDistanceMeasure extends AStringMeasure {

  private final int matchingCost;
  private final int insertionCost;
  private final int deletionCost;
  private final int substitutionCost;

  /**
   * Creates a levensthein-like edit distance measure (insert/delete/substiture cost 1, match cost 0)
   */
  public SimpleEditDistanceMeasure() {
    this(0, 1, 1, 1);
  }

  /**
   * Creates a edit distance measure with the desired cost values.
   */
  public SimpleEditDistanceMeasure(int matchingCost, int insertionCost, int deletionCost,
      int substitutionCost) {
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

  /**
   * returns the worst possible edit distance for two strings of the given lengths
   *
   * @param length1 length of the first string
   * @param length2 length of the second string
   */
  public int getWorstCaseCost(int length1, int length2) {
    int min = Math.min(length1, length2);
    int result =
        min * Math.min(insertionCost + deletionCost, Math.max(matchingCost, substitutionCost));
    int lengthDifference = length1 - length2;
    if (lengthDifference > 0) {
      result += lengthDifference * deletionCost;
    } else {
      result += -lengthDifference * insertionCost;
    }
    return result;
  }

  /**
   * @param object1, the first string (as Object)
   * @param object2, the second string (as Object)
   * @return the similarity of of the two strings, which is one minus the edit distance
   * between them, normalized by the worst case edit cost that could have been for strings
   * of the same lengths.
   */
  @Override
  public double getSimilarity(Object object1, Object object2) {
    String s1 = object1 + "";
    String s2 = object2 + "";
    if (s1.isEmpty() && s2.isEmpty()) {
      return 1.0;
    }
    int edDistance = computeEditDistance(s1, s2);
    return 1.0 - edDistance / (double) getWorstCaseCost(s1.length(), s2.length());
  }

  public static int computeEditDistance(String s1, String s2) {
    return computeEditDistance(s1, s2, 0, 1, 1, 1);
  }

  public static int computeEditDistance(String s1, String s2, int matchingCost, int insertionCost,
      int deletionCost, int substitutionCost) {
    int length1 = s1.length(), length2 = s2.length();
    int[] previousRow = new int[length1 + 1];
    for (int i = 0; i <= length1; i++) {
      previousRow[i] = i * deletionCost;
    }
    for (int y = 0; y < length2; y++) {
      int[] currentRow = new int[length1 + 1];
      currentRow[0] = (y + 1) * insertionCost;
      char c2 = s2.charAt(y);
      for (int x = 0; x < length1; x++) {
        char c1 = s1.charAt(x);
        if (c1 == c2) {
          currentRow[x + 1] = previousRow[x];
        } else {
          int matchingSubstitutionCost = matchingCost;
          if (s1.charAt(x) != s2.charAt(y)) {
            matchingSubstitutionCost = substitutionCost;
          }
          currentRow[x + 1] = NumberUtils.min(previousRow[x] + matchingSubstitutionCost,
              previousRow[x + 1] + insertionCost,
              currentRow[x] + deletionCost);
        }
      }
      previousRow = currentRow;
    }
    return previousRow[length1];
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

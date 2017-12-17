package org.aksw.limes.core.measures.measure.string;

import java.util.Collection;

/**
 * Base class for any kind of Dictionary (in particular the BilangDictionary).
 * It implements Collection<String> in order to allow for iterating through all the entries in it.
 * It has a static Default dictionary that can be accessed from anywhere after it has been set once.
 */
public abstract class ADictionary implements Iterable<String> {

  private static ADictionary DEFAULT = null;

  public static void setDefaultDictionary(ADictionary defaultDictionary) {
    if (DEFAULT != null) {
      throw new RuntimeException("Default dictionary has already been set.");
    }
    DEFAULT = defaultDictionary;
  }
  
  public static ADictionary getDefaultDictionary() {
    if (DEFAULT == null) {
      throw new RuntimeException("Default dictionary before it has been set.");
    }
    return DEFAULT;
  }

  /**
   * @param potentiallyMisspelledWord the word to be spelling corrected
   * @return the entry of this dictionary that has the lowest edit distance to the given word
   */
  public String correctSpelling(String potentiallyMisspelledWord) {
    int bestEdDistance = Integer.MAX_VALUE;
    String bestDictionaryEntry = "";
    for (String dictionaryEntry : this) {
        int editDistance = SimpleEditDistanceMeasure.computeEditDistance(potentiallyMisspelledWord, dictionaryEntry);
        if (editDistance < bestEdDistance) {
          bestEdDistance = editDistance;
          bestDictionaryEntry = dictionaryEntry;
        }
    }
    return bestDictionaryEntry;
  }

}

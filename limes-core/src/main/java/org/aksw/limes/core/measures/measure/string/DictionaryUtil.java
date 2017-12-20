package org.aksw.limes.core.measures.measure.string;

import static com.google.common.primitives.Ints.min;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class DictionaryUtil {

  private static DictionaryUtil instance;

  public static void initInstance(WordFrequencies wordFrequencies) {
    if (instance != null) {
      throw new RuntimeException("instance has already been initialized.");
    }
    instance = new DictionaryUtil(wordFrequencies);
  }

  public static DictionaryUtil getInstance() {
    if (instance == null) {
      throw new RuntimeException("trying to access instance before it has been set.");
    }
    return instance;
  }

  public static final int MAX_EDIT_DISTANCE = 2;
  /**
   * maps (potentially) misspelled strings to the possible roots within MAX_EDIT_DISTANCE
   */
  private HashMap<String, ArrayList<String>> rootsForStrings = new HashMap<>();
  /**
   * maps all true words to their frequencies (= prior probabilities)
   */
  private final WordFrequencies wordFrequencies;
  private int longestWordLength = 0;

  private DictionaryUtil(WordFrequencies wordFrequencies) {
    this.wordFrequencies = wordFrequencies;
    for (String word : wordFrequencies.keySet()) {
      addDeletesForWordToDictionary(word);
    }
  }

  /**
   * @return derive strings with up to MAX_EDIT_DISTANCE characters deleted, and returns
   * them as set. word itself is not part of that set.
   */
  private static Set<String> getDeletesSet(String word) {
    Set<String> deletes = new HashSet<>();
    ArrayList<String> queue = new ArrayList<>();
    queue.add(word);
    for (int d = 0; d < MAX_EDIT_DISTANCE; d++) {
      Set<String> tmpQueue = new HashSet<>();
      for (String qItem : queue) {
        if (qItem.length() == 1) {
          continue;
        }
        for (int c = 0; c < qItem.length(); c++) {
          String word_minus_c = qItem.substring(0, c) + qItem.substring(c+1);
          deletes.add(word_minus_c);
          tmpQueue.add(word_minus_c);

        }
      }
      queue.clear();
      queue.addAll(tmpQueue);
    }
    return deletes;
  }

  /**
   * add the root for word and its derived deletions to dictionary
   *
   * @param word the word to be added to the dictionary
   */
  private void addDeletesForWordToDictionary(String word) {
    longestWordLength = Math.max(longestWordLength, word.length());
    rootsForStrings.put(word, new ArrayList<>(1));
    rootsForStrings.get(word).add(word);

    Set<String> deletes = getDeletesSet(word);
    for (String item : deletes) {
      if (!rootsForStrings.containsKey(item)) {
        rootsForStrings.put(item, new ArrayList<>(1));
      }
      rootsForStrings.get(item).add(word);
    }
  }

  /**
   * @return the Damerau-Levenshtein distance between the two given words.
   */
  public static int damerauLevenshteinDistance(String word1, String word2) {
    if (word1 == null) {
      throw new NullPointerException("s1 must not be null");
    }

    if (word2 == null) {
      throw new NullPointerException("s2 must not be null");
    }
    if (word1.equals(word2)) {
      return 0;
    }
    int inf = word1.length() + word2.length();
    HashMap<Character, Integer> da = new HashMap<>();

    for (int d = 0; d < word1.length(); d++) {
      da.put(word1.charAt(d), 0);
    }

    for (int d = 0; d < word2.length(); d++) {
      da.put(word2.charAt(d), 0);
    }
    int[][] m = new int[word1.length() + 2][word2.length() + 2];
    for (int i = 0; i <= word1.length(); i++) {
      m[i + 1][0] = inf;
      m[i + 1][1] = i;
    }

    for (int j = 0; j <= word2.length(); j++) {
      m[0][j + 1] = inf;
      m[1][j + 1] = j;

    }
    for (int i = 1; i <= word1.length(); i++) {
      int db = 0;
      for (int j = 1; j <= word2.length(); j++) {
        int i1 = da.get(word2.charAt(j - 1));
        int j1 = db;

        int cost = 1;
        if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
          cost = 0;
          db = j;
        }
        m[i + 1][j + 1] = min(
            m[i][j] + cost, // substitution
            m[i + 1][j] + 1, // insertion
            m[i][j + 1] + 1, // deletion
            m[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1));
      }
      da.put(word1.charAt(i - 1), i);
    }

    return m[word1.length() + 1][word2.length() + 1];
  }

  /**
   * words with lower edit distance to inputWord are preferred over words with higher distance,
   * independent of their prior probabilities
   * between candidates with the same edit distance to the inputWord, the one with the highest
   * frequence (= prior probability) is chosen
   *
   * @param inputWord potentially incorrectly spelled word
   * @return corrected word for inputWord
   */
  public String correctSpelling(String inputWord) {
    if (wordFrequencies.containsWord(inputWord)) {
      return inputWord; // distance of 0 always has precedence
    }
    int inputWordLength = inputWord.length();
    if (inputWordLength - longestWordLength > MAX_EDIT_DISTANCE) {
      return inputWord; // given word is too long to be corrected with the allowed number of operations
    }

    String bestSuggestion = inputWord;
    double bestSuggestionDistance = Double.MAX_VALUE; // DamerauLevenshtein + prior probability of word

    ArrayDeque<String> stringsToBeChecked = new ArrayDeque<>();
    stringsToBeChecked.add(inputWord);
    HashSet<String> checkedStrings = new HashSet<>();

    while (stringsToBeChecked.size() > 0) {
      String currentString = stringsToBeChecked.pollFirst();
      if (inputWordLength - currentString.length() > bestSuggestionDistance) {
        break;
      }

      if (rootsForStrings.containsKey(currentString)) {
        // currentString can be transformed to a true word
        if (wordFrequencies.containsWord(currentString)) {
          // currentString is already a true word
          // distance is just the number of deletes so far plus word frequency
          double suggestionDistance =
              inputWordLength - currentString.length() + wordFrequencies.get(currentString);
          if (suggestionDistance < bestSuggestionDistance) { // new best suggestion found
            bestSuggestionDistance = suggestionDistance;
            bestSuggestion = currentString;
          }
        }

        // iterate through possible root words for currentString
        for (String rootCandidate : rootsForStrings.get(currentString)) {
          double suggestionDistance = rootCandidate.length() - currentString.length();
          if (currentString.length() != inputWordLength) {
            // there might be deletes from both sides, so we have to compute the distance
            // manually to know the definite number of required edit operations
            suggestionDistance = damerauLevenshteinDistance(rootCandidate, inputWord);
          }
          suggestionDistance += wordFrequencies.get(rootCandidate);
          if (suggestionDistance < bestSuggestionDistance) {
            bestSuggestionDistance = suggestionDistance;
            bestSuggestion = rootCandidate;
          }
        }
      }
      int lenDiff = inputWordLength - currentString.length();
      if (lenDiff <= bestSuggestionDistance && lenDiff < MAX_EDIT_DISTANCE
          && currentString.length() > 1) {
        for (int c = 0; c < currentString.length(); c++) {
          String child = currentString.substring(0, c) + currentString.substring(c + 1);
          if (!checkedStrings.contains(child)) {
            stringsToBeChecked.add(child);
            checkedStrings.add(child);
          }
        }
      }
    }
    return bestSuggestion;
  }
}
package org.aksw.limes.core.measures.measure.string;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;
import org.apache.jena.tdb.store.Hash;

public class WordFrequencies {
  private HashMap<String, Double> wordFrequencies = new HashMap<>();
  public WordFrequencies(HashMap<String, Double> wordFrequencies) {
    this.wordFrequencies.putAll(wordFrequencies);
  }

  /**
   * scales all frequencies so that they sum up to 1.0
   */
  public void normalizeFrequencies() {
    double totalFrequency = wordFrequencies.values().stream().mapToDouble(Double::doubleValue).sum();
    wordFrequencies.entrySet().forEach(it -> it.setValue(it.getValue() / totalFrequency));
  }

  /**
   * @param wordFrequenciesFile file should have two columns, separated by a space, word first,
   * then frequency (as int or double)
   * @return WordFrequencies instance based on these frequencies
   */
  public static WordFrequencies fromWordFrequencyFile(Path wordFrequenciesFile) {
    try {
      HashMap<String, Double> map = new HashMap<>();
      Files.lines(wordFrequenciesFile).forEach(line -> {
        if (line.isEmpty()) {
          return;
        }
        String[] parts = line.split(" ");
        if (parts.length != 2) {
          throw new RuntimeException("Invalid file format.");
        }
        String word = parts[0];
        double frequency = Double.parseDouble(parts[1]);
        map.put(word, frequency);
      });
      return new WordFrequencies(map);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Wasn't able to read frequency file.");
    }
  }

  /**
   * @param other
   * @return new WordFrequencies instance, the result of a merge of this and the other instance,
   * normalized
   */
  public WordFrequencies merge(WordFrequencies other) {
    HashMap<String, Double> resultMap = new HashMap<>();
    resultMap.putAll(this.wordFrequencies);
    resultMap.putAll(other.wordFrequencies);
    WordFrequencies result = new WordFrequencies(resultMap);
    result.normalizeFrequencies();
    return result;
  }

  public Set<String> keySet() {
    return wordFrequencies.keySet();
  }

  public boolean containsKey(String word) {
    return wordFrequencies.containsKey(word);
  }
}

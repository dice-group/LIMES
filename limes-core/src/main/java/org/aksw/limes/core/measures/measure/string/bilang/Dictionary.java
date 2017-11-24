package org.aksw.limes.core.measures.measure.string.bilang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Dictionary for translations from a source language to a target language
 */
public class Dictionary {

  private HashMap<String, ArrayList<String>> source2target = new HashMap<>();
  private HashMap<String, ArrayList<String>> target2source = new HashMap<>();

  public static Path DEFAULT_DICTIONARY_PATH = Paths.get("src/test/resources/en-de-small.txt");

  public Dictionary() {

  }

  public Dictionary(Path path) {
    readDictionaryFile(path);
  }

  public void readDictionaryFile(Path path) {
    try {
      Stream<String> lines = Files.lines(path);
      lines.forEach(line -> {
        String[] parts = line.trim().split("\t");
        if (parts.length > 1) {
          addTranslation(parts[0], parts[1]);
        }
      });
      lines.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void addTranslation(String sourceWord, String targetWord) {
    if (!source2target.containsKey(sourceWord)) {
      source2target.put(sourceWord, new ArrayList<>(1));
    }
    source2target.get(sourceWord).add(targetWord);
    if (!target2source.containsKey(targetWord)) {
      target2source.put(targetWord, new ArrayList<>(1));
    }
    target2source.get(targetWord).add(sourceWord);
  }

  public int sourceSize() {
    return source2target.size();
  }

  public int targetSize() {
    return target2source.size();
  }
}

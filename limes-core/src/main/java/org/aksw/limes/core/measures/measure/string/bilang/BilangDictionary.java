package org.aksw.limes.core.measures.measure.string.bilang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Dictionary for translations from a source language to a target language.
 * Two maps, one for each translation direction, are kept.
 */
public class BilangDictionary {

  private HashMap<String, ArrayList<String>> source2target = new HashMap<>();
  private HashMap<String, ArrayList<String>> target2source = new HashMap<>();


  /**
   * Default path to the dictionary file
   */
  public static Path DEFAULT_DICTIONARY_PATH = Paths.get("src/test/resources/en-de-small.txt");

  /**
   * Creates an empty dictionary
   */
  public BilangDictionary() {

  }

  /**
   * Creates empty dictionary, then reads the given dictionary file.
   * @param pathToDictionary the path to the dictionary file.
   */
  public BilangDictionary(Path pathToDictionary) {
    readDictionaryFile(pathToDictionary);
  }

  /**
   * Reads the given dictionary file and adds all of its contents to any existing entries.
   * @param pathToDictionary the path to the dictionary file
   */
  public void readDictionaryFile(Path pathToDictionary) {
    try {
      Stream<String> lines = Files.lines(pathToDictionary);
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

  /**
   * Adds a translation to this directory.
   * @param sourceWord the word of the original language
   * @param targetWord the translated word in the other language
   */
  public void addTranslation(String sourceWord, String targetWord) {
    sourceWord = sourceWord.toLowerCase();
    targetWord = targetWord.toLowerCase();
    if (!source2target.containsKey(sourceWord)) {
      source2target.put(sourceWord, new ArrayList<>(1));
    }
    source2target.get(sourceWord).add(targetWord);
    if (!target2source.containsKey(targetWord)) {
      target2source.put(targetWord, new ArrayList<>(1));
    }
    target2source.get(targetWord).add(sourceWord);
  }

  /**
   * @return the number of words in this dictionary that are of the original language
   */
  public int sourceSize() {
    return source2target.size();
  }

  /**
   * @return the number of words in this dictionary that are of the original language
   */
  public int targetSize() {
    return target2source.size();
  }

  /**
   * @return the HashMap from words the original language to an ArrayList of the
   *   possible translations of that word in the other language.
   */
  public HashMap<String, ArrayList<String>> getSource2TargetMap() {
    return source2target;
  }

  /**
   * @return the HashMap from words the other language to an ArrayList of the
   *   possible translations of that word in the original language.
   */
  public HashMap<String, ArrayList<String>> getTarget2SourceMap() {
    return target2source;
  }


}

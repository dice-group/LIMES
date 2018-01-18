package org.aksw.limes.core.measures.measure.string.bilang;

import static org.apache.jena.system.JenaSystem.forEach;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.aksw.limes.core.measures.measure.string.bilang.WordEmbeddings.Vectord;
import org.apache.commons.math3.util.Pair;
import org.junit.Before;
import org.junit.Test;

public class WordEmbeddingsTest {

  WordEmbeddings we = new WordEmbeddings("src/test/resources/unsup.128");

  @Test
  public void testSimilarity() {
    double eps = 0.00001;
    System.out.println(we.getWordVector("Fußball"));
    System.out.println(we.getCosineSimilarityForWords("football", "fußball"));
    System.out.println(we.getCosineSimilarityForWords("hund", "cat") + " " + we
        .getCosineSimilarityForWords("katze", "dog"));
    System.out.println(we.getCosineSimilarityForWords("mammal", "hund") + " " + we
        .getCosineSimilarityForWords("hund", "rocket"));
    System.out.println(we.getCosineSimilarityForWords("car", "rakete") + " " + we
        .getCosineSimilarityForWords("football", "universe"));
    assertEquals(1.0, we.getCosineSimilarityForWords("cat", "cat"), eps);
    assertEquals(1.0, we.getCosineSimilarityForWords("hund", "hund"), eps);
    assertTrue(0.5 < we.getCosineSimilarityForWords("dog", "hund"));
    assertTrue(0.5 < we.getCosineSimilarityForWords("katze", "cat"));
    assertEquals(
        we.getCosineSimilarityForWords("hund", "cat"),
        we.getCosineSimilarityForWords("katze", "dog"), 0.1);
    assertTrue(we.getCosineSimilarityForWords("mammal", "hund") > we
        .getCosineSimilarityForWords("hund", "rocket"));
    assertTrue(we.getCosineSimilarityForWords("car", "rakete") > we
        .getCosineSimilarityForWords("football", "universe"));
  }

  @Test
  public void testNearestNeighbors() {
    List<Pair<String, Double>> neighbors = we
        .computeNNearestWords(we.getWordVector("university"), 50);
    neighbors.forEach(System.out::println);
    assertTrue(neighbors.stream().anyMatch(it -> it.getKey().equals("niederlande")));
  }

  @Test
  public void testAnalogies1() {
    Vectord a = we.getWordVector("man");
    Vectord b = we.getWordVector("woman");
    Vectord c = we.getWordVector("königin");
    List<Pair<String, Double>> neighbors = we.computeNNearestWords(c.minus(b).plus(a), 50);
    neighbors.forEach(System.out::println);
    assertTrue(neighbors.stream().anyMatch(it -> it.getKey().equals("king")));
  }

  @Test
  public void testAnalogies2() {
    Vectord a = we.getWordVector("germany");
    Vectord b = we.getWordVector("berlin");
    Vectord c = we.getWordVector("frankreich");
    List<Pair<String, Double>> neighbors = we.computeNNearestWords(c.minus(a).plus(b), 50);
    neighbors.forEach(System.out::println);
    assertTrue(neighbors.stream().anyMatch(it -> it.getKey().equals("paris")));
  }

  @Test
  public void testSum() {
    Vectord a = we.getWordVector("water");
    Vectord b = we.getWordVector("plane");
    List<Pair<String, Double>> neighbors = we.computeNNearestWords(a.plus(b), 50);
    neighbors.forEach(System.out::println);
    assertTrue(neighbors.stream().anyMatch(it -> it.getKey().equals("ship")));
  }

  @Test
  public void testCompareVersusWordNet() {
    try {
      ArrayList<String> words1 = new ArrayList<>();
      ArrayList<String> words2 = new ArrayList<>();
      ArrayList<Double> similarities = new ArrayList<>();
      Stream<String> lines = Files.lines(Paths.get("src/test/resources/wordsim352.tab"));
      lines.forEach(line -> {
        String[] parts = line.split("\t");
        String word1 = parts[0];
        String word2 = parts[1];
        double similarity = Double.parseDouble(parts[2]);
        words1.add(word1);
        words2.add(word2);
        similarities.add(similarity);
      });
      lines.close();
      WordNetInterface wn = new WordNetInterface("src/test/resources/WordNet-3.0");
      int size = words1.size();
      int weCorrect = 0;
      int wnCorrect = 0;
      for (int i = 0; i < size; i += 2) {
        String a = words1.get(i);
        String b = words2.get(i);
        String c = words1.get(i + 1);
        String d = words2.get(i + 1);
        double sim1 = similarities.get(i);
        double sim2 = similarities.get(i + 1);
        System.out.println(a + " " + b + " " + c + " " + d + " " + sim1 + " " + sim2);
        if (sim1 < sim2 == we.getCosineSimilarityForWords(a, b) < we
            .getCosineSimilarityForWords(c, d)) {
          weCorrect++;
          System.out.println("word2vec correct");
        } else {
          System.out.println("word2vec incorrect");
        }
        if (sim1 < sim2 == wn.computeWuPalmerSimilarity(a,b) < wn.computeWuPalmerSimilarity(c,d)) {
          wnCorrect++;
          System.out.println("wordnet correct");
        } else {
          System.out.println("wordnet incorrect");
        }

      }
      System.out.println("word2vec score (out of " + (size/2) + "): " + weCorrect);
      System.out.println("WordNet score (out of " + (size/2) + "): " + wnCorrect);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
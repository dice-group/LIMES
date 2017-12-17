package org.aksw.limes.core.measures.measure.string.bilang;

import static org.apache.jena.system.JenaSystem.forEach;
import static org.junit.Assert.*;

import java.util.List;
import org.aksw.limes.core.measures.measure.string.bilang.WordEmbeddings.Vectord;
import org.apache.commons.math3.util.Pair;
import org.junit.Before;
import org.junit.Test;

public class WordEmbeddingsTest {

  WordEmbeddings we = new WordEmbeddings();

  @Before
  public void setUp() {
    we.readBilangDataFiles("src/test/resources/unsup.128");
  }

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
        we.getCosineSimilarityForWords("hund", "cat"), we.getCosineSimilarityForWords("katze", "dog"), 0.1);
    assertTrue(we.getCosineSimilarityForWords("mammal", "hund") > we
        .getCosineSimilarityForWords("hund", "rocket"));
    assertTrue(we.getCosineSimilarityForWords("car", "rakete") > we
        .getCosineSimilarityForWords("football", "universe"));
  }

  @Test
  public void testNearestNeighbors() {
    List<Pair<String, Double>> neighbors = we.computeNNearestWords(we.getWordVector("germany"),50);
    neighbors.forEach(System.out::println);
    assertTrue(neighbors.stream().anyMatch(it-> it.getKey().equals("niederlande")));
  }

  @Test
  public void testAnalogies1() {
    Vectord a = we.getWordVector("man");
    Vectord b = we.getWordVector("woman");
    Vectord c = we.getWordVector("königin");
    List<Pair<String, Double>> neighbors = we.computeNNearestWords(c.minus(b).plus(a),50);
    neighbors.forEach(System.out::println);
    assertTrue(neighbors.stream().anyMatch(it-> it.getKey().equals("king")));
  }

  @Test
  public void testAnalogies2() {
    Vectord a = we.getWordVector("germany");
    Vectord b = we.getWordVector("berlin");
    Vectord c = we.getWordVector("frankreich");
    List<Pair<String, Double>> neighbors = we.computeNNearestWords(c.minus(a).plus(b),50);
    neighbors.forEach(System.out::println);
    assertTrue(neighbors.stream().anyMatch(it-> it.getKey().equals("paris")));
  }

  @Test
  public void testSum() {
    Vectord a = we.getWordVector("woman");
    Vectord b = we.getWordVector("universität");
    List<Pair<String, Double>> neighbors = we.computeNNearestWords(a.plus(b),50);
    neighbors.forEach(System.out::println);
    assertTrue(neighbors.stream().anyMatch(it-> it.getKey().equals("scientist")));
  }

}
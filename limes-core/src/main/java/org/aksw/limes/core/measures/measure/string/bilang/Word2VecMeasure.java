
package org.aksw.limes.core.measures.measure.string.bilang;

import org.aksw.limes.core.measures.measure.string.AStringMeasure;

/**
 * A bilingual semantic string similarity measure based on the cosine similarity
 * of the (potentially bilingual) word vectors created by word2vec-like algorithms.
 * In particular, the data from the paper "Bilingual Word Representations with Monolingual Quality in Mind"
 * is the basis for this measure.
 */
public class Word2VecMeasure extends AStringMeasure {

  private final WordEmbeddings wordEmbeddings;

  public Word2VecMeasure(WordEmbeddings wordEmbeddings) {
    this.wordEmbeddings = wordEmbeddings;
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

  /**
   * First converts the strings to lowerCase, then applies the cosine similarity
   * to the word embeddings
   * @param object1, the first word
   * @param object2, the second word
   * @return the similarity between these words, as described above.
   */
  @Override
  public double getSimilarity(Object object1, Object object2) {
    String a = ("" + object1).toLowerCase();
    String b = ("" + object2).toLowerCase();
    return wordEmbeddings.getCosineSimilarityForWords(a, b);
  }

  @Override
  public double getRuntimeApproximation(double mappingSize) {
    return mappingSize / 1000d;
  }

  @Override
  public String getName() {
    return "word2vec";
  }
}

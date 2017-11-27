package org.aksw.limes.core.measures.measure.string.bilang;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class WordNetInterface {


  private static POS[] allWordTypes = new POS[]{POS.NOUN, POS.VERB, POS.ADJECTIVE, POS.ADVERB};
  public static String DEFAULT_WORDNET_HOME = "src/test/resources/WordNet-3.0";

  IDictionary dictionary = null;

  public WordNetInterface(String wordNetHome) {
    String path = wordNetHome + File.separator + "dict";

    try {
      dictionary = new Dictionary(new URL("file", null, path));
      dictionary.open();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private ArrayList<ISynset> getAllSynsetsForWordOfWordType(String word, POS wordType) {
    ArrayList<ISynset> result = new ArrayList<>();
    IIndexWord idxWord = dictionary.getIndexWord(word, wordType);
    if (idxWord == null) {
      return result;
    }
    for (IWordID wordId : idxWord.getWordIDs()) {
      ISynset synset = dictionary.getWord(wordId).getSynset();
      result.add(synset);
    }
    return result;
  }

  private ArrayList<ISynset> getPathFromRootToSynset(ISynset synset) {
    ArrayList<ISynset> result = new ArrayList<>();
    while (true) {
      if (result.contains(synset)) {
        break;
      }
      result.add(synset);
      List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM);
      if (hypernyms.size() == 0) {
        break;
      }
      synset = dictionary.getSynset(hypernyms.get(0)); // TODO: consider all possible hypernyms?
    }
    Collections.reverse(result);
    return result;
  }

  private double getBestSimilarityOfSynsets(ISynset synset1, ISynset synset2) {
    ArrayList<ISynset> path1 = getPathFromRootToSynset(synset1);
    ArrayList<ISynset> path2 = getPathFromRootToSynset(synset2);
    HashMap<ISynset, Integer> depths = new HashMap<>();
    for (int i = 0; i < path1.size(); i++) {
      depths.put(path1.get(i), i+1);
    }
    for (int i = path2.size()-1; i >= 0; i--) {
      ISynset s = path2.get(i);
      if (depths.containsKey(s)) {
        int commonRootDepth = i+1;
        return 2.0*commonRootDepth/(double)(path1.size()+path2.size());
      }
    }
    return 0.0;  // No common root found -> no similarity
  }

  private double getBestSimilarityForWordType(String s1, String s2, POS wordType) {
    ArrayList<ISynset> synsets1 = getAllSynsetsForWordOfWordType(s1, wordType);
    ArrayList<ISynset> synsets2 = getAllSynsetsForWordOfWordType(s2, wordType);
    double maxSimilarity = 0.0;
    for (int a = 0; a < synsets1.size(); a++) {
      for (int b = 0; b < synsets2.size(); b++) {
        double similarity = getBestSimilarityOfSynsets(synsets1.get(a), synsets2.get(b));
        maxSimilarity = Math.max(similarity, maxSimilarity);
      }
    }
    return maxSimilarity;
  }



  public double getSimilarity(String s1, String s2) {
    double maxSimilarity = 0.0;
    for (POS wordType : allWordTypes) {
      double similarity = getBestSimilarityForWordType(s1, s2, wordType);
      maxSimilarity = Math.max(similarity, maxSimilarity);
    }
    return maxSimilarity;
  }



}

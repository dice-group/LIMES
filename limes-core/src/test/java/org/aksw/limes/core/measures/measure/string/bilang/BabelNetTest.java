package org.aksw.limes.core.measures.measure.string.bilang;

import com.google.common.collect.Multimap;
import it.uniroma1.lcl.babelnet.BabelNet;
import java.io.File;
import java.nio.file.Paths;
import org.junit.Test;


import it.uniroma1.lcl.babelnet.BabelImage;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelNetUtils;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.BabelSenseComparator;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetComparator;
import it.uniroma1.lcl.babelnet.BabelSynsetID;
import it.uniroma1.lcl.babelnet.BabelSynsetIDRelation;
import it.uniroma1.lcl.babelnet.InvalidBabelSynsetIDException;
import it.uniroma1.lcl.babelnet.data.BabelGloss;
import it.uniroma1.lcl.babelnet.data.BabelPOS;
import it.uniroma1.lcl.babelnet.data.BabelSenseSource;
import it.uniroma1.lcl.jlt.util.Language;
import it.uniroma1.lcl.jlt.util.ScoredItem;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A demo class to test {@link BabelNet}'s various features.
 *
 * @author cecconi, navigli, vannella
 */
public class BabelNetTest {

  /**
   * A demo to see the senses of a word.
   */
  public static void testDictionary(String lemma, Language languageToSearch,
      Language... languagesToPrint) throws IOException {
    BabelNet bn = BabelNet.getInstance();
    System.out.println("SENSES FOR \"" + lemma + "\"");
    List<BabelSense> senses =
        bn.getSenses(lemma, languageToSearch, BabelPOS.NOUN);
    Collections.sort(senses, new BabelSenseComparator());
    for (BabelSense sense : senses) {
      System.out.println("\t=>" + sense.getSenseString());
    }
    System.out.println();
    System.out.println("SYNSETS WITH \"" + lemma + "\"");
    List<BabelSynset> synsets =
        bn.getSynsets(lemma, languageToSearch, BabelPOS.NOUN);
    Collections.sort(synsets, new BabelSynsetComparator(lemma));
    for (BabelSynset synset : synsets) {
      System.out.println(
          "\t=>(" + synset.getId() +
              ") SOURCE: " + synset.getSynsetSource() +
              ") TYPE: " + synset.getSynsetType() +
              "; WN SYNSET: " + synset.getWordNetOffsets() +
              "; MAIN SENSE: " + synset.getMainSense(Language.EN) +
              "; SENSES: " + synset.toString(languagesToPrint));
    }
    System.out.println();
  }

  /**
   * A demo to see the senses of a word.
   */
  public static void testDictionary(String lemma, Language languageToSearch,
      BabelSenseSource... allowedSources) throws IOException {
    BabelNet bn = BabelNet.getInstance();
    System.out.println("SENSES FOR \"" + lemma + "\"");
    List<BabelSense> senses =
        bn.getSenses(lemma, languageToSearch, BabelPOS.NOUN,
            allowedSources);
    Collections.sort(senses, new BabelSenseComparator());
    for (BabelSense sense : senses) {
      System.out.println("\t=>" + sense.getSenseString());
    }
    System.out.println();
    System.out.println("SYNSETS WITH \"" + lemma + "\"");
    List<BabelSynset> synsets =
        bn.getSynsets(lemma, languageToSearch, BabelPOS.NOUN,
            allowedSources);
    Collections.sort(synsets, new BabelSynsetComparator(lemma));
    for (BabelSynset synset : synsets) {
      System.out.println(
          "\t=>(" + synset.getId() +
              ") SOURCE: " + synset.getSynsetSource() +
              ") TYPE: " + synset.getSynsetType() +
              "; WN SYNSET: " + synset.getWordNetOffsets() +
              "; MAIN SENSE: " + synset.getMainSense(Language.EN) +
              "; SENSES: " + synset.toString());
    }
    System.out.println();
  }

  /**
   * A demo to explore the BabelNet graph.
   */
  public static void testGraph(String id) throws IOException, InvalidBabelSynsetIDException {
    testGraph(new BabelSynsetID(id));
  }

  /**
   * A demo to explore the BabelNet graph.
   */
  public static void testGraph(String lemma, Language language) throws IOException {
    BabelNet bn = BabelNet.getInstance();
    List<BabelSynset> synsets = bn.getSynsets(lemma, language);
    Collections.sort(synsets, new BabelSynsetComparator(lemma));

    for (BabelSynset synset : synsets) {
      testGraph(synset.getId());
    }
  }

  /**
   * A demo to explore the BabelNet graph.
   */
  public static void testGraph(BabelSynsetID synsetId) throws IOException {
    List<BabelSynsetIDRelation> successorsEdges = synsetId.getRelatedIDs();

    System.out.println("SYNSET ID:" + synsetId);
    System.out.println("# OUTGOING EDGES: " + successorsEdges.size());

    for (BabelSynsetIDRelation edge : successorsEdges) {
      System.out.println("\tEDGE " + edge);
      System.out
          .println("\t" + edge.getBabelSynsetIDTarget().toBabelSynset().toString(Language.EN));
      System.out.println();
    }
  }

  /**
   * A demo to see the translations of a word.
   */
  public static void testTranslations(String lemma, Language languageToSearch,
      Language... languagesToPrint) throws IOException {
    List<Language> allowedLanguages = Arrays.asList(languagesToPrint);
    Multimap<Language, ScoredItem<String>> translations =
        BabelNetUtils.getTranslations(languageToSearch, lemma);

    System.out.println("TRANSLATIONS FOR " + lemma);
    for (Language language : translations.keySet()) {
      if (allowedLanguages.contains(language)) {
        System.out.println("\t" + language + "=>" + translations.get(language));
      }
    }
  }

  /**
   * A demo to see the glosses of a {@link BabelSynset} given its id.
   */
  public static void testGloss(String id) throws IOException, InvalidBabelSynsetIDException {
    BabelSynset synset = new BabelSynsetID(id).toBabelSynset();
    testGloss(synset);
  }

  /**
   * A demo to see the glosses of a word in a certain language
   */
  public static void testGloss(String lemma, Language language) throws IOException {
    BabelNet bn = BabelNet.getInstance();
    List<BabelSynset> synsets = bn.getSynsets(lemma, language);
    for (BabelSynset synset : synsets) {
      testGloss(synset);
    }
  }

  /**
   * A demo to see the glosses of a {@link BabelSynset}
   */
  public static void testGloss(BabelSynset synset) throws IOException {
    BabelSynsetID id = synset.getId();
    List<BabelGloss> glosses = synset.getGlosses();

    System.out.println("GLOSSES FOR SYNSET " + synset + " -- ID: " + id);
    for (BabelGloss gloss : glosses) {
      System.out.println(" * " + gloss.getLanguage() + " " + gloss.getSource() + " " +
          gloss.getSourceSense() + "\n\t" + gloss.getGloss());
    }
    System.out.println();
  }

  /**
   * A demo to see the images of a {@link BabelSynset}
   */
  public static void testImages(String lemma, Language language) throws IOException {
    BabelNet bn = BabelNet.getInstance();
    System.out.println("SYNSETS WITH word: \"" + lemma + "\"");
    List<BabelSynset> synsets = bn.getSynsets(lemma, language);
    for (BabelSynset synset : synsets) {
      System.out.println("  =>(" + synset.getId() + ")" +
          "  MAIN LEMMA: " + synset.getMainSense(language));
      for (BabelImage img : synset.getImages()) {
        System.out.println("\tIMAGE URL:" + img.getURL());
        System.out.println("\tIMAGE VALIDATED URL:" + img.getValidatedURL());
        System.out.println("\t==");
      }
      System.out.println("  -----");
    }
  }

  public static void mainTest() throws IOException {
    BabelNet bn = BabelNet.getInstance();
    String word = "bank";
    System.out.println("SYNSETS WITH English word: \"" + word + "\"");
    List<BabelSynset> synsets = bn.getSynsets(word, Language.EN);
    Collections.sort(synsets, new BabelSynsetComparator(word));
    for (BabelSynset synset : synsets) {
      System.out.print("  =>(" + synset.getId() + ") SOURCE: " + synset.getSynsetSource() +
          "; TYPE: " + synset.getSynsetType() +
          "; WN SYNSET: " + synset.getWordNetOffsets() + ";\n" +
          "  MAIN LEMMA: " + synset.getMainSense(Language.EN) +
          ";\n  IMAGES: " + synset.getImages() +
          ";\n  CATEGORIES: " + synset.getCategories() +
          ";\n  SENSES (Italian): { ");
      for (BabelSense sense : synset.getSenses(Language.IT)) {
        System.out.print(sense.toString() + " " + sense.getPronunciations() + " ");
      }
      System.out.println("}\n  -----");
    }
  }

  @Test
  public void test() {
    System.out.println(System.getProperty("java.class.path"));
    System.out.println(new File("").getAbsolutePath());
    try {

      BabelNet bn = BabelNet.getInstance();

      System.out.println("=== DEMO ===");
      BabelSynset synset = bn.getSynset(new BabelSynsetID("bn:03083790n"));
      System.out
          .println("Welcome on " + synset.getMainSense(Language.EN).getLemma().replace("_", " "));
      System.out.println(synset.getMainGloss(Language.EN).getGloss());

      mainTest();

//			testImages("balloon", Language.EN);
//
//			System.out.println("===============TESTING BABELNET DICT===============\n");
//			for (String test : new String[] {
//					"bank",
//					"house",
//					"car",
//					"account"
//			}) testDictionary(test, Language.EN, Language.IT);
//			System.out.println("=====================DONE=====================");
//
//			System.out.println("===============TESTING BABELNET GRAPH===============\n");
//			testGraph("bank", Language.EN);
//			testGraph("bn:00000010n");
//			System.out.println("=====================DONE=====================");
//
//			System.out.println("===============TESTING BABELNET TRANSLATIONS===============\n");
//			List<Language> languages = BabelNetConfiguration.getInstance().getBabelLanguages();
//			languages.add(Language.EN);
//			testTranslations("apple", Language.EN, languages.toArray(new Language[]{}));
//			System.out.println("=====================DONE=====================");
//
//			System.out.println("===============TESTING BABELNET GLOSSES===============\n");
//			testGloss("play", Language.EN);
//			testGloss("bn:00000010n");
//			System.out.println("=====================DONE=====================");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

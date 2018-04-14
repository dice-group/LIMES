package org.aksw.limes.core.measures.measure.string;

import org.junit.Test;


import java.nio.file.Paths;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Doc2VecMeasureTest {

  private static final Logger log = LoggerFactory.getLogger(Doc2VecMeasure.class);

  @Test
  public void testMeasure() throws Exception {
    System.out.println(new File("").getAbsolutePath());
    File file = new File("src/test/resources/raw_sentences.txt");
    SentenceIterator iter = new BasicLineIterator(file);

    AbstractCache<VocabWord> cache = new AbstractCache<>();

    TokenizerFactory t = new DefaultTokenizerFactory();
    t.setTokenPreProcessor(new CommonPreprocessor());

        /*
             if you don't have LabelAwareIterator handy, you can use synchronized labels generator
              it will be used to label each document/sequence/line with it's own label.

              But if you have LabelAwareIterator ready, you can can provide it, for your in-house labels
        */
    LabelsSource source = new LabelsSource("DOC_");

    ParagraphVectors vec = new ParagraphVectors.Builder()
        .minWordFrequency(1)
        .iterations(5)
        .epochs(1)
        .layerSize(100)
        .learningRate(0.025)
        .labelsSource(source)
        .windowSize(5)
        .iterate(iter)
        .trainWordVectors(false)
        .vocabCache(cache)
        .tokenizerFactory(t)
        .sampling(0)
        .build();

    vec.fit();

        /*
            In training corpus we have few lines that contain pretty close words invloved.
            These sentences should be pretty close to each other in vector space

            line 3721: This is my way .
            line 6348: This is my case .
            line 9836: This is my house .
            line 12493: This is my world .
            line 16393: This is my work .

            this is special sentence, that has nothing common with previous sentences
            line 9853: We now have one .

            Note that docs are indexed from 0
         */

    double similarity1 = vec.similarity("DOC_9835", "DOC_12492");
    log.info("9836/12493 ('This is my house .'/'This is my world .') similarity: " + similarity1);

    double similarity2 = vec.similarity("DOC_3720", "DOC_16392");
    log.info("3721/16393 ('This is my way .'/'This is my work .') similarity: " + similarity2);

    double similarity3 = vec.similarity("DOC_6347", "DOC_3720");
    log.info("6348/3721 ('This is my case .'/'This is my way .') similarity: " + similarity3);

    // likelihood in this case should be significantly lower
    double similarityX = vec.similarity("DOC_3720", "DOC_9852");
    log.info("3721/9853 ('This is my way .'/'We now have one .') similarity: " + similarityX +
        "(should be significantly lower)");
  }
}


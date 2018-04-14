package org.aksw.limes.core.measures.measure.string;

import java.io.File;
import org.junit.Test;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Doc2VecMeasureTest {

  private static final Logger log = LoggerFactory.getLogger(Doc2VecMeasure.class);

  @Test
  public void testDL4J() throws Exception {
    TokenizerFactory t = new DefaultTokenizerFactory();
    t.setTokenPreProcessor(new CommonPreprocessor());

    // we load externally originated model
    ParagraphVectors vectors = WordVectorSerializer.readParagraphVectors(new File("src/test/resources/doc2vec-precomputed.pv"));
    vectors.setTokenizerFactory(t);
    vectors.getConfiguration().setIterations(1); // please note, we set iterations to 1 here, just to speedup inference

        /*
        // here's alternative way of doing this, word2vec model can be used directly
        // PLEASE NOTE: you can't use Google-like model here, since it doesn't have any Huffman tree information shipped.

        ParagraphVectors vectors = new ParagraphVectors.Builder()
            .useExistingWordVectors(word2vec)
            .build();
        */
    // we have to define tokenizer here, because restored model has no idea about it


    INDArray inferredVectorA = vectors.inferVector("This is my world .");
    INDArray inferredVectorA2 = vectors.inferVector("This is my world .");
    INDArray inferredVectorB = vectors.inferVector("This is my way .");

    // high similarity expected here, since in underlying corpus words WAY and WORLD have really close context
    log.info("Cosine similarity A/B: {}", Transforms.cosineSim(inferredVectorA, inferredVectorB));

    // equality expected here, since inference is happening for the same sentences
    log.info("Cosine similarity A/A2: {}", Transforms.cosineSim(inferredVectorA, inferredVectorA2));
  }
}


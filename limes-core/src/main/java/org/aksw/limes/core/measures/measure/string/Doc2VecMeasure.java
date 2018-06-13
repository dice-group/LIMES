package org.aksw.limes.core.measures.measure.string;


import java.io.File;
import java.io.IOException;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

/**
 * computes similarity between arbitrary-length strings (sentences/paragraphs/documents)
 * based on precomputed document embeddings that are loaded in the constructor
 */
public class Doc2VecMeasure extends AStringMeasure {
	
	public static String DEFAULT_PRECOMPUTED_VECTORS_FILE_PATH = "src/test/resources/doc2vec-precomputed.pv";
	
	private ParagraphVectors vectors = null;
	
	/**
	 * @param precomputedVectorsFilePath Path to the file with precomputed vectors
	 */
	public Doc2VecMeasure(String precomputedVectorsFilePath) {
		TokenizerFactory t = new DefaultTokenizerFactory();
		t.setTokenPreProcessor(new CommonPreprocessor());
		
		// we load externally originated model
		try {
			vectors = WordVectorSerializer
				.readParagraphVectors(new File(precomputedVectorsFilePath));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("file doesn't exist.");
		}
		vectors.setTokenizerFactory(t);
		vectors.getConfiguration().setIterations(1);
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
	
	@Override
	public double getSimilarity(Object object1, Object object2) {
		String a = ("" + object1).toLowerCase();
		String b = ("" + object2).toLowerCase();
		try {
			INDArray inferredVectorA = vectors.inferVector(a);
			INDArray inferredVectorB = vectors.inferVector(b);
			return getSimilarityForInferredVectors(inferredVectorA, inferredVectorB);
		} catch (RuntimeException e) {
			return 0.0;
		}
	}
	
	@Override
	public double getRuntimeApproximation(double mappingSize) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public String getName() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public INDArray inferVector(String text) {
		return vectors.inferVector(text);
	}
	
	public static double getSimilarityForInferredVectors(INDArray inferredVector1,
		INDArray inferredVector2) {
		return Math.max(0, Math.min(1, Transforms.cosineSim(inferredVector1, inferredVector2)));
	}
}

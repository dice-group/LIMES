package org.aksw.limes.core.ml.setting;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.algorithm.IMLAlgorithm;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Klaus Lyko
 */
public class BatchLearningSetting extends LearningSetting {
	
	int inquirySize = 10;
	
	public int getInquirySize() {
		return inquirySize;
	}

	public void setInquirySize(int inquirySize) {
		this.inquirySize = inquirySize;
	}

	public BatchLearningSetting(IMLAlgorithm algorithm) {
		super(algorithm);
	}
	
	@Override
	public void learn() {
		// TODO Auto-generated method stub
		// will use the following
		Mapping trainingData = selectExamples();
		algorithm.learn(trainingData);
	}
	
	public Mapping selectExamples() {
		// TODO implement
		return null;
	}

}

package org.aksw.limes.core.ml.setting;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.oldalgorithm.IMLAlgorithm;

/**
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 * @author Klaus Lyko
 */
@Deprecated
public class BatchLearningSetting extends LearningSetting {

    int inquirySize = 10;

    public BatchLearningSetting(IMLAlgorithm algorithm) {
        super(algorithm);
    }

    public int getInquirySize() {
        return inquirySize;
    }

    public void setInquirySize(int inquirySize) {
        this.inquirySize = inquirySize;
    }

    @Override
    public void learn() {
        // TODO Auto-generated method stub
        // will use the following
        AMapping trainingData = selectExamples();
        algorithm.learn(trainingData);
    }

    public AMapping selectExamples() {
        // TODO implement
        return null;
    }

}

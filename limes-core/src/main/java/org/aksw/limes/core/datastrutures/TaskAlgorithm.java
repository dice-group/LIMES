package org.aksw.limes.core.datastrutures;

import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.setting.LearningParameters;

public class TaskAlgorithm {
    private MLImplementationType mlType;
    private AMLAlgorithm mlAlgorithm;
    private LearningParameters mlParameter;

    public TaskAlgorithm() {
    }
    
    public TaskAlgorithm(MLImplementationType mlType,AMLAlgorithm mlAlgorithm,LearningParameters mlParameter) {
        this.mlType = mlType;
        this.mlAlgorithm=mlAlgorithm;
        this.mlParameter=mlParameter;
    }

    public MLImplementationType getMlType() {
        return mlType;
    }

    public void setMlType(MLImplementationType mlType) {
        this.mlType = mlType;
    }

    public AMLAlgorithm getMlAlgorithm() {
        return mlAlgorithm;
    }

    public void setMlAlgorithm(AMLAlgorithm mlAlgorithm) {
        this.mlAlgorithm = mlAlgorithm;
    }

    public LearningParameters getMlParameter() {
        return mlParameter;
    }

    public void setMlParameter(LearningParameters mlParameter) {
        this.mlParameter = mlParameter;
    }
}
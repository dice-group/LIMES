package org.aksw.limes.core.datastrutures;

import java.util.List;

import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.setting.LearningParameter;

public class TaskAlgorithm {
    private MLImplementationType mlType;
    private AMLAlgorithm mlAlgorithm;
    private List<LearningParameter> mlParameter;

    public TaskAlgorithm() {
    }
    
    public TaskAlgorithm(MLImplementationType mlType,AMLAlgorithm mlAlgorithm,List<LearningParameter> mlParameter) {
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

    public List<LearningParameter> getMlParameter() {
        return mlParameter;
    }

    public void setMlParameter(List<LearningParameter> mlParameter) {
        this.mlParameter = mlParameter;
    }
}
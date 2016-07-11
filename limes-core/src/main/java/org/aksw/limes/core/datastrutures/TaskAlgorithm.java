package org.aksw.limes.core.datastrutures;

import java.util.List;

import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;

/**
 * This class combines a machine learning algorithm information to be executed  
 *
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */

public class TaskAlgorithm {
    /** The implementation type of the machine learning algorithm such as unsupervised, supervised batch and supervised active */
    private MLImplementationType mlType;
    /** The machine learning algorithm method such as EAGLE, WOMBAT and  LION */
    private AMLAlgorithm mlAlgorithm;
    /** The parameters required by the machine learning algorithm*/
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
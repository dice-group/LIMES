package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningParameters;

public abstract class AMLAlgorithm {

    private ACoreMLAlgorithm ml;

    public String getName() {
        return getMl().getName();
    }

    public ACoreMLAlgorithm getMl() {
        return ml;
    }


    public void init(LearningParameters lp, Cache source, Cache target) {
        getMl().init(lp, source, target);
    }

    public AMapping predict(Cache source, Cache target, MLModel mlModel) {
        return getMl().predict(source, target, mlModel);
    }

    public SupervisedMLAlgorithm asSupervised() {
        return (SupervisedMLAlgorithm) this;
    }

    public UnsupervisedMLAlgorithm asUnsupervised() {
        return (UnsupervisedMLAlgorithm) this;
    }

    public ActiveMLAlgorithm asActive() {
        return (ActiveMLAlgorithm) this;
    }

    public LearningParameters getParameters() {
        return getMl().getParameters();
    }

}

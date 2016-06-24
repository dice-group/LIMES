package org.aksw.limes.core.ml.algorithm;

import java.util.List;

import org.aksw.limes.core.exceptions.NoSuchParameterException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.setting.LearningParameter;

public abstract class AMLAlgorithm {

    private ACoreMLAlgorithm ml;

    public String getName() {
        return getMl().getName();
    }
    
    public ACoreMLAlgorithm getMl() {
        return ml;
    }


    public void init(List<LearningParameter> lp, Cache source, Cache target) {
        getMl().init(lp, source, target);
    }

    public AMapping predict(Cache source, Cache target, MLResults mlModel) {
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

    public List<LearningParameter> getParameters() {
        return getMl().getParameters();
    }

    public void setMl(ACoreMLAlgorithm ml) {
        this.ml = ml;
    }

	public void setParameter(String par, Object val) {
        for(LearningParameter lp : getParameters())
        	if(lp.getName().equals(par)) {
        		lp.setValue(val);
        		return;
        	}
        // if not found
        throw new NoSuchParameterException(par);
	}

}

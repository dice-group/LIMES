package org.aksw.limes.core.gui.controller.ml;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.ml.MachineLearningModel;
import org.aksw.limes.core.gui.view.ml.MachineLearningView;
import org.aksw.limes.core.io.cache.Cache;

public abstract class MachineLearningController {

	protected MachineLearningView mlView;
	
	protected MachineLearningModel mlModel;

	public MachineLearningView getMlView() {
		return mlView;
	}

	public void setMlView(MachineLearningView mlView) {
		this.mlView = mlView;
	}
	
	public void setMLAlgorithmToModel(String algorithmName){
		this.mlModel.initializeData(algorithmName);
	}
	
}
 
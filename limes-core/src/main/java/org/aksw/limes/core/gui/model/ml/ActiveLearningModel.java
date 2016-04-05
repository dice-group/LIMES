package org.aksw.limes.core.gui.model.ml;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.view.ml.MachineLearningView;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;

public class ActiveLearningModel extends MachineLearningModel {

	public ActiveLearningModel(Config config, Cache sourceCache, Cache targetCache) {
		super(config, sourceCache, targetCache);
	}

	@Override
	public void learn(MachineLearningView view) {
		// TODO Auto-generated method stub
		
	}

}

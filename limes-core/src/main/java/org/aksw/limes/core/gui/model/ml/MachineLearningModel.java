package org.aksw.limes.core.gui.model.ml;

import java.util.HashMap;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.view.ml.MachineLearningView;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.algorithm.MLAlgorithm;
import org.aksw.limes.core.ml.setting.LearningSetting;

public abstract class MachineLearningModel {

	private HashMap<String,?> params;
	
	private MachineLearningView mlview;
	
	private MLAlgorithm mlalgorithm;
	
	private LearningSetting learningsetting;
	
	private Config config;
	
	private Cache sourceCache;
	
	private Cache targetCache;
	
	public abstract Mapping learn();

}

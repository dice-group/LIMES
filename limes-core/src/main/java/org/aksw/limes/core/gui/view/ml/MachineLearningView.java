package org.aksw.limes.core.gui.view.ml;

import java.util.HashMap;

import org.aksw.limes.core.gui.controller.ml.MachineLearningController;
import org.aksw.limes.core.gui.view.MainView;

public abstract class MachineLearningView {

	private MainView mainview;
	
	private MachineLearningController mlcontroller;
	
	public abstract void createRootPane(HashMap<String,?> params);

}

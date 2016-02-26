package org.aksw.limes.core.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.gui.model.ActiveLearningModel;
import org.aksw.limes.core.gui.model.ActiveLearningResult;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.InstanceProperty;
import org.aksw.limes.core.gui.view.ActiveLearningResultView;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * Controller that corresponds to the view
 * 
 * @author Felix Brei
 *
 */
public class ActiveLearningResultController {

	/**
	 * The corresponding view
	 */
	private ActiveLearningResultView view;

	/**
	 * ResultView to manipulate
	 */

	/**
	 * Config to get instance information
	 */
	private Config currentConfig;

	private ActiveLearningModel model;

	/**
	 * Sets the Config
	 * 
	 * @param c
	 *            Config of LIMES-query
	 */
	public void setCurrentConfig(Config c) {

		this.currentConfig = c;

	}

	/**
	 * Default constructor
	 * 
	 * @param v
	 *            View that is to be observed
	 */
	public ActiveLearningResultController(ActiveLearningResultView v, Config c,
			ActiveLearningModel m) {
		view = v;
		currentConfig = c;
		model = m;
	}

	/**
	 * shows the properties of an instancematch
	 * 
	 * @param item
	 *            the clicked instancematch of the Resultview
	 */
	public void showProperties(ActiveLearningResult item) {
		String sourceURI = item.getSourceURI();
		String targetURI = item.getTargetURI();

		ObservableList<InstanceProperty> sourcePropertyList = FXCollections
				.observableArrayList();
		ObservableList<InstanceProperty> targetPropertyList = FXCollections
				.observableArrayList();

		Instance i1 = currentConfig.getSourceEndpoint().getCache()
				.getInstance(sourceURI);
		Instance i2 = currentConfig.getTargetEndpoint().getCache()
				.getInstance(targetURI);
		for (String prop : i1.getAllProperties()) {
			String value = "";
			for (String s : i1.getProperty(prop)) {
				value += s + " ";
			}
			sourcePropertyList.add(new InstanceProperty(prop, value));

		}

		view.showSourceInstance(sourcePropertyList);

		for (String prop : i2.getAllProperties()) {
			String value = "";
			for (String s : i2.getProperty(prop)) {
				value += s + " ";
			}
			targetPropertyList.add(new InstanceProperty(prop, value));
		}
		view.showTargetInstance(targetPropertyList);
	}

	public void learnButtonPressed() {
		view.learnProgress.setVisible(true);
		for (ActiveLearningResult item : view.getMatchingTable().getItems()) {
			if (item.isMatchProperty().get()) {
				model.bestMapping.add(item.getSourceURI(), item.getTargetURI(),
						1.0d);
			}
		}

		ObservableList<ActiveLearningResult> results = FXCollections
				.observableArrayList();
		Thread thread = new Thread() {
			public void run() {
				Mapping bestMapping = model.learn(currentConfig);
				bestMapping.getMap().forEach((sourceURI, map2) -> {
					map2.forEach((targetURI, value) -> {
						results.add(new ActiveLearningResult(sourceURI,
								targetURI, value));
					});
				});
				onFinish(results);
			}
		};
		thread.start();
	}

	private void onFinish(ObservableList<ActiveLearningResult> results) {
		view.learnProgress.setVisible(false);
		view.showResults(results);
	}

	public Metric getMetric() {
		view.setLabel(model.getMetric().toString());
		return model.getMetric();
	}
}

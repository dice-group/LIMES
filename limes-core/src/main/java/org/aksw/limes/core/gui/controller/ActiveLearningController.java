package org.aksw.limes.core.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.aksw.limes.core.gui.model.ActiveLearningModel;
import org.aksw.limes.core.gui.model.ActiveLearningResult;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.view.ActiveLearningResultView;
import org.aksw.limes.core.gui.view.ActiveLearningView;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * Class that handles the input on the ActiveLearningView
 * 
 * @author Felix Brei
 *
 */
public class ActiveLearningController {

	/**
	 * Connected view
	 */
	private ActiveLearningView view;

	/**
	 * Corresponding model
	 */
	private ActiveLearningModel model;

	private Thread thread;

	/**
	 * The current config of the query
	 */
	private Config currentConfig;

	/**
	 * Default constructor
	 * 
	 * @param v
	 *            Corresponding view
	 * @param c
	 *            Current config
	 */
	public ActiveLearningController(ActiveLearningView v, Config c) {
		view = v;
		currentConfig = c;
		model = new ActiveLearningModel(v, c);
	}

	/**
	 * Event that fires when the learning process starts
	 */
	public void goButtonPressed() {
		view.learningProgress.setVisible(true);
		ActiveLearningResultView r = new ActiveLearningResultView(
				currentConfig, model, view);
		thread = new Thread() {
			public void run() {
				Mapping bestMapping = model.learn(currentConfig);
				ObservableList<ActiveLearningResult> results = FXCollections
						.observableArrayList();
				bestMapping.getMap().forEach((sourceURI, map2) -> {
					System.out.println(sourceURI + " " + map2);
					map2.forEach((targetURI, value) -> {
						results.add(new ActiveLearningResult(sourceURI,
								targetURI, value));
					});
				});
				onFinish(r, results);
			}
		};
		thread.start();

	};

	private void onFinish(ActiveLearningResultView r,
			ObservableList<ActiveLearningResult> results) {
		view.learningProgress.setVisible(false);
		r.showResults(results);
	}
}

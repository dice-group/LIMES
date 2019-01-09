package org.aksw.limes.core.gui.controller.ml;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.gui.model.ActiveLearningResult;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.InstanceProperty;
import org.aksw.limes.core.gui.model.ml.ActiveLearningModel;
import org.aksw.limes.core.gui.view.ml.ActiveLearningResultView;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.MLResults;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class handles the user input from {@link ActiveLearningResultView}
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class ActiveLearningResultController {

	/**
	 * The corresponding view
	 */
	private final ActiveLearningResultView view;

	/**
	 * Config to get instance information
	 */
	private Config currentConfig;

	/**
	 * The corresponding model
	 */
	private final ActiveLearningModel model;

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
	 * Constructor
	 * 
	 * @param v
	 *            view
	 * @param c
	 *            config
	 * @param m
	 *            model
	 */
	public ActiveLearningResultController(ActiveLearningResultView v, Config c, ActiveLearningModel m) {
		this.view = v;
		this.currentConfig = c;
		this.model = m;
	}

	/**
	 * shows the properties of matched instances
	 *
	 * @param item
	 *            the clicked matched instances of the ActiveLearningResultView
	 */
	public void showProperties(ActiveLearningResult item) {
		final String sourceURI = item.getSourceURI();
		final String targetURI = item.getTargetURI();

		final ObservableList<InstanceProperty> sourcePropertyList = FXCollections.observableArrayList();
		final ObservableList<InstanceProperty> targetPropertyList = FXCollections.observableArrayList();

		final Instance i1 = this.currentConfig.getSourceEndpoint().getCache().getInstance(sourceURI);
		final Instance i2 = this.currentConfig.getTargetEndpoint().getCache().getInstance(targetURI);
		for (final String prop : i1.getAllProperties()) {
			String value = "";
			for (final String s : i1.getProperty(prop)) {
				value += s + " ";
			}
			sourcePropertyList.add(new InstanceProperty(prop, value));

		}

		this.view.showSourceInstance(sourcePropertyList);

		for (final String prop : i2.getAllProperties()) {
			String value = "";
			for (final String s : i2.getProperty(prop)) {
				value += s + " ";
			}
			targetPropertyList.add(new InstanceProperty(prop, value));
		}
		this.view.showTargetInstance(targetPropertyList);
	}

	/**
	 * Starts a new active learning process as a thread
	 */
	public void learnButtonPressed() {
		final AMapping trainingMap = MappingFactory.createDefaultMapping();
		this.view.learnProgress.setVisible(true);
		for (final ActiveLearningResult item : this.view.getMatchingTable().getItems()) {
			if (item.isMatchProperty().get()) {
				trainingMap.add(item.getSourceURI(), item.getTargetURI(), 1.0d);
			}
		}

		final ObservableList<ActiveLearningResult> results = FXCollections.observableArrayList();
		final Thread thread = new Thread() {
			@Override
			public void run() {
				MLResults mlModel = null;
				try {
					mlModel = ActiveLearningResultController.this.model.getMlalgorithm().asActive()
							.activeLearn(trainingMap);
					ActiveLearningResultController.this.model.setNextExamples(ActiveLearningResultController.this.model
							.getMlalgorithm().asActive().getNextExamples(ActiveLearningModel.nextExamplesNum));
				} catch (final UnsupportedMLImplementationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ActiveLearningResultController.this.model.getNextExamples().getMap().forEach((sourceURI, map2) -> {
					map2.forEach((targetURI, value) -> {
						results.add(new ActiveLearningResult(sourceURI, targetURI, value));
					});
				});
				ActiveLearningResultController.this.model.setLearnedMapping(
						ActiveLearningResultController.this.model.getMlalgorithm().asActive().predict(
								ActiveLearningResultController.this.model.getConfig().getSourceEndpoint().getCache(),
								ActiveLearningResultController.this.model.getConfig().getTargetEndpoint().getCache(),
								mlModel));
				ActiveLearningResultController.this.onFinish(results);
			}
		};
		thread.start();
	}

	/**
	 * Gets called after the active learning thread is finished and displays the
	 * results
	 * 
	 * @param results
	 */
	private void onFinish(ObservableList<ActiveLearningResult> results) {
		this.view.learnProgress.setVisible(false);
		this.view.showResults(results);
	}
}

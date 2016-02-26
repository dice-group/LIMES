package org.aksw.limes.core.gui.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.aksw.limes.core.gui.view.ResultView;
import org.aksw.limes.core.gui.view.SelfConfigurationPanelInterface;
import org.aksw.limes.core.io.mapping.Mapping;
import org.jgap.InvalidConfigurationException;

/**
 * Class to perform genetic self configuration using parameters from
 * SelfConfigurationView
 * 
 * @author Sascha Hahne, Daniel Obraczka
 *
 */
public class GeneticSelfConfigurationModel implements
		SelfConfigurationModelInterface {
	/**
	 * Contains the parameters of the currentConfig
	 */
	private UnSupervisedLearnerParameters params;

	/**
	 * Output metric of the selfconfiguration
	 */
	private Metric learnedMetric;

	/**
	 * learned Mapping of the selfconfiguration
	 */
	private Mapping learnedMapping = new Mapping();

	/**
	 * thread for better performance
	 */
	private Thread thread;

	/**
	 * Constructor
	 */
	public GeneticSelfConfigurationModel() {
		// TODO Auto-generated constructor stub
	}

	@Override
	/**
	 * Performs genetic self configuration.
	 * Uses the parameters of the currentConfig and of the UserInterface in SelfConfigurationView
	 * @param currentConfig the currently used config
	 * @param view the corresponding view
	 */
	public void learn(Config currentConfig, SelfConfigurationPanelInterface view) {
		// check if PropertyMapping was set else set to default
		if (!currentConfig.propertyMapping.wasSet()) {
			currentConfig.propertyMapping.setDefault(
					currentConfig.getSourceInfo(),
					currentConfig.getTargetInfo());
		}
		// Get Parameters
		this.params = new UnSupervisedLearnerParameters(
				currentConfig.getConfigReader(), currentConfig.propertyMapping);
		double[] UIparams = view.getUIParams();
		params.setPFMBetaValue(UIparams[0]);
		params.setCrossoverRate((float) UIparams[1]);
		params.setGenerations((int) UIparams[2]);
		PseudoMeasures pseudoMeasure = new PseudoMeasures();
		if ((int) UIparams[3] == 1) {
			pseudoMeasure = new ReferencePseudoMeasures();
		}
		params.setPseudoFMeasure(pseudoMeasure);
		params.setMutationRate((float) UIparams[4]);
		params.setPopulationSize((int) UIparams[5]);

		// Start Learning
		thread = new Thread() {
			public void run() {
				GeneticSelfConfigurator learner = new BasicGeneticSelfConfigurator();
				try {
					learnedMetric = learner.learn(params);
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
				}
				currentConfig
						.setMetricExpression(learnedMetric.getExpression());
				currentConfig.setAcceptanceThreshold(learnedMetric
						.getThreshold());
				learnedMapping = learner.getMapping();
				onFinish(currentConfig, view);
			}
		};
		thread.start();

	}

	/**
	 * Enables Buttons Defines Action of mapButton
	 * 
	 * @param currentConfig
	 *            Config that was used
	 * @param view
	 *            SelfConfigurationView that was used
	 */
	private void onFinish(Config currentConfig,
			SelfConfigurationPanelInterface view) {
		view.learnButton.setDisable(false);
		view.mapButton.setOnAction(e -> {
			ObservableList<Result> results = FXCollections
					.observableArrayList();
			learnedMapping.map.forEach((sourceURI, map2) -> {
				map2.forEach((targetURI, value) -> {
					results.add(new Result(sourceURI, targetURI, value));
				});
			});
			ResultView resultView = new ResultView(currentConfig);
			resultView.showResults(results);
		});
		if (learnedMapping != null && learnedMapping.size() > 0) {
			view.mapButton.setDisable(false);
			view.progressIndicator.setVisible(false);
			System.out
					.println(currentConfig.getMetricExpression());
			view.selfConfigurationView.view.graphBuild.graphBuildController
					.setConfigFromGraph();
		} else {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					view.progressIndicator.setVisible(false);
					view.selfConfigurationView.createErrorWindow();
				}
			});

		}
	}

}

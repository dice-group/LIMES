package org.aksw.limes.core.gui.model;

import java.util.LinkedList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.aksw.limes.core.gui.view.MeshBasedSelfConfigurationPanel;
import org.aksw.limes.core.gui.view.ResultView;
import org.aksw.limes.core.gui.view.SelfConfigurationPanelInterface;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.mapping.Mapping;

public class MeshBasedSelfConfigurationModel implements
		SelfConfigurationModelInterface {

	private Mapping learnedMapping;

	private ObservableList<ResultsList> savedResults = FXCollections
			.observableArrayList();
	private MeshBasedSelfConfigurator selfConfigurator;
	private List<SimpleClassifier> classifiers;
	private ComplexClassifier cc;
	private ObservableList<String> metricList = FXCollections
			.observableArrayList();

	@Override
	public void learn(Config currentConfig, SelfConfigurationPanelInterface view) {

		Thread thr = new Thread() {
			public void run() {
				Cache sourceCache = currentConfig.getSourceEndpoint()
						.getCache();
				Cache targetCache = currentConfig.getTargetEndpoint()
						.getCache();
				double[] params = view.getUIParams();
				switch ((int) params[1]) {
				case 0:
					selfConfigurator = new MeshBasedSelfConfigurator(
							sourceCache, targetCache, params[5], params[0]);
					break;
				case 1:
					selfConfigurator = new LinearMeshSelfConfigurator(
							sourceCache, targetCache, params[5], params[0]);
					break;
				case 2:
					selfConfigurator = new DisjunctiveMeshSelfConfigurator(
							sourceCache, targetCache, params[5], params[0]);
					break;
				default:
					selfConfigurator = new MeshBasedSelfConfigurator(
							sourceCache, targetCache, params[5], params[0]);
					break;
				}

				classifiers = selfConfigurator.getBestInitialClassifiers();
				showSimpleClassifiers(view, currentConfig);

				if (classifiers.size() > 0) {
					try {
						classifiers = selfConfigurator
								.learnClassifer(classifiers);
						cc = selfConfigurator.getZoomedHillTop((int) params[2],
								(int) params[3], classifiers);

						classifiers = cc.classifiers;

						String generatedMetricexpression = generateMetric(
								cc.classifiers, "", currentConfig);
						showComplexClassifier(view);

						if (cc.mapping != null && cc.mapping.size() > 0)
							learnedMapping = cc.mapping;
						currentConfig
								.setMetricExpression(generatedMetricexpression);
						metricList.add(generatedMetricexpression);
						currentConfig
								.setAcceptanceThreshold(getThreshold(cc.classifiers));
						System.out.println("SelfConfig class= "
								+ selfConfigurator.getClass()
										.getCanonicalName());

						onFinish(currentConfig, view);
					} catch (Exception e) {

					}
					//
				} else {
					// indicator.setValue(new Float(5f/steps));
					// stepPanel.setCaption(messages.getString("MeshBasedSelfConfigPanel.nosimpleclassifiers"));
				}

				onFinish(currentConfig, view);
			}

		};
		thr.start();

	}

	private void onFinish(Config currentConfig,
			SelfConfigurationPanelInterface view) {
		view.selfConfigurationView.view.graphBuild.graphBuildController
				.setConfig(currentConfig);
		ObservableList<Result> results = FXCollections.observableArrayList();
		learnedMapping.getMap().forEach((sourceURI, map2) -> {
			map2.forEach((targetURI, value) -> {
				results.add(new Result(sourceURI, targetURI, value));
			});
		});
		savedResults.add(new ResultsList(results));
		view.learnButton.setDisable(false);
		view.mapButton.setOnAction(e -> {
			int i = ((MeshBasedSelfConfigurationPanel) view).resultSelect
					.getSelectionModel().getSelectedIndex();
			ResultView resultView = new ResultView(currentConfig);
			resultView.showResults(savedResults.get(i).getResults());
			currentConfig.setMetricExpression(metricList.get(i));
			view.selfConfigurationView.view.graphBuild.graphBuildController
					.setConfig(currentConfig);

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

	private void showSimpleClassifiers(SelfConfigurationPanelInterface view,
			Config currentConfig) {
		if (classifiers.size() > 0) {
			currentConfig.propertyMapping = new PropertyMapping();
		}
		for (SimpleClassifier c : classifiers) {
			((MeshBasedSelfConfigurationPanel) view).resultSelect
					.setVisible(true);
			((MeshBasedSelfConfigurationPanel) view).resultSelect.getItems()
					.add(c);
			//avoid manipulating FX application Thread from another Thread
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					((MeshBasedSelfConfigurationPanel) view).resultSelect
							.getSelectionModel().select(c);
				}
			});
			// if(c.measure.equalsIgnoreCase("euclidean"){
			// currentConfig.propertyMapping.a
			// }
		}

	}

	private void showComplexClassifier(SelfConfigurationPanelInterface view) {

	}

	private String generateMetric(SimpleClassifier sl, Config currentConfig) {
		KBInfo source = currentConfig.getSourceInfo();
		KBInfo target = currentConfig.getTargetInfo();
		String metric = ""; //$NON-NLS-1$

		metric += sl.measure
				+ "(" + source.getVar().replaceAll("\\?", "") + "." + sl.sourceProperty; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		metric += "," + target.getVar().replaceAll("\\?", "") + "." + sl.targetProperty + ")|" + sl.threshold; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		return metric;
	}

	private String generateMetric(List<SimpleClassifier> originalCCList,
			String expr, Config currentConfig) {
		// need to copy them
		List<SimpleClassifier> sCList = new LinkedList<SimpleClassifier>();
		for (SimpleClassifier sC : originalCCList)
			sCList.add(sC);

		if (sCList.size() == 0)
			return expr;
		if (expr.length() == 0) {// nothing generated before
			if (sCList.size() == 1) {
				String metric = generateMetric(sCList.get(0), currentConfig);
				return metric.substring(0, metric.lastIndexOf("|"));
			} else {// recursive
				String nestedExpr = "AND("
						+ generateMetric(sCList.remove(0), currentConfig) + ","
						+ generateMetric(sCList.remove(0), currentConfig) + ")";
				return generateMetric(sCList, nestedExpr, currentConfig);
			}
		} else { // have to combine, recursive
			String nestedExpr = "AND(" + expr + ","
					+ generateMetric(sCList.remove(0), currentConfig) + ")";
			return generateMetric(sCList, nestedExpr, currentConfig);
		}
	}

	private double getThreshold(List<SimpleClassifier> classifiers) {
		double min = Double.MAX_VALUE;
		for (SimpleClassifier sC : classifiers) {
			if (sC.threshold <= min)
				min = sC.threshold;
		}
		return min > 1 ? 0.5d : min;
	}

	private static class ResultsList {
		private ObservableList<Result> res;

		public ResultsList(ObservableList<Result> res) {
			this.res = res;
		}

		public ObservableList<Result> getResults() {
			return this.res;
		}

	}

}

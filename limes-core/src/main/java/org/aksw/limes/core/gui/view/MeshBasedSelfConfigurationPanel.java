package org.aksw.limes.core.gui.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;

import org.aksw.limes.core.gui.model.MeshBasedSelfConfigurationModel;

/**
 * Panel of MeshBased Learning
 * 
 * @author Sascha Hahne
 *
 */
public class MeshBasedSelfConfigurationPanel extends
		SelfConfigurationPanelInterface {
	/**
	 * Choice Box to show Mapping of specific Resultmetric
	 */
	public ChoiceBox<SimpleClassifier> resultSelect;

	// public List<String> metricList;
	/**
	 * Contructor
	 * 
	 * @param view
	 *            Corresponding calling View
	 */
	public MeshBasedSelfConfigurationPanel(SelfConfigurationView view) {
		super(view);
		view.controller.setModel(new MeshBasedSelfConfigurationModel());
		showWindow();
	}

	/**
	 * generate Form
	 */
	private void showWindow() {

		selfConfigurationView.selfConfigWrapper.getChildren().clear();
		// Beta value for the pseudo-f-Measure Slider 0.1 2 1
		Label pseudoText = new Label("Beta value for the pseudo-f-Measure");
		Slider pseudoSlider = new Slider(0.1, 2, 1);
		Label pseudoLabel = new Label("1");
		HBox pseudoBox = new HBox();
		pseudoBox.getChildren().add(pseudoSlider);
		pseudoBox.getChildren().add(pseudoLabel);
		selfConfigurationView.selfConfigWrapper.getChildren().add(pseudoText);
		selfConfigurationView.selfConfigWrapper.getChildren().add(pseudoBox);
		pseudoSlider.setShowTickLabels(true);
		pseudoSlider.setShowTickMarks(false);
		pseudoSlider.setMajorTickUnit(0.5);
		pseudoSlider.setMinorTickCount(9);
		pseudoSlider.setSnapToTicks(false);
		pseudoSlider.setBlockIncrement(0.1);

		pseudoSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				pseudoLabel.setText(String.format("%.1f", new_val));
			}
		});
		// Choose classifier MeshBase SelfConfigurator; Linear MeshBase
		// SelfConfigurator; Disjunctive MeshBase SelfConfigurator

		Label classifierLabel = new Label(
				"Choose classifier MeshBase SelfConfigurator");
		ChoiceBox<String> classifierChooser = new ChoiceBox<String>(
				FXCollections.observableArrayList("MeshBase SelfConfigurator",
						"Linear MeshBase SelfConfigurator",
						"Disjunctive MeshBase SelfConfigurator"));
		selfConfigurationView.selfConfigWrapper.getChildren().add(
				classifierLabel);
		selfConfigurationView.selfConfigWrapper.getChildren().add(
				classifierChooser);

		// Number of points used for the grid 1 100 3Clicker
		Label gridPointsText = new Label("Number of points used for the grid");
		Spinner<Integer> gridPointsSpinner = new Spinner<Integer>(1, 100, 3, 1);
		selfConfigurationView.selfConfigWrapper.getChildren().add(
				gridPointsText);
		selfConfigurationView.selfConfigWrapper.getChildren().add(
				gridPointsSpinner);

		// Number of iterations 1 10 3 Clicker
		Label iterationsText = new Label("Number of iterations");
		Spinner<Integer> iterationsSpinner = new Spinner<Integer>(1, 10, 3, 1);
		selfConfigurationView.selfConfigWrapper.getChildren().add(
				iterationsText);
		selfConfigurationView.selfConfigWrapper.getChildren().add(
				iterationsSpinner);

		// Choose a classifier Pseudo F-Measure NGLY12; Pseudo F-Measure NIK+12
		Label classifierMeasureText = new Label("Choose a classifier");
		ChoiceBox<String> classifierMeasureChooser = new ChoiceBox<String>(
				FXCollections.observableArrayList("Pseudo F-Measure NGLY12",
						"Pseudo F-Measure NIK+12"));
		selfConfigurationView.selfConfigWrapper.getChildren().add(
				classifierMeasureText);
		selfConfigurationView.selfConfigWrapper.getChildren().add(
				classifierMeasureChooser);

		// Minimal Coverage for a property 0 1 0.1 0.6
		Label coverageText = new Label("Minimal Coverage for a property");
		Slider coverageSlider = new Slider(0, 1, 0.6);
		Label coverageLabel = new Label("0.6");
		HBox coverageBox = new HBox();
		coverageBox.getChildren().add(coverageSlider);
		coverageBox.getChildren().add(coverageLabel);
		coverageSlider.setShowTickLabels(true);
		coverageSlider.setShowTickMarks(false);
		coverageSlider.setMajorTickUnit(0.5);
		coverageSlider.setMinorTickCount(9);
		coverageSlider.setSnapToTicks(false);
		coverageSlider.setBlockIncrement(0.1);

		coverageSlider.valueProperty().addListener(
				new ChangeListener<Number>() {
					public void changed(ObservableValue<? extends Number> ov,
							Number old_val, Number new_val) {
						coverageLabel.setText(String.format("%.1f", new_val));
					}
				});

		selfConfigurationView.selfConfigWrapper.getChildren().add(coverageText);
		selfConfigurationView.selfConfigWrapper.getChildren().add(coverageBox);

		HBox buttonWrapper = new HBox();
		learnButton = new Button("Start Learning");
		progressIndicator = new ProgressIndicator();
		progressIndicator.setVisible(false);
		learnButton.setOnAction(e -> {
			progressIndicator.setVisible(true);
			double[] params = {
					pseudoSlider.getValue(), // 0
					classifierChooser.getSelectionModel().getSelectedIndex(), // 1
					gridPointsSpinner.getValue(), // 2
					iterationsSpinner.getValue(), // 3
					classifierMeasureChooser.getSelectionModel()
							.getSelectedIndex(),// 4
					coverageSlider.getValue() // 5
			};
			this.UIparams = params;
			learnButton.setDisable(true);
			selfConfigurationView.controller.learn();
		});
		resultSelect = new ChoiceBox<SimpleClassifier>();
		selfConfigurationView.selfConfigWrapper.getChildren().add(resultSelect);
		resultSelect.setVisible(false);
		mapButton = new Button("Show Mapping");
		buttonWrapper.getChildren().add(learnButton);
		buttonWrapper.getChildren().add(mapButton);
		buttonWrapper.getChildren().add(progressIndicator);
		mapButton.setDisable(true);
		// metricList = new Table
		selfConfigurationView.selfConfigWrapper.getChildren()
				.add(buttonWrapper);
	}

}

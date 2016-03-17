package org.aksw.limes.core.gui.view.ml;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import org.aksw.limes.core.gui.controller.ml.MachineLearningController;
import org.aksw.limes.core.gui.view.MainView;
import org.aksw.limes.core.ml.setting.LearningSetting.TerminationCriteria;

public abstract class MachineLearningView {

	protected MainView mainView;

	protected MachineLearningController mlController;

	protected Button learnButton;

	protected ProgressIndicator learningProgress;

	public static final String[] mlAlgorithms = { "Eagle", "Euclid", "Lion",
			"Wombat", "Raven", "Ukulele", "Coala", "Acids", "Mandolin",
			"Cluster" };

	public MachineLearningView(MainView mainView,
			MachineLearningController mlController) {
		this.mainView = mainView;
		this.mlController = mlController;
		this.mlController.setMlView(this);
		createMLAlgorithmsRootPane();
	}

	// TODO finish
	/**
	 * checks if the MLAlgorithm is implemented for this LearningSetting
	 * corresponding to the subclass of MachineLearningView and creates the
	 * rootPane accordingly
	 * 
	 * @return ComboBox with corresponding options
	 */
	public void createMLAlgorithmsRootPane() {
		BorderPane border = new BorderPane();
		HBox content = new HBox();
		GridPane root = new GridPane();

		ObservableList<String> mloptions = FXCollections.observableArrayList();
		// FIXME Temporary Solution
		mloptions.add("Lion");
		if (this instanceof ActiveLearningView) {

		} else if (this instanceof BatchLearningView) {

		} else if (this instanceof UnsupervisedLearningView) {

		} else {
			System.err.println("Unknown subclass of MachineLearningView");
		}
		ComboBox<String> mlOptionsChooser = new ComboBox<String>(mloptions);
		mlOptionsChooser.setPromptText("choose algorithm");

		learningProgress = new ProgressIndicator();
		learningProgress.setVisible(false);
		learnButton = new Button("learn");
		learnButton.setDisable(true);
		HBox buttonWrapper = new HBox();
		buttonWrapper.setAlignment(Pos.BASELINE_RIGHT);
		buttonWrapper.setPadding(new Insets(25, 25, 25, 25));
		buttonWrapper.getChildren().addAll(learningProgress, learnButton);
		content.getChildren().add(mlOptionsChooser);
		border.setTop(content);
		border.setBottom(buttonWrapper);
		Scene scene = new Scene(border, 300, 400);
		scene.getStylesheets().add("gui/main.css");

		mlOptionsChooser.setOnAction(e -> {
			border.getChildren().remove(root);
			mlController.setMLAlgorithmToModel(mlOptionsChooser.getValue());
			showParameters(root, mlOptionsChooser.getValue());
			border.setCenter(root);
		});

		Stage stage = new Stage();
		stage.setTitle("LIMES - Machine Learning");
		stage.setScene(scene);
		stage.show();
	}

	// public abstract void createRootPane(HashMap<String, ?> params);
	private GridPane showParameters(GridPane root, String algorithm) {
		Label inquiriesLabel = new Label("Number of inquiries to user");
		Spinner<Integer> inquiriesSpinner = new Spinner<Integer>(5, 100, 10, 1);
		root.add(inquiriesLabel, 0, 0);
		root.add(inquiriesSpinner, 1, 0);

		Label maxDurationLabel = new Label("Maximal duration in seconds");
		Spinner<Integer> maxDurationSpinner = new Spinner<Integer>(5, 100, 60,
				1);
		root.add(maxDurationLabel, 0, 1);
		root.add(maxDurationSpinner, 1, 1);

		Label maxIterationLabel = new Label("Maximal number of iterations");
		Spinner<Integer> maxIterationSpinner = new Spinner<Integer>(5, 100, 60,
				1);
		root.add(maxIterationLabel, 0, 2);
		root.add(maxIterationSpinner, 1, 2);

		Label maxQualityLabel = new Label(
				"Maximal quality in (Pseudo)-F-Measure");
		Label maxQualityValue = new Label("0.4");
		Slider maxQualitySlider = new Slider();
		maxQualitySlider.setMin(0);
		maxQualitySlider.setMax(1);
		maxQualitySlider.setValue(0.5);
		maxQualitySlider.setShowTickLabels(false);
		maxQualitySlider.setShowTickMarks(false);
		maxQualitySlider.setMajorTickUnit(0.1);
		maxQualitySlider.setMinorTickCount(9);
		maxQualitySlider.setSnapToTicks(false);
		maxQualitySlider.setBlockIncrement(0.1);

		maxQualitySlider.valueProperty().addListener(
				new ChangeListener<Number>() {
					public void changed(ObservableValue<? extends Number> ov,
							Number old_val, Number new_val) {
						maxQualityValue.setText(String.format("%.1f", new_val));
					}
				});

		root.add(maxQualityLabel, 0, 3);
		root.add(maxQualitySlider, 1, 3);
		root.add(maxQualityValue, 2, 3);

		Label terminationCriteriaLabel = new Label("Termination criteria");
		List<String> terminationCriteriaList = new ArrayList<String>();
		for (int i = 0; i < TerminationCriteria.values().length; i++) {
			terminationCriteriaList.add(TerminationCriteria.values()[i]
					.toString());
		}
		ObservableList<String> obsTermCritList = FXCollections
				.observableList(terminationCriteriaList);
		SpinnerValueFactory<String> svf = new SpinnerValueFactory.ListSpinnerValueFactory<>(
				obsTermCritList);
		Spinner<String> terminationCriteriaSpinner = new Spinner<String>();
		terminationCriteriaSpinner.setValueFactory(svf);
		root.add(terminationCriteriaLabel, 0, 4);
		root.add(terminationCriteriaSpinner, 1, 4);

		Label betaLabel = new Label("Beta for (Pseudo)-F-Measure");
		Label betaValue = new Label("1");
		Slider betaSlider = new Slider();
		betaSlider.setMin(0);
		betaSlider.setMax(5);
		betaSlider.setValue(1);
		betaSlider.setShowTickLabels(false);
		betaSlider.setShowTickMarks(false);
		betaSlider.setMajorTickUnit(0.1);
		betaSlider.setMinorTickCount(9);
		betaSlider.setSnapToTicks(false);
		betaSlider.setBlockIncrement(0.1);

		betaSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				betaValue.setText(String.format("%.1f", new_val));
			}
		});

		root.add(betaLabel, 0, 5);
		root.add(betaSlider, 1, 5);
		root.add(betaValue, 2, 5);

		switch (algorithm) {
		case "Lion":
			Label gammaScoreLabel = new Label("Gamma score");
			Label gammaScoreValue = new Label("0.15");
			Slider gammaScoreSlider = new Slider();
			gammaScoreSlider.setMin(0);
			// TODO find out if this is reasonable
			gammaScoreSlider.setMax(1);
			gammaScoreSlider.setValue(0.15);
			gammaScoreSlider.setShowTickLabels(false);
			gammaScoreSlider.setShowTickMarks(false);
			gammaScoreSlider.setMajorTickUnit(0.1);
			gammaScoreSlider.setMinorTickCount(99);
			gammaScoreSlider.setSnapToTicks(false);
			gammaScoreSlider.setBlockIncrement(0.01);

			gammaScoreSlider.valueProperty().addListener(
					new ChangeListener<Number>() {
						public void changed(
								ObservableValue<? extends Number> ov,
								Number old_val, Number new_val) {
							gammaScoreValue.setText(String.format("%.2f",
									new_val));
						}
					});

			root.add(gammaScoreLabel, 0, 6);
			root.add(gammaScoreSlider, 1, 6);
			root.add(gammaScoreValue, 2, 6);

			Label expansionPenaltyLabel = new Label("Expansion Penalty");
			Label expansionPenaltyValue = new Label("0.7");
			Slider expansionPenaltySlider = new Slider();
			expansionPenaltySlider.setMin(0);
			// TODO find out if this is reasonable
			expansionPenaltySlider.setMax(1);
			expansionPenaltySlider.setValue(0.7);
			expansionPenaltySlider.setShowTickLabels(false);
			expansionPenaltySlider.setShowTickMarks(false);
			expansionPenaltySlider.setMajorTickUnit(0.1);
			expansionPenaltySlider.setMinorTickCount(9);
			expansionPenaltySlider.setSnapToTicks(false);
			expansionPenaltySlider.setBlockIncrement(0.1);

			expansionPenaltySlider.valueProperty().addListener(
					new ChangeListener<Number>() {
						public void changed(
								ObservableValue<? extends Number> ov,
								Number old_val, Number new_val) {
							expansionPenaltyValue.setText(String.format("%.1f",
									new_val));
						}
					});

			root.add(expansionPenaltyLabel, 0, 7);
			root.add(expansionPenaltySlider, 1, 7);
			root.add(expansionPenaltyValue, 2, 7);

			Label rewardLabel = new Label("Reward");
			Label rewardValue = new Label("1.2");
			Slider rewardSlider = new Slider();
			rewardSlider.setMin(0);
			// TODO find out if this is reasonable
			rewardSlider.setMax(5);
			rewardSlider.setValue(1.2);
			rewardSlider.setShowTickLabels(false);
			rewardSlider.setShowTickMarks(false);
			rewardSlider.setMajorTickUnit(0.1);
			rewardSlider.setMinorTickCount(9);
			rewardSlider.setSnapToTicks(false);
			rewardSlider.setBlockIncrement(0.1);

			rewardSlider.valueProperty().addListener(
					new ChangeListener<Number>() {
						public void changed(
								ObservableValue<? extends Number> ov,
								Number old_val, Number new_val) {
							rewardValue.setText(String.format("%.1f",
									new_val));
						}
					});

			root.add(rewardLabel, 0, 8);
			root.add(rewardSlider, 1, 8);
			root.add(rewardValue, 2, 8);
			
			Label pruneLabel = new Label("Prune Tree?");
			CheckBox pruneCheckBox = new CheckBox();
			pruneCheckBox.setSelected(true);

			root.add(pruneLabel, 0, 9);
			root.add(pruneCheckBox, 1, 9);
			break;
		default:
			System.err.println("unknown algorithm!");
		}

		root.setHgap(10);
		root.setVgap(10);
		root.setPadding(new Insets(25, 25, 25, 25));
		return root;
	}

}

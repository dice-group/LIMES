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
import javafx.scene.control.ChoiceBox;
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
		mloptions.add("Eagle");
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
			root.getChildren().removeAll(root.getChildren());
			mlController.setMLAlgorithmToModel(mlOptionsChooser.getValue());
			showParameters(root, mlOptionsChooser.getValue());
			border.setCenter(root);
			learnButton.setDisable(false);
		});

		Stage stage = new Stage();
		stage.setTitle("LIMES - Machine Learning");
		stage.setScene(scene);
		stage.show();
	}

	// public abstract void createRootPane(HashMap<String, ?> params);
	
	/**
	 * Takes the gridpane in the BorderPane and adds the GUI elements fitting for the algorithm
	 * @param root
	 * @param algorithm
	 * @return the GridPane containing the added Nodes
	 */
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
		case "Eagle":
			Slider crossover = new Slider();
			Label crossoverLabel = new Label("0.4");
			Label crossoverText = new Label("Crossover probability");
			HBox crossoverBox = new HBox();
			
			root.add(crossoverText, 0, 6);
			crossoverBox.getChildren().add(crossover);
			crossoverBox.getChildren().add(crossoverLabel);
			root.add(crossoverBox, 1, 6);
			
			crossover.setMin(0);
			crossover.setMax(1);
			crossover.setValue(0.4);
			crossover.setShowTickLabels(true);
			crossover.setShowTickMarks(false);
			crossover.setMajorTickUnit(0.5);
			crossover.setMinorTickCount(9);
			crossover.setSnapToTicks(false);
			crossover.setBlockIncrement(0.1);
			
			crossover.valueProperty().addListener(new ChangeListener<Number>() {
			        public void changed(ObservableValue<? extends Number> ov,
			                        Number old_val, Number new_val) {
			                crossoverLabel.setText(String.format("%.1f", new_val));
			        }
			});
			
			Label generationsText = new Label("Number of generations");
			Spinner<Integer> generations = new Spinner<Integer>(5, 100, 5, 5);
			
			root.add(generationsText, 0, 7);
			root.add(generations, 1, 7);
			
			ChoiceBox<String> classifierChooser = new ChoiceBox<String>(
			                FXCollections.observableArrayList("Pseudo F-Measure NGLY12",
			                                "Pseudo F-Measure NIK+12"));
			classifierChooser.getSelectionModel().selectedIndexProperty()
			                .addListener((arg0, value, new_value) -> {
			                        switch (new_value.intValue()) {
			                        case 0:
			                                // TODO
			
			                                break;
			                        case 1:
			                                // TODO
			                                break;
			                        default:
			                                break;
			                        }
			
			                });
			HBox mutationsBox = new HBox();
			Slider mutationRate = new Slider();
			Label mutationRateLabel = new Label("0.4");
			Label mutationRateText = new Label("Mutation rate");
			
			root.add(mutationRateText, 0, 8);
			mutationsBox.getChildren().add(mutationRate);
			mutationsBox.getChildren().add(mutationRateLabel);
			root.add(mutationsBox, 1, 8);
			
			mutationRate.setMin(0);
			mutationRate.setMax(1);
			mutationRate.setValue(0.4);
			mutationRate.setShowTickLabels(true);
			mutationRate.setShowTickMarks(false);
			mutationRate.setMajorTickUnit(0.5);
			mutationRate.setMinorTickCount(9);
			mutationRate.setSnapToTicks(false);
			mutationRate.setBlockIncrement(0.1);
			
			mutationRate.valueProperty().addListener(new ChangeListener<Number>() {
			        public void changed(ObservableValue<? extends Number> ov,
			                        Number old_val, Number new_val) {
			                mutationRateLabel.setText(String.format("%.1f", new_val));
			        }
			});
			
			Label populationText = new Label("Population size");
			Spinner<Integer> population = new Spinner<Integer>(5, 100, 5, 5);
			
			root.add(populationText, 0, 9);
			root.add(population, 1, 9);

			HBox reproductionBox = new HBox();
			Slider reproductionRate = new Slider();
			Label reproductionRateLabel = new Label("0.4");
			Label reproductionRateText = new Label("Reproduction rate");
			
			root.add(reproductionRateText, 0, 10);
			reproductionBox.getChildren().add(reproductionRate);
			reproductionBox.getChildren().add(reproductionRateLabel);
			root.add(reproductionBox, 1, 10);
			
			reproductionRate.setMin(0);
			reproductionRate.setMax(1);
			reproductionRate.setValue(0.4);
			reproductionRate.setShowTickLabels(true);
			reproductionRate.setShowTickMarks(false);
			reproductionRate.setMajorTickUnit(0.5);
			reproductionRate.setMinorTickCount(9);
			reproductionRate.setSnapToTicks(false);
			reproductionRate.setBlockIncrement(0.1);
			
			reproductionRate.valueProperty().addListener(new ChangeListener<Number>() {
			        public void changed(ObservableValue<? extends Number> ov,
			                        Number old_val, Number new_val) {
			                reproductionRateLabel.setText(String.format("%.1f", new_val));
			        }
			});
			
			Label preserveFittestLabel = new Label("Preserve Fittest?");
			CheckBox preserveFittestCheckBox = new CheckBox();
			preserveFittestCheckBox.setSelected(true);

			root.add(preserveFittestLabel, 0, 11);
			root.add(preserveFittestCheckBox, 1, 11);
			
			//TODO measure in case this is supervised
//			if(!(this instanceof UnsupervisedLearningView))
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

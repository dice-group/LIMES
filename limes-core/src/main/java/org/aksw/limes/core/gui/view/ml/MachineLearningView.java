package org.aksw.limes.core.gui.view.ml;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.gui.controller.ml.MachineLearningController;
import org.aksw.limes.core.gui.view.MainView;
import org.aksw.limes.core.ml.setting.LearningSetting.TerminationCriteria;
import org.apache.log4j.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public abstract class MachineLearningView {

	private MainView mainView;

	protected static Logger logger = Logger.getLogger("LIMES");

	protected MachineLearningController mlController;

	private Button learnButton;

	protected Spinner<Integer> inquiriesSpinner;

	protected Spinner<Integer> maxDurationSpinner;

	protected Spinner<Integer> maxIterationSpinner;

	protected Slider maxQualitySlider;

	protected Spinner<String> terminationCriteriaSpinner;

	protected Slider terminationCriteriaValueSlider;

	protected Slider betaSlider;

	protected Spinner<Integer> populationSpinner;

	protected Slider gammaScoreSlider;

	protected Slider expansionPenaltySlider;

	protected Slider rewardSlider;

	protected CheckBox pruneCheckBox;

	protected Slider crossoverRateSlider;

	protected Spinner<Integer> generationsSpinner;

	protected Slider mutationRateSlider;

	protected Slider reproductionRateSlider;

	protected CheckBox preserveFittestCheckBox;
	public static final String[] mlAlgorithms = { "Eagle", "Euclid", "Lion",
			"Wombat", "Raven", "Ukulele", "Coala", "Acids", "Mandolin",
			"Cluster" };

	public MachineLearningView(MainView mainView,
			MachineLearningController mlController) {
		this.setMainView(mainView);
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
			logger.info("Unknown subclass of MachineLearningView");
		}
		ComboBox<String> mlOptionsChooser = new ComboBox<String>(mloptions);
		mlOptionsChooser.setPromptText("choose algorithm");

		learnButton = new Button("learn");
		learnButton.setDisable(true);
		HBox buttonWrapper = new HBox();
		buttonWrapper.setAlignment(Pos.BASELINE_RIGHT);
		buttonWrapper.setPadding(new Insets(25, 25, 25, 25));
		buttonWrapper.getChildren().addAll(learnButton);
		content.getChildren().add(mlOptionsChooser);
		border.setTop(content);
		border.setBottom(buttonWrapper);
		Scene scene = new Scene(border, 300, 400);
		scene.getStylesheets().add("gui/main.css");

		mlOptionsChooser
				.setOnAction(e -> {
					root.getChildren().removeAll(root.getChildren());
					this.mlController.setMLAlgorithmToModel(mlOptionsChooser
							.getValue());
					showParameters(root, mlOptionsChooser.getValue());
					border.setCenter(root);
					learnButton.setDisable(false);
				});

		learnButton.setOnAction(e -> {
			this.mlController.setParameters();
			getLearnButton().setDisable(true);
//			new MLPropertyMatchingView(this.mlController.getMlModel().getConfig(), this);
			this.mlController.learn(this);
		});

		Stage stage = new Stage();
		stage.setTitle("LIMES - Machine Learning");
		stage.setScene(scene);
		stage.show();
	}

	// public abstract void createRootPane(HashMap<String, ?> params);

	/**
	 * Takes the gridpane in the BorderPane and adds the GUI elements fitting
	 * for the algorithm
	 * 
	 * @param root
	 * @param algorithm
	 * @return the GridPane containing the added Nodes
	 */
	private GridPane showParameters(GridPane root, String algorithm) {
		Label inquiriesLabel = new Label("Number of inquiries to user");
		inquiriesSpinner = new Spinner<Integer>(5, 100, 10, 1);
		root.add(inquiriesLabel, 0, 0);
		root.add(inquiriesSpinner, 1, 0);

		Label maxDurationLabel = new Label("Maximal duration in seconds");
		maxDurationSpinner = new Spinner<Integer>(5, 100, 60, 1);
		root.add(maxDurationLabel, 0, 1);
		root.add(maxDurationSpinner, 1, 1);

		Label maxIterationLabel = new Label("Maximal number of iterations");
		maxIterationSpinner = new Spinner<Integer>(5, 100, 60, 1);
		root.add(maxIterationLabel, 0, 2);
		root.add(maxIterationSpinner, 1, 2);

		Label maxQualityLabel = new Label(
				"Maximal quality in (Pseudo)-F-Measure");
		Label maxQualityValue = new Label("0.4");
		maxQualitySlider = new Slider();
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
		terminationCriteriaSpinner = new Spinner<String>();
		terminationCriteriaSpinner.setValueFactory(svf);
		root.add(terminationCriteriaLabel, 0, 4);
		root.add(terminationCriteriaSpinner, 1, 4);

		Label terminationCriteriaValueLabel = new Label(
				"Value of termination criteria");
		Label terminationCriteriaValueValue = new Label("0");
		terminationCriteriaValueSlider = new Slider();
		// TODO set ranges according to criteria chosen
		terminationCriteriaValueSlider.setMin(0);
		terminationCriteriaValueSlider.setMax(5);
		terminationCriteriaValueSlider.setValue(0);
		terminationCriteriaValueSlider.setShowTickLabels(false);
		terminationCriteriaValueSlider.setShowTickMarks(false);
		terminationCriteriaValueSlider.setMajorTickUnit(0.1);
		terminationCriteriaValueSlider.setMinorTickCount(9);
		terminationCriteriaValueSlider.setSnapToTicks(false);
		terminationCriteriaValueSlider.setBlockIncrement(0.1);

		terminationCriteriaValueSlider.valueProperty().addListener(
				new ChangeListener<Number>() {
					public void changed(ObservableValue<? extends Number> ov,
							Number old_val, Number new_val) {
						terminationCriteriaValueValue.setText(String.format(
								"%.1f", new_val));
					}
				});

		root.add(terminationCriteriaValueLabel, 0, 5);
		root.add(terminationCriteriaValueSlider, 1, 5);
		root.add(terminationCriteriaValueValue, 2, 5);

		Label betaLabel = new Label("Beta for (Pseudo)-F-Measure");
		Label betaValue = new Label("1");
		betaSlider = new Slider();
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

		root.add(betaLabel, 0, 6);
		root.add(betaSlider, 1, 6);
		root.add(betaValue, 2, 6);

		switch (algorithm) {
		case "Lion":
			Label gammaScoreLabel = new Label("Gamma score");
			Label gammaScoreValue = new Label("0.15");
			gammaScoreSlider = new Slider();
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

			root.add(gammaScoreLabel, 0, 7);
			root.add(gammaScoreSlider, 1, 7);
			root.add(gammaScoreValue, 2, 7);

			Label expansionPenaltyLabel = new Label("Expansion Penalty");
			Label expansionPenaltyValue = new Label("0.7");
			expansionPenaltySlider = new Slider();
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

			root.add(expansionPenaltyLabel, 0, 8);
			root.add(expansionPenaltySlider, 1, 8);
			root.add(expansionPenaltyValue, 2, 8);

			Label rewardLabel = new Label("Reward");
			Label rewardValue = new Label("1.2");
			rewardSlider = new Slider();
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
							rewardValue.setText(String.format("%.1f", new_val));
						}
					});

			root.add(rewardLabel, 0, 9);
			root.add(rewardSlider, 1, 9);
			root.add(rewardValue, 2, 9);

			Label pruneLabel = new Label("Prune Tree?");
			pruneCheckBox = new CheckBox();
			pruneCheckBox.setSelected(true);

			root.add(pruneLabel, 0, 10);
			root.add(pruneCheckBox, 1, 10);
			break;
		case "Eagle":
			crossoverRateSlider = new Slider();
			Label crossoverLabel = new Label("0.4");
			Label crossoverText = new Label("Crossover probability");
			HBox crossoverBox = new HBox();

			root.add(crossoverText, 0, 7);
			crossoverBox.getChildren().add(crossoverRateSlider);
			crossoverBox.getChildren().add(crossoverLabel);
			root.add(crossoverBox, 1, 7);

			crossoverRateSlider.setMin(0);
			crossoverRateSlider.setMax(1);
			crossoverRateSlider.setValue(0.4);
			crossoverRateSlider.setShowTickLabels(true);
			crossoverRateSlider.setShowTickMarks(false);
			crossoverRateSlider.setMajorTickUnit(0.5);
			crossoverRateSlider.setMinorTickCount(9);
			crossoverRateSlider.setSnapToTicks(false);
			crossoverRateSlider.setBlockIncrement(0.1);

			crossoverRateSlider.valueProperty().addListener(
					new ChangeListener<Number>() {
						public void changed(
								ObservableValue<? extends Number> ov,
								Number old_val, Number new_val) {
							crossoverLabel.setText(String.format("%.1f",
									new_val));
						}
					});

			Label generationsText = new Label("Number of generations");
			generationsSpinner = new Spinner<Integer>(1, 100, 1, 1);

			root.add(generationsText, 0, 8);
			root.add(generationsSpinner, 1, 8);

			HBox mutationsBox = new HBox();
			mutationRateSlider = new Slider();
			Label mutationRateLabel = new Label("0.4");
			Label mutationRateText = new Label("Mutation rate");

			root.add(mutationRateText, 0, 9);
			mutationsBox.getChildren().add(mutationRateSlider);
			mutationsBox.getChildren().add(mutationRateLabel);
			root.add(mutationsBox, 1, 9);

			mutationRateSlider.setMin(0);
			mutationRateSlider.setMax(1);
			mutationRateSlider.setValue(0.4);
			mutationRateSlider.setShowTickLabels(true);
			mutationRateSlider.setShowTickMarks(false);
			mutationRateSlider.setMajorTickUnit(0.5);
			mutationRateSlider.setMinorTickCount(9);
			mutationRateSlider.setSnapToTicks(false);
			mutationRateSlider.setBlockIncrement(0.1);

			mutationRateSlider.valueProperty().addListener(
					new ChangeListener<Number>() {
						public void changed(
								ObservableValue<? extends Number> ov,
								Number old_val, Number new_val) {
							mutationRateLabel.setText(String.format("%.1f",
									new_val));
						}
					});

			Label populationText = new Label("Population size");
			populationSpinner = new Spinner<Integer>(5, 100, 5, 5);

			root.add(populationText, 0, 10);
			root.add(populationSpinner, 1, 10);

			HBox reproductionBox = new HBox();
			reproductionRateSlider = new Slider();
			Label reproductionRateLabel = new Label("0.4");
			Label reproductionRateText = new Label("Reproduction rate");

			root.add(reproductionRateText, 0, 11);
			reproductionBox.getChildren().add(reproductionRateSlider);
			reproductionBox.getChildren().add(reproductionRateLabel);
			root.add(reproductionBox, 1, 11);

			reproductionRateSlider.setMin(0);
			reproductionRateSlider.setMax(1);
			reproductionRateSlider.setValue(0.4);
			reproductionRateSlider.setShowTickLabels(true);
			reproductionRateSlider.setShowTickMarks(false);
			reproductionRateSlider.setMajorTickUnit(0.5);
			reproductionRateSlider.setMinorTickCount(9);
			reproductionRateSlider.setSnapToTicks(false);
			reproductionRateSlider.setBlockIncrement(0.1);

			reproductionRateSlider.valueProperty().addListener(
					new ChangeListener<Number>() {
						public void changed(
								ObservableValue<? extends Number> ov,
								Number old_val, Number new_val) {
							reproductionRateLabel.setText(String.format("%.1f",
									new_val));
						}
					});

			Label preserveFittestLabel = new Label("Preserve Fittest?");
			preserveFittestCheckBox = new CheckBox();
			preserveFittestCheckBox.setSelected(true);

			root.add(preserveFittestLabel, 0, 12);
			root.add(preserveFittestCheckBox, 1, 12);

			// TODO measure in case this is supervised
			// if(!(this instanceof UnsupervisedLearningView))
			break;
		default:
			logger.info("unknown algorithm!");
		}

		root.setHgap(10);
		root.setVgap(10);
		root.setPadding(new Insets(25, 25, 25, 25));
		return root;
	}

	/**
	 * Shows if an Error occurred
	 * 
	 * @param header
	 *            Caption of the Error
	 * @param content
	 *            Error Message
	 */
	public void showErrorDialog(String header, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public MachineLearningController getMlController() {
		return mlController;
	}

	public Spinner<Integer> getInquiriesSpinner() {
		return inquiriesSpinner;
	}

	public Spinner<Integer> getMaxDurationSpinner() {
		return maxDurationSpinner;
	}

	public Spinner<Integer> getMaxIterationSpinner() {
		return maxIterationSpinner;
	}

	public Slider getMaxQualitySlider() {
		return maxQualitySlider;
	}

	public Spinner<String> getTerminationCriteriaSpinner() {
		return terminationCriteriaSpinner;
	}

	public Slider getTerminationCriteriaValueSlider() {
		return terminationCriteriaValueSlider;
	}

	public Slider getBetaSlider() {
		return betaSlider;
	}

	public Slider getGammaScoreSlider() {
		return gammaScoreSlider;
	}

	public Slider getExpansionPenaltySlider() {
		return expansionPenaltySlider;
	}

	public Slider getRewardSlider() {
		return rewardSlider;
	}

	public CheckBox getPruneCheckBox() {
		return pruneCheckBox;
	}

	public Slider getCrossoverRateSlider() {
		return crossoverRateSlider;
	}

	public Spinner<Integer> getGenerationsSpinner() {
		return generationsSpinner;
	}

	public Slider getMutationRateSlider() {
		return mutationRateSlider;
	}

	public void setMlController(MachineLearningController mlController) {
		this.mlController = mlController;
	}

	public void setInquiriesSpinner(Spinner<Integer> inquiriesSpinner) {
		this.inquiriesSpinner = inquiriesSpinner;
	}

	public void setMaxDurationSpinner(Spinner<Integer> maxDurationSpinner) {
		this.maxDurationSpinner = maxDurationSpinner;
	}

	public void setMaxIterationSpinner(Spinner<Integer> maxIterationSpinner) {
		this.maxIterationSpinner = maxIterationSpinner;
	}

	public void setMaxQualitySlider(Slider maxQualitySlider) {
		this.maxQualitySlider = maxQualitySlider;
	}

	public void setTerminationCriteriaSpinner(
			Spinner<String> terminationCriteriaSpinner) {
		this.terminationCriteriaSpinner = terminationCriteriaSpinner;
	}

	public void setTerminationCriteriaValueSlider(
			Slider terminationCriteriaValueSlider) {
		this.terminationCriteriaValueSlider = terminationCriteriaValueSlider;
	}

	public void setBetaSlider(Slider betaSlider) {
		this.betaSlider = betaSlider;
	}

	public void setGammaScoreSlider(Slider gammaScoreSlider) {
		this.gammaScoreSlider = gammaScoreSlider;
	}

	public void setExpansionPenaltySlider(Slider expansionPenaltySlider) {
		this.expansionPenaltySlider = expansionPenaltySlider;
	}

	public void setRewardSlider(Slider rewardSlider) {
		this.rewardSlider = rewardSlider;
	}

	public void setPruneCheckBox(CheckBox pruneCheckBox) {
		this.pruneCheckBox = pruneCheckBox;
	}

	public void setCrossoverRateSlider(Slider crossover) {
		this.crossoverRateSlider = crossover;
	}

	public void setGenerationsSpinner(Spinner<Integer> generations) {
		this.generationsSpinner = generations;
	}

	public void setMutationRateSlider(Slider mutationRate) {
		this.mutationRateSlider = mutationRate;
	}

	public Spinner<Integer> getPopulationSpinner() {
		return populationSpinner;
	}

	public void setPopulationSpinner(Spinner<Integer> populationSpinner) {
		this.populationSpinner = populationSpinner;
	}

	public Slider getReproductionRateSlider() {
		return reproductionRateSlider;
	}

	public void setReproductionRateSlider(Slider reproductionRateSlider) {
		this.reproductionRateSlider = reproductionRateSlider;
	}

	public CheckBox getPreserveFittestCheckBox() {
		return preserveFittestCheckBox;
	}

	public void setPreserveFittestCheckBox(CheckBox preserveFittestCheckBox) {
		this.preserveFittestCheckBox = preserveFittestCheckBox;
	}

	public Button getLearnButton() {
		return learnButton;
	}

	public void setLearnButton(Button learnButton) {
		this.learnButton = learnButton;
	}

	public MainView getMainView() {
		return mainView;
	}

	public void setMainView(MainView mainView) {
		this.mainView = mainView;
	}

}

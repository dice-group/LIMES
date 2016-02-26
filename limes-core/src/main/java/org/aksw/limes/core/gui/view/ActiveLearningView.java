package org.aksw.limes.core.gui.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import org.aksw.limes.core.gui.controller.ActiveLearningController;
import org.aksw.limes.core.gui.model.Config;

/**
 * Allows the user to set the parameters for the active learning proces as well
 * as the underlying method
 * 
 * @author Felix Brei
 *
 */
public class ActiveLearningView {

	/**
	 * Reference to the main view
	 */
	public MainView view;

	/**
	 * Corresponding controller
	 */
	private ActiveLearningController controller;

	/**
	 * Stores the crossover probability rate
	 */
	private Slider crossProbSlider;

	/**
	 * Sets the number of generations
	 */
	private Spinner<Integer> numGenSpinner;

	/**
	 * Controls the rate of mutation
	 */
	private Slider mutationSlider;

	/**
	 * Number of possible matches to be shown to the user at once
	 */
	private Spinner<Integer> inquiriesSpinner;

	/**
	 * Sets the population size
	 */
	private Spinner<Integer> popSizeSpinner;

	/**
	 * Allows the user to choose the method of learning
	 */
	private ChoiceBox<String> methodChooser;

	/**
	 * Starts the process
	 */
	private Button goButton;

	/**
	 * Notifies the User that learning is in progress
	 */
	public ProgressIndicator learningProgress;

	/**
	 * Default constructor
	 * 
	 * @param view
	 *            Stores a reference to the main view
	 * @param currentConfig
	 *            The current configuration
	 */
	public ActiveLearningView(MainView view, Config currentConfig) {
		createRootPane();
		this.view = view;
		this.controller = new ActiveLearningController(this, currentConfig);
	}

	/**
	 * Method that creates the actual window
	 */
	private void createRootPane() {

		BorderPane border = new BorderPane();

		GridPane root = new GridPane();

		Text titleLabel = new Text("Learner Configuration");
		titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		root.add(titleLabel, 0, 0);
		root.add(new Separator(), 1, 0);

		Label crossProbLabel = new Label("Crossover probability");
		Label crossProbValue = new Label("0.4");
		crossProbSlider = new Slider();
		crossProbSlider.setMin(0);
		crossProbSlider.setMax(1);
		crossProbSlider.setValue(0.4);
		crossProbSlider.setShowTickLabels(false);
		crossProbSlider.setShowTickMarks(false);
		crossProbSlider.setMajorTickUnit(0.5);
		crossProbSlider.setMinorTickCount(9);
		crossProbSlider.setSnapToTicks(false);
		crossProbSlider.setBlockIncrement(0.1);

		crossProbSlider.valueProperty().addListener(
				new ChangeListener<Number>() {
					public void changed(ObservableValue<? extends Number> ov,
							Number old_val, Number new_val) {
						crossProbValue.setText(String.format("%.1f", new_val));
					}
				});

		root.add(crossProbLabel, 0, 1);
		root.add(crossProbSlider, 1, 1);
		root.add(crossProbValue, 2, 1);

		Label numGenLabel = new Label("Number of generations");
		numGenSpinner = new Spinner<Integer>(5, 100, 5, 5);
		root.add(numGenLabel, 0, 2);
		root.add(numGenSpinner, 1, 2);

		Label mutationLabel = new Label("Mutation rate");
		Label mutationValue = new Label("0.4");
		mutationSlider = new Slider();
		mutationSlider.setMin(0);
		mutationSlider.setMax(1);
		mutationSlider.setValue(0.4);
		mutationSlider.setShowTickLabels(false);
		mutationSlider.setShowTickMarks(false);
		mutationSlider.setMajorTickUnit(0.5);
		mutationSlider.setMinorTickCount(9);
		mutationSlider.setSnapToTicks(false);
		mutationSlider.setBlockIncrement(0.1);

		mutationSlider.valueProperty().addListener(
				new ChangeListener<Number>() {
					public void changed(ObservableValue<? extends Number> ov,
							Number old_val, Number new_val) {
						mutationValue.setText(String.format("%.1f", new_val));
					}
				});

		root.add(mutationLabel, 0, 3);
		root.add(mutationSlider, 1, 3);
		root.add(mutationValue, 2, 3);

		Label inquiriesLabel = new Label("Number of inquiries to user");
		inquiriesSpinner = new Spinner<Integer>(5, 100, 10, 5);
		root.add(inquiriesLabel, 0, 4);
		root.add(inquiriesSpinner, 1, 4);

		Label popSizeLabel = new Label("Population size");
		popSizeSpinner = new Spinner<Integer>(5, 100, 10, 5);
		root.add(popSizeLabel, 0, 5);
		root.add(popSizeSpinner, 1, 5);

		Label methodChooserLabel = new Label("Learning method: ");
		methodChooser = new ChoiceBox<String>(
				FXCollections.observableArrayList(
						"Genetic Programming Batch Learner", "EAGLE",
						"Clustering", "Weight Decay"));
		methodChooser.getSelectionModel().select(0);
		root.add(methodChooserLabel, 0, 6);
		root.add(methodChooser, 1, 6);

		HBox buttonBox = new HBox();
		goButton = new Button("Start learning");
		goButton.setOnAction(e -> {
			controller.goButtonPressed();
		});
		learningProgress = new ProgressIndicator();
		learningProgress.setVisible(false);
		buttonBox.getChildren().addAll(goButton, learningProgress);
		buttonBox.setPadding(new Insets(25, 25, 25, 25));
		// root.add(goButton, 0, 7);

		root.setHgap(10);
		root.setVgap(10);
		root.setPadding(new Insets(25, 25, 25, 25));
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setMinWidth(Control.USE_PREF_SIZE);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setMinWidth(Control.USE_PREF_SIZE);
		column2.setHgrow(Priority.ALWAYS);
		root.getColumnConstraints().addAll(column1, column2);

		border.setTop(root);
		border.setBottom(buttonBox);

		Scene scene = new Scene(border, 800, 600);
		scene.getStylesheets().add("gui/main.css");
		Stage stage = new Stage();
		stage.setTitle("Active Learning Configuration");
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Gets the crossover probability rate
	 * 
	 * @return The crossover probability rate
	 */
	public double getCrossProb() {
		return crossProbSlider.getValue();
	}

	/**
	 * Gets the desired number of generations
	 * 
	 * @return Number of generations
	 */
	public int getNumGenerations() {
		return numGenSpinner.getValue();
	}

	/**
	 * Gets the rate of mutation
	 * 
	 * @return Mutation rate
	 */
	public double getMutationRate() {
		return mutationSlider.getValue();
	}

	/**
	 * Gets the number of items to be reviewed at once
	 * 
	 * @return Number of desired inquiries
	 */
	public int getNumInquiries() {
		return inquiriesSpinner.getValue();
	}

	/**
	 * Gets the size of the population for the genetic learning process
	 * 
	 * @return Population size
	 */
	public int getPopulationSize() {
		return popSizeSpinner.getValue();
	}

}

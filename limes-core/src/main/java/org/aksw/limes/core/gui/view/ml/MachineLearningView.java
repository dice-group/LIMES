package org.aksw.limes.core.gui.view.ml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

import org.aksw.limes.core.gui.controller.ml.MachineLearningController;
import org.aksw.limes.core.gui.view.MainView;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.wombat.AWombat;
import org.aksw.limes.core.ml.setting.LearningParameters;
import org.aksw.limes.core.ml.setting.LearningSetting.TerminationCriteria;
import org.apache.log4j.Logger;

public abstract class MachineLearningView {

    public static final String[] mlAlgorithms = {"Eagle", "Euclid", "Lion",
            "Wombat", "Raven", "Ukulele", "Coala", "Acids", "Mandolin",
            "Cluster"};
    protected static Logger logger = Logger.getLogger("LIMES");

    protected MachineLearningController mlController;
//    protected Spinner<Integer> inquiriesSpinner;
//    protected Spinner<Integer> maxDurationSpinner;
//    protected Spinner<Integer> maxIterationSpinner;
//    protected Slider maxQualitySlider;
//    protected Spinner<String> terminationCriteriaSpinner;
//    protected Slider terminationCriteriaValueSlider;
//    protected Slider betaSlider;
//    protected Spinner<Integer> populationSpinner;
//    protected Slider gammaScoreSlider;
//    protected Slider expansionPenaltySlider;
//    protected Slider rewardSlider;
//    protected CheckBox pruneCheckBox;
//    protected Slider crossoverRateSlider;
//    protected Spinner<Integer> generationsSpinner;
//    protected Slider mutationRateSlider;
//    protected Slider reproductionRateSlider;
//    protected CheckBox preserveFittestCheckBox;
    protected Slider maxRefinementTreeSizeSlider;
    protected Slider maxIterationsNumberSlider;
    protected Slider maxIterationsTimeInMinutesSlider;
    protected Slider executionTimeInMinutesSlider; 
    protected Slider maxFitnessThresholdSlider; 
    protected Slider minPropertyCoverageSlider; 
    protected Slider propertyLearningRateSlider; 
    protected Slider overallPenaltyWeitSlider; 
    protected Slider childrenPenaltyWeitSlider; 
    protected Slider complexityPenaltyWeitSlider; 
    protected CheckBox verboseCheckBox; 
    protected CheckBox saveMappingCheckBox; 
    protected Spinner<String> measuresSpinner; 
    private MainView mainView;
    private Button learnButton;

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
//        mloptions.add("Lion");
//        mloptions.add("Eagle");
        mloptions.add("Wombat Simple");
        mloptions.add("dont click this");
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
	    AMLAlgorithm wombat = this.mlController.getMlModel().getMlalgorithm();
            ((AWombat) wombat.getMl()).setDefaultParameters();
            Iterator<String> parameterKeyIterator = wombat.getParameters().keySet().iterator();
            LearningParameters params = wombat.getParameters();
            while(parameterKeyIterator.hasNext()){
                String key = parameterKeyIterator.next();
                logger.error(key + " : " + params.get(key));
            }
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
	switch(this.mlController.getMlModel().getMlalgorithm().getName().toLowerCase()){
	case "wombat complete":
	    root = setWombatParameters(root);
	    break;
	case "wombat simple":
//	    root = setWombatParameters(root);
	    break;
	default:
	    logger.error("Unknown Machine Learning Algorithm");
	    break;
	}
//        Label inquiriesLabel = new Label("Number of inquiries to user");
//        inquiriesSpinner = new Spinner<Integer>(5, 100, 10, 1);
//        root.add(inquiriesLabel, 0, 0);
//        root.add(inquiriesSpinner, 1, 0);
//
//        Label maxDurationLabel = new Label("Maximal duration in seconds");
//        maxDurationSpinner = new Spinner<Integer>(5, 100, 60, 1);
//        root.add(maxDurationLabel, 0, 1);
//        root.add(maxDurationSpinner, 1, 1);
//
//        Label maxIterationLabel = new Label("Maximal number of iterations");
//        maxIterationSpinner = new Spinner<Integer>(5, 100, 60, 1);
//        root.add(maxIterationLabel, 0, 2);
//        root.add(maxIterationSpinner, 1, 2);
//
//        Label maxQualityLabel = new Label(
//                "Maximal quality in (Pseudo)-F-Measure");
//        Label maxQualityValue = new Label("0.4");
//        maxQualitySlider = new Slider();
//        maxQualitySlider.setMin(0);
//        maxQualitySlider.setMax(1);
//        maxQualitySlider.setValue(0.5);
//        maxQualitySlider.setShowTickLabels(false);
//        maxQualitySlider.setShowTickMarks(false);
//        maxQualitySlider.setMajorTickUnit(0.1);
//        maxQualitySlider.setMinorTickCount(9);
//        maxQualitySlider.setSnapToTicks(false);
//        maxQualitySlider.setBlockIncrement(0.1);
//
//        maxQualitySlider.valueProperty().addListener(
//                new ChangeListener<Number>() {
//                    public void changed(ObservableValue<? extends Number> ov,
//                                        Number old_val, Number new_val) {
//                        maxQualityValue.setText(String.format("%.1f", new_val));
//                    }
//                });
//
//        root.add(maxQualityLabel, 0, 3);
//        root.add(maxQualitySlider, 1, 3);
//        root.add(maxQualityValue, 2, 3);
//
////        Label terminationCriteriaLabel = new Label("Termination criteria");
////        List<String> terminationCriteriaList = new ArrayList<String>();
////        for (int i = 0; i < TerminationCriteria.values().length; i++) {
////            terminationCriteriaList.add(TerminationCriteria.values()[i]
////                    .toString());
////        }
////        ObservableList<String> obsTermCritList = FXCollections
////                .observableList(terminationCriteriaList);
////        SpinnerValueFactory<String> svf = new SpinnerValueFactory.ListSpinnerValueFactory<>(
////                obsTermCritList);
////        terminationCriteriaSpinner = new Spinner<String>();
////        terminationCriteriaSpinner.setValueFactory(svf);
////        root.add(terminationCriteriaLabel, 0, 4);
////        root.add(terminationCriteriaSpinner, 1, 4);
////
////        Label terminationCriteriaValueLabel = new Label(
////                "Value of termination criteria");
////        Label terminationCriteriaValueValue = new Label("0");
////        terminationCriteriaValueSlider = new Slider();
////        // TODO set ranges according to criteria chosen
////        terminationCriteriaValueSlider.setMin(0);
////        terminationCriteriaValueSlider.setMax(5);
////        terminationCriteriaValueSlider.setValue(0);
////        terminationCriteriaValueSlider.setShowTickLabels(false);
////        terminationCriteriaValueSlider.setShowTickMarks(false);
////        terminationCriteriaValueSlider.setMajorTickUnit(0.1);
////        terminationCriteriaValueSlider.setMinorTickCount(9);
////        terminationCriteriaValueSlider.setSnapToTicks(false);
////        terminationCriteriaValueSlider.setBlockIncrement(0.1);
////
////        terminationCriteriaValueSlider.valueProperty().addListener(
////                new ChangeListener<Number>() {
////                    public void changed(ObservableValue<? extends Number> ov,
////                                        Number old_val, Number new_val) {
////                        terminationCriteriaValueValue.setText(String.format(
////                                "%.1f", new_val));
////                    }
////                });
////
////        root.add(terminationCriteriaValueLabel, 0, 5);
////        root.add(terminationCriteriaValueSlider, 1, 5);
////        root.add(terminationCriteriaValueValue, 2, 5);
//
//        Label betaLabel = new Label("Beta for (Pseudo)-F-Measure");
//        Label betaValue = new Label("1");
//        betaSlider = new Slider();
//        betaSlider.setMin(0);
//        betaSlider.setMax(5);
//        betaSlider.setValue(1);
//        betaSlider.setShowTickLabels(false);
//        betaSlider.setShowTickMarks(false);
//        betaSlider.setMajorTickUnit(0.1);
//        betaSlider.setMinorTickCount(9);
//        betaSlider.setSnapToTicks(false);
//        betaSlider.setBlockIncrement(0.1);
//
//        betaSlider.valueProperty().addListener(new ChangeListener<Number>() {
//            public void changed(ObservableValue<? extends Number> ov,
//                                Number old_val, Number new_val) {
//                betaValue.setText(String.format("%.1f", new_val));
//            }
//        });
//
//        root.add(betaLabel, 0, 6);
//        root.add(betaSlider, 1, 6);
//        root.add(betaValue, 2, 6);
//
//        switch (algorithm) {
//            case "Lion":
//                Label gammaScoreLabel = new Label("Gamma score");
//                Label gammaScoreValue = new Label("0.15");
//                gammaScoreSlider = new Slider();
//                gammaScoreSlider.setMin(0);
//                // TODO find out if this is reasonable
//                gammaScoreSlider.setMax(1);
//                gammaScoreSlider.setValue(0.15);
//                gammaScoreSlider.setShowTickLabels(false);
//                gammaScoreSlider.setShowTickMarks(false);
//                gammaScoreSlider.setMajorTickUnit(0.1);
//                gammaScoreSlider.setMinorTickCount(99);
//                gammaScoreSlider.setSnapToTicks(false);
//                gammaScoreSlider.setBlockIncrement(0.01);
//
//                gammaScoreSlider.valueProperty().addListener(
//                        new ChangeListener<Number>() {
//                            public void changed(
//                                    ObservableValue<? extends Number> ov,
//                                    Number old_val, Number new_val) {
//                                gammaScoreValue.setText(String.format("%.2f",
//                                        new_val));
//                            }
//                        });
//
//                root.add(gammaScoreLabel, 0, 7);
//                root.add(gammaScoreSlider, 1, 7);
//                root.add(gammaScoreValue, 2, 7);
//
//                Label expansionPenaltyLabel = new Label("Expansion Penalty");
//                Label expansionPenaltyValue = new Label("0.7");
//                expansionPenaltySlider = new Slider();
//                expansionPenaltySlider.setMin(0);
//                // TODO find out if this is reasonable
//                expansionPenaltySlider.setMax(1);
//                expansionPenaltySlider.setValue(0.7);
//                expansionPenaltySlider.setShowTickLabels(false);
//                expansionPenaltySlider.setShowTickMarks(false);
//                expansionPenaltySlider.setMajorTickUnit(0.1);
//                expansionPenaltySlider.setMinorTickCount(9);
//                expansionPenaltySlider.setSnapToTicks(false);
//                expansionPenaltySlider.setBlockIncrement(0.1);
//
//                expansionPenaltySlider.valueProperty().addListener(
//                        new ChangeListener<Number>() {
//                            public void changed(
//                                    ObservableValue<? extends Number> ov,
//                                    Number old_val, Number new_val) {
//                                expansionPenaltyValue.setText(String.format("%.1f",
//                                        new_val));
//                            }
//                        });
//
//                root.add(expansionPenaltyLabel, 0, 8);
//                root.add(expansionPenaltySlider, 1, 8);
//                root.add(expansionPenaltyValue, 2, 8);
//
//                Label rewardLabel = new Label("Reward");
//                Label rewardValue = new Label("1.2");
//                rewardSlider = new Slider();
//                rewardSlider.setMin(0);
//                // TODO find out if this is reasonable
//                rewardSlider.setMax(5);
//                rewardSlider.setValue(1.2);
//                rewardSlider.setShowTickLabels(false);
//                rewardSlider.setShowTickMarks(false);
//                rewardSlider.setMajorTickUnit(0.1);
//                rewardSlider.setMinorTickCount(9);
//                rewardSlider.setSnapToTicks(false);
//                rewardSlider.setBlockIncrement(0.1);
//
//                rewardSlider.valueProperty().addListener(
//                        new ChangeListener<Number>() {
//                            public void changed(
//                                    ObservableValue<? extends Number> ov,
//                                    Number old_val, Number new_val) {
//                                rewardValue.setText(String.format("%.1f", new_val));
//                            }
//                        });
//
//                root.add(rewardLabel, 0, 9);
//                root.add(rewardSlider, 1, 9);
//                root.add(rewardValue, 2, 9);
//
//                Label pruneLabel = new Label("Prune Tree?");
//                pruneCheckBox = new CheckBox();
//                pruneCheckBox.setSelected(true);
//
//                root.add(pruneLabel, 0, 10);
//                root.add(pruneCheckBox, 1, 10);
//                break;
//            case "Eagle":
//                crossoverRateSlider = new Slider();
//                Label crossoverLabel = new Label("0.4");
//                Label crossoverText = new Label("Crossover probability");
//                HBox crossoverBox = new HBox();
//
//                root.add(crossoverText, 0, 7);
//                crossoverBox.getChildren().add(crossoverRateSlider);
//                crossoverBox.getChildren().add(crossoverLabel);
//                root.add(crossoverBox, 1, 7);
//
//                crossoverRateSlider.setMin(0);
//                crossoverRateSlider.setMax(1);
//                crossoverRateSlider.setValue(0.4);
//                crossoverRateSlider.setShowTickLabels(true);
//                crossoverRateSlider.setShowTickMarks(false);
//                crossoverRateSlider.setMajorTickUnit(0.5);
//                crossoverRateSlider.setMinorTickCount(9);
//                crossoverRateSlider.setSnapToTicks(false);
//                crossoverRateSlider.setBlockIncrement(0.1);
//
//                crossoverRateSlider.valueProperty().addListener(
//                        new ChangeListener<Number>() {
//                            public void changed(
//                                    ObservableValue<? extends Number> ov,
//                                    Number old_val, Number new_val) {
//                                crossoverLabel.setText(String.format("%.1f",
//                                        new_val));
//                            }
//                        });
//
//                Label generationsText = new Label("Number of generations");
//                generationsSpinner = new Spinner<Integer>(1, 100, 1, 1);
//
//                root.add(generationsText, 0, 8);
//                root.add(generationsSpinner, 1, 8);
//
//                HBox mutationsBox = new HBox();
//                mutationRateSlider = new Slider();
//                Label mutationRateLabel = new Label("0.4");
//                Label mutationRateText = new Label("Mutation rate");
//
//                root.add(mutationRateText, 0, 9);
//                mutationsBox.getChildren().add(mutationRateSlider);
//                mutationsBox.getChildren().add(mutationRateLabel);
//                root.add(mutationsBox, 1, 9);
//
//                mutationRateSlider.setMin(0);
//                mutationRateSlider.setMax(1);
//                mutationRateSlider.setValue(0.4);
//                mutationRateSlider.setShowTickLabels(true);
//                mutationRateSlider.setShowTickMarks(false);
//                mutationRateSlider.setMajorTickUnit(0.5);
//                mutationRateSlider.setMinorTickCount(9);
//                mutationRateSlider.setSnapToTicks(false);
//                mutationRateSlider.setBlockIncrement(0.1);
//
//                mutationRateSlider.valueProperty().addListener(
//                        new ChangeListener<Number>() {
//                            public void changed(
//                                    ObservableValue<? extends Number> ov,
//                                    Number old_val, Number new_val) {
//                                mutationRateLabel.setText(String.format("%.1f",
//                                        new_val));
//                            }
//                        });
//
//                Label populationText = new Label("Population size");
//                populationSpinner = new Spinner<Integer>(5, 100, 5, 5);
//
//                root.add(populationText, 0, 10);
//                root.add(populationSpinner, 1, 10);
//
//                HBox reproductionBox = new HBox();
//                reproductionRateSlider = new Slider();
//                Label reproductionRateLabel = new Label("0.4");
//                Label reproductionRateText = new Label("Reproduction rate");
//
//                root.add(reproductionRateText, 0, 11);
//                reproductionBox.getChildren().add(reproductionRateSlider);
//                reproductionBox.getChildren().add(reproductionRateLabel);
//                root.add(reproductionBox, 1, 11);
//
//                reproductionRateSlider.setMin(0);
//                reproductionRateSlider.setMax(1);
//                reproductionRateSlider.setValue(0.4);
//                reproductionRateSlider.setShowTickLabels(true);
//                reproductionRateSlider.setShowTickMarks(false);
//                reproductionRateSlider.setMajorTickUnit(0.5);
//                reproductionRateSlider.setMinorTickCount(9);
//                reproductionRateSlider.setSnapToTicks(false);
//                reproductionRateSlider.setBlockIncrement(0.1);
//
//                reproductionRateSlider.valueProperty().addListener(
//                        new ChangeListener<Number>() {
//                            public void changed(
//                                    ObservableValue<? extends Number> ov,
//                                    Number old_val, Number new_val) {
//                                reproductionRateLabel.setText(String.format("%.1f",
//                                        new_val));
//                            }
//                        });
//
//                Label preserveFittestLabel = new Label("Preserve Fittest?");
//                preserveFittestCheckBox = new CheckBox();
//                preserveFittestCheckBox.setSelected(true);
//
//                root.add(preserveFittestLabel, 0, 12);
//                root.add(preserveFittestCheckBox, 1, 12);
//
//                // TODO measure in case this is supervised
//                // if(!(this instanceof UnsupervisedLearningView))
//                break;
//            default:
//                logger.info("unknown algorithm!");
//        }
//
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(25, 25, 25, 25));
        return root;
    }
    
    private GridPane setWombatParameters(GridPane root){
	AMLAlgorithm wombat = this.mlController.getMlModel().getMlalgorithm();
	((AWombat) wombat.getMl()).setDefaultParameters();
	Iterator<String> parameterKeyIterator = wombat.getParameters().keySet().iterator();
	LearningParameters params = wombat.getParameters();

        HBox maxRefinementTreeSizeBox = new HBox();
        maxRefinementTreeSizeSlider = new Slider();
        Label maxRefinementTreeSizeText = new Label(parameterKeyIterator.next());
        Label maxRefinementTreeSizeLabel = new Label(params.get(maxRefinementTreeSizeText.getText()));

        root.add(maxRefinementTreeSizeText, 0, 0);
        maxRefinementTreeSizeBox.getChildren().add(maxRefinementTreeSizeSlider);
        maxRefinementTreeSizeBox.getChildren().add(maxRefinementTreeSizeLabel);
        root.add(maxRefinementTreeSizeBox, 1, 0);

        maxRefinementTreeSizeSlider.setMin(0);
        maxRefinementTreeSizeSlider.setMax(5000);
        maxRefinementTreeSizeSlider.setValue(Double.valueOf(maxRefinementTreeSizeLabel.getText()));
        maxRefinementTreeSizeSlider.setShowTickLabels(true);
        maxRefinementTreeSizeSlider.setShowTickMarks(false);
        maxRefinementTreeSizeSlider.setMajorTickUnit(2500);
        maxRefinementTreeSizeSlider.setMinorTickCount(100);
        maxRefinementTreeSizeSlider.setSnapToTicks(true);
        maxRefinementTreeSizeSlider.setBlockIncrement(100);

        maxRefinementTreeSizeSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(
                            ObservableValue<? extends Number> ov,
                            Number old_val, Number new_val) {
                        maxRefinementTreeSizeLabel.setText(String.format("%d",
                                new_val));
                    }
                });

        HBox maxIterationsNumberBox = new HBox();
        maxIterationsNumberSlider = new Slider();
        Label maxIterationsNumberText = new Label(parameterKeyIterator.next());
        Label maxIterationsNumberLabel = new Label(params.get(maxIterationsNumberText.getText()));

        root.add(maxIterationsNumberText, 0, 1);
        maxIterationsNumberBox.getChildren().add(maxIterationsNumberSlider);
        maxIterationsNumberBox.getChildren().add(maxIterationsNumberLabel);
        root.add(maxIterationsNumberBox, 1, 1);

        maxIterationsNumberSlider.setMin(0);
        maxIterationsNumberSlider.setMax(100);
        maxIterationsNumberSlider.setValue(Double.valueOf(maxIterationsNumberLabel.getText()));
        maxIterationsNumberSlider.setShowTickLabels(true);
        maxIterationsNumberSlider.setShowTickMarks(false);
        maxIterationsNumberSlider.setMajorTickUnit(50);
        maxIterationsNumberSlider.setMinorTickCount(1);
        maxIterationsNumberSlider.setSnapToTicks(true);
        maxIterationsNumberSlider.setBlockIncrement(1);

        maxIterationsNumberSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(
                            ObservableValue<? extends Number> ov,
                            Number old_val, Number new_val) {
                        maxIterationsNumberLabel.setText(String.format("%d",
                                new_val));
                    }
                });

        HBox maxIterationsTimeInMinutesBox = new HBox();
        maxIterationsTimeInMinutesSlider = new Slider();
        Label maxIterationsTimeInMinutesText = new Label(parameterKeyIterator.next());
        Label maxIterationsTimeInMinutesLabel = new Label(params.get(maxIterationsTimeInMinutesText.getText()));

        root.add(maxIterationsTimeInMinutesText, 0, 2);
        maxIterationsTimeInMinutesBox.getChildren().add(maxIterationsTimeInMinutesSlider);
        maxIterationsTimeInMinutesBox.getChildren().add(maxIterationsTimeInMinutesLabel);
        root.add(maxIterationsTimeInMinutesBox, 1, 2);

        maxIterationsTimeInMinutesSlider.setMin(0);
        maxIterationsTimeInMinutesSlider.setMax(100);
        maxIterationsTimeInMinutesSlider.setValue(Double.valueOf(maxIterationsTimeInMinutesLabel.getText()));
        maxIterationsTimeInMinutesSlider.setShowTickLabels(true);
        maxIterationsTimeInMinutesSlider.setShowTickMarks(false);
        maxIterationsTimeInMinutesSlider.setMajorTickUnit(50);
        maxIterationsTimeInMinutesSlider.setMinorTickCount(1);
        maxIterationsTimeInMinutesSlider.setSnapToTicks(true);
        maxIterationsTimeInMinutesSlider.setBlockIncrement(1);

        maxIterationsTimeInMinutesSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(
                            ObservableValue<? extends Number> ov,
                            Number old_val, Number new_val) {
                        maxIterationsTimeInMinutesLabel.setText(String.format("%d",
                                new_val));
                    }
                });

        HBox executionTimeInMinutesBox = new HBox();
        executionTimeInMinutesSlider = new Slider();
        Label executionTimeInMinutesText = new Label(parameterKeyIterator.next());
        Label executionTimeInMinutesLabel = new Label(params.get(executionTimeInMinutesText.getText()));

        root.add(executionTimeInMinutesText, 0, 3);
        executionTimeInMinutesBox.getChildren().add(executionTimeInMinutesSlider);
        executionTimeInMinutesBox.getChildren().add(executionTimeInMinutesLabel);
        root.add(executionTimeInMinutesBox, 1, 3);

        executionTimeInMinutesSlider.setMin(0);
        executionTimeInMinutesSlider.setMax(100);
        executionTimeInMinutesSlider.setValue(Double.valueOf(executionTimeInMinutesLabel.getText()));
        executionTimeInMinutesSlider.setShowTickLabels(true);
        executionTimeInMinutesSlider.setShowTickMarks(false);
        executionTimeInMinutesSlider.setMajorTickUnit(50);
        executionTimeInMinutesSlider.setMinorTickCount(1);
        executionTimeInMinutesSlider.setSnapToTicks(true);
        executionTimeInMinutesSlider.setBlockIncrement(1);

        executionTimeInMinutesSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(
                            ObservableValue<? extends Number> ov,
                            Number old_val, Number new_val) {
                        executionTimeInMinutesLabel.setText(String.format("%d",
                                new_val));
                    }
                });

        HBox maxFitnessThresholdBox = new HBox();
        maxFitnessThresholdSlider = new Slider();
        Label maxFitnessThresholdText = new Label(parameterKeyIterator.next());
        Label maxFitnessThresholdLabel = new Label(params.get(maxFitnessThresholdText.getText()));

        root.add(maxFitnessThresholdText, 0, 4);
        maxFitnessThresholdBox.getChildren().add(maxFitnessThresholdSlider);
        maxFitnessThresholdBox.getChildren().add(maxFitnessThresholdLabel);
        root.add(maxFitnessThresholdBox, 1, 4);

        maxFitnessThresholdSlider.setMin(0);
        maxFitnessThresholdSlider.setMax(1);
        maxFitnessThresholdSlider.setValue(Double.valueOf(maxFitnessThresholdLabel.getText()));
        maxFitnessThresholdSlider.setShowTickLabels(true);
        maxFitnessThresholdSlider.setShowTickMarks(false);
        maxFitnessThresholdSlider.setMajorTickUnit(0.5);
        maxFitnessThresholdSlider.setMinorTickCount(9);
        maxFitnessThresholdSlider.setSnapToTicks(true);
        maxFitnessThresholdSlider.setBlockIncrement(0.01);

        maxFitnessThresholdSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(
                            ObservableValue<? extends Number> ov,
                            Number old_val, Number new_val) {
                        maxFitnessThresholdLabel.setText(String.format("%.1f",
                                new_val));
                    }
                });

        HBox minPropertyCoverageBox = new HBox();
        minPropertyCoverageSlider = new Slider();
        Label minPropertyCoverageText = new Label(parameterKeyIterator.next());
        Label minPropertyCoverageLabel = new Label(params.get(minPropertyCoverageText.getText()));

        root.add(minPropertyCoverageText, 0, 5);
        minPropertyCoverageBox.getChildren().add(minPropertyCoverageSlider);
        minPropertyCoverageBox.getChildren().add(minPropertyCoverageLabel);
        root.add(minPropertyCoverageBox, 1, 5);

        minPropertyCoverageSlider.setMin(0);
        minPropertyCoverageSlider.setMax(1);
        minPropertyCoverageSlider.setValue(Double.valueOf(minPropertyCoverageLabel.getText()));
        minPropertyCoverageSlider.setShowTickLabels(true);
        minPropertyCoverageSlider.setShowTickMarks(false);
        minPropertyCoverageSlider.setMajorTickUnit(0.5);
        minPropertyCoverageSlider.setMinorTickCount(9);
        minPropertyCoverageSlider.setSnapToTicks(true);
        minPropertyCoverageSlider.setBlockIncrement(0.01);

        minPropertyCoverageSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(
                            ObservableValue<? extends Number> ov,
                            Number old_val, Number new_val) {
                        minPropertyCoverageLabel.setText(String.format("%.1f",
                                new_val));
                    }
                });

        HBox propertyLearningRateBox = new HBox();
        propertyLearningRateSlider = new Slider();
        Label propertyLearningRateText = new Label(parameterKeyIterator.next());
        Label propertyLearningRateLabel = new Label(params.get(propertyLearningRateText.getText()));

        root.add(propertyLearningRateText, 0, 6);
        propertyLearningRateBox.getChildren().add(propertyLearningRateSlider);
        propertyLearningRateBox.getChildren().add(propertyLearningRateLabel);
        root.add(propertyLearningRateBox, 1, 6);

        propertyLearningRateSlider.setMin(0);
        propertyLearningRateSlider.setMax(1);
        propertyLearningRateSlider.setValue(Double.valueOf(propertyLearningRateLabel.getText()));
        propertyLearningRateSlider.setShowTickLabels(true);
        propertyLearningRateSlider.setShowTickMarks(false);
        propertyLearningRateSlider.setMajorTickUnit(0.5);
        propertyLearningRateSlider.setMinorTickCount(9);
        propertyLearningRateSlider.setSnapToTicks(true);
        propertyLearningRateSlider.setBlockIncrement(0.01);

        propertyLearningRateSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(
                            ObservableValue<? extends Number> ov,
                            Number old_val, Number new_val) {
                        propertyLearningRateLabel.setText(String.format("%.1f",
                                new_val));
                    }
                });

        HBox overallPenaltyWeitBox = new HBox();
        overallPenaltyWeitSlider = new Slider();
        Label overallPenaltyWeitText = new Label(parameterKeyIterator.next());
        Label overallPenaltyWeitLabel = new Label(params.get(overallPenaltyWeitText.getText()));

        root.add(overallPenaltyWeitText, 0, 7);
        overallPenaltyWeitBox.getChildren().add(overallPenaltyWeitSlider);
        overallPenaltyWeitBox.getChildren().add(overallPenaltyWeitLabel);
        root.add(overallPenaltyWeitBox, 1, 7);

        overallPenaltyWeitSlider.setMin(0);
        overallPenaltyWeitSlider.setMax(1);
        logger.error("text: " + overallPenaltyWeitLabel.getText());
        overallPenaltyWeitSlider.setValue(Double.valueOf(overallPenaltyWeitLabel.getText()));
        overallPenaltyWeitSlider.setShowTickLabels(true);
        overallPenaltyWeitSlider.setShowTickMarks(false);
        overallPenaltyWeitSlider.setMajorTickUnit(0.5);
        overallPenaltyWeitSlider.setMinorTickCount(9);
        overallPenaltyWeitSlider.setSnapToTicks(true);
        overallPenaltyWeitSlider.setBlockIncrement(0.01);

        overallPenaltyWeitSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(
                            ObservableValue<? extends Number> ov,
                            Number old_val, Number new_val) {
                        overallPenaltyWeitLabel.setText(String.format("%.1f",
                                new_val));
                    }
                });

        HBox childrenPenaltyWeitBox = new HBox();
        childrenPenaltyWeitSlider = new Slider();
        Label childrenPenaltyWeitText = new Label(parameterKeyIterator.next());
        Label childrenPenaltyWeitLabel = new Label(params.get(childrenPenaltyWeitText.getText()));

        root.add(childrenPenaltyWeitText, 0, 8);
        childrenPenaltyWeitBox.getChildren().add(childrenPenaltyWeitSlider);
        childrenPenaltyWeitBox.getChildren().add(childrenPenaltyWeitLabel);
        root.add(childrenPenaltyWeitBox, 1, 8);

        childrenPenaltyWeitSlider.setMin(0);
        childrenPenaltyWeitSlider.setMax(100);
        childrenPenaltyWeitSlider.setValue(Double.valueOf(childrenPenaltyWeitLabel.getText()));
        childrenPenaltyWeitSlider.setShowTickLabels(true);
        childrenPenaltyWeitSlider.setShowTickMarks(false);
        childrenPenaltyWeitSlider.setMajorTickUnit(50);
        childrenPenaltyWeitSlider.setMinorTickCount(10);
        childrenPenaltyWeitSlider.setSnapToTicks(true);
        childrenPenaltyWeitSlider.setBlockIncrement(1);

        childrenPenaltyWeitSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(
                            ObservableValue<? extends Number> ov,
                            Number old_val, Number new_val) {
                        childrenPenaltyWeitLabel.setText(String.format("%d",
                                new_val));
                    }
                });

        HBox complexityPenaltyWeitBox = new HBox();
        complexityPenaltyWeitSlider = new Slider();
        Label complexityPenaltyWeitText = new Label(parameterKeyIterator.next());
        Label complexityPenaltyWeitLabel = new Label(params.get(complexityPenaltyWeitText.getText()));

        root.add(complexityPenaltyWeitText, 0, 9);
        complexityPenaltyWeitBox.getChildren().add(complexityPenaltyWeitSlider);
        complexityPenaltyWeitBox.getChildren().add(complexityPenaltyWeitLabel);
        root.add(complexityPenaltyWeitBox, 1, 9);

        complexityPenaltyWeitSlider.setMin(0);
        complexityPenaltyWeitSlider.setMax(100);
        complexityPenaltyWeitSlider.setValue(Double.valueOf(complexityPenaltyWeitLabel.getText()));
        complexityPenaltyWeitSlider.setShowTickLabels(true);
        complexityPenaltyWeitSlider.setShowTickMarks(false);
        complexityPenaltyWeitSlider.setMajorTickUnit(50);
        complexityPenaltyWeitSlider.setMinorTickCount(10);
        complexityPenaltyWeitSlider.setSnapToTicks(true);
        complexityPenaltyWeitSlider.setBlockIncrement(1);

        complexityPenaltyWeitSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(
                            ObservableValue<? extends Number> ov,
                            Number old_val, Number new_val) {
                        complexityPenaltyWeitLabel.setText(String.format("%d",
                                new_val));
                    }
                });

        Label verboseLabel = new Label(parameterKeyIterator.next());
        verboseCheckBox = new CheckBox();
        verboseCheckBox.setSelected(false);
        root.add(verboseLabel, 0, 10);
        root.add(verboseCheckBox, 1, 10);

        Label measuresLabel = new Label(parameterKeyIterator.next());
        List<String> measuresList = new ArrayList<String>();
        String[] measureArray = params.get(measuresLabel.getText()).replace("[","").replace("]", "").split(",");
        for (int i = 0; i < measureArray.length; i++) {
            measuresList.add(measureArray[i]);
        }
        ObservableList<String> obsTermCritList = FXCollections
                .observableList(measuresList);
        SpinnerValueFactory<String> svf = new SpinnerValueFactory.ListSpinnerValueFactory<>(
                obsTermCritList);
        measuresSpinner = new Spinner<String>();
        measuresSpinner.setValueFactory(svf);
        root.add(measuresLabel, 0, 11);
        root.add(measuresSpinner, 1, 11);

        Label saveMappingLabel = new Label(parameterKeyIterator.next());
        saveMappingCheckBox = new CheckBox();
        saveMappingCheckBox.setSelected(false);
        root.add(saveMappingLabel, 0, 12);
        root.add(saveMappingCheckBox, 1, 12);

	return root;
    }

    /**
     * Shows if an Error occurred
     *
     * @param header
     *         Caption of the Error
     * @param content
     *         Error Message
     */
    public void showErrorDialog(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

//    public MachineLearningController getMlController() {
//        return mlController;
//    }
//
//    public void setMlController(MachineLearningController mlController) {
//        this.mlController = mlController;
//    }
//
//    public Spinner<Integer> getInquiriesSpinner() {
//        return inquiriesSpinner;
//    }
//
//    public void setInquiriesSpinner(Spinner<Integer> inquiriesSpinner) {
//        this.inquiriesSpinner = inquiriesSpinner;
//    }
//
//    public Spinner<Integer> getMaxDurationSpinner() {
//        return maxDurationSpinner;
//    }
//
//    public void setMaxDurationSpinner(Spinner<Integer> maxDurationSpinner) {
//        this.maxDurationSpinner = maxDurationSpinner;
//    }
//
//    public Spinner<Integer> getMaxIterationSpinner() {
//        return maxIterationSpinner;
//    }
//
//    public void setMaxIterationSpinner(Spinner<Integer> maxIterationSpinner) {
//        this.maxIterationSpinner = maxIterationSpinner;
//    }
//
//    public Slider getMaxQualitySlider() {
//        return maxQualitySlider;
//    }
//
//    public void setMaxQualitySlider(Slider maxQualitySlider) {
//        this.maxQualitySlider = maxQualitySlider;
//    }
//
//    public Spinner<String> getTerminationCriteriaSpinner() {
//        return terminationCriteriaSpinner;
//    }
//
//    public void setTerminationCriteriaSpinner(
//            Spinner<String> terminationCriteriaSpinner) {
//        this.terminationCriteriaSpinner = terminationCriteriaSpinner;
//    }
//
//    public Slider getTerminationCriteriaValueSlider() {
//        return terminationCriteriaValueSlider;
//    }
//
//    public void setTerminationCriteriaValueSlider(
//            Slider terminationCriteriaValueSlider) {
//        this.terminationCriteriaValueSlider = terminationCriteriaValueSlider;
//    }
//
//    public Slider getBetaSlider() {
//        return betaSlider;
//    }
//
//    public void setBetaSlider(Slider betaSlider) {
//        this.betaSlider = betaSlider;
//    }
//
//    public Slider getGammaScoreSlider() {
//        return gammaScoreSlider;
//    }
//
//    public void setGammaScoreSlider(Slider gammaScoreSlider) {
//        this.gammaScoreSlider = gammaScoreSlider;
//    }
//
//    public Slider getExpansionPenaltySlider() {
//        return expansionPenaltySlider;
//    }
//
//    public void setExpansionPenaltySlider(Slider expansionPenaltySlider) {
//        this.expansionPenaltySlider = expansionPenaltySlider;
//    }
//
//    public Slider getRewardSlider() {
//        return rewardSlider;
//    }
//
//    public void setRewardSlider(Slider rewardSlider) {
//        this.rewardSlider = rewardSlider;
//    }
//
//    public CheckBox getPruneCheckBox() {
//        return pruneCheckBox;
//    }
//
//    public void setPruneCheckBox(CheckBox pruneCheckBox) {
//        this.pruneCheckBox = pruneCheckBox;
//    }
//
//    public Slider getCrossoverRateSlider() {
//        return crossoverRateSlider;
//    }
//
//    public void setCrossoverRateSlider(Slider crossover) {
//        this.crossoverRateSlider = crossover;
//    }
//
//    public Spinner<Integer> getGenerationsSpinner() {
//        return generationsSpinner;
//    }
//
//    public void setGenerationsSpinner(Spinner<Integer> generations) {
//        this.generationsSpinner = generations;
//    }
//
//    public Slider getMutationRateSlider() {
//        return mutationRateSlider;
//    }
//
//    public void setMutationRateSlider(Slider mutationRate) {
//        this.mutationRateSlider = mutationRate;
//    }
//
//    public Spinner<Integer> getPopulationSpinner() {
//        return populationSpinner;
//    }
//
//    public void setPopulationSpinner(Spinner<Integer> populationSpinner) {
//        this.populationSpinner = populationSpinner;
//    }
//
//    public Slider getReproductionRateSlider() {
//        return reproductionRateSlider;
//    }
//
//    public void setReproductionRateSlider(Slider reproductionRateSlider) {
//        this.reproductionRateSlider = reproductionRateSlider;
//    }
//
//    public CheckBox getPreserveFittestCheckBox() {
//        return preserveFittestCheckBox;
//    }
//
//    public void setPreserveFittestCheckBox(CheckBox preserveFittestCheckBox) {
//        this.preserveFittestCheckBox = preserveFittestCheckBox;
//    }
//
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

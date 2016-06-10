package org.aksw.limes.core.gui.view.ml;

import java.util.List;
import java.util.Set;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.aksw.limes.core.gui.controller.ml.MachineLearningController;
import org.aksw.limes.core.gui.view.MainView;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.setting.LearningParameter;
import org.apache.log4j.Logger;

public abstract class MachineLearningView {

    protected static Logger logger = Logger.getLogger("LIMES");

    protected MachineLearningController mlController;
    private MainView mainView;
    private Button learnButton;

    public MachineLearningView(MainView mainView, MachineLearningController mlController) {
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
	// mloptions.add("Lion");
	// mloptions.add("Eagle");
	mloptions.add("Wombat Simple");
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

	mlOptionsChooser.setOnAction(e -> {
	    root.getChildren().removeAll(root.getChildren());
	    this.mlController.setMLAlgorithmToModel(mlOptionsChooser.getValue());
	    showParameters(root, mlOptionsChooser.getValue());
	    border.setCenter(root);
	    learnButton.setDisable(false);
	});

	learnButton.setOnAction(e -> {
	    this.mlController.setParameters();
	    learnButton.setDisable(true);
	    // new
	    // MLPropertyMatchingView(this.mlController.getMlModel().getConfig(),
	    // this);
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

	AMLAlgorithm mlalgorithm = this.mlController.getMlModel().getMlalgorithm();
	// mlalgorithm.getMl().setDefaultParameters();
	List<LearningParameter> params = mlalgorithm.getParameters();
	for (int i = 0; i < params.size(); i++) {
	    switch (params.get(i).getClazz().getSimpleName().toString().toLowerCase().trim()) {
	    case "integer":
		addNumberParameterHBox(params.get(i), root, i, true);
		break;
	    case "double":
		addNumberParameterHBox(params.get(i), root, i, false);
		break;
	    case "boolean":
		addBooleanParameterHBox(params.get(i), root, i);
		break;
	    case "measuretype":
		addMeasureTypeParameterHBox(params.get(i), root, i);
		break;
	    default:
		logger.error("Unknown LearningParameter clazz " + params.get(i).getClazz().getSimpleName().toString().toLowerCase().trim() + "!");
		break;
	    }
	}
	root.setHgap(10);
	root.setVgap(10);
	root.setPadding(new Insets(25, 25, 25, 25));
	return root;
    }

    private void addNumberParameterHBox(LearningParameter param, GridPane root, int position, boolean integer) {

	HBox parameterBox = new HBox();
	Slider parameterSlider = new Slider();
	Label parameterText = new Label(param.getName());
	Label parameterLabel = new Label(String.valueOf(param.getValue()));

	root.add(parameterText, 0, position);
	parameterBox.getChildren().add(parameterSlider);
	parameterBox.getChildren().add(parameterLabel);
	root.add(parameterBox, 1, position);

	parameterSlider.setTooltip(new Tooltip(param.getDescription()));
	parameterSlider.setMin(param.getRangeStart());
	parameterSlider.setMax(param.getRangeEnd());
	parameterSlider.setValue(Double.valueOf(parameterLabel.getText()));
	parameterSlider.setShowTickLabels(true);
	parameterSlider.setShowTickMarks(false);
	parameterSlider.setMajorTickUnit(parameterSlider.getMax() / 2);
	parameterSlider.setMinorTickCount((int) param.getRangeStep());
	parameterSlider.setSnapToTicks(false);
	parameterSlider.setBlockIncrement(param.getRangeStep());

	parameterSlider.valueProperty().addListener(new ChangeListener<Number>() {
	    public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
		if (integer) {
		    parameterLabel.setText(String.format("%.0f", new_val));
		} else {
		    parameterLabel.setText(String.format("%.2f", new_val));
		}
	    }
	});
    }

    private void addBooleanParameterHBox(LearningParameter param, GridPane root, int position) {
	Label parameterLabel = new Label(param.getName());
	CheckBox parameterCheckBox = new CheckBox();
	parameterCheckBox.setTooltip(new Tooltip(param.getDescription()));
	parameterCheckBox.setSelected(false);
	root.add(parameterLabel, 0, position);
	root.add(parameterCheckBox, 1, position);
    }

    @SuppressWarnings("unchecked")
    private void addMeasureTypeParameterHBox(LearningParameter param, GridPane root, int position) {
	Label parameterLabel = new Label(param.getName());
	ListView<MeasureTypeItem> parameterListView = new ListView<>();
	for (int i = 0; i < MeasureType.values().length; i++) {
	    String measure = MeasureType.values()[i].toString().toLowerCase();
	    MeasureTypeItem item = null;
	    if(((Set<String>)param.getValue()).contains(measure)){
		item = new MeasureTypeItem(measure, true);
	    }else{
		item = new MeasureTypeItem(measure, false);
	    }

	    parameterListView.getItems().add(item);
	}
	parameterListView.setCellFactory(CheckBoxListCell.forListView(new Callback<MeasureTypeItem, ObservableValue<Boolean>>() {
	    @Override
	    public ObservableValue<Boolean> call(MeasureTypeItem item) {
		return item.onProperty();
	    }
	}));
	parameterListView.setOnMouseClicked(new EventHandler<Event>() {
	    @Override
	    public void handle(Event event) {
		parameterListView.getSelectionModel().getSelectedItem().setOn(!parameterListView.getSelectionModel().getSelectedItem().isOn());
	    }
	});
	root.add(parameterLabel, 0, position);
	root.add(parameterListView, 1, position);

    }

    public static class MeasureTypeItem {
	private final StringProperty name = new SimpleStringProperty();
	private final BooleanProperty on = new SimpleBooleanProperty();

	public MeasureTypeItem(String name, boolean on) {
	    setName(name);
	    setOn(on);
	}

	public final StringProperty nameProperty() {
	    return this.name;
	}

	public final String getName() {
	    return this.nameProperty().get();
	}

	public final void setName(final String name) {
	    this.nameProperty().set(name);
	}

	public final BooleanProperty onProperty() {
	    return this.on;
	}

	public final boolean isOn() {
	    return this.onProperty().get();
	}

	public final void setOn(final boolean on) {
	    this.onProperty().set(on);
	}

	@Override
	public String toString() {
	    return getName();
	}

    }

    /*
     * private GridPane setWombatParameters(GridPane root){ AMLAlgorithm wombat
     * = this.mlController.getMlModel().getMlalgorithm(); ((AWombat)
     * wombat.getMl()).setDefaultParameters(); LearningParameters params =
     * wombat.getParameters();
     * 
     * HBox maxRefinementTreeSizeBox = new HBox(); maxRefinementTreeSizeSlider =
     * new Slider(); Label maxRefinementTreeSizeText = new
     * Label(AWombat.PARAMETER_MAX_REFINEMENT_TREE_SIZE); Label
     * maxRefinementTreeSizeLabel = new
     * Label(params.get(AWombat.PARAMETER_MAX_REFINEMENT_TREE_SIZE));
     * 
     * root.add(maxRefinementTreeSizeText, 0, 0);
     * maxRefinementTreeSizeBox.getChildren().add(maxRefinementTreeSizeSlider);
     * maxRefinementTreeSizeBox.getChildren().add(maxRefinementTreeSizeLabel);
     * root.add(maxRefinementTreeSizeBox, 1, 0);
     * 
     * maxRefinementTreeSizeSlider.setMin(0);
     * maxRefinementTreeSizeSlider.setMax(5000);
     * maxRefinementTreeSizeSlider.setValue
     * (Integer.valueOf(params.get(AWombat.PARAMETER_MAX_REFINEMENT_TREE_SIZE
     * ))); maxRefinementTreeSizeSlider.setShowTickLabels(true);
     * maxRefinementTreeSizeSlider.setShowTickMarks(false);
     * maxRefinementTreeSizeSlider.setMajorTickUnit(2500);
     * maxRefinementTreeSizeSlider.setMinorTickCount(100);
     * maxRefinementTreeSizeSlider.setSnapToTicks(true);
     * maxRefinementTreeSizeSlider.setBlockIncrement(100);
     * 
     * maxRefinementTreeSizeSlider.valueProperty().addListener( new
     * ChangeListener<Number>() { public void changed( ObservableValue<? extends
     * Number> ov, Number old_val, Number new_val) {
     * maxRefinementTreeSizeLabel.setText(String.format("%.0f", new_val)); } });
     * 
     * HBox maxIterationsNumberBox = new HBox(); maxIterationsNumberSlider = new
     * Slider(); Label maxIterationsNumberText = new
     * Label(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER); Label
     * maxIterationsNumberLabel = new
     * Label(params.get(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER));
     * 
     * root.add(maxIterationsNumberText, 0, 1);
     * maxIterationsNumberBox.getChildren().add(maxIterationsNumberSlider);
     * maxIterationsNumberBox.getChildren().add(maxIterationsNumberLabel);
     * root.add(maxIterationsNumberBox, 1, 1);
     * 
     * maxIterationsNumberSlider.setMin(0);
     * maxIterationsNumberSlider.setMax(100);
     * maxIterationsNumberSlider.setValue(
     * Integer.valueOf(params.get(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER)));
     * maxIterationsNumberSlider.setShowTickLabels(true);
     * maxIterationsNumberSlider.setShowTickMarks(false);
     * maxIterationsNumberSlider.setMajorTickUnit(50);
     * maxIterationsNumberSlider.setMinorTickCount(1);
     * maxIterationsNumberSlider.setSnapToTicks(true);
     * maxIterationsNumberSlider.setBlockIncrement(1);
     * 
     * maxIterationsNumberSlider.valueProperty().addListener( new
     * ChangeListener<Number>() { public void changed( ObservableValue<? extends
     * Number> ov, Number old_val, Number new_val) {
     * maxIterationsNumberLabel.setText(String.format("%.0f", new_val)); } });
     * 
     * HBox maxIterationsTimeInMinutesBox = new HBox();
     * maxIterationsTimeInMinutesSlider = new Slider(); Label
     * maxIterationsTimeInMinutesText = new
     * Label(AWombat.PARAMETER_MAX_ITERATION_TIME_IN_MINUTES); Label
     * maxIterationsTimeInMinutesLabel = new
     * Label(params.get(AWombat.PARAMETER_MAX_ITERATION_TIME_IN_MINUTES));
     * 
     * root.add(maxIterationsTimeInMinutesText, 0, 2);
     * maxIterationsTimeInMinutesBox
     * .getChildren().add(maxIterationsTimeInMinutesSlider);
     * maxIterationsTimeInMinutesBox
     * .getChildren().add(maxIterationsTimeInMinutesLabel);
     * root.add(maxIterationsTimeInMinutesBox, 1, 2);
     * 
     * maxIterationsTimeInMinutesSlider.setMin(0);
     * maxIterationsTimeInMinutesSlider.setMax(100);
     * maxIterationsTimeInMinutesSlider
     * .setValue(Integer.valueOf(params.get(AWombat
     * .PARAMETER_MAX_ITERATION_TIME_IN_MINUTES)));
     * maxIterationsTimeInMinutesSlider.setShowTickLabels(true);
     * maxIterationsTimeInMinutesSlider.setShowTickMarks(false);
     * maxIterationsTimeInMinutesSlider.setMajorTickUnit(50);
     * maxIterationsTimeInMinutesSlider.setMinorTickCount(1);
     * maxIterationsTimeInMinutesSlider.setSnapToTicks(true);
     * maxIterationsTimeInMinutesSlider.setBlockIncrement(1);
     * 
     * maxIterationsTimeInMinutesSlider.valueProperty().addListener( new
     * ChangeListener<Number>() { public void changed( ObservableValue<? extends
     * Number> ov, Number old_val, Number new_val) {
     * maxIterationsTimeInMinutesLabel.setText(String.format("%.0f", new_val));
     * } });
     * 
     * HBox executionTimeInMinutesBox = new HBox(); executionTimeInMinutesSlider
     * = new Slider(); Label executionTimeInMinutesText = new
     * Label(AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES); Label
     * executionTimeInMinutesLabel = new
     * Label(params.get(AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES));
     * 
     * root.add(executionTimeInMinutesText, 0, 3);
     * executionTimeInMinutesBox.getChildren
     * ().add(executionTimeInMinutesSlider);
     * executionTimeInMinutesBox.getChildren().add(executionTimeInMinutesLabel);
     * root.add(executionTimeInMinutesBox, 1, 3);
     * 
     * executionTimeInMinutesSlider.setMin(0);
     * executionTimeInMinutesSlider.setMax(100);
     * executionTimeInMinutesSlider.setValue
     * (Integer.valueOf(params.get(AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES
     * ))); executionTimeInMinutesSlider.setShowTickLabels(true);
     * executionTimeInMinutesSlider.setShowTickMarks(false);
     * executionTimeInMinutesSlider.setMajorTickUnit(50);
     * executionTimeInMinutesSlider.setMinorTickCount(1);
     * executionTimeInMinutesSlider.setSnapToTicks(true);
     * executionTimeInMinutesSlider.setBlockIncrement(1);
     * 
     * executionTimeInMinutesSlider.valueProperty().addListener( new
     * ChangeListener<Number>() { public void changed( ObservableValue<? extends
     * Number> ov, Number old_val, Number new_val) {
     * executionTimeInMinutesLabel.setText(String.format("%.0f", new_val)); }
     * });
     * 
     * HBox maxFitnessThresholdBox = new HBox(); maxFitnessThresholdSlider = new
     * Slider(); Label maxFitnessThresholdText = new
     * Label(AWombat.PARAMETER_MAX_FITNESS_THRESHOLD); Label
     * maxFitnessThresholdLabel = new
     * Label(params.get(AWombat.PARAMETER_MAX_FITNESS_THRESHOLD));
     * 
     * root.add(maxFitnessThresholdText, 0, 4);
     * maxFitnessThresholdBox.getChildren().add(maxFitnessThresholdSlider);
     * maxFitnessThresholdBox.getChildren().add(maxFitnessThresholdLabel);
     * root.add(maxFitnessThresholdBox, 1, 4);
     * 
     * maxFitnessThresholdSlider.setMin(0); maxFitnessThresholdSlider.setMax(1);
     * maxFitnessThresholdSlider
     * .setValue(Double.valueOf(params.get(AWombat.PARAMETER_MAX_FITNESS_THRESHOLD
     * ))); maxFitnessThresholdSlider.setShowTickLabels(true);
     * maxFitnessThresholdSlider.setShowTickMarks(false);
     * maxFitnessThresholdSlider.setMajorTickUnit(0.5);
     * maxFitnessThresholdSlider.setMinorTickCount(9);
     * maxFitnessThresholdSlider.setSnapToTicks(true);
     * maxFitnessThresholdSlider.setBlockIncrement(0.01);
     * 
     * maxFitnessThresholdSlider.valueProperty().addListener( new
     * ChangeListener<Number>() { public void changed( ObservableValue<? extends
     * Number> ov, Number old_val, Number new_val) {
     * maxFitnessThresholdLabel.setText(String.format("%.1f", new_val)); } });
     * 
     * HBox minPropertyCoverageBox = new HBox(); minPropertyCoverageSlider = new
     * Slider(); Label minPropertyCoverageText = new
     * Label(AWombat.PARAMETER_MIN_PROPERTY_COVERAGE); Label
     * minPropertyCoverageLabel = new
     * Label(params.get(AWombat.PARAMETER_MIN_PROPERTY_COVERAGE));
     * 
     * root.add(minPropertyCoverageText, 0, 5);
     * minPropertyCoverageBox.getChildren().add(minPropertyCoverageSlider);
     * minPropertyCoverageBox.getChildren().add(minPropertyCoverageLabel);
     * root.add(minPropertyCoverageBox, 1, 5);
     * 
     * minPropertyCoverageSlider.setMin(0); minPropertyCoverageSlider.setMax(1);
     * minPropertyCoverageSlider
     * .setValue(Double.valueOf(params.get(AWombat.PARAMETER_MIN_PROPERTY_COVERAGE
     * ))); minPropertyCoverageSlider.setShowTickLabels(true);
     * minPropertyCoverageSlider.setShowTickMarks(false);
     * minPropertyCoverageSlider.setMajorTickUnit(0.5);
     * minPropertyCoverageSlider.setMinorTickCount(9);
     * minPropertyCoverageSlider.setSnapToTicks(true);
     * minPropertyCoverageSlider.setBlockIncrement(0.01);
     * 
     * minPropertyCoverageSlider.valueProperty().addListener( new
     * ChangeListener<Number>() { public void changed( ObservableValue<? extends
     * Number> ov, Number old_val, Number new_val) {
     * minPropertyCoverageLabel.setText(String.format("%.1f", new_val)); } });
     * 
     * HBox propertyLearningRateBox = new HBox(); propertyLearningRateSlider =
     * new Slider(); Label propertyLearningRateText = new
     * Label(AWombat.PARAMETER_PROPERTY_LEARNING_RATE); Label
     * propertyLearningRateLabel = new
     * Label(params.get(AWombat.PARAMETER_PROPERTY_LEARNING_RATE));
     * 
     * root.add(propertyLearningRateText, 0, 6);
     * propertyLearningRateBox.getChildren().add(propertyLearningRateSlider);
     * propertyLearningRateBox.getChildren().add(propertyLearningRateLabel);
     * root.add(propertyLearningRateBox, 1, 6);
     * 
     * propertyLearningRateSlider.setMin(0);
     * propertyLearningRateSlider.setMax(1);
     * propertyLearningRateSlider.setValue(
     * Double.valueOf(params.get(AWombat.PARAMETER_PROPERTY_LEARNING_RATE)));
     * propertyLearningRateSlider.setShowTickLabels(true);
     * propertyLearningRateSlider.setShowTickMarks(false);
     * propertyLearningRateSlider.setMajorTickUnit(0.5);
     * propertyLearningRateSlider.setMinorTickCount(9);
     * propertyLearningRateSlider.setSnapToTicks(true);
     * propertyLearningRateSlider.setBlockIncrement(0.01);
     * 
     * propertyLearningRateSlider.valueProperty().addListener( new
     * ChangeListener<Number>() { public void changed( ObservableValue<? extends
     * Number> ov, Number old_val, Number new_val) {
     * propertyLearningRateLabel.setText(String.format("%.1f", new_val)); } });
     * 
     * HBox overallPenaltyWeitBox = new HBox(); overallPenaltyWeitSlider = new
     * Slider(); Label overallPenaltyWeitText = new
     * Label(AWombat.PARAMETER_OVERALL_PENALTY_WEIT); Label
     * overallPenaltyWeitLabel = new
     * Label(params.get(AWombat.PARAMETER_OVERALL_PENALTY_WEIT));
     * 
     * root.add(overallPenaltyWeitText, 0, 7);
     * overallPenaltyWeitBox.getChildren().add(overallPenaltyWeitSlider);
     * overallPenaltyWeitBox.getChildren().add(overallPenaltyWeitLabel);
     * root.add(overallPenaltyWeitBox, 1, 7);
     * 
     * overallPenaltyWeitSlider.setMin(0); overallPenaltyWeitSlider.setMax(1);
     * logger.error("text: " +
     * params.get(AWombat.PARAMETER_OVERALL_PENALTY_WEIT));
     * overallPenaltyWeitSlider
     * .setValue(Double.valueOf(overallPenaltyWeitLabel.getText()));
     * overallPenaltyWeitSlider.setShowTickLabels(true);
     * overallPenaltyWeitSlider.setShowTickMarks(false);
     * overallPenaltyWeitSlider.setMajorTickUnit(0.5);
     * overallPenaltyWeitSlider.setMinorTickCount(9);
     * overallPenaltyWeitSlider.setSnapToTicks(true);
     * overallPenaltyWeitSlider.setBlockIncrement(0.01);
     * 
     * overallPenaltyWeitSlider.valueProperty().addListener( new
     * ChangeListener<Number>() { public void changed( ObservableValue<? extends
     * Number> ov, Number old_val, Number new_val) {
     * overallPenaltyWeitLabel.setText(String.format("%.1f", new_val)); } });
     * 
     * HBox childrenPenaltyWeitBox = new HBox(); childrenPenaltyWeitSlider = new
     * Slider(); Label childrenPenaltyWeitText = new
     * Label(AWombat.PARAMETER_CHILDREN_PENALTY_WEIT); Label
     * childrenPenaltyWeitLabel = new
     * Label(params.get(AWombat.PARAMETER_CHILDREN_PENALTY_WEIT));
     * 
     * root.add(childrenPenaltyWeitText, 0, 8);
     * childrenPenaltyWeitBox.getChildren().add(childrenPenaltyWeitSlider);
     * childrenPenaltyWeitBox.getChildren().add(childrenPenaltyWeitLabel);
     * root.add(childrenPenaltyWeitBox, 1, 8);
     * 
     * childrenPenaltyWeitSlider.setMin(0);
     * childrenPenaltyWeitSlider.setMax(100);
     * childrenPenaltyWeitSlider.setValue(
     * Integer.valueOf(params.get(AWombat.PARAMETER_CHILDREN_PENALTY_WEIT)));
     * childrenPenaltyWeitSlider.setShowTickLabels(true);
     * childrenPenaltyWeitSlider.setShowTickMarks(false);
     * childrenPenaltyWeitSlider.setMajorTickUnit(50);
     * childrenPenaltyWeitSlider.setMinorTickCount(10);
     * childrenPenaltyWeitSlider.setSnapToTicks(true);
     * childrenPenaltyWeitSlider.setBlockIncrement(1);
     * 
     * childrenPenaltyWeitSlider.valueProperty().addListener( new
     * ChangeListener<Number>() { public void changed( ObservableValue<? extends
     * Number> ov, Number old_val, Number new_val) {
     * childrenPenaltyWeitLabel.setText(String.format("%.0f", new_val)); } });
     * 
     * HBox complexityPenaltyWeitBox = new HBox(); complexityPenaltyWeitSlider =
     * new Slider(); Label complexityPenaltyWeitText = new
     * Label(AWombat.PARAMETER_COMPLEXITY_PENALTY_WEIT); Label
     * complexityPenaltyWeitLabel = new
     * Label(params.get(AWombat.PARAMETER_COMPLEXITY_PENALTY_WEIT));
     * 
     * root.add(complexityPenaltyWeitText, 0, 9);
     * complexityPenaltyWeitBox.getChildren().add(complexityPenaltyWeitSlider);
     * complexityPenaltyWeitBox.getChildren().add(complexityPenaltyWeitLabel);
     * root.add(complexityPenaltyWeitBox, 1, 9);
     * 
     * complexityPenaltyWeitSlider.setMin(0);
     * complexityPenaltyWeitSlider.setMax(100);
     * complexityPenaltyWeitSlider.setValue
     * (Integer.valueOf(params.get(AWombat.PARAMETER_COMPLEXITY_PENALTY_WEIT)));
     * complexityPenaltyWeitSlider.setShowTickLabels(true);
     * complexityPenaltyWeitSlider.setShowTickMarks(false);
     * complexityPenaltyWeitSlider.setMajorTickUnit(50);
     * complexityPenaltyWeitSlider.setMinorTickCount(10);
     * complexityPenaltyWeitSlider.setSnapToTicks(true);
     * complexityPenaltyWeitSlider.setBlockIncrement(1);
     * 
     * complexityPenaltyWeitSlider.valueProperty().addListener( new
     * ChangeListener<Number>() { public void changed( ObservableValue<? extends
     * Number> ov, Number old_val, Number new_val) {
     * complexityPenaltyWeitLabel.setText(String.format("%.0f", new_val)); } });
     * 
     * Label verboseLabel = new Label(AWombat.PARAMETER_VERBOSE);
     * verboseCheckBox = new CheckBox(); verboseCheckBox.setSelected(false);
     * root.add(verboseLabel, 0, 10); root.add(verboseCheckBox, 1, 10);
     * 
     * //TODO I think this is not correct since it should be possible to select
     * multiple measures Label measuresLabel = new
     * Label(AWombat.PARAMETER_MEASURES); List<String> measuresList = new
     * ArrayList<String>(); String[] measureArray =
     * params.get(measuresLabel.getText()).replace("[","").replace("]",
     * "").split(","); for (int i = 0; i < measureArray.length; i++) {
     * measuresList.add(measureArray[i]); } ObservableList<String>
     * obsTermCritList = FXCollections .observableList(measuresList);
     * SpinnerValueFactory<String> svf = new
     * SpinnerValueFactory.ListSpinnerValueFactory<>( obsTermCritList);
     * measuresSpinner = new Spinner<String>();
     * measuresSpinner.setValueFactory(svf); root.add(measuresLabel, 0, 11);
     * root.add(measuresSpinner, 1, 11);
     * 
     * Label saveMappingLabel = new Label(AWombat.PARAMETER_SAVE_MAPPING);
     * saveMappingCheckBox = new CheckBox();
     * saveMappingCheckBox.setSelected(false); root.add(saveMappingLabel, 0,
     * 12); root.add(saveMappingCheckBox, 1, 12);
     * 
     * return root; }
     */
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

    public Button getLearnButton() {
	return learnButton;
    }

    public MainView getMainView() {
	return mainView;
    }

}

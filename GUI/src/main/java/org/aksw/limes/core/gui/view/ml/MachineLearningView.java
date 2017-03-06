package org.aksw.limes.core.gui.view.ml;

import java.util.List;
import java.util.Set;

import org.aksw.limes.core.evaluation.qualititativeMeasures.Accuracy;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.IQualitativeMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Precision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoPrecision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoRecall;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoRefFMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoRefPrecision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoRefRecall;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;
import org.aksw.limes.core.gui.controller.ml.MachineLearningController;
import org.aksw.limes.core.gui.util.EQualitativeMeasure;
import org.aksw.limes.core.gui.view.MainView;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.eagle.util.TerminationCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * This class creates the gui elements for the machine learning according to the
 * parameters of the machine learning algorithm selected.
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class MachineLearningView {

    /**
     * the logger for this class
     */
    protected static Logger logger = LoggerFactory.getLogger("LIMES");

    /**
     * array containing all implemented algorithms
     */
    private static final String[] algorithms = { MLAlgorithmFactory.EAGLE, MLAlgorithmFactory.WOMBAT_COMPLETE,
	    MLAlgorithmFactory.WOMBAT_SIMPLE };

    /**
     * corresponding controller
     */
    protected MachineLearningController mlController;

    /**
     * main view of the LIMES application
     */
    private MainView mainView;

    /**
     * button that starts the learning
     */
    private Button learnButton;

    /**
     * type of view
     */
    private MLImplementationType type;

    /**
     * default constructor setting the variables and creating the view by
     * calling {@link #createMLAlgorithmsRootPane()}
     * 
     * @param mainView
     *            main view
     * @param mlController
     *            controller
     * @param type
     *            implementation type: active, batch, unsupervised
     */
    public MachineLearningView(MainView mainView, MachineLearningController mlController, MLImplementationType type) {
	this.mainView = mainView;
	this.mlController = mlController;
	this.mlController.setMlView(this);
	this.type = type;
	createMLAlgorithmsRootPane();
    }

    /**
     * checks if the MLAlgorithm is implemented for this LearningSetting
     * corresponding to the subclass of MachineLearningView and creates the
     * rootPane accordingly
     */
    public void createMLAlgorithmsRootPane() {
	BorderPane border = new BorderPane();
	HBox content = new HBox();
	GridPane root = new GridPane();

	ObservableList<String> mloptions = FXCollections.observableArrayList();
	for (int i = 0; i < algorithms.length; i++) {
	    if (type == MLImplementationType.SUPERVISED_ACTIVE) {
		try {
		    AMLAlgorithm algorithm = MLAlgorithmFactory.createMLAlgorithm(MLAlgorithmFactory.getAlgorithmType(algorithms[i]),
			    MLAlgorithmFactory.getImplementationType(MLAlgorithmFactory.SUPERVISED_ACTIVE));
		    mloptions.add(algorithm.getName());
		} catch (Exception e1) {
		    // TODO Auto-generated catch block
		}
	    } else if (type == MLImplementationType.SUPERVISED_BATCH) {
		try {
		    AMLAlgorithm algorithm = MLAlgorithmFactory.createMLAlgorithm(MLAlgorithmFactory.getAlgorithmType(algorithms[i]),
			    MLAlgorithmFactory.getImplementationType(MLAlgorithmFactory.SUPERVISED_BATCH));
		    mloptions.add(algorithm.getName());
		} catch (Exception e1) {
		    // TODO Auto-generated catch block
		}
	    } else if (type == MLImplementationType.UNSUPERVISED) {
		try {
		    AMLAlgorithm algorithm = MLAlgorithmFactory.createMLAlgorithm(MLAlgorithmFactory.getAlgorithmType(algorithms[i]),
			    MLAlgorithmFactory.getImplementationType(MLAlgorithmFactory.UNSUPERVISED));
		    mloptions.add(algorithm.getName());
		} catch (Exception e1) {
		    // TODO Auto-generated catch block
		}
	    } else {
		logger.info("Unknown subclass of MachineLearningView");
	    }
	}
	ComboBox<String> mlOptionsChooser = new ComboBox<String>(mloptions);
	mlOptionsChooser.setPromptText("choose algorithm");

	learnButton = new Button("learn");
	learnButton.setTooltip(new Tooltip("start learning"));
	learnButton.setDisable(true);
	HBox buttonWrapper = new HBox();
	buttonWrapper.setAlignment(Pos.BASELINE_RIGHT);
	buttonWrapper.setPadding(new Insets(25, 25, 25, 25));
	buttonWrapper.getChildren().addAll(learnButton);
	content.getChildren().add(mlOptionsChooser);
	border.setTop(content);
	border.setBottom(buttonWrapper);
	Scene scene = new Scene(border, 600, 900);
	scene.getStylesheets().add("gui/main.css");

	mlOptionsChooser.setOnAction(e -> {
	    root.getChildren().removeAll(root.getChildren());
	    this.mlController.setMLAlgorithmToModel(mlOptionsChooser.getValue());
	    this.mlController.getMlModel().getMlalgorithm().getMl().setConfiguration(this.mlController.getMlModel().getConfig());
	    this.mlController.getMlModel().getMlalgorithm().getMl().setDefaultParameters();
	    showParameters(root, mlOptionsChooser.getValue());
	    border.setCenter(root);
	    learnButton.setDisable(false);
	});

	learnButton.setOnAction(e -> {
//	    for(LearningParameter l : this.mlController.getMlModel().getMlalgorithm().getParameters()){
//		System.err.println(l.getValue().toString());
//	    }
	    learnButton.setDisable(true);
	    this.mlController.learn(this);
	});

	Stage stage = new Stage();
	stage.setMinHeight(scene.getHeight());
	stage.setMinWidth(scene.getWidth());
	stage.setTitle("LIMES - Machine Learning");
	stage.setScene(scene);
	stage.show();
    }

    /**
     * Takes the gridpane in the BorderPane and adds the gui elements fitting
     * for the algorithm. The value of the according learning parameter gets
     * changed if the value of the gui element is changed.
     *
     * @param root
     * @param algorithm
     * @return the GridPane containing the added Nodes
     */
    private GridPane showParameters(GridPane root, String algorithm) {

	AMLAlgorithm mlalgorithm = this.mlController.getMlModel().getMlalgorithm();
	List<LearningParameter> params = mlalgorithm.getParameters();
	for (int i = 0; i < params.size(); i++) {
	    switch (params.get(i).getClazz().getSimpleName().toString().toLowerCase().trim()) {
	    case "integer":
		addNumberParameterHBox(params.get(i), root, i);
		break;
	    case "long":
		addNumberParameterHBox(params.get(i), root, i);
		break;
	    case "double":
		addNumberParameterHBox(params.get(i), root, i);
		break;
	    case "float":
		addNumberParameterHBox(params.get(i), root, i);
		break;
	    case "boolean":
		addBooleanParameterHBox(params.get(i), root, i);
		break;
	    case "measuretype":
		addMeasureTypeParameterHBox(params.get(i), root, i);
		break;
	    case "propertymapping":
		handlePropertyMapping(params.get(i));
		break;
	    case "iqualitativemeasure":
		addQualitativeMeasureParameterHBox(params.get(i), root, i);
		break;
	    default:
		if (params.get(i).getClazz().isEnum()) {
		    addEnumParameterHBox(params.get(i), root, i);
		} else {
		    logger.error("Unknown LearningParameter clazz " + params.get(i).getClazz().getSimpleName().toString().toLowerCase().trim() + "!");
		}
		break;
	    }
	}
	root.setHgap(10);
	root.setVgap(10);
	root.setPadding(new Insets(25, 25, 25, 25));
	return root;
    }

    /**
     * creates a {@link javafx.scene.layout.HBox} with the information from the
     * learning parameter if it is a Integer or Double
     * 
     * @param param
     * @param root
     * @param position
     * @param integer
     */
    private void addNumberParameterHBox(LearningParameter param, GridPane root, int position) {

	String type = param.getClazz().getSimpleName().toString().toLowerCase().trim();
	Label parameterText = new Label(param.getName());
	Spinner parameterSpinner = null;
	int intStep = 1;
	double doubleStep = 0.01;
	if (!Double.isNaN(param.getRangeStep())) {
	    intStep = (int) param.getRangeStep();
	    doubleStep = Double.valueOf(param.getRangeStep());
	}
	switch (type) {
	case "integer":
	    parameterSpinner = new Spinner<Integer>((int) param.getRangeStart(), (int) param.getRangeEnd(), (int) param.getValue(), intStep);
	    break;
	case "double":
	    parameterSpinner = new Spinner<Double>(param.getRangeStart(), param.getRangeEnd(), (double) param.getValue(), doubleStep);
	    break;
	case "float":
	    parameterSpinner = new Spinner<Double>(Double.valueOf(param.getRangeStart()), Double.valueOf(param.getRangeEnd()), Double.valueOf(param.getValue()
		    .toString()), doubleStep);
	    break;
	default:
	    // in case it's a too big long
	    int rangeEnd = (param.getRangeEnd() < Integer.MAX_VALUE) ? (int) param.getRangeEnd() : Integer.MAX_VALUE;
	    parameterSpinner = new Spinner<Integer>((int) param.getRangeStart(), rangeEnd, Integer.valueOf(param.getValue().toString()), intStep);
	    break;
	}
	parameterSpinner.setEditable(true);
	root.add(parameterText, 0, position);
	root.add(parameterSpinner, 1, position);
    }

    /**
     * creates a {@link javafx.scene.layout.HBox} with the information from the
     * learning parameter if it is a Boolean
     * 
     * @param param
     * @param root
     * @param position
     */
    private void addBooleanParameterHBox(LearningParameter param, GridPane root, int position) {
	Label parameterLabel = new Label(param.getName());
	CheckBox parameterCheckBox = new CheckBox();
	parameterCheckBox.setTooltip(new Tooltip(param.getDescription()));
	parameterCheckBox.setSelected(false);
	parameterCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
	    public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
		param.setValue(new_val);
	    }
	});
	root.add(parameterLabel, 0, position);
	root.add(parameterCheckBox, 1, position);
    }

    /**
     * If there is a property mapping in the configuration this gets set in the
     * LearningParameters, else a {@link MLPropertyMatchingView} gets
     * instantiated and the property mapping is done by the user
     * 
     * @param param
     */
    private void handlePropertyMapping(LearningParameter param) {
	if (this.mlController.getMlModel().getConfig().propertyMapping != null) {
	    param.setValue(this.mlController.getMlModel().getConfig().propertyMapping);
	} else {
	    new MLPropertyMatchingView(this.mlController.getMlModel().getConfig(), param, mainView.toolBox);
	}
    }

    /**
     * creates a {@link javafx.scene.layout.HBox} with the information from the
     * learning parameter if it is a
     * {@link org.aksw.limes.core.measures.measure.MeasureType}
     * 
     * @param param
     * @param root
     * @param position
     */
    @SuppressWarnings("unchecked")
    private void addMeasureTypeParameterHBox(LearningParameter param, GridPane root, int position) {
	Label parameterLabel = new Label(param.getName());
	ListView<EnumTypeItem> parameterListView = new ListView<>();
	EnumTypeItem.setParams((Set<String>) param.getValue());
	for (int i = 0; i < MeasureType.values().length; i++) {
	    String measure = MeasureType.values()[i].toString().toLowerCase();
	    EnumTypeItem item = null;
	    if (((Set<String>) param.getValue()).contains(measure)) {
		item = new EnumTypeItem(measure, true);
	    } else {
		item = new EnumTypeItem(measure, false);
	    }
	    parameterListView.getItems().add(item);
	}
	parameterListView.setCellFactory(CheckBoxListCell.forListView(new Callback<EnumTypeItem, ObservableValue<Boolean>>() {
	    @Override
	    public ObservableValue<Boolean> call(EnumTypeItem item) {
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

    /**
     * creates a {@link javafx.scene.layout.HBox} with the information from the
     * learning parameter if it is a {@link TerminationCriteria}
     * 
     * @param param
     * @param root
     * @param position
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addEnumParameterHBox(LearningParameter param, GridPane root, int position) {
	Label parameterLabel = new Label(param.getName());
	ChoiceBox cb = new ChoiceBox();
	cb.setItems(FXCollections.observableArrayList(getEnumArrayList((Class<Enum>) param.getClazz())));
	cb.setValue(param.getValue());
	cb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Enum>() {
	    public void changed(ObservableValue ov, Enum value, Enum new_value ){
	    param.setValue(new_value.toString());
	    }
	});
	root.add(parameterLabel, 0, position);
	root.add(cb, 1, position);

    }

    /**
     * creates a {@link javafx.scene.layout.HBox} with the information from the
     * learning parameter if it is a {@link IQualitativeMeasure}
     * 
     * @param param
     * @param root
     * @param position
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addQualitativeMeasureParameterHBox(LearningParameter param, GridPane root, int position) {
	Label parameterLabel = new Label(param.getName());
	ChoiceBox cb = new ChoiceBox();
	cb.setItems(FXCollections.observableArrayList(getEnumArrayList(EQualitativeMeasure.class)));
	try{
	    //this is a bit hacky
	    //Since the output of param.getValue().toString() has the form org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure@238723 we take the class name from this 
	cb.setValue(EQualitativeMeasure.valueOf(param.getValue().toString().substring(param.getValue().toString().lastIndexOf(".") + 1, param.getValue().toString().lastIndexOf("@"))));
	}catch(Exception e){
	    e.printStackTrace();
	}
	cb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Enum>() {
	    public void changed(ObservableValue ov, Enum value, Enum new_value ){
		switch(new_value.toString().toLowerCase().trim()){
		case "accuracy":
		    param.setValue(new Accuracy());
		    break;
		case "pseudofmeasure":
		    param.setValue(new PseudoFMeasure());
		    break;
		case "pseudorefmeasure":
		    param.setValue(new PseudoRefFMeasure());
		    break;
		case "pseudoprecision":
		    param.setValue(new PseudoPrecision());
		    break;
		case "pseudorefprecision":
		    param.setValue(new PseudoRefPrecision());
		    break;
		case "pseudorecall":
		    param.setValue(new PseudoRecall());
		    break;
		case "pseudorefrecall":
		    param.setValue(new PseudoRefRecall());
		    break;
		case "fmeasure":
		    param.setValue(new FMeasure());
		    break;
		case "precision":
		    param.setValue(new Precision());
		    break;
		case "recall":
		    param.setValue(new Recall());
		    break;
		default:
		    logger.error("This IQualitativeMeasure does not exist!");
		}
	    }
	});
	root.add(parameterLabel, 0, position);
	root.add(cb, 1, position);

    }

    /**
     * Helper method for putting generic enums to a list
     * 
     * @param elemType
     *            enum class
     * @return {@link ObservableList} containing all elements in the enum class
     */
    private <E extends Enum<E>> ObservableList<E> getEnumArrayList(Class<E> elemType) {
	ObservableList<E> list = FXCollections.observableArrayList();
	for (E e : java.util.EnumSet.allOf(elemType)) {
	    list.add(e);
	}
	return list;
    }

    /**
     * class for the parameterHBox to use for the cell factory of the ListView
     * 
     * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
     *         studserv.uni-leipzig.de{@literal >}
     *
     */
    private static class EnumTypeItem {

	/**
	 * name of the EnumType
	 */
	private final StringProperty name = new SimpleStringProperty();
	/**
	 * true if it is selected
	 */
	private final BooleanProperty on = new SimpleBooleanProperty();
	/**
	 * the LearningParameters to all EnumTypeItems
	 */
	private static Set<String> params;

	/**
	 * Constructor sets variables and implements listener on {@link #on} the
	 * change {@link #params} according to user input
	 * 
	 * @param name
	 * @param on
	 */
	private EnumTypeItem(String name, boolean on) {
	    setName(name);
	    setOn(on);
	    this.on.addListener(new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		    if (newValue) {
			params.add(name);
		    } else {
			params.remove(name);
		    }
		}
	    });
	}

	/**
	 * sets params
	 * 
	 * @param p
	 */
	private static final void setParams(Set<String> p) {
	    params = p;
	}

	/**
	 * returns nameProperty
	 * 
	 * @return
	 */
	private final StringProperty nameProperty() {
	    return this.name;
	}

	/**
	 * returns name as String
	 * 
	 * @return
	 */
	private final String getName() {
	    return this.nameProperty().get();
	}

	/**
	 * sets name
	 * 
	 * @param name
	 */
	private final void setName(final String name) {
	    this.nameProperty().set(name);
	}

	/**
	 * returns on
	 * 
	 * @return
	 */
	private final BooleanProperty onProperty() {
	    return this.on;
	}

	/**
	 * returns value of on
	 * 
	 * @return
	 */
	private final boolean isOn() {
	    return this.onProperty().get();
	}

	/**
	 * sets on
	 * 
	 * @param on
	 */
	private final void setOn(final boolean on) {
	    this.onProperty().set(on);
	}

	/**
	 * returns name as String
	 */
	@Override
	public String toString() {
	    return getName();
	}

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

    /**
     * returns learn button
     * 
     * @return learnButton
     */
    public Button getLearnButton() {
	return learnButton;
    }

    /**
     * returns mainView
     * 
     * @return mainView
     */
    public MainView getMainView() {
	return mainView;
    }

}

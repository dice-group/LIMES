package org.aksw.limes.core.gui.view.ml;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.view.ToolBox;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Class that creates a view to let the user match the properties
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class MLPropertyMatchingView {

    private ScrollPane rootPane;
    private ListView<String> sourcePropList;
    private ListView<String> targetPropList;
    private boolean sourcePropertyUnmatched = false;
    private boolean targetPropertyUnmatched = false;
    private Button cancelButton;
    private Button finishButton;
    private Stage stage;
    private LearningParameter param;
    /**
     * contains all the unmatchedPropertyBoxes where Properties have already
     * been matched plus one empty/unfinished one
     */
    private VBox matchedPropertiesBox;
    /**
     * the downmost HBox inside matchedPropertiesBox is either empty or contains
     * an unfinished matching
     */
    private HBox unmatchedPropertyBox;
    private Config config;

    private ToolBox mainViewToolBox;

    /**
     * Constructor sets the parameters, creates the view and adds listeners
     * @param config
     * @param param
     */
    public MLPropertyMatchingView(Config config, LearningParameter param, ToolBox tb) {
	this.config = config;
	this.param = param;
	this.mainViewToolBox = tb;
	createRootPane();
	addListeners();
    }

    /**
     * Creates the root pane and opens a stage with all the elements
     */
    private void createRootPane() {
	sourcePropList = new ListView<String>();
	targetPropList = new ListView<String>();
	if(mainViewToolBox.getToolBoxSourceProperties() != null){
	sourcePropList.getItems().addAll(mainViewToolBox.getToolBoxSourceProperties().getItems());
	}else{
	sourcePropList.getItems().addAll(config.getSourceInfo().getProperties());
	}
	if(mainViewToolBox.getToolBoxTargetProperties() != null){
	targetPropList.getItems().addAll(mainViewToolBox.getToolBoxTargetProperties().getItems());
	}else{
	targetPropList.getItems().addAll(config.getTargetInfo().getProperties());
	}
	Label sourceLabel = new Label("available Source Properties:");
	Label targetLabel = new Label("available Target Properties:");
	VBox sourceColumn = new VBox();
	VBox targetColumn = new VBox();
	sourceColumn.getChildren().addAll(sourceLabel, sourcePropList);
	targetColumn.getChildren().addAll(targetLabel, targetPropList);
	matchedPropertiesBox = new VBox();
	cancelButton = new Button("cancel");
	cancelButton.setTooltip(new Tooltip("cancel property matching"));
	finishButton = new Button("finish");
	finishButton.setTooltip(new Tooltip("set this property matching"));
	makeUnmatchedPropertyBox();
	finishButton.setDisable(true);
	HBox buttons = new HBox();
	buttons.getChildren().addAll(cancelButton, finishButton);
	BorderPane root = new BorderPane();
	HBox hb = new HBox();
	hb.getChildren().addAll(sourceColumn, targetColumn);
	HBox.setHgrow(sourceColumn, Priority.ALWAYS);
	HBox.setHgrow(targetColumn, Priority.ALWAYS);
	VBox topBox = new VBox();
	topBox.getChildren().addAll(hb);
	root.setTop(topBox);
	root.setCenter(matchedPropertiesBox);
	root.setBottom(buttons);
	rootPane = new ScrollPane(root);
	rootPane.setFitToHeight(true);
	rootPane.setFitToWidth(true);
	Scene scene = new Scene(rootPane, 300, 400);
	scene.getStylesheets().add("gui/main.css");
	stage = new Stage();
	stage.setTitle("LIMES - Property Matching");
	stage.setScene(scene);
	stage.show();
    }

    /**
     * adds the listeners
     */
    private void addListeners() {
	cancelButton.setOnAction(e -> {
	    stage.close();
	});

	finishButton.setOnAction(e -> {
	    if (!sourcePropertyUnmatched && !targetPropertyUnmatched) {
		PropertyMapping propMap = new PropertyMapping();
		// size -1 because there is always another empty unmatchedPropertyBox
		// inside
		for (int i = 0; i < matchedPropertiesBox.getChildren().size() - 1; i++) {
		    HBox row = (HBox) matchedPropertiesBox.getChildren().get(i);
		    String sourceProp = ((Label) ((VBox) row.getChildren().get(0)).getChildren().get(0)).getText();
		    String targetProp = ((Label) ((VBox) row.getChildren().get(1)).getChildren().get(0)).getText();
		    String type = ((Spinner<String>) ((HBox) row.getChildren().get(2)).getChildren().get(0)).getValue();
		    switch (type) {
		    case "String":
			propMap.addStringPropertyMatch(sourceProp, targetProp);
			break;
		    case "Number":
			propMap.addNumberPropertyMatch(sourceProp, targetProp);
			break;
		    case "Date":
			propMap.addDatePropertyMatch(sourceProp, targetProp);
			break;
		    case "Pointset":
			propMap.addPointsetPropertyMatch(sourceProp, targetProp);
			break;
		    }
		}
		this.param.setValue(propMap);
		this.config.propertyMapping = propMap;
		stage.close();
	    }
	});
	sourcePropList.setOnMouseClicked(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent e) {
		if (sourcePropList.getSelectionModel().getSelectedItem() != null) {
		    if (sourcePropertyUnmatched || (!sourcePropertyUnmatched && !targetPropertyUnmatched)) {
			Label sourceProp = new Label(sourcePropList.getSelectionModel().getSelectedItem());
			sourceProp.setAlignment(Pos.BASELINE_LEFT);
			((VBox) unmatchedPropertyBox.getChildren().get(0)).getChildren().add(sourceProp);
			sourcePropList.getItems().remove(sourcePropList.getSelectionModel().getSelectedItem());

			//because  always  at  least  two  VBoxes  are  contained
			if (unmatchedPropertyBox.getChildren().size() < 3) { 
			    addSpinnerAndButton();
			}
			if (sourcePropertyUnmatched) {
			    sourcePropertyUnmatched = false;
			    makeUnmatchedPropertyBox();
			} else {
			    finishButton.setDisable(true);
			    targetPropertyUnmatched = true;
			}
		    }
		}
	    }
	});

	targetPropList.setOnMouseClicked(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent e) {
		if (targetPropList.getSelectionModel().getSelectedItem() != null) {
		    if (targetPropertyUnmatched || (!sourcePropertyUnmatched && !targetPropertyUnmatched)) {
			Label targetProp = new Label(targetPropList.getSelectionModel().getSelectedItem());
			targetProp.setAlignment(Pos.BASELINE_LEFT);
			((VBox) unmatchedPropertyBox.getChildren().get(1)).getChildren().add(targetProp);
			targetPropList.getItems().remove(targetPropList.getSelectionModel().getSelectedItem());
			if (unmatchedPropertyBox.getChildren().size() < 3) {
			    addSpinnerAndButton();
			}
			if (targetPropertyUnmatched) {
			    targetPropertyUnmatched = false;
			    makeUnmatchedPropertyBox();
			} else {
			    finishButton.setDisable(true);
			    sourcePropertyUnmatched = true;
			}
		    }
		}
	    }
	});
    }

    private void addSpinnerAndButton() {
	List<String> propertyTypeList = new ArrayList<String>();
	propertyTypeList.add("String");
	propertyTypeList.add("Number");
	propertyTypeList.add("Date");
	propertyTypeList.add("Pointset");
	ObservableList<String> obsPropTypeList = FXCollections.observableList(propertyTypeList);
	SpinnerValueFactory<String> svf = new SpinnerValueFactory.ListSpinnerValueFactory<>(obsPropTypeList);
	Spinner<String> propertyTypeSpinner = new Spinner<String>();
	propertyTypeSpinner.setValueFactory(svf);
	Button deleteRowButton = new Button("x");
	HBox spinnerAndButtonBox = new HBox();
	spinnerAndButtonBox.setAlignment(Pos.CENTER_RIGHT);
	HBox.setHgrow(spinnerAndButtonBox, Priority.ALWAYS);
	spinnerAndButtonBox.getChildren().addAll(propertyTypeSpinner, deleteRowButton);
	unmatchedPropertyBox.getChildren().add(spinnerAndButtonBox);
	unmatchedPropertyBox.setAlignment(Pos.CENTER_LEFT);
	unmatchedPropertyBox.setPrefWidth(stage.getWidth());
	deleteRowButton.setOnAction(e_ -> {
	    HBox column = (HBox) deleteRowButton.getParent();
	    String matchedSource = ((Label) ((VBox) column.getChildren().get(0)).getChildren().get(0)).getText();
	    String matchedTarget = ((Label) ((VBox) column.getChildren().get(1)).getChildren().get(0)).getText();
	    if (matchedSource != null) {
		sourcePropList.getItems().add(matchedSource);
	    }
	    if (matchedTarget != null) {
		targetPropList.getItems().add(matchedTarget);
	    }
	    VBox row = (VBox) deleteRowButton.getParent().getParent();
	    row.getChildren().remove(deleteRowButton.getParent());
	});
    }

    private void makeUnmatchedPropertyBox() {
	finishButton.setDisable(false);
	sourcePropertyUnmatched = false;
	unmatchedPropertyBox = new HBox();
	unmatchedPropertyBox.setSpacing(20);
	VBox unmatchedProp1 = new VBox();
	VBox unmatchedProp2 = new VBox();
	unmatchedPropertyBox.getChildren().addAll(unmatchedProp1, unmatchedProp2);
	matchedPropertiesBox.getChildren().add(unmatchedPropertyBox);
    }
    
}

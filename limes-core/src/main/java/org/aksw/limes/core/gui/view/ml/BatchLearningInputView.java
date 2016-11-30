package org.aksw.limes.core.gui.view.ml;

import java.io.File;

import org.aksw.limes.core.gui.controller.ml.BatchLearningController;
import org.aksw.limes.core.gui.model.ml.BatchLearningModel;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.reader.AMappingReader;
import org.aksw.limes.core.io.mapping.reader.CSVMappingReader;
import org.aksw.limes.core.io.mapping.reader.RDFMappingReader;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class BatchLearningInputView {

    private Stage stage;
    private ScrollPane rootPane;
    private static final String filePathFieldError = "File not found!";
    private BatchLearningModel blm;
    private BatchLearningController blc;
    private AMapping trainingMapping;
    public BooleanProperty finished = new SimpleBooleanProperty(false);

    public BatchLearningInputView(BatchLearningModel blm, BatchLearningController blc) {
	this.blm = blm;
	this.blc = blc;
	createRootPane();
    }

    private void createRootPane() {
	HBox fileHBox = new HBox();
	TextField filePathField = new TextField("");
	Tooltip tip = new Tooltip();
	tip.setText("Provide a file with a training mapping");
	filePathField.setTooltip(tip);
	filePathField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
	    if (filePathField.getText() != null && !filePathField.getText().equals("")) {
		if (!newValue) { // when focus lost
		    // check if it is a file
		    File f = new File(filePathField.getText());
		    if (!(f.exists() && !f.isDirectory())) {
			// check if it is a valid URL
			filePathField.setText(filePathFieldError);
			filePathField.setStyle("-fx-text-inner-color: red;");
		    }
		}
	    }
	});
	filePathField.setOnMouseClicked(e -> {
	    if (filePathField.getText() != null && !filePathField.getText().equals("")) {
		if (filePathField.getText().equals(filePathFieldError)) {
		    filePathField.setStyle("");
		    filePathField.setText("");
		}
	    }
	});
	Button fileEndpointButton = new Button();
	Image fileButtonImage = new Image(getClass().getResourceAsStream("/gui/file.png"), 20, 20,
		true, false);
	fileEndpointButton.setGraphic(new ImageView(fileButtonImage));
	fileEndpointButton.setOnAction(e -> {
	    FileChooser fileChooser = new FileChooser();
	    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
		    "Mapping File (*.csv , *.rdf, *.ttl, *.n3, *.nt)", "*.xml", "*.rdf", "*.ttl",
		    "*.n3", "*.nt");
	    fileChooser.getExtensionFilters().add(extFilter);
	    File file = fileChooser.showOpenDialog(stage);
	    if (file != null) {
		filePathField.setText(file.getAbsolutePath());
		;
	    }
	});
	fileHBox.getChildren().addAll(filePathField, fileEndpointButton);
	HBox buttons = new HBox();
	Button save = new Button("Save");
	Button cancel = new Button("Cancel");
	save.setOnAction(e_ -> {
	    if (filePathField.getText() != null && !filePathField.getText().equals("")) {
		AMappingReader reader = null;
		if (filePathField.getText().endsWith("csv")) {
		    reader = new CSVMappingReader(filePathField.getText());
		    trainingMapping = reader.read();
		    blm.setTrainingMapping(trainingMapping);
		} else if (filePathField.getText().endsWith("rdf")
			|| filePathField.getText().endsWith("ttl")
			|| filePathField.getText().endsWith("nt")
			|| filePathField.getText().endsWith("n3")) {
		    System.err.println(filePathField.getText());
		    reader = new RDFMappingReader(filePathField.getText());
		    trainingMapping = reader.read();
		    blm.setTrainingMapping(trainingMapping);
		} else {
		    Alert alert = new Alert(AlertType.INFORMATION);
		    alert.setContentText("Unknown Mapping filetype!");
		    alert.showAndWait();
		}
	    }
	    finished.set(true);
	    stage.close();
	});
	cancel.setOnAction(e -> {
	    stage.close();
	});
	buttons.getChildren().addAll(save, cancel);
	HBox.setHgrow(fileHBox, Priority.ALWAYS);
	HBox.setHgrow(buttons, Priority.ALWAYS);
	BorderPane root = new BorderPane();
	root.setTop(fileHBox);
	root.setBottom(buttons);
	rootPane = new ScrollPane(root);
	Scene scene = new Scene(rootPane, 300, 400);
	scene.getStylesheets().add("gui/main.css");
	stage = new Stage();
	stage.setTitle("LIMES - Get training mapping");
	stage.setScene(scene);
	stage.show();
    }
}

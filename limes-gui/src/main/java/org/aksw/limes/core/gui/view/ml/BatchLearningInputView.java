package org.aksw.limes.core.gui.view.ml;

import java.io.File;

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
	private final BatchLearningModel blm;
	private AMapping trainingMapping;
	public BooleanProperty finished = new SimpleBooleanProperty(false);

	public BatchLearningInputView(BatchLearningModel blm) {
		this.blm = blm;
		this.createRootPane();
	}

	private void createRootPane() {
		final HBox fileHBox = new HBox();
		final TextField filePathField = new TextField("");
		filePathField.setId("filePathField");
		final Tooltip tip = new Tooltip();
		tip.setText("Provide a file with a training mapping");
		filePathField.setTooltip(tip);
		filePathField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
			if (filePathField.getText() != null && !filePathField.getText().equals("")) {
				if (!newValue) { // when focus lost
					// check if it is a file
					final File f = new File(filePathField.getText());
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
		final Button fileEndpointButton = new Button();
		final Image fileButtonImage = new Image(this.getClass().getResourceAsStream("/gui/file.png"), 20, 20, true,
				false);
		fileEndpointButton.setGraphic(new ImageView(fileButtonImage));
		fileEndpointButton.setOnAction(e -> {
			final FileChooser fileChooser = new FileChooser();
			final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
					"Mapping File (*.csv , *.rdf, *.ttl, *.n3, *.nt)", "*.xml", "*.rdf", "*.ttl", "*.n3", "*.nt",
					"*.csv");
			fileChooser.getExtensionFilters().add(extFilter);
			final File file = fileChooser.showOpenDialog(this.stage);
			if (file != null) {
				filePathField.setText(file.getAbsolutePath());
				;
			}
		});
		fileHBox.getChildren().addAll(filePathField, fileEndpointButton);
		final HBox buttons = new HBox();
		final Button save = new Button("Save");
		final Button cancel = new Button("Cancel");
		save.setOnAction(e_ -> {
			if (filePathField.getText() != null && !filePathField.getText().equals("")) {
				AMappingReader reader = null;
				if (filePathField.getText().endsWith("csv")) {
					reader = new CSVMappingReader(filePathField.getText());
					this.trainingMapping = reader.read();
					this.blm.setTrainingMapping(this.trainingMapping);
				} else if (filePathField.getText().endsWith("rdf") || filePathField.getText().endsWith("ttl")
						|| filePathField.getText().endsWith("nt") || filePathField.getText().endsWith("n3")) {
					reader = new RDFMappingReader(filePathField.getText());
					this.trainingMapping = reader.read();
					this.blm.setTrainingMapping(this.trainingMapping);
				} else {
					final Alert alert = new Alert(AlertType.INFORMATION);
					alert.setContentText("Unknown Mapping filetype!");
					alert.showAndWait();
				}
			}
			this.finished.set(true);
			this.stage.close();
		});
		cancel.setOnAction(e -> {
			this.stage.close();
		});
		buttons.getChildren().addAll(save, cancel);
		HBox.setHgrow(fileHBox, Priority.ALWAYS);
		HBox.setHgrow(buttons, Priority.ALWAYS);
		final BorderPane root = new BorderPane();
		root.setTop(fileHBox);
		root.setBottom(buttons);
		this.rootPane = new ScrollPane(root);
		final Scene scene = new Scene(this.rootPane, 300, 400);
		scene.getStylesheets().add("gui/main.css");
		this.stage = new Stage();
		this.stage.setTitle("LIMES - Get training mapping");
		this.stage.setScene(scene);
		this.stage.show();
	}
}

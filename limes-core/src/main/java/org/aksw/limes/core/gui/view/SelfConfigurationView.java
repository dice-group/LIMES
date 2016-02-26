package org.aksw.limes.core.gui.view;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.aksw.limes.core.gui.controller.SelfConfigurationController;

/**
 * UI for selfconfiguration
 * 
 * @author Sascha Hahne, Daniel Obraczka
 *
 */
public class SelfConfigurationView {
	/**
	 * Corresponding controller
	 */
	public SelfConfigurationController controller;

	/**
	 * ChoiceBox to chose selfconfiguration-mode
	 */
	private ChoiceBox<String> selfConfigChooser;

	/**
	 * Vertical Box for the chooser
	 */
	protected VBox selfConfigWrapper;

	public MainView view;

	public SelfConfigurationPanelInterface selfConfigPanel;

	/**
	 * Constructor initializes controller
	 */
	public SelfConfigurationView(MainView view) {
		this.view = view;
		this.controller = new SelfConfigurationController(this);
		createWindow();
	}

	/**
	 * Create the window for User Input
	 */
	private void createWindow() {
		BorderPane root = new BorderPane();
		HBox content = new HBox();
		selfConfigChooser = new ChoiceBox<String>(
				FXCollections.observableArrayList("Genetics", "Meshbased"));
		selfConfigChooser
				.getSelectionModel()
				.selectedIndexProperty()
				.addListener(
						(arg0, value, new_value) -> {
							switch (new_value.intValue()) {
							case 0:
								this.selfConfigPanel = new GeneticSelfConfigurationPanel(
										this);
								break;
							case 1:
								this.selfConfigPanel = new MeshBasedSelfConfigurationPanel(
										this);
								break;
							default:
								break;
							}

						}

				);

		content.getChildren().add(selfConfigChooser);
		selfConfigWrapper = new VBox();
		selfConfigWrapper.setFillWidth(false);
		root.setTop(content);
		root.setCenter(selfConfigWrapper);
		Scene scene = new Scene(root, 300, 400);
		scene.getStylesheets().add("gui/main.css");

		Stage stage = new Stage();
		stage.setTitle("LIMES - Self Configuration");
		stage.setScene(scene);
		stage.show();

								this.selfConfigPanel = new MeshBasedSelfConfigurationPanel(
										this);
	}

	public void createErrorWindow() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setContentText("Something went wrong!");
		alert.showAndWait();
	}
}

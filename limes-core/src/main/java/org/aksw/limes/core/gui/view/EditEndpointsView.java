package org.aksw.limes.core.gui.view;

import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;
import static org.aksw.limes.core.gui.util.SourceOrTarget.TARGET;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.aksw.limes.core.gui.controller.EditEndpointsController;
import org.aksw.limes.core.gui.util.SourceOrTarget;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;

/**
 * used for editing endpoints step in
 * {@link org.aksw.limes.core.gui.view.WizardView}
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class EditEndpointsView implements IEditView {
	private static final String pageSizeError = "Only numbers are permitted!";
	private static final String endpointURLError = "Invalid URL or file not found!";
	/**
	 * Corresponding Controller
	 */
	private EditEndpointsController controller;
	/**
	 * Field to put in the SourceURL
	 */
	private TextField[] sourceFields;
	/**
	 * Field to put in the SourceURL
	 */
	private TextField[] targetFields;
	/**
	 * Pane to arrange the Elements of the View
	 */
	private ScrollPane rootPane;

	private WizardView wizardView;

	/**
	 * Constructor
	 */
	EditEndpointsView(WizardView wizardView) {
		createRootPane();
		this.wizardView = wizardView;
	}

	/**
	 * Sets the Controller of the View
	 *
	 * @param controller
	 *            Corresponding Controller
	 */
	public void setController(EditEndpointsController controller) {
		this.controller = controller;
	}

	/**
	 * Creates a new RootPane with Layout
	 */
	private void createRootPane() {
		HBox hbox = new HBox();
		Node sourcePanelWithTitle = createEndpointPane(SOURCE);
		HBox.setHgrow(sourcePanelWithTitle, Priority.ALWAYS);
		hbox.getChildren().add(sourcePanelWithTitle);
		Node targetPaneWithTitle = createEndpointPane(TARGET);
		HBox.setHgrow(targetPaneWithTitle, Priority.ALWAYS);
		hbox.getChildren().add(targetPaneWithTitle);

		rootPane = new ScrollPane(hbox);
		rootPane.setFitToHeight(true);
		rootPane.setFitToWidth(true);
	}

	/**
	 * returns used rootPane
	 */
	@Override
	public Parent getPane() {
		return rootPane;
	}

	/**
	 * Creates Pane with Textfields to Edit the EndPoints
	 *
	 * @param source
	 *            If True sourcePane else targetPane
	 * @return Created Pane
	 */
	private Node createEndpointPane(SourceOrTarget sourceOrTarget) {
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(10);
		pane.setVgap(10);
		pane.setPadding(new Insets(25, 25, 25, 25));
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setMinWidth(Control.USE_PREF_SIZE);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setMinWidth(300);
		column2.setHgrow(Priority.ALWAYS);
		pane.getColumnConstraints().addAll(column1, column2);

		pane.add(new Label("Endpoint URL"), 0, 0);
		TextField endpointURL = new TextField("");
		endpointURL.setId(sourceOrTarget + "endpointURLTextField");
		endpointURL.focusedProperty().addListener((arg0, oldValue, newValue) -> {
			if (endpointURL.getText() != null && !endpointURL.getText().equals("")) {
				if (!newValue) { // when focus lost
					// check if it is a file
					File f = new File(endpointURL.getText());
					if (!(f.exists() && !f.isDirectory())) {
						// check if it is a valid URL
						try {
							new URL(endpointURL.getText());
						} catch (MalformedURLException e) {
							// set the textField to error msg
							endpointURL.setText(endpointURLError);
							endpointURL.setStyle("-fx-text-inner-color: red;");
						}
					}
				}
			}
		});
		endpointURL.setOnMouseClicked(e -> {
			if (endpointURL.getText() != null && !endpointURL.getText().equals("")) {
				if (endpointURL.getText().equals(endpointURLError)) {
					endpointURL.setStyle("");
					endpointURL.setText("");
				}
			}
		});

		Button fileEndpointButton = new Button();
		fileEndpointButton.setId(sourceOrTarget + "fileEndpointButton");
		Image fileButtonImage = new Image(getClass().getResourceAsStream("/gui/file.png"), 20, 20, true, false);
		fileEndpointButton.setGraphic(new ImageView(fileButtonImage));
		fileEndpointButton.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
					"Endpoint File (*.xml, *.rdf, *.ttl, *.n3, *.nt)", "*.xml", "*.rdf", "*.ttl", "*.n3", "*.nt");
			fileChooser.getExtensionFilters().add(extFilter);
			File file = fileChooser.showOpenDialog(wizardView.getStage());
			if (file != null) {
				endpointURL.setText(file.getAbsolutePath());
				;
			}
		});
		HBox endpointBox = new HBox();
		HBox.setHgrow(endpointURL, Priority.ALWAYS);
		endpointBox.getChildren().addAll(endpointURL, fileEndpointButton);
		pane.add(endpointBox, 1, 0);

		pane.add(new Label("ID / Namespace"), 0, 1);
		TextField idNamespace = new TextField();
		idNamespace.setId(sourceOrTarget + "idNamespaceTextField");
		pane.add(idNamespace, 1, 1);

		pane.add(new Label("Graph"), 0, 2);
		TextField graph = new TextField();
		graph.setId(sourceOrTarget + "graphTextField");
		pane.add(graph, 1, 2);

		pane.add(new Label("Page size"), 0, 3);
		TextField pageSize = new TextField();
		pageSize.setId(sourceOrTarget + "pageSizeTextField");
		pageSize.focusedProperty().addListener((arg0, oldValue, newValue) -> {
			if (!newValue) { // when focus lost
				if (!pageSize.getText().matches("[0-9]+") && !pageSize.getText().equals("-1")) {
					// set the textField to error msg
					pageSize.setText(pageSizeError);
					pageSize.setStyle("-fx-text-inner-color: red;");
				}
			}

		});
		pageSize.setOnMouseClicked(e -> {
			if (pageSize.getText().equals(pageSizeError)) {
				pageSize.setStyle("");
				pageSize.setText("");
			}
		});
		pane.add(pageSize, 1, 3);

		TextField[] textFields = new TextField[] { endpointURL, idNamespace, graph, pageSize };
		if (sourceOrTarget == SOURCE) {
			sourceFields = textFields;
			return new TitledPane("Source endpoint", pane);
		} else {
			targetFields = textFields;
			return new TitledPane("Target endpoint", pane);
		}
	}

	/**
	 * Fills the Textfield with the Information
	 *
	 * @param sourceOrTarget
	 *            if True source else Target
	 * @param endpoint
	 *            URL of the Endpoint
	 * @param idNamespace
	 *            Namespace of the Endpoint
	 * @param graph
	 *            Metrik or Graph of the Endpoint
	 * @param pageSize
	 *            Length of the Limes Query
	 */
	public void setFields(SourceOrTarget sourceOrTarget, String endpoint, String idNamespace, String graph,
			String pageSize) {
		TextField[] textFields = sourceOrTarget == SOURCE ? sourceFields : targetFields;
		textFields[0].setText(endpoint);
		textFields[1].setText(idNamespace);
		textFields[2].setText(graph);
		textFields[3].setText(pageSize);
	}

	/**
	 * Saves the actual Parameters to the Controller
	 */
	@Override
	public void save() {
		controller.save(SOURCE, sourceFields[0].getText(), sourceFields[1].getText(), sourceFields[2].getText(),
				sourceFields[3].getText());
		controller.save(TARGET, targetFields[0].getText(), targetFields[1].getText(), targetFields[2].getText(),
				targetFields[3].getText());
	}

	public TextField[] getSourceFields() {
		return sourceFields;
	}

	public TextField[] getTargetFields() {
		return targetFields;
	}

	@Override
	public Boolean isAutomated() {
		return false;
	}

	@Override
	public void setAutomated(boolean automated) {
		// There are no different modes here so this can stay empty

	}
}

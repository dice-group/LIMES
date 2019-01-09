package org.aksw.limes.core.gui.view;

import java.io.File;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.controller.ResultController;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.InstanceProperty;
import org.aksw.limes.core.gui.model.Result;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * View to show the results of the query, and their instances
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */

public class ResultView {

	/**
	 * Table with the list of InstanceMatches
	 */
	private TableView<Result> table;

	/**
	 * Corresponding Controller for the Resultview
	 */
	private final ResultController controller;

	/**
	 * Lists the Instance Properties of the clicked source Instance
	 */
	private TableView<InstanceProperty> sourceInstanceTable;

	/**
	 * Lists the Instance Properties of the clicked target Instance
	 */
	private TableView<InstanceProperty> targetInstanceTable;

	/**
	 * MenuItem to save Results to File
	 */
	private MenuItem itemSaveResults;

	/**
	 * Used to save learned LinkSpecification. Only visible in machine learning
	 */
	private Button saveLinkSpecButton;

	/**
	 * learned LinkSpecification
	 */
	private LinkSpecification learnedLS;

	/**
	 * Mapping of the results
	 */
	private AMapping mapping;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Constructor
	 * 
	 * @param config
	 *            the current config
	 */
	public ResultView(Config config) {
		this.createWindow();
		this.controller = new ResultController(this, config);
	}

	public ResultView(Config config, LinkSpecification learnedLS, MainController mainController) {
		this.createWindow();
		this.learnedLS = learnedLS;
		this.controller = new ResultController(this, config, mainController);
	}

	/**
	 * Creates the Window, with the 3 Tables, which show the matched Instances,
	 * and the Properties of clicked Source and Target
	 */
	private void createWindow() {
		final Stage stage = new Stage();

		final VBox root = new VBox();
		final HBox resultProperties = new HBox();

		// Build Menubar for saving of results
		final Menu menuFile = new Menu("File");
		this.itemSaveResults = new MenuItem("Save Results");
		this.itemSaveResults.setOnAction(e -> {
			final FileChooser fileChooser = new FileChooser();
			final FileChooser.ExtensionFilter extFilterRDF = new FileChooser.ExtensionFilter("RDF File", ".n3", ".nt",
					".ttl", ".rdf", ".jsonld");
			final FileChooser.ExtensionFilter extFilterCSV = new FileChooser.ExtensionFilter("CSV File", ".csv");
			fileChooser.getExtensionFilters().add(extFilterRDF);
			fileChooser.getExtensionFilters().add(extFilterCSV);
			final File file = fileChooser.showSaveDialog(stage);
			if (file != null) {
				if (this.mapping != null) {
					this.controller.saveResults(this.mapping, file);
				} else if (this.controller.getCurrentConfig().getMapping() != null) {
					this.controller.saveResults(this.controller.getCurrentConfig().getMapping(), file);
				} else {
					this.logger.error("No mapping to save found!");
				}
			}
		});
		menuFile.getItems().add(this.itemSaveResults);
		root.getChildren().add(new MenuBar(menuFile));

		// Build table for sourceproperties
		this.sourceInstanceTable = new TableView<>();
		final TableColumn<InstanceProperty, String> sourceInstancePropertyColumn = new TableColumn<>(
				"Property");
		sourceInstancePropertyColumn.setCellValueFactory(new PropertyValueFactory<>("property"));

		this.sourceInstanceTable.getColumns().add(sourceInstancePropertyColumn);
		final TableColumn<InstanceProperty, String> sourceInstanceValueColumn = new TableColumn<>(
				"Value");
		sourceInstanceValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

		this.sourceInstanceTable.getColumns().add(sourceInstanceValueColumn);
		resultProperties.getChildren().add(this.sourceInstanceTable);

		// set size of columns
		sourceInstancePropertyColumn.prefWidthProperty().bind(this.sourceInstanceTable.widthProperty().divide(2));
		sourceInstanceValueColumn.prefWidthProperty().bind(this.sourceInstanceTable.widthProperty().divide(2));

		// Build table for targetproperties
		this.targetInstanceTable = new TableView<>();
		final TableColumn<InstanceProperty, String> targetInstancePropertyColumn = new TableColumn<>(
				"Property");
		targetInstancePropertyColumn.setCellValueFactory(new PropertyValueFactory<>("property"));
		this.targetInstanceTable.getColumns().add(targetInstancePropertyColumn);
		final TableColumn<InstanceProperty, String> targetInstanceValueColumn = new TableColumn<>(
				"Value");
		targetInstanceValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
		this.targetInstanceTable.getColumns().add(targetInstanceValueColumn);
		resultProperties.getChildren().add(this.targetInstanceTable);

		// set size of columns
		targetInstancePropertyColumn.prefWidthProperty().bind(this.targetInstanceTable.widthProperty().divide(2));
		targetInstanceValueColumn.prefWidthProperty().bind(this.targetInstanceTable.widthProperty().divide(2));

		// Build table for instances
		this.table = new TableView<>();
		final TableColumn<Result, String> columnSource = new TableColumn<>("Source URI");
		columnSource.setCellValueFactory(new PropertyValueFactory<>("sourceURI"));
		this.table.getColumns().add(columnSource);
		final TableColumn<Result, String> columnTarget = new TableColumn<>("Target URI");
		columnTarget.setCellValueFactory(new PropertyValueFactory<>("targetURI"));
		this.table.getColumns().add(columnTarget);
		final TableColumn<Result, Double> columnValue = new TableColumn<>("value");
		columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
		this.table.getColumns().add(columnValue);
		this.table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				this.controller.showProperties(this.table.getSelectionModel().getSelectedItem());
			}
		});
		// set size
		columnSource.prefWidthProperty().bind(this.table.widthProperty().divide(40).multiply(17));
		columnTarget.prefWidthProperty().bind(this.table.widthProperty().divide(40).multiply(17));
		columnValue.prefWidthProperty().bind(this.table.widthProperty().divide(10));

		root.getChildren().add(resultProperties);
		root.getChildren().add(this.table);

		// Set sourceInstanceTable and targetInstanceTable to currentWindowsize
		root.widthProperty().addListener((ChangeListener<Number>) (arg0, arg1, arg2) -> {
			ResultView.this.sourceInstanceTable.setMinWidth(arg2.doubleValue() / 2);
			ResultView.this.sourceInstanceTable.setMaxWidth(arg2.doubleValue() / 2);
			ResultView.this.sourceInstanceTable.setPrefWidth(arg2.doubleValue() / 2);
			ResultView.this.targetInstanceTable.setMinWidth(arg2.doubleValue() / 2);
			ResultView.this.targetInstanceTable.setMaxWidth(arg2.doubleValue() / 2);
			ResultView.this.targetInstanceTable.setPrefWidth(arg2.doubleValue() / 2);

		});
		this.saveLinkSpecButton = new Button("Save Linkspecification");
		this.saveLinkSpecButton.setTooltip(new Tooltip("Puts the learned link specification to the metric builder"));
		this.saveLinkSpecButton.setVisible(false);
		root.getChildren().add(this.saveLinkSpecButton);
		this.saveLinkSpecButton.setOnMouseClicked(e -> {
			this.controller.saveLinkSpec(this.learnedLS);
		});

		final Scene scene = new Scene(root, 800, 600);
		scene.getStylesheets().add("gui/main.css");
		this.sourceInstanceTable.setPrefWidth(scene.getWidth() / 2);
		this.targetInstanceTable.setPrefWidth(scene.getWidth() / 2);
		stage.setMinHeight(600);
		stage.setMinWidth(800);

		stage.setTitle("LIMES");
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Puts the Results of a LIMES-query in the Table table and makes the
	 * {@link #saveLinkSpecButton} visible
	 *
	 * @param results
	 *            List of the Limes results following the Model of Results
	 */
	public void showResults(ObservableList<Result> results, AMapping resultMapping) {
		if (this.learnedLS != null) {
			this.saveLinkSpecButton.setVisible(true);
		}
		this.table.setItems(results);
		this.mapping = resultMapping;
		this.controller.setCachesFixed();
	}

	/**
	 * Show the Items of instanceProperty in sourceInstanceTable
	 *
	 * @param instanceProperty
	 *            List of Source-InstanceProperties
	 */
	public void showSourceInstance(ObservableList<InstanceProperty> instanceProperty) {
		this.sourceInstanceTable.setItems(instanceProperty);
	}

	/**
	 * Show the Items of instanceProperty in targetInstanceTable
	 *
	 * @param instanceProperty
	 *            List of Target-InstanceProperties
	 */
	public void showTargetInstance(ObservableList<InstanceProperty> instanceProperty) {
		this.targetInstanceTable.setItems(instanceProperty);
	}

	/**
	 * returns the mapping
	 * 
	 * @return mapping
	 */
	public AMapping getMapping() {
		return this.mapping;
	}

	/**
	 * sets mapping
	 * 
	 * @param mapping
	 *            mapping
	 */
	public void setMapping(AMapping mapping) {
		this.mapping = mapping;
	}

}

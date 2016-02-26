package org.aksw.limes.core.gui.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.aksw.limes.core.gui.controller.ActiveLearningResultController;
import org.aksw.limes.core.gui.model.ActiveLearningModel;
import org.aksw.limes.core.gui.model.ActiveLearningResult;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.InstanceProperty;

/**
 * Extends the original result view by adding the possibility to review matches
 * and continue the learning process
 * 
 * @author Felix Brei
 *
 */
public class ActiveLearningResultView {

	/**
	 * Table that contains the results of the learning process, extended by an
	 * 'is match'-flag
	 */
	private TableView<ActiveLearningResult> table;

	/**
	 * Corresponding controller
	 */
	private ActiveLearningResultController controller;

	/**
	 * Lists the Instance Properties of the clicked source Instance
	 */
	private TableView<InstanceProperty> sourceInstanceTable;

	/**
	 * Lists the Instance Properties of the clicked target Instance
	 */
	private TableView<InstanceProperty> targetInstanceTable;

	private Config config;

	private Button learnButton = new Button("Learn");

	private Button getMetricButton = new Button("Get Metric");

	private ActiveLearningModel model;

	private Label metricLabel = new Label("");

	private ActiveLearningView view;

	public ProgressIndicator learnProgress;

	/**
	 * Default constructor
	 */
	public ActiveLearningResultView(Config c, ActiveLearningModel m,
			ActiveLearningView v) {

		this.model = m;
		this.config = c;
		this.controller = new ActiveLearningResultController(this, config,
				model);
		view = v;

		Stage stage = new Stage();

		VBox root = new VBox();
		HBox resultProperties = new HBox();

		// Build table for sourceproperties
		sourceInstanceTable = new TableView<InstanceProperty>();
		TableColumn<InstanceProperty, String> sourceInstancePropertyColumn = new TableColumn<InstanceProperty, String>(
				"Property");
		sourceInstancePropertyColumn
				.setCellValueFactory(new PropertyValueFactory<>("property"));

		sourceInstanceTable.getColumns().add(sourceInstancePropertyColumn);
		TableColumn<InstanceProperty, String> sourceInstanceValueColumn = new TableColumn<InstanceProperty, String>(
				"Value");
		sourceInstanceValueColumn
				.setCellValueFactory(new PropertyValueFactory<>("value"));

		sourceInstanceTable.getColumns().add(sourceInstanceValueColumn);
		resultProperties.getChildren().add(sourceInstanceTable);

		// set size of columns
		sourceInstancePropertyColumn.prefWidthProperty().bind(
				sourceInstanceTable.widthProperty().divide(2));
		sourceInstanceValueColumn.prefWidthProperty().bind(
				sourceInstanceTable.widthProperty().divide(2));

		// Build table for targetproperties
		targetInstanceTable = new TableView<InstanceProperty>();
		TableColumn<InstanceProperty, String> targetInstancePropertyColumn = new TableColumn<InstanceProperty, String>(
				"Property");
		targetInstancePropertyColumn
				.setCellValueFactory(new PropertyValueFactory<>("property"));
		targetInstanceTable.getColumns().add(targetInstancePropertyColumn);
		TableColumn<InstanceProperty, String> targetInstanceValueColumn = new TableColumn<InstanceProperty, String>(
				"Value");
		targetInstanceValueColumn
				.setCellValueFactory(new PropertyValueFactory<>("value"));
		targetInstanceTable.getColumns().add(targetInstanceValueColumn);
		resultProperties.getChildren().add(targetInstanceTable);

		// set size of columns
		targetInstancePropertyColumn.prefWidthProperty().bind(
				targetInstanceTable.widthProperty().divide(2));
		targetInstanceValueColumn.prefWidthProperty().bind(
				targetInstanceTable.widthProperty().divide(2));

		// Build table for instances
		table = new TableView<ActiveLearningResult>();
		TableColumn<ActiveLearningResult, String> columnSource = new TableColumn<ActiveLearningResult, String>(
				"Source URI");
		columnSource
				.setCellValueFactory(new PropertyValueFactory<>("sourceURI"));
		table.getColumns().add(columnSource);
		TableColumn<ActiveLearningResult, String> columnTarget = new TableColumn<ActiveLearningResult, String>(
				"Target URI");
		columnTarget
				.setCellValueFactory(new PropertyValueFactory<>("targetURI"));
		table.getColumns().add(columnTarget);
		TableColumn<ActiveLearningResult, Double> columnValue = new TableColumn<ActiveLearningResult, Double>(
				"value");
		columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
		table.getColumns().add(columnValue);

		TableColumn<ActiveLearningResult, Boolean> columnIsMatch = new TableColumn<ActiveLearningResult, Boolean>(
				"Is Match?");
		columnIsMatch
				.setCellValueFactory(new PropertyValueFactory<ActiveLearningResult, Boolean>(
						"isMatch"));
		columnIsMatch
				.setCellFactory(

				new Callback<TableColumn<ActiveLearningResult, Boolean>, TableCell<ActiveLearningResult, Boolean>>() {
					public TableCell<ActiveLearningResult, Boolean> call(
							TableColumn<ActiveLearningResult, Boolean> p) {
						return new CheckBoxTableCell<ActiveLearningResult, Boolean>();
					}
				});

		columnIsMatch.setEditable(true);
		table.setEditable(true);

		table.getColumns().add(columnIsMatch);

		table.setOnMouseClicked(e -> {
			controller.showProperties(table.getSelectionModel()
					.getSelectedItem());
		});

		// set size
		columnSource.prefWidthProperty().bind(
				table.widthProperty().divide(40).multiply(17));
		columnTarget.prefWidthProperty().bind(
				table.widthProperty().divide(40).multiply(17));
		columnValue.prefWidthProperty().bind(table.widthProperty().divide(10));

		root.getChildren().add(resultProperties);
		root.getChildren().add(table);

		// Set sourceInstanceTable and targetInstanceTable to currentWindowsize
		root.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {
				sourceInstanceTable.setMinWidth(arg2.doubleValue() / 2);
				sourceInstanceTable.setMaxWidth(arg2.doubleValue() / 2);
				sourceInstanceTable.setPrefWidth(arg2.doubleValue() / 2);
				targetInstanceTable.setMinWidth(arg2.doubleValue() / 2);
				targetInstanceTable.setMaxWidth(arg2.doubleValue() / 2);
				targetInstanceTable.setPrefWidth(arg2.doubleValue() / 2);

			}
		});
		learnProgress = new ProgressIndicator();
		learnProgress.setVisible(false);
		learnButton.setOnAction(e -> {
			controller.learnButtonPressed();
		});
		getMetricButton
				.setOnAction(e -> {
					String metricString = controller.getMetric()
							.getExpression();
					config.setMetricExpression(metricString);
					config.setAcceptanceThreshold(controller.getMetric()
							.getThreshold());
					view.view.graphBuild.graphBuildController
							.generateGraphFromConfig();
				});
		HBox buttonBox = new HBox();
		buttonBox.getChildren().addAll(learnButton, getMetricButton,
				learnProgress);

		buttonBox.setSpacing(10);
		root.setSpacing(10);

		root.getChildren().add(buttonBox);
		root.getChildren().add(metricLabel);

		Scene scene = new Scene(root, 800, 600);
		scene.getStylesheets().add("gui/main.css");
		sourceInstanceTable.setPrefWidth(scene.getWidth() / 2);
		targetInstanceTable.setPrefWidth(scene.getWidth() / 2);

		stage.setTitle("LIMES");
		stage.setScene(scene);
		stage.show();

	}

	public void setLabel(String bestMetric) {
		metricLabel.setText(bestMetric);

	}

	public void showResults(ObservableList<ActiveLearningResult> results) {
		table.setItems(results);
	}

	/**
	 * Show the Items of instanceProperty in sourceInstanceTable
	 * 
	 * @param instanceProperty
	 *            List of Source-InstanceProperties
	 */
	public void showSourceInstance(
			ObservableList<InstanceProperty> instanceProperty) {
		sourceInstanceTable.setItems(instanceProperty);
	}

	/**
	 * Show the Items of instanceProperty in targetInstanceTable
	 * 
	 * @param instanceProperty
	 *            List of Target-InstanceProperties
	 */
	public void showTargetInstance(
			ObservableList<InstanceProperty> instanceProperty) {
		targetInstanceTable.setItems(instanceProperty);
	}

	public ActiveLearningResultController getActiveLearningResultController() {
		return this.controller;
	}

	public TableView<ActiveLearningResult> getMatchingTable() {
		return this.table;
	}

}
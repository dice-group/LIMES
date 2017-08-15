package org.aksw.limes.core.gui.view.ml;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.controller.ml.ActiveLearningResultController;
import org.aksw.limes.core.gui.model.ActiveLearningResult;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.InstanceProperty;
import org.aksw.limes.core.gui.model.Result;
import org.aksw.limes.core.gui.model.ml.ActiveLearningModel;
import org.aksw.limes.core.gui.view.ResultView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;


/**
 * Extends the original result view by adding the possibility to review matches
 * and continue the learning process
 * 
 * @author Daniel Obraczka
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

	private Button getResultsButton = new Button("Get Results");

	private ActiveLearningModel model;

	public ProgressIndicator learnProgress;
	
	private MainController mainController;

	/**
	 * Default constructor builds the view
	 * @param c config
	 * @param m model
	 * @param mainController mainController
	 */
	public ActiveLearningResultView(Config c, ActiveLearningModel m, MainController mainController) {

	    	this.mainController = mainController;
		this.model = m;
		this.config = c;
		this.controller = new ActiveLearningResultController(this, config,
				model);

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
				"Match?");
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
				table.widthProperty().divide(40).multiply(15));
		columnTarget.prefWidthProperty().bind(
				table.widthProperty().divide(40).multiply(15));
		columnValue.prefWidthProperty().bind(table.widthProperty().divide(9));
		columnIsMatch.prefWidthProperty().bind(table.widthProperty().divide(9));

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
		learnButton.setTooltip(new Tooltip("start learning"));
		getResultsButton.setTooltip(new Tooltip("compute results"));
		HBox buttonBox = new HBox();
		buttonBox.getChildren().addAll(learnButton, getResultsButton,
				learnProgress);

		buttonBox.setSpacing(10);
		root.setSpacing(10);

		root.getChildren().add(buttonBox);

		Scene scene = new Scene(root, 1000, 600);
		scene.getStylesheets().add("gui/main.css");
		sourceInstanceTable.setPrefWidth(scene.getWidth() / 2);
		targetInstanceTable.setPrefWidth(scene.getWidth() / 2);
		getResultsButton
				.setOnAction(e -> {
				    stage.close();
				    ResultView resultView = new ResultView(config, model.getLearnedLS(), mainController);
		ObservableList<Result> resultList = FXCollections
				.observableArrayList();
				model.getLearnedMapping().getMap().forEach((sourceURI, map2) -> {
					map2.forEach((targetURI, value) -> {
						resultList.add(new Result(sourceURI,
								targetURI, value));
					});
				});
				    resultView.showResults(resultList, model.getLearnedMapping());
				});

		stage.setTitle("LIMES");
		stage.setMinHeight(scene.getHeight());
		stage.setMinWidth(scene.getWidth());
		stage.setScene(scene);
		stage.show();

	}

	/**
	 * updates the view with the results
	 * @param results results fo active learning
	 */
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

	/**
	 * returns the corresponding controller
	 * @return controller
	 */
	public ActiveLearningResultController getActiveLearningResultController() {
		return this.controller;
	}

	/**
	 * returns the matching table
	 * @return table
	 */
	public TableView<ActiveLearningResult> getMatchingTable() {
		return this.table;
	}

}

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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

	private final Button learnButton = new Button("Learn");

	private final Button getResultsButton = new Button("Get Results");

	private ActiveLearningModel model;

	public ProgressIndicator learnProgress;

	/**
	 * Default constructor builds the view
	 * 
	 * @param c
	 *            config
	 * @param m
	 *            model
	 * @param mainController
	 *            mainController
	 */
	public ActiveLearningResultView(Config c, ActiveLearningModel m, MainController mainController) {

		this.model = m;
		this.config = c;
		this.controller = new ActiveLearningResultController(this, this.config, this.model);

		final Stage stage = new Stage();

		final VBox root = new VBox();
		final HBox resultProperties = new HBox();

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
		final TableColumn<ActiveLearningResult, String> columnSource = new TableColumn<>(
				"Source URI");
		columnSource.setCellValueFactory(new PropertyValueFactory<>("sourceURI"));
		this.table.getColumns().add(columnSource);
		final TableColumn<ActiveLearningResult, String> columnTarget = new TableColumn<>(
				"Target URI");
		columnTarget.setCellValueFactory(new PropertyValueFactory<>("targetURI"));
		this.table.getColumns().add(columnTarget);
		final TableColumn<ActiveLearningResult, Double> columnValue = new TableColumn<>(
				"value");
		columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
		this.table.getColumns().add(columnValue);

		final TableColumn<ActiveLearningResult, Boolean> columnIsMatch = new TableColumn<>(
				"Match?");
		columnIsMatch.setCellValueFactory(new PropertyValueFactory<ActiveLearningResult, Boolean>("isMatch"));
		columnIsMatch.setCellFactory(

				p -> new CheckBoxTableCell<>());

		columnIsMatch.setEditable(true);
		this.table.setEditable(true);

		this.table.getColumns().add(columnIsMatch);

		this.table.setOnMouseClicked(e -> {
			this.controller.showProperties(this.table.getSelectionModel().getSelectedItem());
		});

		// set size
		columnSource.prefWidthProperty().bind(this.table.widthProperty().divide(40).multiply(15));
		columnTarget.prefWidthProperty().bind(this.table.widthProperty().divide(40).multiply(15));
		columnValue.prefWidthProperty().bind(this.table.widthProperty().divide(9));
		columnIsMatch.prefWidthProperty().bind(this.table.widthProperty().divide(9));

		root.getChildren().add(resultProperties);
		root.getChildren().add(this.table);

		// Set sourceInstanceTable and targetInstanceTable to currentWindowsize
		root.widthProperty().addListener((ChangeListener<Number>) (arg0, arg1, arg2) -> {
			ActiveLearningResultView.this.sourceInstanceTable.setMinWidth(arg2.doubleValue() / 2);
			ActiveLearningResultView.this.sourceInstanceTable.setMaxWidth(arg2.doubleValue() / 2);
			ActiveLearningResultView.this.sourceInstanceTable.setPrefWidth(arg2.doubleValue() / 2);
			ActiveLearningResultView.this.targetInstanceTable.setMinWidth(arg2.doubleValue() / 2);
			ActiveLearningResultView.this.targetInstanceTable.setMaxWidth(arg2.doubleValue() / 2);
			ActiveLearningResultView.this.targetInstanceTable.setPrefWidth(arg2.doubleValue() / 2);

		});
		this.learnProgress = new ProgressIndicator();
		this.learnProgress.setVisible(false);
		this.learnButton.setOnAction(e -> {
			this.controller.learnButtonPressed();
		});
		this.learnButton.setTooltip(new Tooltip("start learning"));
		this.getResultsButton.setTooltip(new Tooltip("compute results"));
		final HBox buttonBox = new HBox();
		buttonBox.getChildren().addAll(this.learnButton, this.getResultsButton, this.learnProgress);

		buttonBox.setSpacing(10);
		root.setSpacing(10);

		root.getChildren().add(buttonBox);

		final Scene scene = new Scene(root, 1000, 600);
		scene.getStylesheets().add("gui/main.css");
		this.sourceInstanceTable.setPrefWidth(scene.getWidth() / 2);
		this.targetInstanceTable.setPrefWidth(scene.getWidth() / 2);
		this.getResultsButton.setOnAction(e -> {
			stage.close();
			final ResultView resultView = new ResultView(this.config, this.model.getLearnedLS(), mainController);
			final ObservableList<Result> resultList = FXCollections.observableArrayList();
			this.model.getLearnedMapping().getMap().forEach((sourceURI, map2) -> {
				map2.forEach((targetURI, value) -> {
					resultList.add(new Result(sourceURI, targetURI, value));
				});
			});
			resultView.showResults(resultList, this.model.getLearnedMapping());
		});

		stage.setTitle("LIMES");
		stage.setMinHeight(scene.getHeight());
		stage.setMinWidth(scene.getWidth());
		stage.setScene(scene);
		stage.show();

	}

	/**
	 * updates the view with the results
	 * 
	 * @param results
	 *            results fo active learning
	 */
	public void showResults(ObservableList<ActiveLearningResult> results) {
		this.table.setItems(results);
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
	 * returns the corresponding controller
	 * 
	 * @return controller
	 */
	public ActiveLearningResultController getActiveLearningResultController() {
		return this.controller;
	}

	/**
	 * returns the matching table
	 * 
	 * @return table
	 */
	public TableView<ActiveLearningResult> getMatchingTable() {
		return this.table;
	}

}

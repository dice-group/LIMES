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
import javafx.beans.value.ObservableValue;
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
    private ResultController controller;

    /**
     * Lists the Instance Properties of the clicked source Instance
     */
    private TableView<InstanceProperty> sourceInstanceTable;

    /**
     * Lists the Instance Properties of the clicked target Instance
     */
    private TableView<InstanceProperty> targetInstanceTable;

    /**
     * List of Results from Mapping
     */
    private ObservableList<Result> results;

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
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Constructor
     * @param config the current config
     */
    public ResultView(Config config) {
        createWindow();
        this.controller = new ResultController(this, config);
    }

    public ResultView(Config config, LinkSpecification learnedLS, MainController mainController) {
        createWindow();
        this.learnedLS = learnedLS;
        this.controller = new ResultController(this, config, mainController);
    }
    /**
     * Creates the Window, with the 3 Tables, which show the matched Instances,
     * and the Properties of clicked Source and Target
     */
    private void createWindow() {
        Stage stage = new Stage();

        VBox root = new VBox();
        HBox resultProperties = new HBox();

        // Build Menubar for saving of results
        Menu menuFile = new Menu("File");
        itemSaveResults = new MenuItem("Save Results");
        itemSaveResults
                .setOnAction(e -> {
                    FileChooser fileChooser = new FileChooser();
                    FileChooser.ExtensionFilter extFilterRDF = new FileChooser.ExtensionFilter("RDF File", ".n3", ".nt", ".ttl", ".rdf", ".jsonld");
                    FileChooser.ExtensionFilter extFilterCSV = new FileChooser.ExtensionFilter("CSV File", ".csv");
                    fileChooser.getExtensionFilters().add(extFilterRDF);
                    fileChooser.getExtensionFilters().add(extFilterCSV);
                    File file = fileChooser.showSaveDialog(stage);
                    if (file != null) {
                	if(this.mapping != null){
                            controller.saveResults(this.mapping, file);
                	}else if(controller.getCurrentConfig().getMapping() != null){
                            controller.saveResults(controller.getCurrentConfig().getMapping(), file);
                	}else{
                	   logger.error("No mapping to save found!"); 
                	}
                    }
                });
        menuFile.getItems().add(itemSaveResults);
        root.getChildren().add(new MenuBar(menuFile));

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
        table = new TableView<Result>();
        TableColumn<Result, String> columnSource = new TableColumn<Result, String>(
                "Source URI");
        columnSource
                .setCellValueFactory(new PropertyValueFactory<>("sourceURI"));
        table.getColumns().add(columnSource);
        TableColumn<Result, String> columnTarget = new TableColumn<Result, String>(
                "Target URI");
        columnTarget
                .setCellValueFactory(new PropertyValueFactory<>("targetURI"));
        table.getColumns().add(columnTarget);
        TableColumn<Result, Double> columnValue = new TableColumn<Result, Double>(
                "value");
        columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        table.getColumns().add(columnValue);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
            controller.showProperties(table.getSelectionModel()
                    .getSelectedItem());
            }
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
        saveLinkSpecButton = new Button("Save Linkspecification");
        saveLinkSpecButton.setTooltip(new Tooltip("Puts the learned link specification to the metric builder"));
        saveLinkSpecButton.setVisible(false);
        root.getChildren().add(saveLinkSpecButton);
        saveLinkSpecButton.setOnMouseClicked(e -> {
            controller.saveLinkSpec(learnedLS);
        });

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("gui/main.css");
        sourceInstanceTable.setPrefWidth(scene.getWidth() / 2);
        targetInstanceTable.setPrefWidth(scene.getWidth() / 2);
        stage.setMinHeight(600);
        stage.setMinWidth(800);

        stage.setTitle("LIMES");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Puts the Results of a LIMES-query in the Table table and makes the {@link #saveLinkSpecButton} visible
     *
     * @param results
     *         List of the Limes results following the Model of Results
     */
    public void showResults(ObservableList<Result> results, AMapping resultMapping) {
	if(learnedLS != null){
	saveLinkSpecButton.setVisible(true);
	}
        this.results = results;
        table.setItems(results);
        this.mapping = resultMapping;
    }

    /**
     * Show the Items of instanceProperty in sourceInstanceTable
     *
     * @param instanceProperty
     *         List of Source-InstanceProperties
     */
    public void showSourceInstance(
            ObservableList<InstanceProperty> instanceProperty) {
        sourceInstanceTable.setItems(instanceProperty);
    }

    /**
     * Show the Items of instanceProperty in targetInstanceTable
     *
     * @param instanceProperty
     *         List of Target-InstanceProperties
     */
    public void showTargetInstance(
            ObservableList<InstanceProperty> instanceProperty) {
        targetInstanceTable.setItems(instanceProperty);
    }

    /**
     * returns the mapping
     * @return mapping
     */
    public AMapping getMapping() {
        return mapping;
    }

    /**
     * sets mapping
     * @param mapping mapping
     */
    public void setMapping(AMapping mapping) {
        this.mapping = mapping;
    }


}

package org.aksw.limes.core.gui.view;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.view.graphBuilder.GraphBuildView;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Main View of the Application
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class MainView {
    /**
     * Toolbox of the MainView adds Nodes to Graph
     */
    public ToolBox toolBox;
    /**
     * GraphBuildView to Model and View the Metric
     */
    public GraphBuildView graphBuild;
    /**
     * Corresponding Controller
     */
    private MainController controller;
    /**
     * MenuItem to do a Save Operation
     */
    private MenuItem itemSave;
    
    /**
     * Menu to edit a configuration
     */
    private Menu menuEdit;
    /**
     * Button to run mapping
     */
    private Button runButton;
    /**
     * MenuItem to start the BatchLearning Dialog
     */
    private MenuItem itemBatchLearning;
    /**
     * MenuItem to start the UnsupervisedLearning Dialog
     */
    private MenuItem itemUnsupervisedLearning;
    /**
     * MenuItem to start the Active Learning Dialog
     */
    private MenuItem itemActiveLearning; 
    /**
     * root pane
     */
    private BorderPane root;
   /** 
    * Scene of the main view
    */
    private Scene scene;

    /**
     * Constructor
     *
     * @param stage
     *         Used Stage of the Application
     */
    public MainView(Stage stage) {
        showWindow(stage);
    }

    /**
     * Sets corresponding Controller
     *
     * @param controller
     *         Corresponding Controller
     */
    public void setController(MainController controller) {
        this.controller = controller;
    }

    /**
     * Builds and Shows the Window
     *
     * @param stage
     */
    private void showWindow(Stage stage) {
        root = new BorderPane();

        MenuBar menuBar = buildMenuBar(stage);
        FlowPane flow = new FlowPane(Orientation.HORIZONTAL);
        flow.setAlignment(Pos.CENTER_RIGHT);
        flow.setStyle("-fx-background-color: linear-gradient(to top, -fx-base, derive(-fx-base,30%));");
        flow.getChildren().add(new ImageView(new Image("gui/limes.png")));
        HBox menuBox = new HBox(0);
        menuBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(flow, Priority.ALWAYS);
        menuBox.getChildren().addAll(menuBar, flow);
        toolBox = new ToolBox(this);
        graphBuild = new GraphBuildView(toolBox);
        HBox runBox = new HBox(0);
        runBox.setAlignment(Pos.CENTER_RIGHT);
        runButton = new Button("Run");
        runButton.setId("runButton");
        runButton.setTooltip(new Tooltip("Execute this link specification"));
        runButton.setOnAction(e -> {
            controller.map();
        });
        runBox.getChildren().add(runButton);
        root.setTop(menuBox);
        root.setLeft(toolBox);
        root.setRight(graphBuild);
        root.setBottom(runBox);
        graphBuild.widthProperty().bind(
                root.widthProperty().subtract(toolBox.widthProperty()));
        graphBuild.heightProperty().bind(toolBox.heightProperty().subtract(runBox.heightProperty()));
        toolBox.prefHeightProperty().bind(root.heightProperty().subtract(runBox.heightProperty()));
        toolBox.setMinHeight(toolBox.prefHeightProperty().doubleValue());
        

        graphBuild.start();

        scene = new Scene(root, 950, 650);
        root.prefHeightProperty().bind(scene.heightProperty());
        root.prefWidthProperty().bind(scene.widthProperty());
        root.minHeightProperty().bind(scene.heightProperty());
        root.minWidthProperty().bind(scene.widthProperty());
        scene.getStylesheets().add("gui/main.css");
        stage.setMinHeight(scene.getHeight());
        stage.setMinWidth(scene.getWidth());
        stage.setMaximized(true);
        stage.setTitle("LIMES");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Builds and returns MenuBar for the MainView
     *
     * @param stage
     *         Used Stage of the Application
     * @return MenuBar of the Application
     */
    private MenuBar buildMenuBar(Window stage) {
        Menu menuConfiguration = new Menu("Configuration");
       	menuConfiguration.setId("menuConfiguration"); 
        
        //============ New Configuration ====================
        MenuItem itemNew = new MenuItem("New");
        itemNew.setId("#itemNew");
        itemNew.setOnAction(e -> {
        WizardView wizardView = new WizardView();
        controller.newConfig(wizardView,new EditEndpointsView(wizardView), new EditClassMatchingView(wizardView),
                new EditPropertyMatchingView(wizardView));
        });
        menuConfiguration.getItems().add(itemNew);
        
        //=========== Edit Configuration ====================
        menuEdit = new Menu("Edit");
        menuEdit.setId("menuEdit");
        MenuItem itemEditClasses = new MenuItem("Edit Classes");
        itemEditClasses.setId("itemEditClasses");
        itemEditClasses.setOnAction(e -> {
        WizardView wizardView = new WizardView();
        controller.editConfig(wizardView, new EditClassMatchingView(wizardView),
                new EditPropertyMatchingView(wizardView));
        });
        MenuItem itemEditProperties = new MenuItem("Edit Properties");
        itemEditProperties.setId("itemEditProperties"); 
        itemEditProperties.setOnAction(e -> {
        WizardView wizardView = new WizardView();
        controller.editConfig(wizardView, new EditPropertyMatchingView(wizardView));
        });
        menuEdit.getItems().addAll(itemEditClasses, itemEditProperties);
        menuConfiguration.getItems().add(menuEdit);
        menuConfiguration.getItems().add(new SeparatorMenuItem());
        
        //=========== Load Configuration ===================
        MenuItem itemLoad = new MenuItem("Load Configuration");
        itemLoad.setId("itemLoad");
        itemLoad.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("LIMES Configuration File (*.xml, *.rdf, *.ttl, *.n3, *.nt)", "*.xml", "*.rdf", "*.ttl", "*.n3", "*.nt");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                controller.loadConfig(file);
            }
        });
        menuConfiguration.getItems().add(itemLoad);
        
        //========== Save Configuration ===================
        itemSave = new MenuItem("Save Configuration");
        itemSave.setId("itemSave");
        itemSave.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("LIMES Configuration File (*.rdf, *.ttl, *.n3, *.nt)", "*.rdf", "*.ttl", "*.n3", "*.nt");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                controller.saveConfig(file);
            }
        });
        menuConfiguration.getItems().add(itemSave);
        menuConfiguration.getItems().add(new SeparatorMenuItem());
        
        //========== Exit Application =====================
        MenuItem itemExit = new MenuItem("Exit");
        itemExit.setId("itemExit");
        itemExit.setOnAction(e -> controller.exit());
        menuConfiguration.getItems().add(itemExit);

        //========== Layout ==============================
        Menu menuLayout = new Menu("Layout");
       	menuLayout.setId("menuLayout"); 
        MenuItem layoutGraph = new MenuItem("Refresh Layout");
       	layoutGraph.setId("layoutGraph"); 
        layoutGraph.setOnAction(e -> {
            graphBuild.graphBuildController.layoutGraph();
        });
        MenuItem deleteGraph = new MenuItem("Delete Graph");
       	deleteGraph.setId("deleteGraph"); 
        deleteGraph.setOnAction(e -> {
            graphBuild.graphBuildController.deleteGraph();
        });
        menuLayout.getItems().addAll(layoutGraph, deleteGraph);

        //============ Learning ========================
        Menu menuLearn = new Menu("Learn");
       	menuLearn.setId("menuLearn"); 

        itemBatchLearning = new MenuItem("Batch Learning");
        itemBatchLearning.setOnAction(e -> {
            controller.showBatchLearning();
        });


        itemUnsupervisedLearning = new MenuItem("Unsupervised Learning");
        itemUnsupervisedLearning.setOnAction(e -> {
            controller.showUnsupervisedLearning();
        });

        itemActiveLearning = new MenuItem("Active Learning");
        itemActiveLearning.setOnAction(e -> {
            controller.showActiveLearning();
        });
        menuLearn.getItems().add(itemActiveLearning);
        menuLearn.getItems().add(itemBatchLearning);
        menuLearn.getItems().add(itemUnsupervisedLearning);
        MenuBar menuBar = new MenuBar(menuConfiguration, menuLayout, menuLearn);
        menuBar.setId("menuBar");
        return menuBar;
    }

    /**
     * Enables menu and run buttons, if config is loaded
     *
     * @param isLoaded
     *         True if Config is Loaded
     */
    public void showLoadedConfig(boolean isLoaded) {
    	menuEdit.setDisable(!isLoaded);
        itemSave.setDisable(!isLoaded);
        runButton.setDisable(!isLoaded);
        itemBatchLearning.setDisable(!isLoaded);
        itemUnsupervisedLearning.setDisable(!isLoaded);
        itemActiveLearning.setDisable(!isLoaded);
    }


    /**
     * shows an error with the given header and content message, also displays the stack trace
     * @param header header of message
     * @param content content of message
     * @param ex thrown exception
     */
    public static void showErrorWithStacktrace(String header, String content, Throwable ex) {
	Alert alert = new Alert(AlertType.ERROR);
	alert.setHeaderText(header);
	alert.setContentText(content);

	// Create expandable Exception.
	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw);
	ex.printStackTrace(pw);
	ex.printStackTrace();
	String exceptionText = sw.toString();

	Label label = new Label("The exception stacktrace was:");

	TextArea textArea = new TextArea(exceptionText);
	textArea.setEditable(false);
	textArea.setWrapText(true);

	textArea.setMaxWidth(Double.MAX_VALUE);
	textArea.setMaxHeight(Double.MAX_VALUE);
	GridPane.setVgrow(textArea, Priority.ALWAYS);
	GridPane.setHgrow(textArea, Priority.ALWAYS);

	GridPane expContent = new GridPane();
	expContent.setMaxWidth(Double.MAX_VALUE);
	expContent.add(label, 0, 0);
	expContent.add(textArea, 0, 1);

	// Set expandable Exception into the dialog pane.
	alert.getDialogPane().setExpandableContent(expContent);
	alert.showAndWait();
    }

	public BorderPane getRoot() {
		return root;
	}

	public Scene getScene() {
		return scene;
	}
    
    
}

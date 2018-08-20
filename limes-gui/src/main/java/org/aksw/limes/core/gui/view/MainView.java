package org.aksw.limes.core.gui.view;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.view.graphBuilder.GraphBuildView;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import javafx.scene.layout.VBox;
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
	private GraphBuildView graphBuild;
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
	// private Button runButton;

	private Button toolBarNewConfigButton;
	private Button toolBarLoadConfigButton;
	private Button toolBarSaveConfigButton;
	private Button toolBarRunButton;

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
	 * path to toolbar icons
	 */
	private final String toolbarPath = "gui/toolbar/";

	/**
	 * Constructor
	 *
	 * @param stage
	 *            Used Stage of the Application
	 */
	public MainView(Stage stage) {
		this.showWindow(stage);
	}

	/**
	 * Sets corresponding Controller
	 *
	 * @param controller
	 *            Corresponding Controller
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
		this.root = new BorderPane();

		final MenuBar menuBar = this.buildMenuBar(stage);
		final FlowPane flow = new FlowPane(Orientation.HORIZONTAL);
		flow.setAlignment(Pos.CENTER_RIGHT);
		flow.setStyle("-fx-background-color: linear-gradient(to top, -fx-base, derive(-fx-base,30%));");
		flow.getChildren().add(new ImageView(new Image("gui/limes.png")));
		final HBox menuBox = new HBox(0);
		menuBox.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(flow, Priority.ALWAYS);
		menuBox.getChildren().addAll(menuBar, flow);
		final VBox menuAndToolbarBox = new VBox();
		menuAndToolbarBox.getChildren().addAll(menuBox, this.buildToolbar(stage));
		this.toolBox = new ToolBox(this);
		this.graphBuild = new GraphBuildView(this.toolBox);
		this.root.setTop(menuAndToolbarBox);
		this.root.setLeft(this.toolBox);
		this.root.setRight(this.graphBuild);
		this.graphBuild.widthProperty().bind(this.root.widthProperty().subtract(this.toolBox.widthProperty()));
		this.graphBuild.heightProperty().bind(this.toolBox.heightProperty());
		this.toolBox.prefHeightProperty().bind(this.root.heightProperty());
		this.toolBox.setMinHeight(this.toolBox.prefHeightProperty().doubleValue());

		this.graphBuild.start();

		this.scene = new Scene(this.root, 950, 650);
		this.root.prefHeightProperty().bind(this.scene.heightProperty());
		this.root.prefWidthProperty().bind(this.scene.widthProperty());
		this.root.minHeightProperty().bind(this.scene.heightProperty());
		this.root.minWidthProperty().bind(this.scene.widthProperty());
		this.scene.getStylesheets().add("gui/main.css");
		stage.setMinHeight(this.scene.getHeight());
		stage.setMinWidth(this.scene.getWidth());
		stage.setMaximized(true);
		stage.setTitle("LIMES");
		stage.setScene(this.scene);
		stage.show();
	}

	@SuppressWarnings("unchecked")
	private HBox buildToolbar(Window stage) {
		final double imageSize = 20.0;
		// Load icons
		final Image imageNewConfig = new Image(this.toolbarPath + "new_file.png", imageSize, imageSize, true, true);
		final Image imageSaveConfig = new Image(this.toolbarPath + "save_file.png", imageSize, imageSize, true, true);
		final Image imageLoadConfig = new Image(this.toolbarPath + "load_file.png", imageSize, imageSize, true, true);
		final Image imageRun = new Image(this.toolbarPath + "run.png", imageSize, imageSize, true, true);
		// Create Buttons
		this.toolBarNewConfigButton = new Button("", new ImageView(imageNewConfig));
		this.toolBarLoadConfigButton = new Button("", new ImageView(imageLoadConfig));
		this.toolBarSaveConfigButton = new Button("", new ImageView(imageSaveConfig));
		this.toolBarRunButton = new Button("", new ImageView(imageRun));
		// Add tooltips
		this.toolBarNewConfigButton.setTooltip(new Tooltip("Create a new configuration"));
		this.toolBarLoadConfigButton.setTooltip(new Tooltip("Load a new configuration file"));
		this.toolBarSaveConfigButton.setTooltip(new Tooltip("Save this configuration to a file"));
		this.toolBarRunButton.setTooltip(new Tooltip("Execute this link specification"));
		// Set ids
		this.toolBarNewConfigButton.setId("toolBarNewConfigButton");
		this.toolBarLoadConfigButton.setId("toolBarLoadConfigButton");
		this.toolBarSaveConfigButton.setId("toolBarSaveConfigButton");
		this.toolBarRunButton.setId("toolBarRunButton");
		// Custom style class
		this.toolBarNewConfigButton.getStyleClass().add("toolBarButton");
		this.toolBarLoadConfigButton.getStyleClass().add("toolBarButton");
		this.toolBarSaveConfigButton.getStyleClass().add("toolBarButton");
		this.toolBarRunButton.getStyleClass().add("toolBarButton");

		// Functionality
		this.toolBarNewConfigButton.setOnMouseClicked(e -> {
			final WizardView wizardView = new WizardView();
			this.controller.newConfig(wizardView, new EditEndpointsView(wizardView),
					new EditClassMatchingView(wizardView), new EditPropertyMatchingView(wizardView));
		});
		this.toolBarLoadConfigButton.setOnMouseClicked(new LoadConfigEventHandler(stage));
		this.toolBarSaveConfigButton.setOnMouseClicked(new SaveConfigEventHandler(stage));
		this.toolBarRunButton.setOnMouseClicked(e -> {
			this.controller.map();
		});

		// put in hbox and style it
		final HBox toolBarBox = new HBox();
		toolBarBox.getChildren().addAll(this.toolBarNewConfigButton, this.toolBarLoadConfigButton,
				this.toolBarSaveConfigButton, this.toolBarRunButton);
		toolBarBox.setStyle(
				"-fx-background-color: linear-gradient(to bottom, derive(-fx-base,30%), derive(-fx-base,60%));");
		toolBarBox.setSpacing(5.0);
		toolBarBox.setPadding(new Insets(0.0, 0.0, 1.0, 15.0));
		return toolBarBox;
	}

	/**
	 * Builds and returns MenuBar for the MainView
	 *
	 * @param stage
	 *            Used Stage of the Application
	 * @return MenuBar of the Application
	 */
	@SuppressWarnings("unchecked")
	private MenuBar buildMenuBar(Window stage) {
		final Menu menuConfiguration = new Menu("Configuration");
		menuConfiguration.setId("menuConfiguration");

		// ============ New Configuration ====================
		final MenuItem itemNew = new MenuItem("New");
		itemNew.setId("#itemNew");
		itemNew.setOnAction(e -> {
			final WizardView wizardView = new WizardView();
			this.controller.newConfig(wizardView, new EditEndpointsView(wizardView),
					new EditClassMatchingView(wizardView), new EditPropertyMatchingView(wizardView));
		});
		menuConfiguration.getItems().add(itemNew);

		// =========== Edit Configuration ====================
		this.menuEdit = new Menu("Edit");
		this.menuEdit.setId("menuEdit");
		final MenuItem itemEditClasses = new MenuItem("Edit Classes");
		itemEditClasses.setId("itemEditClasses");
		itemEditClasses.setOnAction(e -> {
			final WizardView wizardView = new WizardView();
			this.controller.editConfig(wizardView, new EditClassMatchingView(wizardView),
					new EditPropertyMatchingView(wizardView));
		});
		final MenuItem itemEditProperties = new MenuItem("Edit Properties");
		itemEditProperties.setId("itemEditProperties");
		itemEditProperties.setOnAction(e -> {
			final WizardView wizardView = new WizardView();
			this.controller.editConfig(wizardView, new EditPropertyMatchingView(wizardView));
		});
		this.menuEdit.getItems().addAll(itemEditClasses, itemEditProperties);
		menuConfiguration.getItems().add(this.menuEdit);
		menuConfiguration.getItems().add(new SeparatorMenuItem());

		// =========== Load Configuration ===================
		final MenuItem itemLoad = new MenuItem("Load Configuration");
		itemLoad.setId("itemLoad");
		itemLoad.setOnAction(new LoadConfigEventHandler(stage));
		menuConfiguration.getItems().add(itemLoad);

		// ========== Save Configuration ===================
		this.itemSave = new MenuItem("Save Configuration");
		this.itemSave.setId("itemSave");
		this.itemSave.setOnAction(new SaveConfigEventHandler(stage));
		menuConfiguration.getItems().add(this.itemSave);
		menuConfiguration.getItems().add(new SeparatorMenuItem());

		// ========== Exit Application =====================
		final MenuItem itemExit = new MenuItem("Exit");
		itemExit.setId("itemExit");
		itemExit.setOnAction(e -> this.controller.exit());
		menuConfiguration.getItems().add(itemExit);

		// ========== Layout ==============================
		final Menu menuLayout = new Menu("Layout");
		menuLayout.setId("menuLayout");
		final MenuItem layoutGraph = new MenuItem("Refresh Layout");
		layoutGraph.setId("layoutGraph");
		layoutGraph.setOnAction(e -> {
			this.graphBuild.graphBuildController.layoutGraph();
		});
		final MenuItem deleteGraph = new MenuItem("Delete Graph");
		deleteGraph.setId("deleteGraph");
		deleteGraph.setOnAction(e -> {
			this.graphBuild.graphBuildController.deleteGraph();
		});
		menuLayout.getItems().addAll(layoutGraph, deleteGraph);

		// ============ Learning ========================
		final Menu menuLearn = new Menu("Learn");
		menuLearn.setId("menuLearn");

		this.itemBatchLearning = new MenuItem("Batch Learning");
		this.itemBatchLearning.setId("#menuLearnBatchLearning");
		this.itemBatchLearning.setOnAction(e -> {
			this.controller.showBatchLearning();
		});

		this.itemUnsupervisedLearning = new MenuItem("Unsupervised Learning");
		this.itemUnsupervisedLearning.setId("#menuLearnUnsupervisedLearning");
		this.itemUnsupervisedLearning.setOnAction(e -> {
			this.controller.showUnsupervisedLearning();
		});

		this.itemActiveLearning = new MenuItem("Active Learning");
		this.itemActiveLearning.setId("#menuLearnActiveLearning");
		this.itemActiveLearning.setOnAction(e -> {
			this.controller.showActiveLearning();
		});
		menuLearn.getItems().add(this.itemActiveLearning);
		menuLearn.getItems().add(this.itemBatchLearning);
		menuLearn.getItems().add(this.itemUnsupervisedLearning);
		final MenuBar menuBar = new MenuBar(menuConfiguration, menuLayout, menuLearn);
		menuBar.setId("menuBar");
		return menuBar;
	}

	@SuppressWarnings("rawtypes")
	private class LoadConfigEventHandler implements EventHandler {
		private final Window stage;

		public LoadConfigEventHandler(Window stage) {
			this.stage = stage;
		}

		@Override
		public void handle(Event event) {
			final FileChooser fileChooser = new FileChooser();
			final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
					"LIMES Configuration File (*.xml, *.rdf, *.ttl, *.n3, *.nt)", "*.xml", "*.rdf", "*.ttl", "*.n3",
					"*.nt");
			fileChooser.getExtensionFilters().add(extFilter);
			final File file = fileChooser.showOpenDialog(this.stage);
			if (file != null) {
				MainView.this.controller.loadConfig(file);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private class SaveConfigEventHandler implements EventHandler {
		private final Window stage;

		public SaveConfigEventHandler(Window stage) {
			this.stage = stage;
		}

		@Override
		public void handle(Event event) {
			final FileChooser fileChooser = new FileChooser();
			final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
					"LIMES Configuration File (*.rdf, *.ttl, *.n3, *.nt)", "*.rdf", "*.ttl", "*.n3", "*.nt");
			fileChooser.getExtensionFilters().add(extFilter);
			final File file = fileChooser.showSaveDialog(this.stage);
			if (file != null) {
				MainView.this.controller.saveConfig(file);
			}
		}
	}

	/**
	 * Enables menu and run buttons, if config is loaded
	 *
	 * @param isLoaded
	 *            True if Config is Loaded
	 */
	public void showLoadedConfig(boolean isLoaded) {
		this.menuEdit.setDisable(!isLoaded);
		this.itemSave.setDisable(!isLoaded);
		this.toolBarSaveConfigButton.setDisable(!isLoaded);
		this.toolBarRunButton.setDisable(!isLoaded);
		this.itemBatchLearning.setDisable(!isLoaded);
		this.itemUnsupervisedLearning.setDisable(!isLoaded);
		this.itemActiveLearning.setDisable(!isLoaded);
	}

	/**
	 * shows an error with the given header and content message, also displays
	 * the stack trace
	 * 
	 * @param header
	 *            header of message
	 * @param content
	 *            content of message
	 * @param ex
	 *            thrown exception
	 */
	public static void showErrorWithStacktrace(String header, String content, Throwable ex) {
		final Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText(header);
		alert.setContentText(content);

		// Create expandable Exception.
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		ex.printStackTrace();
		final String exceptionText = sw.toString();

		final Label label = new Label("The exception stacktrace was:");

		final TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		final GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);
		alert.showAndWait();
	}

	public BorderPane getRoot() {
		return this.root;
	}

	public Scene getScene() {
		return this.scene;
	}

	public GraphBuildView getGraphBuild() {
		return this.graphBuild;
	}

}

package org.aksw.limes.core.gui.view;

import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;
import static org.aksw.limes.core.gui.util.SourceOrTarget.TARGET;

import java.util.List;
import java.util.function.Predicate;

import org.aksw.limes.core.gui.controller.EditClassMatchingController;
import org.aksw.limes.core.gui.model.AutomatedClassMatchingNode;
import org.aksw.limes.core.gui.model.ClassMatchingNode;
import org.aksw.limes.core.gui.util.SourceOrTarget;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * used for class matching step in {@link WizardView} There are two different
 * modes implemented in this class: automated and manual, depending on the value
 * of {@link #automated}.
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class EditClassMatchingView implements IEditView {

	// ========== GENERAL FIELDS ==============================
	private EditClassMatchingController controller;
	private ScrollPane rootPane;
	private WizardView wizardView;
	/**
	 * Initializing the button at this point is important to be able to
	 * manipulate visibility in WizardController if automation is not possible
	 */
	private Button switchModeButton = new Button();
	/**
	 * mode of matching Through a change listener changing of this value changes
	 * the rootPane
	 */
	private BooleanProperty automated = new SimpleBooleanProperty(true);

	// ========== FIELDS FOR MANUAL MATCHING =================
	private TreeView<ClassMatchingNode> sourceTreeView;
	private TreeView<ClassMatchingNode> targetTreeView;
	private Label errorManualMissingClassMatchingLabel = new Label("One source and one target class must be chosen!");
	private TitledPane sourcePanelWithTitle;
	private TitledPane targetPanelWithTitle;

	// ========== FIELDS FOR AUTOMATED MATCHING ===============
	private TableView<AutomatedClassMatchingNode> tableView;
	private Label errorAutomatedMissingClassMatchingLabel = new Label("One class must be chosen!");

	/**
	 * Constructor creates the root pane
	 * 
	 * @param wizardView
	 *            corresponding view where this is embedded
	 */
	EditClassMatchingView(WizardView wizardView) {
		this.wizardView = wizardView;
		createRootPane();
	}

	/**
	 * sets the corresponding controller
	 * 
	 * @param controller
	 *            controller
	 */
	public void setController(EditClassMatchingController controller) {
		this.controller = controller;
	}

	/**
	 * creates rootPane according to {@link #automated}
	 */
	private Parent createRootPane() {
		
		//============= create root pane according to mode ==================
		if (automated.get()) {
			rootPane = createAutomatedRootPane();
			rootPane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					errorAutomatedMissingClassMatchingLabel.setVisible(false);
				}
			});
		} else {
			rootPane = createManualRootPane();
			rootPane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					errorManualMissingClassMatchingLabel.setVisible(false);
				}
			});
		}

		//=========== functionality of switch mode button =================
		switchModeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
					rootPane = null;
					//this also changes the root pane
					automated.set(!automated.get());
					wizardView.setToRootPane(rootPane);
					controller.load(automated.get());
			}
		});
		
		//========== ensure the correct root pane is always loaded ============
		automated.addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            	if(rootPane == null){
            		System.err.println("Changed: " + automated.get());
            	createRootPane();
            	}
                //If automated is false and manual root pane has not been created yet
                if(!automated.get() && sourceTreeView == null){
                    createRootPane();
                }
            }
        });
		rootPane.setFitToHeight(true);
		rootPane.setFitToWidth(true);
		return rootPane;
	}

	/**
	 * creates the manual root pane
	 * 
	 * @return pane the finished pane
	 */
	private ScrollPane createManualRootPane() {
		// ========= CREATE TREE VIEWS AND TITLE PANELS ================
		HBox hbox = new HBox();
		sourcePanelWithTitle = createClassMatchingPane(SOURCE);
		sourcePanelWithTitle.setId("sourcePanel");
		HBox.setHgrow(sourcePanelWithTitle, Priority.ALWAYS);
		hbox.getChildren().add(sourcePanelWithTitle);
		targetPanelWithTitle = createClassMatchingPane(TARGET);
		targetPanelWithTitle.setId("targetPanel");
		HBox.setHgrow(targetPanelWithTitle, Priority.ALWAYS);
		hbox.getChildren().add(targetPanelWithTitle);
		VBox vbox = new VBox();

		// ========= ADD BUTTON AND ERROR LABEL =======================
		switchModeButton.setText("Automated Matching");
		switchModeButton.setId("switchModeButton");
		errorManualMissingClassMatchingLabel.setTextFill(Color.RED);
		errorManualMissingClassMatchingLabel.setVisible(false);

		// ======== PUT TO PANE =======================================
		vbox.getChildren().addAll(hbox, errorManualMissingClassMatchingLabel, switchModeButton);
		ScrollPane pane = new ScrollPane(vbox);
		pane.setPadding(new Insets(5.0));
		return pane;
	}

	/**
	 * creates the automated root pane
	 * 
	 * @return pane the finished pane
	 */
	private ScrollPane createAutomatedRootPane() {
		// ========= CREATE TABLE VIEW ================================
		tableView = createTableView();
		VBox vbox = new VBox();
		errorAutomatedMissingClassMatchingLabel.setTextFill(Color.RED);
		errorAutomatedMissingClassMatchingLabel.setVisible(false);

		// ========= ADD BUTTON AND ERROR LABEL =======================
		switchModeButton.setText("Manual Matching");
		switchModeButton.setId("switchModeButton");

		// ======== PUT TO PANE =======================================
		vbox.getChildren().addAll(tableView, errorAutomatedMissingClassMatchingLabel, switchModeButton);
		ScrollPane pane = new ScrollPane(vbox);
		pane.setPadding(new Insets(5.0));
		return pane;
	}

	/**
	 * creates table view for automated matching
	 * 
	 * @return tableView finished TableView
	 */
	@SuppressWarnings("unchecked")
	private TableView<AutomatedClassMatchingNode> createTableView() {
		tableView = new TableView<AutomatedClassMatchingNode>();
		tableView.setId("tableView");
		TableColumn<AutomatedClassMatchingNode, String> sourceColumn = new TableColumn<AutomatedClassMatchingNode, String>(
				"Source classes");
		sourceColumn.setId("sourceColumn");
		TableColumn<AutomatedClassMatchingNode, String> targetColumn = new TableColumn<AutomatedClassMatchingNode, String>(
				"Target classes");
		targetColumn.setId("targetColumn");
		sourceColumn.setCellValueFactory(new PropertyValueFactory<AutomatedClassMatchingNode, String>("sourceName"));
		targetColumn.setCellValueFactory(new PropertyValueFactory<AutomatedClassMatchingNode, String>("targetName"));
		tableView.getColumns().addAll(sourceColumn, targetColumn);
		tableView.setEditable(false);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		return tableView;
	}

	/**
	 * returns the pane
	 */
	@Override
	public Parent getPane() {
		return rootPane;
	}

	/**
	 * Create source or target pane depending on the given enum
	 * 
	 * @param sourceOrTarget
	 * @return
	 */
	private TitledPane createClassMatchingPane(SourceOrTarget sourceOrTarget) {
		BorderPane pane = new BorderPane();

		TreeView<ClassMatchingNode> treeView = new TreeView<ClassMatchingNode>();
		treeView.setCellFactory(tv -> new ClassMatchingTreeCell());
		pane.setCenter(treeView);

		if (sourceOrTarget == SOURCE) {
			sourceTreeView = treeView;
			sourceTreeView.setId("sourceTreeView");
			return new TitledPane("Source class", pane);
		} else {
			targetTreeView = treeView;
			targetTreeView.setId("targetTreeView");
			return new TitledPane("Target class", pane);
		}
	}

	/**
	 * adds the children to the class
	 * 
	 * @param parent
	 * @param childNodes
	 * @param shouldSelect
	 */
	private void addTreeChildren(TreeItem<ClassMatchingNode> parent, List<ClassMatchingNode> childNodes,
			Predicate<ClassMatchingNode> shouldSelect) {
		for (ClassMatchingNode childNode : childNodes) {
			TreeItem<ClassMatchingNode> child = new TreeItem<ClassMatchingNode>(childNode);
			parent.getChildren().add(child);
			addTreeChildren(child, childNode.getChildren(), shouldSelect);
		}
	}

	/**
	 * shows the tree after classes are loaded from controller
	 * 
	 * @param sourceOrTarget
	 *            enum for source or target
	 * @param items
	 *            list of class matching nodes
	 * @param currentClass
	 *            current class matching node
	 */
	public void showTree(SourceOrTarget sourceOrTarget, List<ClassMatchingNode> items, ClassMatchingNode currentClass) {
		TreeItem<ClassMatchingNode> root = new TreeItem<ClassMatchingNode>();
		addTreeChildren(root, items, node -> node == currentClass);
		TreeView<ClassMatchingNode> treeView = sourceOrTarget == SOURCE ? sourceTreeView : targetTreeView;
		treeView.setShowRoot(false);
		treeView.setRoot(root);
		if (sourceOrTarget == SourceOrTarget.SOURCE) {
			sourcePanelWithTitle.setText(controller.getConfig().getSourceInfo().getId() + " classes");
		} else {
			targetPanelWithTitle.setText(controller.getConfig().getTargetInfo().getId() + " classes");
		}
	}

	public void showTable(ObservableList<AutomatedClassMatchingNode> items) {
		tableView.setItems(items);
		// Use a fixed cell size to eliminate empty rows by binding the height
		// of the tableView to the number of cells
		double fixedCellSize = 30;
		tableView.setFixedCellSize(fixedCellSize);
		// adding of fixedCellSize/9 is necessary to eliminate the ugly
		// scrollbar if it's unnecessary
		tableView.prefHeightProperty().bind(Bindings.size(tableView.getItems()).multiply(tableView.getFixedCellSize())
				.add(fixedCellSize).add(fixedCellSize / 9));
	}

	/**
	 * saves the selected classes to the config
	 */
	@Override
	public void save() {
		if (automated.get()) {
			controller.save(tableView.getSelectionModel().getSelectedItem().getSourceUri().toString(),
					tableView.getSelectionModel().getSelectedItem().getTargetUri().toString());
		} else {
			TreeItem<ClassMatchingNode> selectedSourceClass = sourceTreeView.getSelectionModel().getSelectedItem();
			TreeItem<ClassMatchingNode> selectedTargetClass = targetTreeView.getSelectionModel().getSelectedItem();
			controller.save(selectedSourceClass == null ? null : selectedSourceClass.getValue(),
					selectedTargetClass == null ? null : selectedTargetClass.getValue());
		}
	}

	public TreeView<ClassMatchingNode> getSourceTreeView() {
		return sourceTreeView;
	}

	public TreeView<ClassMatchingNode> getTargetTreeView() {
		return targetTreeView;
	}

	public TableView<AutomatedClassMatchingNode> getTableView() {
		return tableView;
	}

	public Label getErrorAutomatedMissingClassMatchingLabel() {
		return errorAutomatedMissingClassMatchingLabel;
	}

	public Label getErrorManualMissingClassMatchingLabel() {
		return errorManualMissingClassMatchingLabel;
	}

	public Button getSwitchModeButton() {
		return switchModeButton;
	}

	@Override
	public void setAutomated(boolean automated) {
	//	rootPane = null;
		this.automated.set(automated);
		this.switchModeButton.setVisible(automated);
	}

	@Override
	public Boolean isAutomated() {
		return automated.get();
	}

}

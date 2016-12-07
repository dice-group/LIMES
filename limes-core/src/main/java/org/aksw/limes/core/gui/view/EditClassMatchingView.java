package org.aksw.limes.core.gui.view;

import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;
import static org.aksw.limes.core.gui.util.SourceOrTarget.TARGET;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Predicate;

import org.aksw.limes.core.gui.controller.EditClassMatchingController;
import org.aksw.limes.core.gui.model.AutomatedClassMatchingNode;
import org.aksw.limes.core.gui.model.ClassMatchingNode;
import org.aksw.limes.core.gui.util.SourceOrTarget;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
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
 * used for class matching step in {@link WizardView}
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class EditClassMatchingView implements IEditView {
	private EditClassMatchingController controller;
	private ScrollPane rootPane;
	private TreeView<ClassMatchingNode> sourceTreeView;
	private TreeView<ClassMatchingNode> targetTreeView;
	private TableView<AutomatedClassMatchingNode> tableView;
	private Label errorAutomatedMissingClassMatchingLabel = new Label("One class must be chosen!");
	private Label errorManualMissingClassMatchingLabel = new Label("One source and one target class must be chosen!");
	private TitledPane sourcePanelWithTitle;
	private TitledPane targetPanelWithTitle;

	public Boolean automated = true;

	/**
	 * Constructor creates the root pane
	 */
	EditClassMatchingView() {
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
	 * helper class to create rootPane
	 */
	private void createRootPane() {
		if (automated) {
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
		rootPane.setFitToHeight(true);
		rootPane.setFitToWidth(true);
	}

	private ScrollPane createManualRootPane() {
		HBox hbox = new HBox();
		sourcePanelWithTitle = createClassMatchingPane(SOURCE);
		HBox.setHgrow(sourcePanelWithTitle, Priority.ALWAYS);
		hbox.getChildren().add(sourcePanelWithTitle);
		targetPanelWithTitle = createClassMatchingPane(TARGET);
		HBox.setHgrow(targetPanelWithTitle, Priority.ALWAYS);
		hbox.getChildren().add(targetPanelWithTitle);
		VBox vbox = new VBox();
		errorManualMissingClassMatchingLabel.setTextFill(Color.RED);
		errorManualMissingClassMatchingLabel.setVisible(false);
		vbox.getChildren().addAll(hbox, errorManualMissingClassMatchingLabel);
		return new ScrollPane(vbox);
	}

	private ScrollPane createAutomatedRootPane() {
		tableView = createTableView();
		VBox vbox = new VBox();
		errorAutomatedMissingClassMatchingLabel.setTextFill(Color.RED);
		errorAutomatedMissingClassMatchingLabel.setVisible(false);
		vbox.getChildren().addAll(tableView, errorAutomatedMissingClassMatchingLabel);
		ScrollPane pane = new ScrollPane(vbox);
		pane.setPadding(new Insets(5.0));
		return pane;
	}

	private TableView<AutomatedClassMatchingNode> createTableView() {
		tableView = new TableView<AutomatedClassMatchingNode>();
		TableColumn<AutomatedClassMatchingNode, String> sourceColumn = new TableColumn<AutomatedClassMatchingNode, String>(
				"Source classes");
		TableColumn<AutomatedClassMatchingNode, String> targetColumn = new TableColumn<AutomatedClassMatchingNode, String>(
				"Target classes");
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
			return new TitledPane("Source class", pane);
		} else {
			targetTreeView = treeView;
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

	public void showTable(ObservableList<AutomatedClassMatchingNode> items){
		tableView.setItems(items);
		//Use a fixed cell size to eliminate empty rows by binding the height of the tableView to the number of cells
		double fixedCellSize = 30;
		tableView.setFixedCellSize(fixedCellSize);
		//adding of fixedCellSize/9 is necessary to eliminate the ugly scrollbar if it's unnecessary
		tableView.prefHeightProperty().bind(Bindings.size(tableView.getItems()).multiply(tableView.getFixedCellSize()).add(fixedCellSize).add(fixedCellSize/9));
	}

	/**
	 * saves the selected classes to the config
	 */
	@Override
	public void save() {
		if (automated) {
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

}

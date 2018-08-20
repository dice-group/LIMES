package org.aksw.limes.core.gui.view;

import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;
import static org.aksw.limes.core.gui.util.SourceOrTarget.TARGET;

import java.util.Collections;
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
import javafx.collections.ObservableList;
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
	private final WizardView wizardView;
	/**
	 * Initializing the button at this point is important to be able to
	 * manipulate visibility in WizardController if automation is not possible
	 */
	private final Button switchModeButton = new Button();
	/**
	 * mode of matching Through a change listener changing of this value changes
	 * the rootPane
	 */
	private final BooleanProperty automated = new SimpleBooleanProperty(true);

	// ========== FIELDS FOR MANUAL MATCHING =================
	private TreeView<ClassMatchingNode> sourceTreeView;
	private TreeView<ClassMatchingNode> targetTreeView;
	private final Label errorManualMissingClassMatchingLabel = new Label(
			"One source and one target class must be chosen!");
	private TitledPane sourcePanelWithTitle;
	private TitledPane targetPanelWithTitle;

	// ========== FIELDS FOR AUTOMATED MATCHING ===============
	private TableView<AutomatedClassMatchingNode> tableView;
	private final Label errorAutomatedMissingClassMatchingLabel = new Label("One class must be chosen!");

	/**
	 * Constructor creates the root pane
	 *
	 * @param wizardView
	 *            corresponding view where this is embedded
	 */
	EditClassMatchingView(WizardView wizardView) {
		this.wizardView = wizardView;
		this.createRootPane();
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

		// ============= create root pane according to mode ==================
		if (this.automated.get()) {
			this.rootPane = this.createAutomatedRootPane();
			this.rootPane.addEventFilter(MouseEvent.MOUSE_PRESSED,
					mouseEvent -> EditClassMatchingView.this.errorAutomatedMissingClassMatchingLabel.setVisible(false));
		} else {
			this.rootPane = this.createManualRootPane();
			this.rootPane.addEventFilter(MouseEvent.MOUSE_PRESSED,
					mouseEvent -> EditClassMatchingView.this.errorManualMissingClassMatchingLabel.setVisible(false));
		}

		// =========== functionality of switch mode button =================
		this.switchModeButton.setOnAction(e -> {
			EditClassMatchingView.this.rootPane = null;
			// this also changes the root pane
			EditClassMatchingView.this.automated.set(!EditClassMatchingView.this.automated.get());
			EditClassMatchingView.this.wizardView.setToRootPane(EditClassMatchingView.this.rootPane);
			EditClassMatchingView.this.controller.load(EditClassMatchingView.this.automated.get());
		});

		// ========== ensure the correct root pane is always loaded ============
		this.automated.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (EditClassMatchingView.this.rootPane == null) {
				EditClassMatchingView.this.createRootPane();
			}
			// If automated is false and manual root pane has not been created
			// yet
			if (!EditClassMatchingView.this.automated.get() && EditClassMatchingView.this.sourceTreeView == null) {
				EditClassMatchingView.this.createRootPane();
			}
		});
		this.rootPane.setFitToHeight(true);
		this.rootPane.setFitToWidth(true);
		return this.rootPane;
	}

	/**
	 * creates the manual root pane
	 *
	 * @return pane the finished pane
	 */
	private ScrollPane createManualRootPane() {
		// ========= CREATE TREE VIEWS AND TITLE PANELS ================
		final HBox hbox = new HBox();
		this.sourcePanelWithTitle = this.createClassMatchingPane(SOURCE);
		this.sourcePanelWithTitle.setId("sourcePanel");
		HBox.setHgrow(this.sourcePanelWithTitle, Priority.ALWAYS);
		hbox.getChildren().add(this.sourcePanelWithTitle);
		this.targetPanelWithTitle = this.createClassMatchingPane(TARGET);
		this.targetPanelWithTitle.setId("targetPanel");
		HBox.setHgrow(this.targetPanelWithTitle, Priority.ALWAYS);
		hbox.getChildren().add(this.targetPanelWithTitle);
		final VBox vbox = new VBox();

		// ========= ADD BUTTON AND ERROR LABEL =======================
		this.switchModeButton.setText("Automated Matching");
		this.switchModeButton.setId("switchModeButton");
		this.errorManualMissingClassMatchingLabel.setTextFill(Color.RED);
		this.errorManualMissingClassMatchingLabel.setVisible(false);

		// ======== PUT TO PANE =======================================
		vbox.getChildren().addAll(hbox, this.errorManualMissingClassMatchingLabel, this.switchModeButton);
		final ScrollPane pane = new ScrollPane(vbox);
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
		this.tableView = this.createTableView();
		final VBox vbox = new VBox();
		this.errorAutomatedMissingClassMatchingLabel.setTextFill(Color.RED);
		this.errorAutomatedMissingClassMatchingLabel.setVisible(false);

		// ========= ADD BUTTON AND ERROR LABEL =======================
		this.switchModeButton.setText("Manual Matching");
		this.switchModeButton.setId("switchModeButton");

		// ======== PUT TO PANE =======================================
		vbox.getChildren().addAll(this.tableView, this.errorAutomatedMissingClassMatchingLabel, this.switchModeButton);
		final ScrollPane pane = new ScrollPane(vbox);
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
		this.tableView = new TableView<>();
		this.tableView.setId("tableView");
		final TableColumn<AutomatedClassMatchingNode, String> sourceColumn = new TableColumn<>(
				"Source classes");
		sourceColumn.setId("sourceColumn");
		final TableColumn<AutomatedClassMatchingNode, String> targetColumn = new TableColumn<>(
				"Target classes");
		targetColumn.setId("targetColumn");
		sourceColumn.setCellValueFactory(new PropertyValueFactory<AutomatedClassMatchingNode, String>("sourceName"));
		targetColumn.setCellValueFactory(new PropertyValueFactory<AutomatedClassMatchingNode, String>("targetName"));
		this.tableView.getColumns().addAll(sourceColumn, targetColumn);
		this.tableView.setEditable(false);
		this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		return this.tableView;
	}

	/**
	 * returns the pane
	 */
	@Override
	public Parent getPane() {
		return this.rootPane;
	}

	/**
	 * Create source or target pane depending on the given enum
	 *
	 * @param sourceOrTarget
	 * @return
	 */
	private TitledPane createClassMatchingPane(SourceOrTarget sourceOrTarget) {
		final BorderPane pane = new BorderPane();

		final TreeView<ClassMatchingNode> treeView = new TreeView<>();
		treeView.setCellFactory(tv -> new ClassMatchingTreeCell());
		pane.setCenter(treeView);

		if (sourceOrTarget == SOURCE) {
			this.sourceTreeView = treeView;
			this.sourceTreeView.setId("sourceTreeView");
			return new TitledPane("Source class", pane);
		} else {
			this.targetTreeView = treeView;
			this.targetTreeView.setId("targetTreeView");
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
		Collections.sort(childNodes, ClassMatchingNode.CLASS_MATCHING_NODE_COMPARATOR);
		for (final ClassMatchingNode childNode : childNodes) {
			final TreeItem<ClassMatchingNode> child = new TreeItem<>(childNode);
			parent.getChildren().add(child);
			this.addTreeChildren(child, childNode.getChildren(), shouldSelect);
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
		final TreeItem<ClassMatchingNode> root = new TreeItem<>();
		this.addTreeChildren(root, items, node -> node == currentClass);
		final TreeView<ClassMatchingNode> treeView = sourceOrTarget == SOURCE ? this.sourceTreeView
				: this.targetTreeView;
		treeView.setShowRoot(false);
		treeView.setRoot(root);
		if (sourceOrTarget == SourceOrTarget.SOURCE) {
			this.sourcePanelWithTitle.setText(this.controller.getConfig().getSourceInfo().getId() + " classes");
		} else {
			this.targetPanelWithTitle.setText(this.controller.getConfig().getTargetInfo().getId() + " classes");
		}
	}

	public void showTable(ObservableList<AutomatedClassMatchingNode> items) {
		Collections.sort(items, AutomatedClassMatchingNode.AUTOMATED_CLASS_MATCHING_NODE_COMPARATOR);
		this.tableView.setItems(items);
		// Use a fixed cell size to eliminate empty rows by binding the height
		// of the tableView to the number of cells
		final double fixedCellSize = 30;
		this.tableView.setFixedCellSize(fixedCellSize);
		// adding of fixedCellSize/9 is necessary to eliminate the ugly
		// scrollbar if it's unnecessary
		this.tableView.prefHeightProperty().bind(Bindings.size(this.tableView.getItems())
				.multiply(this.tableView.getFixedCellSize()).add(fixedCellSize).add(fixedCellSize / 9));
	}

	/**
	 * saves the selected classes to the config
	 */
	@Override
	public void save() {
		if (this.automated.get()) {
			this.controller.save(this.tableView.getSelectionModel().getSelectedItem().getSourceUri().toString(),
					this.tableView.getSelectionModel().getSelectedItem().getTargetUri().toString());
		} else {
			final TreeItem<ClassMatchingNode> selectedSourceClass = this.sourceTreeView.getSelectionModel()
					.getSelectedItem();
			final TreeItem<ClassMatchingNode> selectedTargetClass = this.targetTreeView.getSelectionModel()
					.getSelectedItem();
			this.controller.save(selectedSourceClass == null ? null : selectedSourceClass.getValue(),
					selectedTargetClass == null ? null : selectedTargetClass.getValue());
		}
	}

	public TreeView<ClassMatchingNode> getSourceTreeView() {
		return this.sourceTreeView;
	}

	public TreeView<ClassMatchingNode> getTargetTreeView() {
		return this.targetTreeView;
	}

	public TableView<AutomatedClassMatchingNode> getTableView() {
		return this.tableView;
	}

	public Label getErrorAutomatedMissingClassMatchingLabel() {
		return this.errorAutomatedMissingClassMatchingLabel;
	}

	public Label getErrorManualMissingClassMatchingLabel() {
		return this.errorManualMissingClassMatchingLabel;
	}

	public Button getSwitchModeButton() {
		return this.switchModeButton;
	}

	@Override
	public void setAutomated(boolean automated) {
		// rootPane = null;
		this.automated.set(automated);
		this.switchModeButton.setVisible(automated);
	}

	@Override
	public Boolean isAutomated() {
		return this.automated.get();
	}

}

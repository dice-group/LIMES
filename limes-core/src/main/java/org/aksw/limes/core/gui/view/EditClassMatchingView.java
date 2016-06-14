package org.aksw.limes.core.gui.view;

import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;
import static org.aksw.limes.core.gui.util.SourceOrTarget.TARGET;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.function.Predicate;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import org.aksw.limes.core.gui.controller.EditClassMatchingController;
import org.aksw.limes.core.gui.model.ClassMatchingNode;
import org.aksw.limes.core.gui.util.SourceOrTarget;

/**
 * View class for class matching step in create wizard
 *
 * @author Manuel Jacob
 */
public class EditClassMatchingView implements IEditView {
    private EditClassMatchingController controller;
    private ScrollPane rootPane;
    private TreeView<ClassMatchingNode> sourceTreeView;
    private TreeView<ClassMatchingNode> targetTreeView;

    EditClassMatchingView() {
	createRootPane();
    }

    public void setController(EditClassMatchingController controller) {
	this.controller = controller;
    }

    private void createRootPane() {
	HBox hbox = new HBox();
	Node sourcePanelWithTitle = createClassMatchingPane(SOURCE);
	HBox.setHgrow(sourcePanelWithTitle, Priority.ALWAYS);
	hbox.getChildren().add(sourcePanelWithTitle);
	Node targetPaneWithTitle = createClassMatchingPane(TARGET);
	HBox.setHgrow(targetPaneWithTitle, Priority.ALWAYS);
	hbox.getChildren().add(targetPaneWithTitle);

	rootPane = new ScrollPane(hbox);
	rootPane.setFitToHeight(true);
	rootPane.setFitToWidth(true);
    }

    @Override
    public Parent getPane() {
	return rootPane;
    }

    private Node createClassMatchingPane(SourceOrTarget sourceOrTarget) {
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

    private void addTreeChildren(TreeItem<ClassMatchingNode> parent, List<ClassMatchingNode> childNodes, Predicate<ClassMatchingNode> shouldSelect) {
	for (ClassMatchingNode childNode : childNodes) {
	    TreeItem<ClassMatchingNode> child = new TreeItem<ClassMatchingNode>(childNode);
	    parent.getChildren().add(child);
	    addTreeChildren(child, childNode.getChildren(), shouldSelect);
	}
    }

    public void showTree(SourceOrTarget sourceOrTarget, List<ClassMatchingNode> items, ClassMatchingNode currentClass) {
	TreeItem<ClassMatchingNode> root = new TreeItem<ClassMatchingNode>();
	addTreeChildren(root, items, node -> node == currentClass);
	TreeView<ClassMatchingNode> treeView = sourceOrTarget == SOURCE ? sourceTreeView : targetTreeView;
	treeView.setShowRoot(false);
	treeView.setRoot(root);
    }

    @Override
    public void save() {
	TreeItem<ClassMatchingNode> selectedSourceClass = sourceTreeView.getSelectionModel().getSelectedItem();
	TreeItem<ClassMatchingNode> selectedTargetClass = targetTreeView.getSelectionModel().getSelectedItem();
	controller.save(selectedSourceClass == null ? null : selectedSourceClass.getValue(),
		selectedTargetClass == null ? null : selectedTargetClass.getValue());
    }
}

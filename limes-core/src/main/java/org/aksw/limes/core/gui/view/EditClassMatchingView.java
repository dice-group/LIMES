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

    /**
     * Constructor creates the root pane
     */
    EditClassMatchingView() {
	createRootPane();
    }

    /**
     * sets the corresponding controller
     * @param controller controller
     */
    public void setController(EditClassMatchingController controller) {
	this.controller = controller;
    }

    /**
     * helper class to create rootPane
     */
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

    /**
     * returns the pane
     */
    @Override
    public Parent getPane() {
	return rootPane;
    }

    /**
     * Create source or target pane depending on the given enum
     * @param sourceOrTarget
     * @return
     */
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

    /**
     * adds the children to the class
     * @param parent
     * @param childNodes
     * @param shouldSelect
     */
    private void addTreeChildren(TreeItem<ClassMatchingNode> parent, List<ClassMatchingNode> childNodes, Predicate<ClassMatchingNode> shouldSelect) {
	for (ClassMatchingNode childNode : childNodes) {
	    TreeItem<ClassMatchingNode> child = new TreeItem<ClassMatchingNode>(childNode);
	    parent.getChildren().add(child);
	    addTreeChildren(child, childNode.getChildren(), shouldSelect);
	}
    }

    /**
     * shows the tree after classes are loaded from controller
     * @param sourceOrTarget enum for source or target
     * @param items list of class matching nodes
     * @param currentClass current class matching node
     */
    public void showTree(SourceOrTarget sourceOrTarget, List<ClassMatchingNode> items, ClassMatchingNode currentClass) {
	TreeItem<ClassMatchingNode> root = new TreeItem<ClassMatchingNode>();
	addTreeChildren(root, items, node -> node == currentClass);
	TreeView<ClassMatchingNode> treeView = sourceOrTarget == SOURCE ? sourceTreeView : targetTreeView;
	treeView.setShowRoot(false);
	treeView.setRoot(root);
    }

    /**
     * saves the selected classes to the config
     */
    @Override
    public void save() {
	TreeItem<ClassMatchingNode> selectedSourceClass = sourceTreeView.getSelectionModel().getSelectedItem();
	TreeItem<ClassMatchingNode> selectedTargetClass = targetTreeView.getSelectionModel().getSelectedItem();
	controller.save(selectedSourceClass == null ? null : selectedSourceClass.getValue(),
		selectedTargetClass == null ? null : selectedTargetClass.getValue());
    }
}

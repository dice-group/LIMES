package org.aksw.limes.core.gui.view.graphBuilder;


import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 * Used when a {@link org.aksw.limes.core.gui.view.graphBuilder.NodeView} is right-clicked to show a small menu to give the possibility
 * to link to another node or delete it
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class NodeContextMenu extends ContextMenu {
    /**
     * Corresponding View
     */
    private GraphBuildView graphBuildView;

    /**
     * Close MenuItem
     */
    private MenuItem close;

    /**
     * MenuItem to start Linking
     */
    private MenuItem linkTo;

    /**
     * MenutItem to delete Node
     */
    private MenuItem delete;

    /**
     * Clicked NodeView
     */
    private NodeView node;

    /**
     * Constructor
     *
     * @param view
     *         Corresponding view
     * @param clickedNode
     *         clicked Node
     */
    public NodeContextMenu(GraphBuildView view, NodeView clickedNode) {
        this.graphBuildView = view;
        this.linkTo = new MenuItem("Link To");
        this.delete = new MenuItem("Delete");
        this.close = new MenuItem("Close");
        addListeners();
        this.getItems().addAll(linkTo, delete, close);
        this.node = clickedNode;
        this.graphBuildView.contextMenuIsShown = true;
    }

    /**
     * Add Listeners to the MenuItems
     */
    private void addListeners() {

        this.delete.setOnAction(e -> {
            graphBuildView.removeNodeView(node);
            this.graphBuildView.contextMenuIsShown = false;
        });
        this.linkTo.setOnAction(e -> {
            graphBuildView.isLinking = true;
            graphBuildView.linkNode = this.node;
            this.graphBuildView.contextMenuIsShown = false;
        });

        this.close.setOnAction(e -> {
            this.graphBuildView.contextMenuIsShown = false;
        });
    }

}

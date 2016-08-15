package org.aksw.limes.core.gui.view;

import org.aksw.limes.core.gui.model.ClassMatchingNode;

import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;

/**
 * helper class used in {@link org.aksw.limes.core.gui.view.EditClassMatchingView} to display classes and subclasses as tree
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class ClassMatchingTreeCell extends TreeCell<ClassMatchingNode> {
    @Override
    protected void updateItem(ClassMatchingNode item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setTooltip(null);
        } else {
            setText(item.getName());
            setTooltip(new Tooltip(item.getUri().toString()));
        }
    }
    
}

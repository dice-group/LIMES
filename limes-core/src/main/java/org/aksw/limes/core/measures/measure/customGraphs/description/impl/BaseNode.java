package org.aksw.limes.core.measures.measure.customGraphs.description.impl;

import org.aksw.limes.core.measures.measure.customGraphs.description.INode;

import java.util.Objects;

/**
 * Class to handle operations on base node
 *
 * @author Cedric Richter
 */
public class BaseNode implements INode {

    private String label;
    private NodeType type;

    public BaseNode(String label, NodeType type) {
        this.label = label;
        this.type = type;
    }

    /**
     * @return label of base node
     */
    @Override
    public String getLabel() {
        return label;
    }

    /**
     * @return type of base node
     */
    @Override
    public NodeType getType() {
        return type;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseNode baseNode = (BaseNode) o;
        return Objects.equals(label, baseNode.label) &&
                type == baseNode.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(label, type);
    }

    /**
     * @return Base Node's label and type
     */
    @Override
    public String toString() {
        return "BaseNode{" +
                "label='" + label + '\'' +
                ", type=" + type +
                '}';
    }
}

package org.aksw.limes.core.measures.measure.customGraphs.description.impl;

import org.aksw.limes.core.measures.measure.customGraphs.description.IEdge;
import org.aksw.limes.core.measures.measure.customGraphs.description.INode;

import java.util.Objects;

/**
 * @author Cedric Richter
 */
public class BaseEdgeContainer implements IEdge {


    private INode source;
    private String type;
    private INode target;

    BaseEdgeContainer(INode source, String type, INode target) {
        this.source = source;
        this.type = type;
        this.target = target;
    }

    @Override
    public INode getSource() {
        return source;
    }

    @Override
    public String getEdgeType() {
        return type;
    }

    @Override
    public INode getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEdgeContainer that = (BaseEdgeContainer) o;
        return Objects.equals(source, that.source) &&
                Objects.equals(type, that.type) &&
                Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {

        return Objects.hash(source, type, target);
    }

    @Override
    public String toString() {
        return "BaseEdgeContainer{" +
                "source=" + source.getLabel() +
                ", type='" + type + '\'' +
                ", target=" + target.getLabel() +
                '}';
    }
}

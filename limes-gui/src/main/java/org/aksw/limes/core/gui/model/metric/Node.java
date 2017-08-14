package org.aksw.limes.core.gui.model.metric;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * class to represent graphical nodes
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public abstract class Node {
    /**
     * Color of node
     */
    protected static int colors = 0;
    /**
     * Node Id
     */
    public final String id;
    /**
     * Acceptance Threshold
     */
    public Double param1 = null;
    /**
     * Verification Threshold
     */
    public Double param2 = null;
    /**
     * other Color of the node
     */
    protected int color;
    /**
     * Parent of the node
     */
    protected Node parent = null;
    /**
     * Contains the list of children
     */
    protected List<Node> childs = new Vector<Node>();

    /**
     * Constructor
     *
     * @param id
     *         Identifier of the Node
     */
    public Node(String id) {
        this.id = id;
        color = colors++;
    }

    /**
     * Create new Node
     *
     * @param id
     *         Identifier of new Node
     * @return New Node
     */
    public static Node createNode(String id) {
        id = id.toLowerCase();
        if (new Measure("").identifiers().contains(id))
            return new Measure(id);
        if (Operator.identifiers.contains(id))
            return new Operator(id);
        throw new MetricFormatException("There is no node with id \"" + id
                + "\", creation is not possible.");
    }

    /**
     * Returns the valid identifiers of the Node
     *
     * @return valid identifiers
     */
    public abstract Set<String> identifiers();

    /**
     * Returns the maxmimum number of children of the node
     *
     * @return number of children
     */
    public byte getMaxChilds() {
        return 0;
    }

    /**
     * Valid accepted Child classes of the node
     *
     * @return valid accepted classes
     */
    public abstract Set<Class<? extends Node>> validChildClasses();

    /**
     * Returns if node can be a parent of this specific node
     *
     * @param node
     *         To be tested node
     * @return true if valid
     */
    public final boolean isValidParentOf(Node node) {
        return validChildClasses().contains(node.getClass());
    }

    /**
     * Returns the List of children
     *
     * @return List of Children
     */
    public List<Node> getChilds() {
        return Collections.unmodifiableList(childs);
    }

    /**
     * Adds a Child to the Node
     *
     * @param child
     *         Child to be added
     * @return True if successful
     */
    public boolean addChild(Node child) {
        if (!acceptsChild(child)) {
            return false;
        }
        childs.add(child);
        child.parent = this;
        child.pushDownColor(this.color);
        return true;
    }

    /**
     * Removes child from node
     *
     * @param child
     *         to be removed
     */
    public void removeChild(Node child) {
        childs.remove(child);
        child.parent = null;
        child.pushDownColor(colors++);
    }

    /**
     * Returns true if Child will be accepted
     *
     * @param n
     *         node to test
     * @return true if Child is accepted
     */
    public boolean acceptsChild(Node n) {
        return isValidParentOf(n) && n.parent == null
                && childs.size() < getMaxChilds() && this.color != n.color;
    }

    /**
     * Gives the Reason why Child is not accepted
     *
     * @param n
     *         node to be tested
     * @return Reason why it is not accepted
     */
    public Acceptance acceptsChildWithReason(Node n) {
        if (!isValidParentOf(n)) {
            return Acceptance.INVALID_PARENT;
        }
        if (n.parent != null) {
            return Acceptance.ALREADY_HAS_A_PARENT;
        }
        if (childs.size() >= getMaxChilds()) {
            return Acceptance.CANNOT_ACCEPT_ANYMORE_CHILDREN;
        }
        return Acceptance.OK;
    }

    /**
     * Tests if Node has all needed Children
     *
     * @return True if node has maximum childs
     */
    public boolean isComplete() {
        if (childs.size() < getMaxChilds()) {
            return false;
        }
        for (Node child : childs) {
            if (!child.isComplete())
                return false;
        }
        return true;
    }

    /**
     * Changes Color of Node and children
     *
     * @param color
     */
    protected void pushDownColor(int color) {
        this.color = color;
        for (Node child : childs) {
            child.pushDownColor(color);
        }
    }

    /**
     * Builds MetricExpressionString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(id);
        synchronized (this) {
            if (getChilds().isEmpty()) {
                return sb.toString();
            }
            int i = 0;
            for (Node child : getChilds()) {
                if (i == 0) {
                    sb.append('(');
                } else {
                    sb.append(',');
                }
                if (this.hasFrontParameters()) {
                    Double p;
                    if ((p = (i == 0 ? param1 : param2)) != null) {
                        sb.append(p);
                        sb.append('*');
                    }
                }
                sb.append(child.toString());

                if (!this.hasFrontParameters()) {
                    Double p;
                    if ((p = (i == 0 ? param1 : param2)) != null) {
                        sb.append('|');
                        sb.append(p);
                    }
                }
                i++;
            }
        }
        sb.append(')');
        return sb.toString();
    }

    /**
     * Returns true if node has FrontParameters
     *
     * @return true if equals add = true
     */
    public boolean hasFrontParameters() {
        return "add".equalsIgnoreCase(id);
    }

    /**
     * Compares Object and this Node
     *
     * @return true if they are the same
     */
    public boolean equals(Object o) {
        return equals(o, false);
    }

    /**
     * Test if Object and Node are the same
     *
     * @param o
     *         Tested Object
     * @param strict
     *         If True test String and Parents
     * @return Return True if they are equal
     */
    @SuppressWarnings("unused")
    public boolean equals(Object o, boolean strict) {
        if (o == null || !(this.getClass() == o.getClass()))
            return false;
        Node n2 = (Node) o;
        boolean stringTest = false, parentTest = true, childTest = true, completeTest;
        stringTest = this.toString().equals(o.toString());
        if ((this.parent == null && n2.parent != null)
                || (n2.parent == null && this.parent != null)) {
        } else if (this.parent != null && n2.parent != null)
            if (this.parent == n2.parent) { // same parent

            } else {
                parentTest = false;
            }
        if (this.getMaxChilds() == n2.getMaxChilds() && this.getMaxChilds() > 0) {
            List<Node> childrens1 = this.getChilds();
            List<Node> childrens2 = n2.getChilds();
            for (int i = 0; i < childrens1.size(); i++) {
                for (int j = 0; j < childrens2.size(); j++)
                    if (!childrens1.get(i).equals(childrens2.get(j)))
                        childTest = false;
            }
        } else {
            childTest = false;
        }
        completeTest = this.isComplete() == n2.isComplete();
        if (strict)
            return stringTest && parentTest;
        else
            return stringTest;
    }

    /**
     * Set the Parent of this Node to Null
     */
    public void removeParent() {
        this.parent = null;
    }

    /**
     * set a New Parent to this Node
     *
     * @param parent
     *         New Parent
     */
    public void overwriteParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Enum of Acceptance values
     */
    public enum Acceptance {
        OK, INVALID_PARENT, ALREADY_HAS_A_PARENT, CANNOT_ACCEPT_ANYMORE_CHILDREN, SOURCE_PROPERTY_EXPECTED, TARGET_PROPERTY_EXPECTED
    }
}

/**
 *
 */
package org.aksw.limes.core.datastrutures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @param <T> tree node type
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 12, 2016
 */
public class Tree<T> {
    private List<Tree<T>> children = null;//new ArrayList<Tree<T>>();
    private Tree<T> parent;
    private T value;


    public Tree(Tree<T> parent, T value, List<Tree<T>> childrenlist) {
        this.parent = parent;
        this.value = value;
        if (childrenlist != null) {
            for (Tree<T> child : childrenlist) {
                children.add(new Tree<T>(this, child.value, child.children));
            }
        }
    }

    /**
     * @param value of the new node
     */
    public Tree(T value) {
        this.parent = null;
        this.value = value;
        children = null;
    }

    /**
     * create an empty tree
     */
    public Tree() {
        children = null;
        parent = null;
        value = null;
    }



    /**
     * @return set of the tree nodes
     */
    public Set<Tree<T>> getLeaves() {
        Set<Tree<T>> leaves = new HashSet<Tree<T>>();
        for (Tree<T> child : this.children) {
            if (child.children == null) {
                leaves.add(child);
            } else {
                Set<Tree<T>> childrenLeaves = child.getLeaves();
                for (Tree<T> l : childrenLeaves) {
                    leaves.add(l);
                }
            }
        }
        return leaves;
    }

    
    /**
     * @param child to be added
     */
    public void addChild(Tree<T> child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
        child.parent = this;
    }

    /**
     * @param child to be removed
     */
    public void removeChild(Tree<T> child) {
        children.remove(child);
    }

    /**
     * remove current node and all its children
     */
    public void remove() {
        if (children != null && children.size() > 0) {
            for (Tree<T> c : children) {
                c.remove();
            }
        }
        this.parent.children.remove(this);
    }

    /**
     * @return parent of the current node
     */
    public Tree<T> getParent() {
        return parent;
    }

    /**
     * @return list of children of current node
     */
    public List<Tree<T>> getchildren() {
        return children;
    }

    /**
     * @return value contained in current node
     */
    public T getValue() {
        return value;
    }

    /**
     * print tree
     */
    public void print() {
        print("", true);
    }

    private void print(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + ((this.parent == null) ? "ROOT(⟂)" : this.value));
        if (children != null) {
            for (int i = 0; i < children.size() - 1; i++) {
                children.get(i).print(prefix + (isTail ? "    " : "│   "), false);
            }
            if (children.size() > 0) {
                children.get(children.size() - 1).print(prefix + (isTail ? "    " : "│   "), true);
            }
        }
    }

    /**
     * @return tree size (number of nodes)
     */
    public long size() {
        long size = 0;
        if (children == null || children.size() == 0) {
            return 1;
        }
        for (Tree<T> child : children) {
            size += child.size();
        }
        return 1 + size;
    }

    /**
     * @return tree depth (number of levels)
     */
    public long depth() {
        if (children == null || children.size() == 0) {
            return 1;
        }
        long maxDepth = 0;
        for (Tree<T> child : children) {
            long d = child.depth();
            if (maxDepth < d) {
                maxDepth = d;
            }
        }
        return maxDepth + 1;
    }

    /**
     * @return current node's level
     */
    public long level() {
        long level = 0;
        Tree<T> t = this;
        while (t.parent != null) {
            level++;
            t = t.parent;
        }
        return level;
    }


}

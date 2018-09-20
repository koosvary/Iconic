package org.iconic.ea.chromosome.graph;
import java.util.LinkedList;
import java.util.List;

public abstract class Node<T> implements Cloneable {
    private List<Node<T>> children;

    public Node() {
        this.children = new LinkedList<>();
    }

    public void addChild(Node<T> n) {
        children.add(n);
    }

    public void addChildren(List<Node<T>> children) {
        this.children.addAll(children);
    }

    public void removeAllChildren() { children = new LinkedList<>(); }

    protected List<Node<T>> getChildren() {
        return children;
    }

    public abstract T apply(List<T> sampleRowValues);

    public abstract int getNumberOfChildren();

    public abstract Node<T> clone();

    /**
     * <p>
     * Recursively go through all children to determine the size of the Solution.
     * </p>
     * @return the size of the solution.
     */
    public int getSize() {
        if (children.size() == 0) { return 1; }

        int sum = 1;
        for (Node<T> child : children) {
            sum += child.getSize();
        }

        return sum;
    }
}

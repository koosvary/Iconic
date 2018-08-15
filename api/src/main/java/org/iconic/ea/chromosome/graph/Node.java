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
}

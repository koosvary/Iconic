package org.iconic.ea.chromosome;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.LinkedList;
import java.util.List;

public class Node<T> {
    private enum NodeType { FUNCTION, FEATURE, CONSTANT }
    private NodeType nodeType; // Used to determine what this node is holding
    private List<Node<T>> children;
    private FunctionalPrimitive<T, T> function; // Function such as + - * /
    private int featureIndex; // The feature index refers to the feature to use from the data, e.g. sex, age, height
    private T constant; // Just a random constant that is passed in

    public Node(FunctionalPrimitive<T, T> function) {
        this.nodeType = NodeType.FUNCTION;
        this.function = function;
        this.children = new LinkedList<>();
    }

    public Node(int featureIndex) {
        this.nodeType = NodeType.FEATURE;
        this.featureIndex = featureIndex;
    }

    public Node(T constant) {
        this.nodeType = NodeType.CONSTANT;
        this.constant = constant;
    }

    public void addChild(Node<T> n) {
        children.add(n);
    }

    public void addChildren(List<Node<T>> children) {
        this.children.addAll(children);
    }

    public void removeAllChildren() { children = new LinkedList<>(); }

    public T apply(List<T> sampleRowValues) {
        switch (nodeType) {
            case FUNCTION:
                // It will call the same function on all its children. Eventually the children
                // will be constants or a feature, then it will bubble those values back up the
                // line to pass on to function.
                List<T> values = new LinkedList<>();

                for (Node<T> n : children)
                    values.add(n.apply(sampleRowValues));

                return function.apply(values);

            case FEATURE:
                return sampleRowValues.get(featureIndex);

            case CONSTANT: return constant;
            default:
                throw new IllegalArgumentException();
        }
    }

    public String toString() {
        switch(nodeType) {
            case CONSTANT: return constant + "";
            case FEATURE: return featureIndex + "";
            case FUNCTION:
                if (children.size() > 0)
                    return "(" + children.get(0) + " " + function + " " + children.get(1) + ")";
                else
                    return function.toString();
            default: return ":)";
        }
    }

    public int getChildrenSize() {
        switch(nodeType) {
            case CONSTANT: case FEATURE: return 0;
            case FUNCTION: return function.getArity();
            default: return 0;
        }
    }
}

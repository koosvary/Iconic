package org.iconic.ea.chromosome;

import java.util.ArrayList;
import java.util.List;

public class TreeChromosome<T> extends Chromosome<T> {
    private Node<T> root;
    private List<Node<T>> expression;

    public TreeChromosome() {
        expression = new ArrayList<>();
    }

    public void generateTree() {
        generateTree(expression);
    }

    private void generateTree(List<Node<T>> expression) {
        int currentNodeIndex = 0;
        int currentChildrenIndex = 1;

        while (currentNodeIndex < currentChildrenIndex) {
            Node<T> currentNode = expression.get(currentNodeIndex);
            for (int i = 0; i < currentNode.getChildrenSize(); i++) {
                currentNode.addChild(expression.get(currentChildrenIndex));
                currentChildrenIndex++;
            }
            currentNodeIndex++;
        }
    }

    public void setExpression(List<Node<T>> expression) {
        this.expression = expression;
        root = expression.get(0);
    }

    @Override
    public T evaluate(List<T> sampleRowValues) {
        return root.apply(sampleRowValues);
    }

    @Override
    public List evolve(List population) {
        return null;
    }

    public String toString() {
        String output = "";

        for (Node<T> n : expression)
            output += n.toString();

        return output;
    }
}

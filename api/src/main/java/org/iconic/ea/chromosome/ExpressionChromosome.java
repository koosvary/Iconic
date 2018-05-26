package org.iconic.ea.chromosome;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ExpressionChromosome<T> extends Chromosome<T> implements LinearChromosome<T>, TreeChromosome<T> {
    private Node<T> root;
    private List<Node<T>> expression;
    private int activeNodes;

    public ExpressionChromosome() {
        expression = new ArrayList<>();
    }

    public void generateTree() {
        generateTree(expression);
    }

    private void generateTree(List<Node<T>> expression) {
        root = expression.get(0);
        int currentNodeIndex = 0;
        int currentChildrenIndex = 1;

        for (Node<T> n : expression)
            n.removeAllChildren();

        while (currentNodeIndex < currentChildrenIndex) {
            Node<T> currentNode = expression.get(currentNodeIndex);
            for (int i = 0; i < currentNode.getChildrenSize(); i++) {
                currentNode.addChild(expression.get(currentChildrenIndex));
                currentChildrenIndex++;
            }
            currentNodeIndex++;
        }

        activeNodes = currentNodeIndex;
    }

    public int getExpressionLength() { return expression.size(); }

    public List<Node<T>> getExpression() { return expression; }

    public void setExpression(List<Node<T>> expression) {
        this.expression = expression;
        root = expression.get(0);
    }

    @Override
    public ExpressionChromosome<T> mutate(final double p) {
        System.out.println("ExpressionChromosome    mutate  This function just returns null.. Dont think it should be called");
        return null;
    }

    @Override
    public List<T> evaluate(List<List<T>> input) {
        List<T> calculatedValues = new LinkedList<>();

        for (List<T> row : input) {
            calculatedValues.add(root.apply(row));
        }

        return calculatedValues;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();

        //for (Node<T> n : expression)
        //    output.append(n.toString());
        output.append(root.toString());

        return output.toString();
    }

    // The expression only uses some nodes and others are there to be used later after mutation.
    // This returns the current active ones determined when building the tree
    public int getActiveNodes() { return activeNodes; }
}

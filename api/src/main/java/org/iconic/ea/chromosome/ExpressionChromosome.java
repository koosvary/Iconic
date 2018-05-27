package org.iconic.ea.chromosome;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ExpressionChromosome<T> extends Chromosome<T> implements LinearChromosome<T>, TreeChromosome<T> {
    private Node<T> root;
    private List<Node<T>> expression;
    private final int headLength;
    private final int tailLength;
    private int treeIndex;

    public ExpressionChromosome(final int headLength, final int tailLength) {
        super();
        this.expression = new LinkedList<>();
        this.headLength = headLength;
        this.tailLength = tailLength;
        this.treeIndex = 0;
    }

    public void generateTree() {
        generateTree(expression);
    }

    /**
     * <p>
     * Generate the phenotypic expression tree from the genotype
     * </p>
     *
     * @param expression
     */
    private void generateTree(List<Node<T>> expression) {
        assert(expression.size() == 7);
        // Start at the root and greedily fill the tree
        expression.forEach(Node::removeAllChildren);
        root = recursivelyGenerateTree(expression.get(treeIndex++));
    }

    private Node<T> recursivelyGenerateTree(Node<T> root) {
        if (root.getNumberOfChildren() < 1) {
            return root;
        }

        for (int i = 0; i < root.getNumberOfChildren(); ++i) {
            root.addChild(
                    recursivelyGenerateTree(getExpression().get(treeIndex++))
            );
        }

        return root;
    }

    public int getExpressionLength() {
        return expression.size();
    }

    public List<Node<T>> getExpression() {
        return expression;
    }

    public void setExpression(List<Node<T>> expression) {
        this.expression = new LinkedList<>();

        this.expression.addAll(
                expression.stream().map(Node::clone).collect(Collectors.toList())
        );

        root = this.expression.get(0);
    }

    @Override
    public List<T> evaluate(List<List<T>> input) {
        List<T> calculatedValues = new LinkedList<>();

        for (List<T> row : input) {
            calculatedValues.add(root.apply(row));
        }

        return calculatedValues;
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public int getHeadLength() {
        return headLength;
    }

    public int getTailLength() {
        return tailLength;
    }
}

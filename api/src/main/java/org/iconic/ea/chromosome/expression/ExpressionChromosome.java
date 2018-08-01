package org.iconic.ea.chromosome.expression;

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.LinearChromosome;
import org.iconic.ea.chromosome.graph.Node;
import org.iconic.ea.chromosome.TreeChromosome;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 * <p>A chromosome that encodes an expression tree.</p>
 */
public class ExpressionChromosome<T> extends Chromosome<T> implements TreeChromosome<T>, LinearChromosome<Node<T>>, Cloneable {
    private List<Node<T>> genome;
    private Node<T> root;
    private final int headLength;
    private final int tailLength;
    private int treeIndex;

    /**
     * <p>
     * Constructs a new expression chromosome with the provided head length, tail length, and number of feeatures.
     * </p>
     *
     * @param headLength  The length of the chromosome's head
     * @param tailLength  The length of the chromosome's tail
     * @param numFeatures The number of features that may be expressed by the chromosome
     */
    public ExpressionChromosome(final int headLength, final int tailLength, final int numFeatures) {
        super(numFeatures);
        this.headLength = headLength;
        this.tailLength = tailLength;
        this.treeIndex = 0;
    }

    /**
     * <p>
     * Generates a phenotypic expression tree from the genotype.
     * </p>
     */
    protected void generateTree() {
        // Reset the tree index
        setTreeIndex(0);

        final int treeIndex = getTreeIndex();
        final List<Node<T>> genome = getGenome();

        // Remove all children from each node in the genome to make sure we're not left with any old connections
        genome.forEach(Node::removeAllChildren);

        // Start at the root and greedily fill the tree
        setRoot(
                recursivelyGenerateTree(genome.get(treeIndex))
        );
    }

    /**
     * <p>
     * A recursive helper function for {@link #generateTree()}, it fills the tree starting from the root.
     * </p>
     *
     * @param root The root of the tree
     * @return the root with all of its child nodes populated
     */
    private Node<T> recursivelyGenerateTree(Node<T> root) {
        final int treeIndex = getTreeIndex() + 1;
        final List<Node<T>> genome = getGenome();

        // Increment the tree index
        setTreeIndex(treeIndex);

        // If the root has no children simply return it
        if (root.getNumberOfChildren() < 1) {
            return root;
        }

        // For each child that root needs, recursively fill its children
        for (int i = 0; i < root.getNumberOfChildren(); ++i) {
            root.addChild(
                    recursivelyGenerateTree(genome.get(getTreeIndex()))
            );
        }

        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> evaluate(List<List<T>> input) {
        List<T> calculatedValues = new LinkedList<>();

        for (List<T> row : input) {
            calculatedValues.add(getRoot().apply(row));
        }

        return calculatedValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return root.toString();
    }

    /**
     * <p>
     * Returns the length of this chromosome's head.
     * </p>
     *
     * @return the length of the chromosome's head
     */
    public int getHeadLength() {
        return headLength;
    }

    /**
     * <p>
     * Returns the length of this chromosome's tail
     * </p>
     *
     * @return the length of the chromosome's tail
     */
    public int getTailLength() {
        return tailLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Node<T>> getGenome() {
        return genome;
    }

    /**
     * <p>
     * Sets the genome of this chromosome to the specified value.
     * </p>
     *
     * @param genome The new genome of the chromosome
     */
    public void setGenome(List<Node<T>> genome) {
        this.genome = new LinkedList<>();

        this.genome.addAll(
                genome.stream().map(Node::clone).collect(Collectors.toList())
        );

        generateTree();
    }

    /**
     * <p>
     * Sets the tree index of this chromosome to the specified value.
     * </p>
     * <p>
     * The tree index is used to track the current active node of this chromosome's tree.
     * </p>
     *
     * @param treeIndex The new tree index of the chromosome
     */
    protected void setTreeIndex(final int treeIndex) {
        this.treeIndex = treeIndex;
    }

    /**
     * <p>
     * Returns the tree index of this chromosome which tracks the current active node of the tree.
     * </p>
     *
     * @return the tree index of the chromosome
     */
    protected int getTreeIndex() {
        return treeIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node<T> getRoot() {
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRoot(final Node<T> root) {
        this.root = root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExpressionChromosome<T> clone() {
        List<Node<T>> genome = getGenome().stream()
                .map(Node::clone)
                .collect(Collectors.toList());

        ExpressionChromosome<T> clone = new ExpressionChromosome<>(getHeadLength(), getTailLength(), getNumFeatures());
        clone.setGenome(genome);

        return clone;
    }
}

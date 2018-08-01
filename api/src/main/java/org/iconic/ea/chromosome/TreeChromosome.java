package org.iconic.ea.chromosome;

import org.iconic.ea.chromosome.graph.Node;

/**
 * <p>
 * A tree-backed chromosome. The user of this interface stores their genotype as a tree-like structure and has
 * access to the root of the tree.
 * </p>
 *
 * @param <T> The type class of the data to pass through the chromosome
 */
public interface TreeChromosome<T> {
    /**
     * <p>
     * Returns the root of this chromosome
     * </p>
     *
     * @return the root of the chromosome
     */
    Node<T> getRoot();

    /**
     * <p>
     * Sets the root of this chromosome to the specified value
     * </p>
     *
     * @param root The new root of the chromosome
     */
    void setRoot(final Node<T> root);
}

package org.iconic.ea.chromosome;

import org.iconic.ea.chromosome.graph.Node;

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

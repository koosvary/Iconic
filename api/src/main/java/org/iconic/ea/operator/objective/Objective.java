package org.iconic.ea.operator.objective;

import org.iconic.ea.chromosome.Chromosome;

/**
 * <p>Defines a functional interface for an objective</p>
 *
 * <p>
 * An objective is a measure used by an {@see org.iconic.ea.EvolutionaryAlgorithm} to determine the fitness
 * of chromosomes.
 * </p>
 *
 * @param <T> The type of the {@link org.iconic.ea.chromosome.Chromosome chromosome's} input and output
 */
@FunctionalInterface
public interface Objective<T> {
    /**
     * <p>Applies this objective to the given {@link org.iconic.ea.chromosome.Chromosome chromosome}</p>
     *
     * @param c The chromosome to apply this objective to
     * @return the fitness of the chromosome
     * @see org.iconic.ea.chromosome.Chromosome
     */
    double apply(final Chromosome<T> c);
}

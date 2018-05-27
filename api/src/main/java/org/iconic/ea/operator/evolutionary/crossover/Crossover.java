package org.iconic.ea.operator.evolutionary.crossover;

import org.iconic.ea.chromosome.Chromosome;

@FunctionalInterface
public interface Crossover<T extends Chromosome<R>, R> {
    T apply(final T c1, final T c2);
}

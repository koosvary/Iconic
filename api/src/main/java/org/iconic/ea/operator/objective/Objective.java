package org.iconic.ea.operator.objective;

import org.iconic.ea.chromosome.Chromosome;

@FunctionalInterface
public interface Objective<T extends Chromosome<R>, R> {
    double apply(final T c);
}

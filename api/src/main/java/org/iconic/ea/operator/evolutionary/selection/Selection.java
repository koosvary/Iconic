package org.iconic.ea.operator.evolutionary.selection;

import org.iconic.ea.chromosome.Chromosome;

import java.util.List;

@FunctionalInterface
public interface Selection<T extends Chromosome<R>, R> {
    T apply(final T parent, final List<T> children);
}

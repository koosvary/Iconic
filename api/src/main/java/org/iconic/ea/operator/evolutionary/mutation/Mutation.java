package org.iconic.ea.operator.evolutionary.mutation;

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.List;

@FunctionalInterface
public interface Mutation<T extends Chromosome<R>, R> {
    T apply(final List<FunctionalPrimitive<R>> functionalPrimitives, final T chromosome);
}

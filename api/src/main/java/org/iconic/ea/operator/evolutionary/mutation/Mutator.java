package org.iconic.ea.operator.evolutionary.mutation;

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.List;

@FunctionalInterface
public interface Mutator<T extends Chromosome<R>, R> {
    /**
     * <p>Applies the mutation to a copy of the parent</p>
     *
     * @param functionalPrimitives The list of functional primitives that this chromosome has available
     * @param chromosome           The parent chromosome which will be cloned then mutated to create the mutant
     * @return a mutated copy of the parent chromosome
     */
    T apply(final List<FunctionalPrimitive<R, R>> functionalPrimitives, final T chromosome);
}

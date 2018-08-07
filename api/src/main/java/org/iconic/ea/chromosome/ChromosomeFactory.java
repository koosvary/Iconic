package org.iconic.ea.chromosome;

import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * A ChromosomeFactory is used for easy replication of chromosomes.
 * </p>
 *
 * @param <T> The type of chromosome to be constructed
 * @param <R> The type class of the data to pass through the chromosome
 */
public abstract class ChromosomeFactory<T extends Chromosome<R>, R> {
    protected final List<FunctionalPrimitive<R, R>> functionalPrimitives;

    protected ChromosomeFactory() {
        functionalPrimitives = new LinkedList<>();
    }

    public abstract T getChromosome();

    public List<FunctionalPrimitive<R, R>> getFunctionalPrimitives() {
        List<FunctionalPrimitive<R, R>> clone = new LinkedList<>();
        clone.addAll(functionalPrimitives);

        return clone;
    }

    public FunctionalPrimitive<R, R> getFunction(int i) {
        return getFunctionalPrimitives().get(i);
    }

    public void addFunction(List<FunctionalPrimitive<R, R>> functions) {
        functionalPrimitives.addAll(functions);
    }

}

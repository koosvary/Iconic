package org.iconic.ea;

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.LinkedList;
import java.util.List;

public abstract class EvolutionaryAlgorithm<T, R extends Chromosome<T>> {
    private final List<FunctionalPrimitive<T>> functionalPrimitives;

    protected EvolutionaryAlgorithm() {
        this.functionalPrimitives = new LinkedList<>();
    }

    public abstract List<R> evolve(final List<R> population);

    public List<FunctionalPrimitive<T>> getFunctionalPrimitives() { return functionalPrimitives; }

    public void addFunctionalPrimitive(FunctionalPrimitive<T> func) {
        functionalPrimitives.add(func);
    }

    public int getFunctionalPrimitivesSize() { return functionalPrimitives.size(); }

//    public abstract T evaluate(List<T> sampleRowValues);
}

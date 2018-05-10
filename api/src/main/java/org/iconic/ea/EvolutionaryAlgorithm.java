package org.iconic.ea;

import java.util.LinkedList;
import java.util.List;

public abstract class EvolutionaryAlgorithm<T> {
    private final List<FunctionalPrimitive<T>> functionalPrimitives;

    protected EvolutionaryAlgorithm() {
        this.functionalPrimitives = new LinkedList<>();
    }

    public abstract List evolve(final List population);

    public List<FunctionalPrimitive<T>> getFunctionalPrimitives() { return functionalPrimitives; }

    public void addFunctionalPrimitive(FunctionalPrimitive<T> func) {
        functionalPrimitives.add(func);
    }

    public int getFunctionalPrimitivesSize() { return functionalPrimitives.size(); }

    public abstract T evaluate(List<T> sampleRowValues);
}

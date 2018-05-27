package org.iconic.ea.chromosome;

import org.iconic.ea.operator.evolutionary.mutation.Mutator;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>A chromosome is an individual within a population</p>
 * <p>Chromosomes are strongly typed and must return an output of the same form as its input.</p>
 * @param <T> The type of data that passes through the chromosome
 */
public abstract class Chromosome<T> {
    private boolean changed;
    private double fitness;
    private final List<Mutator<Chromosome<T>, T>> mutators;

    public Chromosome() {
        this.changed = true;
        this.mutators = new LinkedList<>();
    }

    public void setFitness(final double fitness) {
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public abstract List<T> evaluate(List<List<T>> input);

    protected final List<Mutator<Chromosome<T>, T>> getMutators() {
        List<Mutator<Chromosome<T>, T>> copy = new LinkedList<>();
        copy.addAll(mutators);

        return copy;
    }

    public void addMutator(Mutator<Chromosome<T>, T> mutator) {
        getMutators().add(mutator);
    }
}

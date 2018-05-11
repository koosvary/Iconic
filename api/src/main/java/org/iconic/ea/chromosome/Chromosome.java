package org.iconic.ea.chromosome;

import java.util.List;

/**
 * <p>A chromosome is an individual within a population</p>
 * <p>Chromosomes are strongly typed and must return an output of the same form as its input.</p>
 * @param <T> The type of data that passes through the chromosome
 */
public abstract class Chromosome<T> {
    private double fitness;
    private int size;
    private int complexity;

    public Chromosome() {}

    public void setFitness(final double fitness) {
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }

    public abstract List<T> evaluate(List<List<T>> sampleData);

}

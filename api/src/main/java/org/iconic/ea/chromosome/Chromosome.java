package org.iconic.ea.chromosome;

import org.iconic.ea.data.DataManager;

import java.util.List;
import java.util.Map;

/**
 * A chromosome is equivalent to an individual within a population, they're strongly typed and must return an
 * output of the same form as its input.
 * @param <T> The type class of the data to pass through the chromosome
 */
public abstract class Chromosome<T> {
    private boolean changed;
    private double fitness;
    private final int numFeatures;

    /**
     * Constructs a new chromosome with the specified number of features.
     * @param numFeatures The maximum number of features for the chromosome to express
     */
    public Chromosome(final int numFeatures) {
        this.changed = true;
        this.numFeatures = numFeatures;
    }

    /**
     * Sets the fitness of the chromosome to the specified value
     * @param fitness The new fitness of the chromosome
     */
    public void setFitness(final double fitness) {
        this.fitness = fitness;
    }

    /**
     * Returns the fitness of this chromosome.
     * @return the fitness of the chromosome
     */
    public double getFitness() {
        return fitness;
    }

    /**
     * <p>
     * Returns true if this chromosome has been changed.
     * </p>
     *
     * <p>
     * The changed flag is used to determine whether or not a chromosome needs to be re-evaluated. If the
     * flag isn't set to true after modifying the chromosome's genotype then its phenotype won't be updated to
     * reflect the changes.
     * </p>
     *
     * @return true if the chromosome has been changed
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * Sets the changed status of this chromosome to the specified value.
     * @param changed The new changed value
     * @see Chromosome#isChanged()
     */
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    /**
     * <p>Evaluates the specified input samples and returns a list of output values for each sample.</p>
     * <p>The samples used should be a two-dimensional matrix, with each sample on a separate row. The final
     * column needs to contain the expected result for its corresponding row.</p>
     * @param input The input samples to evaluate
     * @return A list of outputs, one for each input sample, referenced by output id
     */
    public abstract List<Map<Integer, T>> evaluate(final DataManager<T> input);

    /**
     * Returns the number of features this chromosome can express.
     * @return the number of features the chromosome can express
     */
    public int getInputs() {
        return numFeatures;
    }

    /**
     * Returns the size of the chromosome.
     * @return The size of the chromosome
     */
    public abstract int getSize();
}

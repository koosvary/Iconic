package org.iconic.ea.operator.objective;

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.objective.error.ErrorFunction;

import java.util.LinkedList;
import java.util.List;

/**
 * {@inheritDoc}
 * <p>
 * An error based objective is an objective function based around an error function.
 * </p>
 */
public abstract class ErrorBasedObjective<T> implements Objective<T> {
    private final ErrorFunction lambda;
    private List<List<T>> samples;
    private List<T> expectedResults;
    private boolean changed;

    /**
     * <p>
     * Constructs a new ErrorBasedObjective with the provided error function and samples.
     * </p>
     *
     * <p>
     * The samples used should be a two-dimensional matrix, with each sample on a separate row. The final
     * column must contain the expected result.
     * </p>
     *
     * @param lambda  The error function to apply
     * @param samples The samples to use with the error function
     */
    public ErrorBasedObjective(final ErrorFunction lambda, final List<List<T>> samples) {
        this.lambda = lambda;
        this.samples = samples;
        this.expectedResults = new LinkedList<>();
        this.changed = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract double apply(final Chromosome<T> c);

    /**
     * <p>Returns the samples used by this objective.</p>
     *
     * @return the samples used by this objective
     */
    protected List<List<T>> getSamples() {
        return samples;
    }

    /**
     * <p>Returns the error function used by this objective.</p>
     *
     * @return the error function used by this objective
     */
    protected ErrorFunction getLambda() {
        return lambda;
    }

    /**
     * <p>Returns the expected results for the samples used by this objective.</p>
     *
     * @return the expected results for the samples used by this objective
     */
    protected List<T> getExpectedResults() {
        // Check if the expected results need to be recalculated
        if (isChanged()) {
            List<T> results = new LinkedList<>();

            // Collect the expected answers
            for (List<T> sample : getSamples()) {
                T result = sample.get(sample.size() - 1);
                results.add(result);
            }

            expectedResults = results;
            setChanged(false);
        }

        return expectedResults;
    }

    /**
     * <p>Returns the changed status of this objective.</p>
     *
     * <p>
     * If the backing samples used by this objective are changed then the expected results need to be
     * recalculated.
     * </p>
     *
     * @return true if the backing samples used by this objective have changed
     */
    private boolean isChanged() {
        return changed;
    }

    /**
     * <p>
     * Sets the changed status of this objective to the provided value.
     * </p>
     *
     * @param changed The value to set the changed status of this objective to
     */
    private void setChanged(final boolean changed) {
        this.changed = changed;
    }

    /**
     * <p>
     * Sets the samples of this objective to the provided value and marks it as changed.
     * </p>
     *
     * @param samples The value to set the samples of this objective to
     */
    public void setSamples(final List<List<T>> samples) {
        setChanged(true);
        this.samples = samples;
    }
}

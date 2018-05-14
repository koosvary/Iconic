package org.iconic.ea.operator.objective;

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.objective.error.ErrorFunction;

import java.util.LinkedList;
import java.util.List;

public abstract class ErrorBasedObjective<T extends Chromosome<R>, R> implements Objective<T, R> {
    private final ErrorFunction lambda;
    private List<List<R>> samples;
    private List<R> expectedResults;
    private boolean changed;

    public ErrorBasedObjective(ErrorFunction lambda, List<List<R>> samples) {
        this.lambda = lambda;
        this.samples = samples;
        this.expectedResults = new LinkedList<>();
        this.changed = true;
    }

    @Override
    public abstract double apply(final T c);

    protected List<List<R>> getSamples() {
        return samples;
    }

    protected ErrorFunction getLambda() {
        return lambda;
    }

    protected List<R> getExpectedResults() {
        if(changed) {
            // Collect the expected answers
            List<R> results = new LinkedList<>();

            for (List<R> sample : getSamples()) {
                R result = sample.get(sample.size() - 1);
                results.add(result);
            }

            expectedResults = results;
            setChanged(false);
        }

        return expectedResults;
    }

    private void setChanged(final boolean changed) {
        this.changed = changed;
    }

    public void setSamples(List<List<R>> samples) {
        setChanged(true);
        this.samples = samples;
    }
}

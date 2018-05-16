package org.iconic.ea.operator.objective;

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.objective.error.ErrorFunction;

import java.util.List;

/**
 * {@inheritDoc}
 * <p>A default objective that uses a chromosome's error as the fitness.</p>
 */
public class DefaultObjective<T extends Chromosome<R>, R> extends ErrorBasedObjective<T, R> {

    /**
     * <p>Constructs a new DefaultObjective</p>
     *
     * @param lambda  The error function to apply
     * @param samples The samples to use with the error function
     */
    public DefaultObjective(ErrorFunction lambda, List<List<R>> samples) {
        super(lambda, samples);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double apply(final T c) {
        List<Double> results = (List<Double>) c.evaluate(getSamples());

        final double fitness = getLambda().apply(results, (List<Double>) getExpectedResults());
        ;
        c.setFitness(fitness);

        return fitness;
    }
}

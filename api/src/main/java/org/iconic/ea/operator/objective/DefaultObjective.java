package org.iconic.ea.operator.objective;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.objective.error.ErrorFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 * <p>
 * A default objective that uses a chromosome's error as the fitness.
 * </p>
 */
@Log4j2
public class DefaultObjective<T> extends ErrorBasedObjective<T> {

    /**
     * <p>
     * Constructs a new DefaultObjective.
     * </p>
     *
     * @param lambda  The error function to apply
     * @param samples The samples to use with the error function
     */
    public DefaultObjective(ErrorFunction lambda, List<List<T>> samples) {
        super(lambda, samples);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double apply(final Chromosome<T> c) {
        List<Map<Integer, T>> results = c.evaluate(getSamples());
        List<Double> summatedResults = new ArrayList<>(results.size());

        // Convert results into a List<T> with each output summed
        results.forEach(result -> summatedResults.add(result.values().stream().mapToDouble(i -> (Double) i).sum()));

        final double fitness = getLambda().apply(summatedResults, (List<Double>) getExpectedResults());

        c.setFitness(fitness);

        return fitness;
    }
}

package org.iconic.ea.operator.objective;

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.objective.error.ErrorFunction;

import java.util.List;

public class DefaultObjective<T extends Chromosome<R>, R> extends ErrorBasedObjective<T, R> {

    public DefaultObjective(ErrorFunction lambda, List<List<R>> samples) {
        super(lambda, samples);
    }

    @Override
    public double apply(final T c) {
        List<Double> results = (List<Double>) c.evaluate(getSamples());

        final double fitness = getLambda().apply(results, (List<Double>) getExpectedResults());;
        c.setFitness(fitness);

        return fitness;
    }
}

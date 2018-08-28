package org.iconic.ea.operator.objective;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.data.DataManager;
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
     * <p>Constructs a new DefaultObjective</p>
     *
     * @param lambda      The error function to apply
     * @param dataManager The samples to use with the error function
     */
    public DefaultObjective(final ErrorFunction lambda, final DataManager<T> dataManager) {
        super(lambda, dataManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double apply(final Chromosome<T> c) {
        List<Map<Integer, T>> results = c.evaluate(getDataManager());
        List<Double> summedResults = new ArrayList<>(results.size());

        // Convert results into a list with each output summed
        results.forEach(result -> summedResults.add(
                result.values().stream().mapToDouble(i -> (Double) i).sum()
        ));

        final double fitness = getLambda().apply(summedResults, (List<Double>) getExpectedResults());
        c.setFitness(fitness);

        return fitness;
    }
}

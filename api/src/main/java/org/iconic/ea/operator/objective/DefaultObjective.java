/**
 * Copyright 2018 Iconic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iconic.ea.operator.objective;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.operator.objective.error.ErrorBasedObjective;
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
public class DefaultObjective extends ErrorBasedObjective {

    /**
     * <p>Constructs a new DefaultObjective</p>
     *
     * @param lambda      The error function to apply
     * @param dataManager The samples to use with the error function
     */
    public DefaultObjective(final ErrorFunction lambda, final DataManager<Double> dataManager) {
        super(lambda, dataManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double apply(final Chromosome<Double> c) {
        List<Map<Integer, Double>> results = c.evaluate(getDataManager());
        List<Double> summedResults = new ArrayList<>(results.size());

        // Convert results into a list with each output summed
        results.forEach(result -> summedResults.add(
                result.values().stream().mapToDouble(i -> i).sum()
        ));

        double fitness = getLambda().apply(summedResults, getExpectedResults());

        // If an erroneous value is given replace it with the worst possible fitness
        if (Double.isNaN(fitness)) {
            fitness = getWorstValue();
        }

        c.setFitness(fitness);

        return fitness;
    }
}

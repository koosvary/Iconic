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
package org.iconic.ea.strategies.seamo;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.strategies.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.ChromosomeFactory;
import org.iconic.ea.operator.evolutionary.selection.RandomUniformSelector;
import org.iconic.ea.operator.evolutionary.selection.Selector;
import org.iconic.ea.operator.evolutionary.selection.SequentialSelector;
import org.iconic.ea.operator.objective.MultiObjective;
import org.iconic.ea.operator.objective.Objective;
import org.iconic.ea.strategies.MultiObjectiveEvolutionaryAlgorithm;

import java.util.*;

/**
 * {@inheritDoc}
 * <p>
 * A simple evolutionary algorithm for multiple-objectives. Based on the paper written by
 * Christine L. Mumford-Valenzuela.
 *
 * @see <a href="https://doi.org/10.1109/CEC.2002.1007014">A simple evolutionary algorithm for multi-objective optimization (SEAMO)</a>
 */
@Log4j2
public class SEAMO<R extends Chromosome<T>, T extends Comparable<T>>
        extends MultiObjectiveEvolutionaryAlgorithm<R, T> {
    /**
     * {@inheritDoc}
     */
    public SEAMO(ChromosomeFactory<R, T> chromosomeFactory) {
        this(chromosomeFactory, 1);
    }

    /**
     * {@inheritDoc}
     */
    public SEAMO(ChromosomeFactory<R, T> chromosomeFactory, int lambda) {
        super(chromosomeFactory, lambda);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<R> evolve(List<R> population) {
        final List<R> newPopulation = elitism(population);

        // For every member of the population use it as a parent for generating
        // at least one offspring
        for (int i = 0; i < newPopulation.size(); i++) {
            // Populate an initial pool of parents
            final List<R> parents = new LinkedList<>();

            // For each selector insert a parent using the selector
            // A minimum of two parents are required
            for (int j = 0; j < getSelectors().size() || j < 2; ++j) {
                final R parent = getSelector(j).apply(newPopulation);
                parents.add(parent);
            }

            // Create a single offspring by performing crossover on the first two parents
            R offspring = crossover((R) parents.get(0).clone(), (R) parents.get(1).clone());
            offspring = mutate((R) offspring.clone());

            boolean alreadyReplaced = false;

            // For each parent see if it can be replaced by its offspring
            for (int j = 0; j < parents.size(); ++j) {
                // Stop once the offspring has replaced a parent
                if (alreadyReplaced) {
                    break;
                }

                R parent = parents.get(j);

                // Replace the parent by its offspring if it's dominated by them
                if (isDominatedBy(getObjective(), parent, offspring)) {
                    alreadyReplaced = replaceParent(getObjective(), newPopulation, parent, offspring);
                }
                // Otherwise check if the offspring should still replace the parent
                else if (shouldReplace(getGlobals(), getObjective(), newPopulation, offspring)) {
                    // If the parent isn't a global best it can be replaced
                    if (!isGlobalBest(parent)) {
                        alreadyReplaced = replaceParent(getObjective(), newPopulation, parent, offspring);
                    }
                    // Otherwise pick a new parent
                    else if (parents.size() <= newPopulation.size()) {
                        final Selector<R> selector = getSelector(parents.size() - 1);
                        final R newParent = selector.apply(newPopulation);
                        parents.add(newParent);
                    }
                }
            }
        }
        return newPopulation;
    }
}

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
package org.iconic.ea.strategies.gsemo;

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
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@inheritDoc}
 * <p>
 * A  simple evolutionary algorithm for multiple-objectives. Based on the paper written by
 * Oliver Giel.
 * <p>
 * The population of GSEMO grows with the number of non-dominated solutions, therefore it's recommended
 * to initialise the population with only a single member.
 *
 * @see <a href="https://doi.org/10.1109/CEC.2003.1299908">Expected runtimes of a simple multi-objective evolutionary algorithm</a>
 */
@Log4j2
public class GSEMO<R extends Chromosome<T>, T extends Comparable<T>>
        extends MultiObjectiveEvolutionaryAlgorithm<R, T> {

    /**
     * {@inheritDoc}
     */
    public GSEMO(ChromosomeFactory<R, T> chromosomeFactory) {
        this(chromosomeFactory, 1);
    }

    /**
     * {@inheritDoc}
     */
    public GSEMO(ChromosomeFactory<R, T> chromosomeFactory, int lambda) {
        super(chromosomeFactory, lambda);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<R> evolve(List<R> population) {
        final List<R> newPopulation = elitism(population);
        // Select a parent using the selector
        // Exactly one parent is required
        final R parent = getSelector(0).apply(newPopulation);

        // Create a single offspring by performing mutation on the parent
        R offspring = mutate((R) parent.clone());

        // Continue mutating the offspring based on its parent's length
        // reducing the probability of mutation with each attempt
        for (int j = 2; j <= parent.getSize(); ++j) {
            if ((1./ (double) j) >= ThreadLocalRandom.current().nextDouble()) {
                offspring = mutate(offspring);
            }
        }

        boolean add = true;

        for (final R candidate : newPopulation) {
            if (isDominatedBy(getObjective(), offspring, candidate)) {
                add = false;
                break;
            }
        }

        if (add) {
            for (int i = 0; i < newPopulation.size(); i++) {
                final R candidate = newPopulation.get(i);
                if (isDominatedBy(getObjective(), candidate, offspring)) {
                    newPopulation.remove(candidate);
                }
            }
            newPopulation.add(offspring);
        }
        return newPopulation;
    }
}

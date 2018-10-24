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
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.ChromosomeFactory;
import org.iconic.ea.operator.objective.MultiObjective;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@inheritDoc}
 * <p>
 * This version of SEAMO implements a basic form of elitism.
 */
@Log4j2
public class ElitistSEAMO<R extends Chromosome<T>, T extends Comparable<T>>
        extends SEAMO<R, T> {

    /**
     * {@inheritDoc}
     */
    public ElitistSEAMO(ChromosomeFactory<R, T> chromosomeFactory) {
        this(chromosomeFactory, 1);
    }

    /**
     * {@inheritDoc}
     */
    public ElitistSEAMO(ChromosomeFactory<R, T> chromosomeFactory, int lambda) {
        super(chromosomeFactory, lambda);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Culls members of the population based on their rank. A new population
     * of the same size is returned where the survivors are ordered by their fitness.
     * <p>
     * The size of the original population is preserved within the new population
     * by creating new individuals.
     * <p>
     * It's possible for duplicate members to be introduced to the population.
     *
     * @return A new population of individuals ordered by their fitness.
     */
    @Override
    protected List<R> elitism(List<R> population) {
        final List<R> newPopulation = new LinkedList<>(population);
        final Comparator<R> comparator =
                Comparator.comparingDouble(Chromosome::getFitness);

        // Sort the population so that the fittest individual comes first
        // TODO: tie the ordering to the objective -- this will fail on non-minimisation problems
        population.sort(comparator.reversed());

        // Add elites (fitter individuals) to the population
        for (int i = 0; i < population.size(); ++i) {
            final R chromosome = population.get(i);
            // The likelihood of an individual surviving grows inversely with the population
            // Global bests survive automatically
            if (isGlobalBest(chromosome) || ThreadLocalRandom.current().nextDouble() < 1. / (double) i) {
                newPopulation.add(chromosome);
            }
        }

        // Repopulate the population
        while (newPopulation.size() < population.size()) {
            final R chromosome = getChromosomeFactory().getChromosome();
            newPopulation.add(chromosome);
        }

        MultiObjective<T> multiObjective = (MultiObjective<T>) getObjective();

        // The global bests need to be recalculated for the new population
        // (unfortunately this makes the function impure)
        newPopulation.forEach(chromosome -> {
                    multiObjective.getGoals().parallelStream().forEach(goal ->
                            addGlobal(getGlobals(), chromosome, goal, goal.apply(chromosome)));
                    multiObjective.apply(chromosome);
                }
        );

        return newPopulation;
    }
}

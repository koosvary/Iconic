/**
 * Copyright (C) 2018 Iconic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.iconic.ea.strategies.seamo;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.ChromosomeFactory;
import org.iconic.ea.operator.evolutionary.selection.RandomUniformSelector;
import org.iconic.ea.operator.evolutionary.selection.Selector;
import org.iconic.ea.operator.evolutionary.selection.SequentialSelector;
import org.iconic.ea.operator.objective.MultiObjective;
import org.iconic.ea.operator.objective.Objective;

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

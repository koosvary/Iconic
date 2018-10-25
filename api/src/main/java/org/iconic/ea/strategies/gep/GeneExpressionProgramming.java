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
package org.iconic.ea.strategies.gep;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.ChromosomeFactory;
import org.iconic.ea.chromosome.expression.ExpressionChromosome;
import org.iconic.ea.chromosome.expression.ExpressionChromosomeFactory;
import org.iconic.ea.operator.objective.MonoObjective;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Log4j2
public class GeneExpressionProgramming<T extends Comparable<T>>
        extends EvolutionaryAlgorithm<ExpressionChromosome<T>, T> {
    public GeneExpressionProgramming(ExpressionChromosomeFactory<T> chromosomeFactory) {
        super(chromosomeFactory);
    }

    public void initialisePopulation(int populationSize) {
        for (int i = 0; i < populationSize; i++) {
            ExpressionChromosome<T> chromosome = getChromosomeFactory().getChromosome();
            getObjective().apply(chromosome);
            getChromosomes().add(chromosome);
        }
    }

    @Override
    public List<ExpressionChromosome<T>> evolve(List<ExpressionChromosome<T>> population) {
        final double crossoverChance = getCrossoverProbability();
        final double mutationChance = getMutationProbability();

        final Comparator<Chromosome<T>> comparator = Comparator.comparing(Chromosome::getFitness);
        final ExpressionChromosome<T> bestCandidate = population
                .stream().min(comparator).get();

        for (int i = 0, populationSize = population.size(); i < populationSize; i++) {
            ExpressionChromosome<T> c = population.get(i);
            // Perform crossover
            if (ThreadLocalRandom.current().nextDouble(0, 1) <= crossoverChance) {
                if (!c.equals(bestCandidate)) {
                    population.set(i, crossover(bestCandidate, c));
                }
            }

            // Perform mutation
            if (ThreadLocalRandom.current().nextDouble(0, 1) <= mutationChance) {
                population.set(i, mutate(c));
            }
        }

        return population;
    }

    private ExpressionChromosome<T> crossover(ExpressionChromosome<T> c1, ExpressionChromosome<T> c2) {
        assert (getCrossovers().size() > 0);

        ExpressionChromosome<T> child = getCrossover(0).apply(c1, c2);
        getObjective().apply(child);

        return child;
    }

    private ExpressionChromosome<T> mutate(ExpressionChromosome<T> chromosome) {
        assert (getMutators().size() > 0);
        Objects.requireNonNull(getObjective(), "An objective is required");

        ExpressionChromosome<T> child = getMutator(0).apply(
                getChromosomeFactory().getFunctionalPrimitives(),
                chromosome
        );

        // Evaluate the fitness of both chromosomes
        double parentFitness = getObjective().apply(chromosome);
        double childFitness = getObjective().apply(child);

        // Return the new chromosome if it's objectively better or equivalent to its parent
        if (childFitness <= parentFitness) {
            return child;
        }

        return chromosome;
    }
}

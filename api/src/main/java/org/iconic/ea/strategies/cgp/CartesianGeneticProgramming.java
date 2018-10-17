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
package org.iconic.ea.strategies.cgp;

import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.cartesian.CartesianChromosome;
import org.iconic.ea.chromosome.cartesian.CartesianChromosomeFactory;
import org.iconic.ea.operator.objective.MonoObjective;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CartesianGeneticProgramming<T extends Comparable<T>>
		extends EvolutionaryAlgorithm<CartesianChromosome<T>, T>{
	public CartesianGeneticProgramming(CartesianChromosomeFactory<T> chromosomeFactory) {
		super(chromosomeFactory);
	}

	@Override
	public void initialisePopulation(int populationSize){
		for (int i = 0; i < populationSize; i++) {
			CartesianChromosome<T> chromosome = getChromosomeFactory().getChromosome();
			getObjective().apply(chromosome);
			getChromosomes().add(chromosome);
		}
	}

	@Override
	public List<CartesianChromosome<T>> evolve(List<CartesianChromosome<T>> population){
		final Comparator<Chromosome<T>> comparator = Comparator.comparing(Chromosome::getFitness);
		final CartesianChromosome<T> bestCandidate = population
				.stream().min(comparator).get();

		population.set(0, bestCandidate);

		for(int populationIndex = 1; populationIndex < population.size(); populationIndex++){
			population.set(populationIndex, mutate(bestCandidate));
		}
		return population;
	}

	private CartesianChromosome<T> mutate(CartesianChromosome<T> chromosome){
		assert (getMutators().size() > 0);
		Objects.requireNonNull(getObjective(), "An objective is required");

		final int lambda = 4;
        final Comparator<Chromosome<T>> comparator = Comparator.comparing(Chromosome::getFitness);

        // Generate a pool of mutants
		List<CartesianChromosome<T>> children = new ArrayList<>(lambda);

		for (int i = 0; i < lambda; ++i) {
			CartesianChromosome<T> child = getMutator(0).apply(
					getChromosomeFactory().getFunctionalPrimitives(),
					chromosome
			);
			getObjective().apply(child);
			children.add(child);
		}

		// Select the best mutant
        CartesianChromosome<T> bestChild = children
                .stream().min(comparator).get();

		// If they're equal to or better than the parent, replace the parent with the mutant
        return (bestChild.getFitness() <= chromosome.getFitness())
                ? bestChild
                : chromosome;
	}
}

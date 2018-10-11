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
package org.iconic.ea.strategies.cgp;

import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.cartesian.CartesianChromosome;
import org.iconic.ea.chromosome.cartesian.CartesianChromosomeFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CartesianGeneticProgramming<T> extends EvolutionaryAlgorithm<CartesianChromosome<T>, T>{
	public final CartesianChromosomeFactory<T> chromosomeFactory;

	public CartesianGeneticProgramming(CartesianChromosomeFactory<T> chromosomeFactory) {
		super();
		this.chromosomeFactory = chromosomeFactory;
	}

	@Override
	public void initialisePopulation(int populationSize){
		for (int i = 0; i < populationSize; i++) {
			CartesianChromosome<T> chromosome = chromosomeFactory.getChromosome();
			getObjective(0).apply(chromosome);
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

	public CartesianChromosome<T> mutate(CartesianChromosome<T> chromosome){
		assert (getMutators().size() > 0);
		assert (getObjectives().size() > 0);
		final int lambda = 4;
        final Comparator<Chromosome<T>> comparator = Comparator.comparing(Chromosome::getFitness);

        // Generate a pool of mutants
		List<CartesianChromosome<T>> children = new ArrayList<>(lambda);

		for (int i = 0; i < lambda; ++i) {
			CartesianChromosome<T> child = getMutator(0).apply(
					chromosomeFactory.getFunctionalPrimitives(),
					chromosome
			);
			getObjective(0).apply(child);
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

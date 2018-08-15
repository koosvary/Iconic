package org.iconic.ea.strategies.cgp;

import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.cartesian.CartesianChromosome;
import org.iconic.ea.chromosome.cartesian.CartesianChromosomeFactory;

import java.util.Comparator;
import java.util.List;

public class CartesianGeneticProgramming<T> extends EvolutionaryAlgorithm<CartesianChromosome<T>, T>{
	public final CartesianChromosomeFactory<T> chromosomeFactory;

	public CartesianGeneticProgramming(CartesianChromosomeFactory<T> chromosomeFactory) {
		super();
		this.chromosomeFactory = chromosomeFactory;
	}

	@Override
	public void initialisePopulation(int populationSize, int numFeatures){
		for (int i = 0; i < populationSize; i++) {
			Chromosome<T> chromosome = chromosomeFactory.getChromosome();
			getObjective(0).apply(chromosome);
			getChromosomes().add((CartesianChromosome<T>) chromosome);
		}
	}

	@Override
	public List<CartesianChromosome<T>> evolve(List<CartesianChromosome<T>> population){
		final Comparator<Chromosome<T>> comparator = Comparator.comparing(Chromosome::getFitness);
		final CartesianChromosome<T> bestCandidate = population
				.stream().min(comparator).get();

		for(int populationIndex = 1; populationIndex < population.size(); populationIndex++){
			CartesianChromosome<T> c = population.get(populationIndex);
			population.set(populationIndex, mutate(c));
		}

		return null;
	}

	public CartesianChromosome<T> mutate(CartesianChromosome<T> chromosome){
		return null;
	}
}

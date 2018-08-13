package org.iconic.ea.strategies.cgp;

import org.iconic.ea.EvolutionaryAlgorithm;
import org.iconic.ea.chromosome.cartesian.CartesianChromosome;
import org.iconic.ea.chromosome.cartesian.CartesianChromosomeFactory;

import java.util.List;

public class CartesianGeneticProgramming<T> extends EvolutionaryAlgorithm<CartesianChromosome<T>, T>{
	public final CartesianChromosomeFactory<T> chromosomeFactory;

	public CartesianGeneticProgramming(CartesianChromosomeFactory<T> chromosomeFactory) {
		super();
		this.chromosomeFactory = chromosomeFactory;
	}

	@Override
	public void initialisePopulation(int populationSize, int numFeatures){

	}

	@Override
	public List<CartesianChromosome<T>> evolve(List<CartesianChromosome<T>> population){
		return null;
	}

	public CartesianChromosome<T> mutate(CartesianChromosome<T> chromosome){
		return null;
	}
}

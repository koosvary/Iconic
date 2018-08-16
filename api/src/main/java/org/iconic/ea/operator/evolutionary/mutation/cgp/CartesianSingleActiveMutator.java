package org.iconic.ea.operator.evolutionary.mutation.cgp;

import org.iconic.ea.chromosome.cartesian.CartesianChromosome;
import org.iconic.ea.operator.evolutionary.mutation.Mutator;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.List;

public class CartesianSingleActiveMutator<R> implements Mutator<CartesianChromosome<R>, R>{
	@Override
	public CartesianChromosome<R> apply(final List<FunctionalPrimitive<R, R>> functionalPrimitives,
										final CartesianChromosome<R> chromosome){
		assert (chromosome.getGenome().size() > 0);
		final CartesianChromosome mutant = chromosome.clone();
		return mutant;
	}
}

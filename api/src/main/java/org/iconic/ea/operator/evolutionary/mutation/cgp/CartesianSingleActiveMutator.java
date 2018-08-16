package org.iconic.ea.operator.evolutionary.mutation.cgp;

import org.iconic.ea.chromosome.cartesian.CartesianChromosome;
import org.iconic.ea.operator.evolutionary.mutation.Mutator;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@inheritDoc}
 * <p>This class implements single active gene mutation which randomly selects genes from the child genotype and mutates them
 * When it has mutated an active gene, it stops mutating and returns the child</p>
 */

public class CartesianSingleActiveMutator<R> implements Mutator<CartesianChromosome<R>, R>{

	/**
	 * <p>Applies the mutation to a copy of the parent</p>
	 *
	 * @param functionalPrimitives The list of functional primitives that this chromosome has available
	 * @param chromosome           The parent chromosome which will be cloned then mutated to create the mutant
	 * @return a mutated copy of the parent chromosome
	 */
	@Override
	public CartesianChromosome<R> apply(final List<FunctionalPrimitive<R, R>> functionalPrimitives,
										final CartesianChromosome<R> chromosome){
		assert (chromosome.getGenome().size() > 0);
		//this creates the soon-to-be mutant from the genome of the parent
		final CartesianChromosome mutant = chromosome.clone();
		final int numFunctions = functionalPrimitives.size();
		//getting the total number of nodes in the genome
		final int numNodes = (mutant.getGenome().size() - mutant.getInputs()) / (mutant.getMaxArity() + 1);
		//this boolean is used to check if an active gene has been mutated or not
		boolean activeNodeMutated = false;
		//the main loop runs until an active gene is mutated
		while(!activeNodeMutated){
			//picking the gene to mutate
			final int mutateNodeIndex = ThreadLocalRandom.current().nextInt(numNodes);
			//finding its index in the genome
			final int index = (mutant.getMaxArity() + 1) * mutateNodeIndex - mutant.getInputs() - mutant.getMaxArity();
			//deciding whether we're going to mutate the gene's function or one of its connections
			final int functionOrConnection = ThreadLocalRandom.current().nextInt(2);
			//0 for function 1 for connection
			if(functionOrConnection == 0){
				//if it's function we just generate a random number within the number of functions
				//the index of the gene will contain the function address
				mutant.getGenome().set(index, ThreadLocalRandom.current().nextInt(numFunctions));
			}
			else{
				//if it's connection we pick a random number with the bound of the arity of this gene's function
				final int connectionToChange = ThreadLocalRandom.current().nextInt(functionalPrimitives.get(index).getArity()) + 1;
				//we then set that connection to a new randomConnection that fits in the constraints of CGP
				mutant.getGenome().set(index+connectionToChange, getRandomConnection(index-mutant.getInputs(), mutant.getRows(), mutant.getLevelsBack(), mutant.getInputs()));
			}
			//then just check if the gene was active and set the boolean appropriately
			activeNodeMutated = isActiveGene(mutant, index);
		}
		return mutant;
	}

	/**
	 * <p>Returns the index to a randomly selected node within the connectivity restraints of the graph</p>
	 *
	 * @param index      The index of the originating node
	 * @param numRows    The number of rows in the graph
	 * @param levelsBack The maximum levels back that the originating node is permitted to connect to
	 * @return the index of a random primitive
	 */
	private int getRandomConnection(int index, int numRows, int levelsBack, int numInputs) {
		final int column = index / numRows;
		final int upperBound = numInputs + column * numRows;

		if (column >= levelsBack) {
			return ThreadLocalRandom.current().nextInt(
					numInputs + (column - levelsBack) * numRows, upperBound
			);
		}

		return ThreadLocalRandom.current().nextInt(0, upperBound);
	}

	/**
	 * <p>Returns the index to a randomly selected node within the connectivity restraints of the graph</p>
	 *
	 * @param mutant	 The chromosome being mutated
	 * @param index      The index of the gene being mutated
	 * @return boolean stating whether or not the gene was active
	 */
	private boolean isActiveGene(CartesianChromosome<R> mutant, int index){
		//get the map of lists of active nodes
		final Map<Integer, List<Integer>> activeNodes = mutant.getPhenome();
		//loop through the map's lists
		for(Integer output : mutant.getOutputs()){
			//if one of the lists contains the index for the gene then return true
			if(activeNodes.get(output).contains(index)){
				return true;
			}
		}
		return false;
	}
}

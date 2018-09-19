package org.iconic.ea.operator.evolutionary.mutation.cgp;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
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
public class CartesianSingleActiveMutator<R> implements Mutator<CartesianChromosome<R>, R> {

    /**
     * {@inheritDoc}
     *
     * <p>A mutant is created by mutating every gene in the chromosome until an active gene is mutated.</p>
     */
    @Override
    public CartesianChromosome<R> apply(final List<FunctionalPrimitive<R, R>> functionalPrimitives,
                                        final CartesianChromosome<R> chromosome) {
        assert (chromosome.getGenome().size() > 0);
        //this creates the soon-to-be mutant from the genome of the parent
        final CartesianChromosome<R> mutant = chromosome.clone();
        final int numFunctions = functionalPrimitives.size();
        //getting the total number of nodes excluding inputs and outputs in the genome
        final int numNodes = mutant.getNumberOfNodes();
        //getting the total number of outputs
        final int numOutputs = mutant.getOutputs().size();
        //this boolean is used to check if an active gene has been mutated or not
        boolean activeNodeMutated = false;
        //probability distribution to select the node to mutate
        UniformIntegerDistribution distribution = new UniformIntegerDistribution(
                mutant.getInputs(), mutant.getInputs() + numNodes + numOutputs - 1
        );

        //the main loop runs until an active gene is mutated
        while (!activeNodeMutated) {
            //picking the gene to mutate
            final int mutateNodeIndex = distribution.sample();

            //check if it's an output
            if (mutateNodeIndex >= numNodes + mutant.getInputs()) {
                //if it's output we pick a random function node
                final int outputToChange = ThreadLocalRandom.current().nextInt(numOutputs);
                final int newConnection = ThreadLocalRandom.current().nextInt(
                        (mutant.getInputs() + mutant.getColumns() * mutant.getRows()) - 1
                );

                mutant.getOutputs().set(outputToChange, newConnection);
                activeNodeMutated = true;
            } else {
                //finding its index in the genome
                final int index = CartesianChromosome.nodeToIndex(
                        mutateNodeIndex, mutant.getInputs(), mutant.getMaxArity()
                );
                //deciding whether we're going to mutate the gene's function, one of its connections,
                // or just mutate an output instead
                final int functionOrConnection = ThreadLocalRandom.current().nextInt(2);
                //0 for function 1 for connection
                if (functionOrConnection == 0) {
                    //if it's function we just generate a random number within the number of functions
                    //the index of the gene will contain the function address
                    final int newPrimitive = ThreadLocalRandom.current().nextInt(numFunctions);
                    mutant.getGenome().set(index, newPrimitive);
                } else {
                    //if it's connection we pick a random number with the bound of the arity of this gene's function
                    // TODO: let unused connections be mutated as well
                    final int connectionToChange = ThreadLocalRandom.current().nextInt(
                            functionalPrimitives.get(
                                    mutant.getGenome().get(index)
                            ).getArity()
                    ) + 1;

                    final int newConnection = getRandomConnection(
                            mutateNodeIndex - mutant.getInputs(),
                            mutant.getRows(),
                            mutant.getLevelsBack(),
                            mutant.getInputs()
                    );

                    // Ensure new connections are valid
                    assert !(newConnection >= mutant.getNumberOfNodes() + mutant.getInputs());

                    //we then set that connection to a new randomConnection that fits in the constraints of CGP
                    mutant.getGenome().set(index + connectionToChange, newConnection);
                }
                //then just check if the gene was active and set the boolean appropriately
                activeNodeMutated = isActiveGene(mutant, index);
            }
        }

        //ensure the mutant gets updated the next time it's evaluated
        mutant.setChanged(true);

        return mutant;
    }

    /**
     * <p>Returns the index to a randomly selected node within the connectivity restraints of the graph</p>
     *
     * @param index      The index of the originating node
     * @param numRows    The number of rows in the graph
     * @param levelsBack The maximum levels back that the originating node is permitted to connect to
     * @param numInputs  The number of inputs
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

        final int connection = ThreadLocalRandom.current().nextInt(0, upperBound);

        return connection;
    }

    /**
     * <p>Returns the index to a randomly selected node within the connectivity restraints of the graph</p>
     *
     * @param mutant The chromosome being mutated
     * @param index  The index of the gene being mutated
     * @return boolean stating whether or not the gene was active
     */
    private boolean isActiveGene(CartesianChromosome<R> mutant, int index) {
        //get the map of lists of active nodes
        final Map<Integer, List<Integer>> activeNodes = mutant.getPhenome();
        //loop through the map's lists
        for (Integer output : mutant.getOutputs()) {
            //if one of the lists contains the index for the gene then return true
            if (activeNodes.get(output).contains(index)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Single Active Gene Mutation";
    }
}

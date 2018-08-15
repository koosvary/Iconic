package org.iconic.ea.chromosome.cartesian;

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.LinearChromosome;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.*;

/**
 * {@inheritDoc}
 * <p>A chromosome that encodes a graph.</p>
 */
public class CartesianChromosome<T> extends Chromosome<T> implements LinearChromosome<Integer>, Cloneable {
    private Map<Integer, List<Integer>> phenomes;
    private List<Integer> outputs;
    private List<Integer> genome;
    private List<FunctionalPrimitive<T, T>> primitives;
    private final int rows;
    private final int columns;
    private final int levelsBack;
    private final int maxArity;

    /**
     * <p>Constructs a new cartesian chromosome with the provided head length, tail length, and number of inputs</p>
     *
     * @param numInputs  The number of inputs that may be expressed by the chromosome
     * @param columns    The number of columns
     * @param rows       The number of rows
     * @param levelsBack The
     */
    public CartesianChromosome(
            List<FunctionalPrimitive<T, T>> primitives,
            int numInputs,
            int columns,
            int rows,
            int levelsBack,
            List<Integer> outputs,
            List<Integer> genome
    ) {
        super(numInputs);
        this.primitives = primitives;
        this.columns = columns;
        this.rows = rows;
        this.levelsBack = levelsBack;

        assert (outputs.size() > 0);

        this.outputs = outputs;
        this.phenomes = new HashMap<>();
        this.genome = genome;

        // Create a comparator for calculating the maximum arity
        final Comparator<FunctionalPrimitive<T, T>> comparator =
                Comparator.comparing(FunctionalPrimitive::getArity);

        // Find the maximum arity
        final Optional<FunctionalPrimitive<T, T>> max = primitives.stream().max(comparator);

        if (max.isPresent()) {
            maxArity = max.get().getArity();
        } else { // If no max value is present something has gone horribly wrong (how'd it even get here?)
            throw new IllegalStateException(
                    "Invalid number of primitives present in chromosome." +
                            "There should be at least one primitive present.");
        }
    }

    /**
     * <p>Generates the output of all active nodes in this chromosome</p>
     */
    protected Map<Integer, T> generateOutput(Map<Integer, List<Integer>> activeNodes, List<Integer> genome,
                                                         int inputs, List<Integer> outputs,
                                                         List<FunctionalPrimitive<T, T>> primitives,
                                                         List<T> samples) {

        Map<Integer, List<T>> out = new HashMap<>();
        Map<Integer, T> results = new HashMap<>();

        // Construct the output of every active node starting from the inputs
        // Todo: skip nodes that don't need to be recalculated
        for (Integer output : outputs) {
            out.put(output, new ArrayList<>(activeNodes.size()));

            // Assign all inputs
            for (int i = 0; i < inputs; ++i) {
                out.get(output).add(samples.get(i));
            }

            // Assign all non-input nodes
            for (int i = 0; i < activeNodes.size(); ++i) {
                // Calculate the node's index in the genome
                final int index = (getMaxArity() + 1) * i - inputs - getMaxArity();
                final int functionGene = genome.get(index);
                final FunctionalPrimitive<T, T> f = primitives.get(functionGene);
                final List<T> params = new ArrayList<>(f.getArity());

                // Add all of the node's required parameters
                for (int j = 0; j < getMaxArity(); ++j) {
                    params.add(
                            out.get(output).get(
                                    genome.get(index + j)
                            )
                    );
                }

                // Calculate the output
                f.apply(params);
            }

            // Store the final output
            final List<T> o = out.get(output);
            results.put(output, o.get(o.size() - 1));
        }

        return results;
    }

    /**
     * <p>Returns the active nodes for each output in the genome, a.k.a. the phenotype</p>
     *
     * @param inputs     The number of inputs
     * @param genome     The genome
     * @param outputs    The outputs
     * @param primitives The primitives used in the genome
     * @return an ordered list of active nodes sorted by their respective output node
     */
    public Map<Integer, List<Integer>> findActiveNodes(int inputs, List<Integer> genome, List<Integer> outputs,
                                                       List<FunctionalPrimitive<T, T>> primitives) {
        final int numNodes = (genome.size() - inputs) / (getMaxArity() + 1);
        final Map<Integer, List<Integer>> activeNodes = new HashMap<>();

        // Find the active nodes for every output and store them in the map
        for (Integer output : outputs) {
            // Create an array to track which nodes are active
            List<Boolean> isActive = new ArrayList<>(numNodes + inputs);

            // Initialise all nodes as inactive
            for (int i = 0; i < numNodes + inputs; ++i) {
                isActive.add(false);
            }

            // Set the output node as active
            isActive.set(output, true);

            // In descending order, go through each non-input node and activate all of its children
            for (int i = (numNodes - 1) + inputs; i >= inputs; --i) {
                // Find the index of the node within the genome
                final int index = (getMaxArity() + 1) * i - inputs - getMaxArity();

                // Only activate its children if the node is active
                if (isActive.get(i)) {
                    final int functionGene = genome.get(index);
                    FunctionalPrimitive<T, T> function = primitives.get(functionGene);

                    // Only activate the number of children required by the function
                    for (int j = 1; j <= function.getArity(); ++j) {
                        final int connection = genome.get(index + j);
                        isActive.set(connection, true);
                    }
                }
            }

            // Add each active node to a list
            activeNodes.put(output, new LinkedList<>());

            for (int i = 0; i < isActive.size(); ++i) {
                if (isActive.get(i)) {
                    activeNodes.get(output).add(i);
                }
            }
        }

        return activeNodes;
    }

    private List<Integer> getOutputs() {
        return outputs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<Integer, T>> evaluate(List<List<T>> input) {
        if (isChanged()) {
            setPhenomes(findActiveNodes(getInputs(), getGenome(), getOutputs(), getPrimitives()));
        }

        List<Map<Integer, T>> calculatedValues = new ArrayList<>(input.size());

        for (final List<T> sample: input) {
            final Map<Integer, T> output = generateOutput(
                    getPhenome(), getGenome(), getInputs(), getOutputs(), getPrimitives(), sample
            );

            calculatedValues.add(output);
        }

        return calculatedValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getPhenome().toString();
    }

    public int getMaxArity() {
        return maxArity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getGenome() {
        return genome;
    }

    /**
     * <p>Sets the genome of this chromosome to the specified value</p>
     *
     * @param genome The new genome of the chromosome
     */
    private void setGenome(List<Integer> genome) {
        this.genome = new LinkedList<>();
        this.genome.addAll(genome);

        setChanged(true);
    }

    public void setPhenomes(Map<Integer, List<Integer>> phenomes) {
        this.phenomes = phenomes;
    }

    public Map<Integer, List<Integer>> getPhenome() {
        return phenomes;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getLevelsBack() {
        return levelsBack;
    }

    public List<FunctionalPrimitive<T, T>> getPrimitives() {
        return primitives;
    }

    public void setPrimitives(List<FunctionalPrimitive<T, T>> primitives) {
        this.primitives = primitives;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CartesianChromosome<T> clone() {
        CartesianChromosome<T> clone = new CartesianChromosome<>(
                getPrimitives(), getInputs(), getColumns(), getRows(), getLevelsBack(), getOutputs(), null
        );

        clone.setGenome(getGenome());

        return clone;
    }
}

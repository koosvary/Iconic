package org.iconic.ea.chromosome.cartesian;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.LinearChromosome;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.data.FeatureClass;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.*;

/**
 * {@inheritDoc}
 * <p>A chromosome that encodes a graph.</p>
 */
@Log4j2
public class CartesianChromosome<T> extends Chromosome<T> implements LinearChromosome<Integer>, Cloneable {
    private Map<Integer, List<Integer>> phenome;
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
        this.phenome = new HashMap<>();
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
     * <p>Returns the number of nodes in this chromosome's genome</p>
     *
     * @return the number of nodes in the chromosome's genome
     */
    public int getNumberOfNodes() {
        return (getGenome().size() - getInputs()) / (getMaxArity() + 1);
    }

    /**
     * @param node
     * @param inputs
     * @param maxArity
     * @return
     */
    static public int nodeToIndex(int node, int inputs, int maxArity) {
        return (node < inputs) ? node : (maxArity + 1) * (node - inputs) + inputs;
    }

    /**
     * <p>Generates the output of all active nodes in this chromosome</p>
     */
    protected Map<Integer, T> generateOutput(Map<Integer, List<Integer>> activeNodes, List<Integer> genome,
                                             int inputs, List<Integer> outputs,
                                             List<FunctionalPrimitive<T, T>> primitives,
                                             List<T> samples) {

        Map<Integer, T> results = new HashMap<>();
        Map<Integer, T> allActiveNodes = new TreeMap<>();

        for (List<Integer> nodes : activeNodes.values()) {
            for (Integer node : nodes) {
                allActiveNodes.put(node, null);
            }
        }

        for (Integer output : outputs) {
            allActiveNodes.put(output, null);
        }

        for (int i = 0; i < inputs; ++i) {
            allActiveNodes.put(i, samples.get(i));
        }

        for (Integer node : allActiveNodes.keySet()) {
            final int index = nodeToIndex(node, inputs, getMaxArity());

            if (allActiveNodes.get(node) == null) {
                final int functionGene = genome.get(index);
                final FunctionalPrimitive<T, T> f = primitives.get(functionGene);
                final List<T> params = new ArrayList<>(f.getArity());

                // Add all of the node's required parameters
                for (int j = 0; j < f.getArity(); ++j) {
                    final int child = genome.get(index + j + 1);
                    params.add(
                            allActiveNodes.get(child)
                    );
                }

                // Calculate the output
                allActiveNodes.put(node, f.apply(params));
            }
        }

        // Store the final output
        for (Integer output : outputs) {
            results.put(output, allActiveNodes.get(output));
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
    public Map<Integer, List<Integer>> getActiveNodes(int inputs, List<Integer> genome, List<Integer> outputs,
                                                      List<FunctionalPrimitive<T, T>> primitives) {
        final int numNodes = getNumberOfNodes();
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
                final int index = nodeToIndex(i, inputs, getMaxArity());

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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<Integer, T>> evaluate(final DataManager<T> dataManager) {
        List<String> headers = dataManager.getSampleHeaders();
        int numSamples = dataManager.getSampleSize();
        List<Map<Integer, T>> calculatedValues = new ArrayList<>(numSamples);

        for (int i = 0; i < numSamples; ++i) {
            List<T> row = new LinkedList<>();

            for (String header : headers) {
                FeatureClass<Number> feature = dataManager.getDataset().get(header);

                row.add(
                        (T) feature.getSampleValue(i)
                );
            }

            final Map<Integer, T> output = generateOutput(
                    getPhenome(), getGenome(), getInputs(), getOutputs(), getPrimitives(), row
            );

            calculatedValues.add(output);
        }

        return calculatedValues;
    }

    /**
     * <p>Returns a human-readable representation of a node in this chromosome</p>
     *
     * @param node       the node to format
     * @param inputs     the number of inputs in the chromosome
     * @param maxArity   the maximum arity of the chromosome
     * @param primitives the primitives available to the chromosome
     * @return a human-readable representation of a node in the chromosome
     */
    private StringBuilder formatNode(final int node, final int inputs, final int maxArity,
                                     final List<FunctionalPrimitive<T, T>> primitives
    ) {
        final StringBuilder outputBuilder = new StringBuilder();
        final int index = nodeToIndex(node, inputs, maxArity);

        // FOr now just print F + the input number for input nodes
        // TODO: change to feature labels
        if (index < inputs) {
            outputBuilder.append("F").append(node);
        }
        // Any other node is treated as a function node
        else {
            final int functionGene = genome.get(index);
            final FunctionalPrimitive<?, ?> primitive = primitives.get(functionGene);
            final int arity = primitive.getArity();
            final String leftBracket = " ( ";
            final String rightBracket = " ) ";
            final String symbol = primitive.getSymbol();

            // Start by printing the symbol and enclosing the parameters with the delimiters
            outputBuilder.append(symbol).append(leftBracket);
            // Only append the number of children that will actually be used
            for (int i = 1; i <= arity; ++i) {
                final int connectionGene = genome.get(index + i);

                // Recursively append its children
                outputBuilder.append(
                        formatNode(connectionGene, inputs, maxArity, primitives)
                );

                // Append a comma, but only if it's not the last parameter
                if (i != arity) {
                    outputBuilder.append(", ");
                }
            }
            outputBuilder.append(rightBracket);
        }
        return outputBuilder;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The string returned is a human-readable representation of this chromosome's phenotype</p>
     */
    @Override
    public String toString() {
        StringBuilder outputBuilder = new StringBuilder();
        log.info(this::getPhenome);

        // For each output
        getOutputs().forEach(output -> {
            // Append its phenotype
            outputBuilder
                    .append("\nOutput = ")
                    .append(formatNode(output, getInputs(), getMaxArity(), getPrimitives()));
        });

        return outputBuilder.toString();
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

    public List<Integer> getOutputs() {
        return outputs;
    }

    /**
     * <p>Sets the outputs of this chromosome to the specified value</p>
     *
     * @param outputs The new outputs of the chromosome
     */
    private void setOutputs(List<Integer> outputs) {
        this.outputs = new LinkedList<>();
        this.outputs.addAll(outputs);

        setChanged(true);
    }

    public void setPhenome(Map<Integer, List<Integer>> phenome) {
        this.phenome = phenome;
    }

    public Map<Integer, List<Integer>> getPhenome() {
        if (isChanged()) {
            setPhenome(getActiveNodes(getInputs(), getGenome(), getOutputs(), getPrimitives()));
        }

        return phenome;
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
        clone.setOutputs(getOutputs());
        clone.setFitness(getFitness());
        clone.setChanged(isChanged());

        return clone;
    }
}

package org.iconic.ea.chromosome.cartesian;

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.LinearChromosome;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.data.FeatureClass;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.*;

/**
 * {@inheritDoc}
 * <p>Cartesian chromosomes encodes a graph and can have different graph dimensions, controlled by
 * the number of columns, rows, and the amount of connectivity between nodes.</p>
 *
 * <p>This implementation of a cartesian chromosome is based on feed-forward Cartesian Genetic Programming.
 * Cycles in the graph are prevented by restricting nodes in the graph such that they can only connect to
 * preceding nodes. The genome is linearly encoded and can support multiple outputs.</p>
 */
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
     * <p>Constructs a new cartesian chromosome with the provided number of inputs, columns, rows, and
     * maximum number of levels back that those nodes may connect to</p>
     *
     * @param numInputs  The number of inputs that may be expressed by the chromosome
     * @param columns    The number of columns in the chromosome
     * @param rows       The number of rows in the chromosome
     * @param levelsBack The maximum number of levels back that nodes may connect to
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
     * <p>Converts the node at the provided position in the graph to an index within the entire genome</p>
     *
     * @param node     the position of the node to produce an index for
     * @param inputs   the number of inputs in the chromosome the node belongs to
     * @param maxArity the maximum arity of the chromosome the node belongs to
     * @return         the index of the node within its genome
     */
    static public int nodeToIndex(int node, int inputs, int maxArity) {
        return (node < inputs) ? node : (maxArity + 1) * (node - inputs) + inputs;
    }


    /**
     * <p>Generates the output of all active nodes in this chromosome</p>
     *
     * @param activeNodes the active nodes in the chromosome to evaluate grouped by output node
     * @param genome      the genome of the chromosome
     * @param inputs      the number of inputs in the chromosome
     * @param outputs     the outputs in the chromosome
     * @param primitives  the primitives available to the chromosome
     * @param samples     the samples to use for generating the output
     * @return a result for every output based on the provided samples
     */
    protected Map<Integer, T> generateOutput(Map<Integer, List<Integer>> activeNodes, List<Integer> genome,
                                             int inputs, List<Integer> outputs,
                                             List<FunctionalPrimitive<T, T>> primitives,
                                             List<T> samples) {

        Map<Integer, T> results = new HashMap<>();
        Map<Integer, T> allActiveNodes = new TreeMap<>();

        // Flatten the map of active nodes as we no longer care what output, each node is associated with.
        // Each key in the new map is the node's position and the value is its evaluated result
        for (List<Integer> nodes : activeNodes.values()) {
            for (Integer node : nodes) {
                allActiveNodes.put(node, null);
            }
        }

        // The chromosome will produce one result for every output
        // all outputs will eventually (must) produce a result
        for (Integer output : outputs) {
            allActiveNodes.put(output, null);
        }

        // Preallocate the map with the input values
        for (int i = 0; i < inputs; ++i) {
            allActiveNodes.put(i, samples.get(i));
        }

        // For every active node calculate its output
        for (Integer node : allActiveNodes.keySet()) {
            final int index = nodeToIndex(node, inputs, getMaxArity());

            // If the node isn't null, don't bother recalculating it
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

                // Calculate the output - this works because we start from the inputs and work our
                // way up to the outputs; the preceding nodes are always calculated first
                allActiveNodes.put(node, f.apply(params));
            }
        }

        // Store the final output in a single list
        for (Integer output : outputs) {
            results.put(output, allActiveNodes.get(output));
        }

        return results;
    }

    /**
     * <p>Returns the active nodes for each output in the genome, a.k.a. the phenotype</p>
     *
     * @param inputs     The number of inputs in the chromosome
     * @param genome     The genome of the chromosome
     * @param outputs    The outputs of the chromosome
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
        // Fetch all labels so we can access each sample by column later on
        final List<String> headers = dataManager.getSampleHeaders();
        final int numSamples = dataManager.getSampleSize();
        // Store the calculates values in a map, where each key is an output node
        final List<Map<Integer, T>> calculatedValues = new ArrayList<>(numSamples);

        // For every sample put together a row and evaluate it
        for (int i = 0; i < numSamples; ++i) {
            List<T> row = new LinkedList<>();

            // Labels *must* be in order for this to work correctly
            for (String header : headers) {
                FeatureClass<Number> feature = dataManager.getDataset().get(header);

                //noinspection unchecked
                row.add(
                        (T) feature.getSampleValue(i)
                );
            }

            // Using the row as input, calculate the output it produces
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

        // For each output
        getOutputs().forEach(output -> {
            // Append its phenotype
            outputBuilder
                    .append("\nOutput = ")
                    .append(formatNode(output, getInputs(), getMaxArity(), getPrimitives()));
        });

        return outputBuilder.toString();
    }

    public String getExpression(){
        Map<Integer, List<Integer>> nodes = getPhenome();
        String[] expressions = new String[genome.size()+getOutputs().size()];
        Set<Integer> keysSet = nodes.keySet();
        Integer[] keys = keysSet.toArray(new Integer[0]);
        List<Integer> actualNodes = null;
        String alph = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for(int i = 0; i < getInputs(); i++){
                expressions[i] =  String.valueOf(alph.charAt(i));
        }
        for(int i = 0; i < getOutputs().size(); i++){
            actualNodes = nodes.get(keys[i]);
            for(int j = 0; j < actualNodes.size(); j++){
                final int index = nodeToIndex(actualNodes.get(j), getInputs(), maxArity);
                if(index < getInputs())
                    continue;
                final int functionGene = genome.get(index);
                final FunctionalPrimitive<?, ?> primitive = primitives.get(functionGene);
                final int arity = primitive.getArity();
                if(arity == 1){
                    final int connectionGene = genome.get(index + 1);
                    expressions[index] = "(" + primitive.getSymbol() + "(" + expressions[nodeToIndex(connectionGene, getInputs(), maxArity)] + "))";
                }
                else{
                    final int fConnectionGene = genome.get(index + 1);
                    final int sConnectionGene = genome.get(index + 2);
                    expressions[index] = "(" + expressions[nodeToIndex(fConnectionGene, getInputs(), maxArity)] + primitive.getSymbol() + expressions[nodeToIndex(sConnectionGene, getInputs(), maxArity)] + ")";
                }
            }
        }
        return expressions[nodeToIndex(actualNodes.get(actualNodes.size()-1), getInputs(), maxArity)];
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

    /**
     * <p>Sets the phenome of this chromosome to the specified value</p>
     *
     * @param phenome The new phenome of the chromosome
     */
    private void setPhenome(Map<Integer, List<Integer>> phenome) {
        this.phenome = phenome;
    }

    /**
     * <p>Returns the phenome of this chromosome</p>
     *
     * @return the phenome of the chromosome
     */
    public Map<Integer, List<Integer>> getPhenome() {
        // Ensure that the most up-to-date phenome is returned
        if (isChanged()) {
            setPhenome(getActiveNodes(getInputs(), getGenome(), getOutputs(), getPrimitives()));
        }

        return phenome;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getGenome() {
        return genome;
    }

    /**
     * <p>Returns the maximum arity of the primitives available to this chromosome</p>
     *
     * @return the maximum arity of the primitive set used by the chromosome
     */
    public int getMaxArity() {
        return maxArity;
    }


    /**
     * <p>Returns the number of outputs used by this chromosome</p>
     *
     * @return the number of outputs used by the chromosome
     */
    public List<Integer> getOutputs() {
        return outputs;
    }

    /**
     * <p>Returns the number of columns used by this chromosome</p>
     *
     * @return the number of columns used by the chromosome
     */
    public int getColumns() {
        return columns;
    }

    /**
     * <p>Returns the number of rows used by this chromosome</p>
     *
     * @return the number of rows used by the chromosome
     */
    public int getRows() {
        return rows;
    }

    /**
     * <p>Returns the maximum number of levels back that nodes in this chromosome may connect to</p>
     *
     * @return the maximum number of levels back used by the chromosome
     */
    public int getLevelsBack() {
        return levelsBack;
    }

    /**
     * <p>Returns the primitives available to this chromosome</p>
     *
     * @return the primitives available to the chromosome
     */
    public List<FunctionalPrimitive<T, T>> getPrimitives() {
        return primitives;
    }

    /**
     * <p>Sets the primitives available to this chromosome to the provided value</p>
     *
     * @param primitives The new primitive set to make available to the chromosome
     */
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

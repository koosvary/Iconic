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

    /**
     * <p>Constructs a new cartesian chromosome with the provided head length, tail length, and number of inputs</p>
     *
     * @param numInputs     The number of inputs that may be expressed by the chromosome
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
    }

    /**
     * <p>Generates the phenotype for all outputs in this chromosome</p>
     */
    protected Map<Integer, List<Integer>> generatePhenotype(List<Integer> genotype, Map<Integer, List<Integer>> activeNodes) {
        // Construct the initial empty phenotype
        for (Integer output : getOutputs()) {
            phenomes.put(output, new LinkedList<>());
        }

        return new HashMap<>();
    }

    public Map<Integer, List<Integer>> findActiveNodes(int numFeatures, List<Integer> genotype, List<Integer> outputs) {
        Map<Integer, List<Integer>> activeNodes = new HashMap<>();

        for (Integer output : outputs) {
            genotype.addAll(outputs);
            // Remove input nodes
            genotype.subList(numFeatures, genotype.size());

            // Create an array to track which nodes are active
            List<Boolean> isActive = new ArrayList<>(genotype.size() - numFeatures);
            // And create another array to place them in later on
            activeNodes.put(output, new ArrayList<>(genotype.size() - numFeatures));

            // Initialise all nodes as unused
            for (int i = 0; i < genotype.size() - numFeatures; ++i) {
                isActive.add(false);
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
    public List<T> evaluate(List<List<T>> input) {
        List<T> calculatedValues = new LinkedList<>();

        if (isChanged()) {
            Map<Integer, List<Integer>> activeNodes = findActiveNodes(getNumFeatures(), getGenome(), getOutputs());
            setPhenomes(generatePhenotype(getGenome(), activeNodes));
        }

        for (List<Integer> output : getPhenomes().values()) {
            for (List<T> row : input) {
//                calculatedValues.add(getRoot().apply(row));
            }
        }

        return calculatedValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "na";
//        return root.toString();
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

    public Map<Integer, List<Integer>> getPhenomes() {
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
                getPrimitives(), getNumFeatures(), getColumns(), getRows(), getLevelsBack(), getOutputs(), null
        );

        clone.setGenome(getGenome());

        return clone;
    }
}

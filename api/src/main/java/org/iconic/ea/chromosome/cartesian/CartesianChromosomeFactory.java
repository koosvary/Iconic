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
package org.iconic.ea.chromosome.cartesian;

import org.iconic.ea.chromosome.ChromosomeFactory;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * {@inheritDoc}
 *
 * <p>Chromosomes constructed by this factory form a graph, where the number of levels back determines
 * the maximum number of columns back that any node in the graph can connect to.</p>
 *
 * @param <T> The type class of the data to pass through the chromosome
 * TODO: incorporate references to J. Miller's textbook
 */
public class CartesianChromosomeFactory<T> extends ChromosomeFactory<CartesianChromosome<T>, T> {
    private final int numInputs;
    private final int numOutputs;
    private final int columns;
    private final int rows;
    private final int levelsBack;

    /**
     * <p>Constructs a new cartesian chromosome factory that constructs cartesian chromosomes with the provided
     * number of outputs, inputs, columns, rows, and levels back</p>
     *
     * @param numOutputs The number of outputs that will be used by the chromosome's constructed by the factory
     * @param numInputs  The number of features that may be expressed by the chromosome's constructed by the factory
     * @param columns    The number of columns that will be used by the chromosome's constructed by the factory
     * @param rows       The number of rows that will be used by the chromosome's constructed by the factory
     * @param levelsBack The number of levels back that will be used by the chromosome's constructed by the factory
     */
    public CartesianChromosomeFactory(int numOutputs, int numInputs, int columns, int rows, int levelsBack) {
        super();

        assert (numOutputs > 0);
        assert (numInputs > 0);
        assert (columns > 0);
        assert (rows > 0);
        assert (levelsBack > 0);

        this.numOutputs = numOutputs;
        this.numInputs = numInputs;
        this.columns = columns;
        this.rows = rows;
        this.levelsBack = levelsBack;
    }

    /**
     * {@inheritDoc}
     */
    public CartesianChromosome<T> getChromosome() {
        final int numPrimitives = getFunctionalPrimitives().size();

        assert (numPrimitives > 0);

        return new CartesianChromosome<>(
                getFunctionalPrimitives(), getNumInputs(), getColumns(), getRows(), getLevelsBack(),
                encodeTail(getNumOutputs(), getNumInputs(), getColumns(), getRows()),
                encodeBody(getNumInputs(), numPrimitives, getColumns(), getRows(), getLevelsBack())
        );
    }

    /**
     * <p>Encodes the tail of the chromosome using the provided values</p>
     *
     * <p>A cartesian chromosome's tail is a list of connection genes, one for each output. A connection gene
     * is an index to a node from within the chromosome's graph.</p>
     *
     * @param numOutputs The number of outputs to encode
     * @param numInputs  The number of inputs to encode
     * @param numColumns The number of columns to encode
     * @param numRows    The number of rows to encode
     * @return the encoded tail of the chromosome
     */
     private List<Integer> encodeTail(int numOutputs, int numInputs, int numColumns, int numRows) {
        List<Integer> outputs = new ArrayList<>(numOutputs);

        for (int i = 0; i < numOutputs; ++i) {
            final int index = ThreadLocalRandom.current().nextInt(getAddressUpperBound(
                    numInputs, numColumns, numRows
            ) - 1);
            outputs.add(index);
        }

        return outputs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addFunction(List<FunctionalPrimitive<T, T>> functions) {
        super.addFunction(functions);

        // Create a comparator for recalculating the maximum arity after we've added all the new functions
        final Comparator<FunctionalPrimitive<T, T>> comparator =
                Comparator.comparing(FunctionalPrimitive::getArity);

        // Find the new maximum arity
        final Optional<FunctionalPrimitive<T, T>> max = getFunctionalPrimitives().stream().max(comparator);

        if (max.isPresent()) {
            setMaxArity(max.get().getArity());
        } else { // If no max value is present something has gone horribly wrong (how'd it even get here?)
            throw new IllegalStateException(
                    "Invalid number of primitives present in chromosome factory." +
                            "There should be at least one primitive present.");
        }
    }

    /**
     * <p>Encodes the body of the chromosome using the provided values</p>
     *
     * <p>A cartesian chromosome's body is a list of input genes, followed by a list of function and connection
     * genes, where each gene is an integer.</p>
     *
     * @param numInputs     The number of inputs to encode
     * @param numPrimitives The number of primitives that are available
     * @param numColumns    The number of columns to encode
     * @param numRows       The number of rows to encode
     * @param levelsBack    The maximum levels back a node is permitted to connect to
     * @return the encoded body of the chromosome
     */
    private List<Integer> encodeBody(int numInputs, int numPrimitives, int numColumns, int numRows, int levelsBack) {
        // Use in-built math operations to prevent overflow/underflow
        final int numGenes =
                Math.multiplyExact(
                        Math.addExact(numInputs, getGraphSize(numColumns, numRows)),
                        Math.addExact(getMaxArity(), 1)
                );

        List<Integer> genome = new ArrayList<>(numGenes);

        // Add inputs
        for (int i = 0; i < numInputs; ++i) {
            genome.add(i);
        }

        // Add nodes
        for (int i = numInputs; i < getAddressUpperBound(numInputs, numColumns, numRows); ++i) {
            final int primitiveGene = getRandomPrimitive(numPrimitives);
            genome.add(primitiveGene);

            // Add connections within the node
            for (int j = 1; j <= getMaxArity(); ++j) {
                final int connectionGene = getRandomConnection(i - numInputs, numRows, levelsBack, numInputs);
                genome.add(connectionGene);
            }
        }
        return genome;
    }

    /**
     * <p>Returns the graph size of chromosomes constructed by this factory</p>
     *
     * <p>The size is determined by multiplying the number of columns by the number of rows.
     * Inputs and outputs aren't counted as part of the graph.</p>
     *
     * @return the graph size of chromosomes constructed by the factory
     */
    private int getGraphSize(int numColumns, int numRows) {
        return numColumns * numRows;
    }

    /**
     * <p>Returns the largest address in the graph that can be connected to for chromosomes constructed by this
     * factory</p>
     *
     * <p>The address upper bound is the graph size plus the number of inputs.</p>
     *
     * @return the largest address in the graph that can be connected to for chromosomes constructed by the factory
     */
    private int getAddressUpperBound(int numInputs, int numColumns, int numRows) {
        return Math.addExact(numInputs, getGraphSize(numColumns, numRows));
    }

    /**
     * <p>Returns the index to a randomly selected primitive from the provided number of options</p>
     *
     * @param numPrimitives The number of primitives available
     * @return the index of a random primitive
     */
    private int getRandomPrimitive(int numPrimitives) {
        assert (numPrimitives > 0);
        return ThreadLocalRandom.current().nextInt(numPrimitives);
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
        final int upperBound = Math.addExact(numInputs, Math.multiplyExact(column, numRows));

        if (column >= levelsBack) {
            return ThreadLocalRandom.current().nextInt(
                    Math.addExact(numInputs, Math.multiplyExact(Math.subtractExact(column, levelsBack), numRows)),
                    upperBound
            );
        }

        return ThreadLocalRandom.current().nextInt(0, upperBound);
    }

    /**
     * <p>
     * Returns the number of outputs supported by chromosomes constructed by this factory.
     * </p>
     *
     * @return the number of outputs supported by chromosomes constructed by the factory
     */
    private int getNumOutputs() {
        return numOutputs;
    }

    /**
     * <p>Returns the number of features that chromosomes constructed by this factory can express</p>
     *
     * @return the number of features that can be expressed by chromosomes constructed by the factory
     */
    private int getNumInputs() {
        return numInputs;
    }

    /**
     * <p>Returns the number of columns that chromosomes constructed by this factory will have</p>
     *
     * @return the number of columns within chromosomes constructed by the factory
     */
    private int getColumns() {
        return columns;
    }

    /**
     * <p>Returns the number of rows that chromosomes constructed by this factory will have</p>
     *
     * @return the number of rows within chromosomes constructed by the factory
     */
    private int getRows() {
        return rows;
    }

    /**
     * <p>Returns the number of levels back that chromosomes constructed by this factory will adhere to</p>
     *
     * @return the number of levels back adhered to by chromosomes constructed by the factory
     */
    private int getLevelsBack() {
        return levelsBack;
    }
}

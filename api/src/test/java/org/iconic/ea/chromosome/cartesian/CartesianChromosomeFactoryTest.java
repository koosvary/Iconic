/**
 * Copyright 2018 Iconic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iconic.ea.chromosome.cartesian;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.operator.primitive.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>
 * A test suite for the {@link CartesianChromosomeFactory} class.
 *
 *
 * <p>
 * This test suite ensures the following:
 * - the factory produces syntactically valid chromosomes
 * - the factory cannot be instantiated under invalid parameter combinations
 *
 */
@Log4j2
class CartesianChromosomeFactoryTest {
    private List<FunctionalPrimitive<Double, Double>> primitives;
    private FunctionalPrimitive<Double, Double> func;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void init() {
        func = mock(FunctionalPrimitive.class);
        // Define our sole mock function
        when(func.getArity()).thenReturn(2);
        when(func.getSymbol()).thenReturn("MOCK");

        //noinspection unchecked
        primitives = new LinkedList<>();
        primitives.add(func);
    }

    @ParameterizedTest
    @MethodSource("badInitialisationTestProvider")
    @DisplayName("Test that a cartesian factory can't be initialised with invalid parameters")
    void badInitialisationTest(int numOutputs, int numInputs, int columns, int rows, int levelsBack) {
        List<String> inputs = new ArrayList<>(numInputs);

        for (int i = 0; i < numInputs; ++i) {
            inputs.add(String.valueOf(i));
        }

        assertThrows(
                AssertionError.class,
                () -> new CartesianChromosomeFactory<>(numOutputs, inputs, columns, rows, levelsBack)
        );
    }

    @ParameterizedTest
    @MethodSource("getChromosomeTestProvider")
    @DisplayName("Test that a cartesian factory produces a well-formed chromosome")
    @Disabled
    void getChromosomeTest(int numOutputs, int numInputs, int columns, int rows, int levelsBack) {
        List<String> inputs = new ArrayList<>(numInputs);

        for (int i = 0; i < numInputs; ++i) {
            inputs.add(String.valueOf(i));
        }

        CartesianChromosomeFactory<Double> supplier = new CartesianChromosomeFactory<>(
                numOutputs, inputs, columns, rows, levelsBack
        );
        supplier.addFunction(primitives);
        CartesianChromosome<Double> c = supplier.getChromosome();

        // Test that the size of the chromosome is correct
        final int numGenes = numInputs + numOutputs +
                (columns * rows) * (supplier.getMaxArity() + 1);

        assertEquals(
                numGenes,
                c.getGenome().size() + c.getOutputs().size(),
                "chromosome's size is equal to the number of nodes plus outputs");

        // Test that all outputs connect to a node inside the graph
        final int numNodes = numInputs + (columns * rows);
        c.getOutputs().parallelStream().forEach(output ->
                assertTrue(
                        output < numNodes && output >= 0,
                        "outputs can only connect to existing nodes in the chromosome"
                )
        );

        // Test that all function genes correspond to an actual function
        for (int i = numInputs; i < numNodes; i += (supplier.getMaxArity() + 1)) {
            final int functionGene = c.getGenome().get(i);
            //noinspection ResultOfMethodCallIgnored
            supplier.getFunctionalPrimitives().get(functionGene);

            // and that all connection genes connect to a node in the graph that abide by the
            // chromosome's connectivity constraints
            for (int j = 1; j < supplier.getMaxArity(); ++j) {
                final int connectionGene = c.getGenome().get(i + j);
                final int column = (i - numInputs) / rows;
                // Calculate the upper bound as the first node in the same column
                final int upperBound = numInputs + column * rows;

                // When the node is in a column greater than or equal to the levels back
                // we need to assert that any connection made doesn't go past the allowed levels back
                if (column >= levelsBack) {
                    assertTrue(
                            connectionGene >= (numInputs + (column - levelsBack)) &&
                                    connectionGene < upperBound,
                            "connection gene: " + (numInputs + (column - levelsBack)) + " <= " +
                                    connectionGene + " < " + upperBound
                    );
                }
                // Otherwise the levels back is greater than the number of columns available,
                // in this case the node can connect to any preceding node
                else {
                    assertTrue(
                            connectionGene >= 0 &&
                                    connectionGene < upperBound,
                            "connection gene: " + 0 + " <= " + connectionGene + " < " +
                                    upperBound
                    );
                }
            }
        }
    }

    /**
     * <p>This test won't be needed after we change the data structure used to store primitives
     */
    @Test
    @DisplayName("Test that the add function method adds functions and correctly recalculates max arity")
    @Deprecated
    @Disabled
    void addFunctionTest() {
        List<String> inputs = new ArrayList<>(1);

        for (int i = 0; i < 1; ++i) {
            inputs.add(String.valueOf(i));
        }

        CartesianChromosomeFactory<Double> factory = new CartesianChromosomeFactory<>(1, inputs, 1, 1, 1);

        assertEquals(factory.getMaxArity(), 0);
        factory.addFunction(primitives);

        assertEquals(factory.getFunctionalPrimitives().size(), 1);
        assertEquals(factory.getFunctionalPrimitives().get(0).getSymbol(), "MOCK");
        assertEquals(factory.getMaxArity(), 2);

        factory.addFunction(primitives);

        assertEquals(factory.getFunctionalPrimitives().size(), 1);

        factory.addFunction(Arrays.asList(
                new Addition(), new Subtraction(), new Multiplication(), new Division(),
                new Power(), new Root(), new Cos(), new Tan()
        ));
        assertEquals(factory.getFunctionalPrimitives().size(), 9);
        assertEquals(factory.getMaxArity(), 2);
    }

    /**
     * <p>Returns an n-tuple of arguments where the first argument is the number of outputs
     * in a chromosome, the second is the number of inputs, the third is the number of columns,
     * the fourth is the number of rows and the fifth is the maximum number of levels back
     * that a connection in the chromosome can be made.
     *
     * <p>Every argument combination should be able to produce a chromosome.
     *
     * @return the number of outputs, inputs, columns, rows, and levels back
     */
    private static Stream<Arguments> getChromosomeTestProvider() {
        return Stream.of(
                // The smallest possible chromosome
                Arguments.of(1, 1, 1, 1, 1),
                // The smallest possible chromosome with more levels back than there are columns
                Arguments.of(1, 1, 1, 1, 2),
                // The chromosome with the largest possible graph dimensions and the minimum levels back
                Arguments.of(1, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, 1),
                // The chromosome with the largest possible graph dimensions and the maximum levels back
                Arguments.of(1, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
                // Multiple outputs - note that a sufficiently large number of outputs or inputs
                // will result in an overflow and as such isn't a valid chromosome
                Arguments.of(2, 1, 1, 1, 1),
                Arguments.of(2, 1, 1, 1, 2),
                Arguments.of(2, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, 1),
                Arguments.of(2, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
                // Multiple inputs
                Arguments.of(1, 2, 1, 1, 1),
                Arguments.of(1, 2, 1, 1, 2),
                Arguments.of(1, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, 1),
                Arguments.of(1, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
                // Multiple inputs and outputs
                Arguments.of(2, 2, 1, 1, 1),
                Arguments.of(2, 2, 1, 1, 2),
                Arguments.of(2, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, 1),
                Arguments.of(2, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
                // These tests wll take longer to evaluate as they dramatically increase
                // the size of the chromosome
                // The smallest possible graph dimensions with an arbitrarily large number of outputs
                Arguments.of(Integer.MAX_VALUE / 100, 1, 1, 1, 1),
                // The smallest possible graph dimensions with an arbitrarily large number of inputs
                Arguments.of(1, Integer.MAX_VALUE / 100, 1, 1, 1)
        );
    }

    private static Stream<Arguments> badInitialisationTestProvider() {
        return Stream.of(
                Arguments.of(0, 1, 1, 1, 1),
                Arguments.of(1, 0, 1, 1, 1),
                Arguments.of(1, 1, 0, 1, 1),
                Arguments.of(1, 1, 1, 0, 1),
                Arguments.of(1, 1, 1, 1, 0)
        );
    }
}
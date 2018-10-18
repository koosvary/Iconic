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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>
 * A test suite for the {@link CartesianChromosome} class.
 * </p>
 *
 * <p>
 * This test suite ensures the following:
 * - the active nodes of chromosomes are correctly evaluated
 * - the output of chromosomes are correctly evaluated
 * - nodes are correctly transformed into indices within the whole genome
 * </p>
 */
@Log4j2
public class CartesianChromosomeTest {

    @ParameterizedTest
    @MethodSource("nodeToIndexTestProvider")
    @DisplayName("Test that the output generated for a cartesian chromosome is correct")
    void nodeToIndexTest(int node, int inputs, int maxArity, int expected) {
        assertEquals(expected, CartesianChromosome.nodeToIndex(node, inputs, maxArity));
    }

    /**
     * <p>Test incomplete</p>
     *
     * @param inputs
     * @param genome
     * @param outputs
     * @param primitives
     */
    @ParameterizedTest
    @MethodSource("activeNodeTestProvider")
    @DisplayName("Test that the actives nodes of a cartesian chromosome can be found")
    void activeTest(int inputs, List<Integer> genome, List<Integer> outputs,
                    List<FunctionalPrimitive<Double, Double>> primitives) {
        // A chromosome is only created to access the class method
        CartesianChromosome<Double> c = getChromosome(inputs, outputs, primitives);
    }

    /**
     * <p>Test incomplete</p>
     *
     * @param inputs
     * @param genome
     * @param outputs
     * @param primitives
     * @param activeNodes
     * @param samples
     * @param expected
     */
    @ParameterizedTest
    @MethodSource("generateOutputTestProvider")
    @DisplayName("Test that the output generated for a cartesian chromosome is correct")
    void outputTest(int inputs, List<Integer> genome,
                    List<Integer> outputs, List<FunctionalPrimitive<Double, Double>> primitives,
                    Map<Integer, List<Integer>> activeNodes,
                    List<Double> samples,
                    Map<Integer, Double> expected
    ) {
        // A chromosome is only created to access the class method
        CartesianChromosome<Double> c = getChromosome(inputs, outputs, primitives);
        c.generateOutput(activeNodes, genome, inputs, outputs, primitives, samples);
    }

    @Disabled
    @Test
    @DisplayName("Test that the expression generated is correct")
    public void getExpressionTest(){
        CartesianChromosome<Double> chrome = new CartesianChromosome<>(Arrays.asList(
                new Addition(), new Subtraction(), new Multiplication(), new Division(),
                new Power(), new Root(), new Sin(), new Cos(), new Tan()
        ),0,0,0,0, Arrays.asList(0), null);
        String expected = "TAN(TAN(((((COS(F9))POW((F0)ROOT(F2)))ADD((F4)MUL(F2)))DIV(COS(F9)))ROOT(SIN(SIN(SIN(F0))))))";
        String result = chrome.getExpression("TAN ( TAN ( ROOT ( DIV ( ADD ( POW ( COS ( F9 ) , ROOT ( F0, F2 )  )" +
                        " , MUL ( F4, F2 )  ) , COS ( F9 )  ) , SIN ( SIN ( SIN ( F0 )  )  )  )  )  ) ",
                Arrays.asList(new Addition(), new Subtraction(), new Multiplication(), new Division(),
                        new Power(), new Root(), new Sin(), new Cos(), new Tan()
                ),true);
        assertEquals(expected, result);
    }

    /**
     * <p>Helper method for fetching a chromosome</p>
     *
     * @param inputs
     * @param outputs
     * @param primitives
     * @return
     */
    private CartesianChromosome<Double> getChromosome(int inputs, List<Integer> outputs, List<FunctionalPrimitive<Double, Double>> primitives) {
        final int columns = 2;
        final int rows = 2;
        final int levelsBack = 2;

        CartesianChromosomeFactory<Double> supplier = new CartesianChromosomeFactory<>(
                outputs.size(), inputs, columns, rows, levelsBack
        );

        supplier.addFunction(primitives);
        return supplier.getChromosome();
    }

    /**
     */
    private static Stream<Arguments> activeNodeTestProvider() {
        List<Integer> genome = new ArrayList<>();
        List<Integer> outputs = new ArrayList<>();
        List<FunctionalPrimitive<Double, Double>> primitives = new ArrayList<>();

        Collections.addAll(genome,
                0, 1, // inputs
                0, 0, 0,        // n = 2
                1, 0, 1,        // n = 3
                4, 2, 3,        // n = 4
                3, 1, 3        // n = 5
        );

        Collections.addAll(outputs, 4, 2, 5);

        Collections.addAll(primitives,
                new Addition(), new Subtraction(), new Multiplication(), new Division(), new Sin()
        );

        // Number of inputs, genome, outputs, primitives
        return Stream.of(Arguments.of(2, genome, outputs, primitives));
    }


    private static Stream<Arguments> nodeToIndexTestProvider() {
        // Node, number of inputs, max arity, expected
        return Stream.of(
                Arguments.of(1, 1, 2, 1),
                Arguments.of(1, 2, 2, 1),
                Arguments.of(3, 2, 2, 5),
                Arguments.of(2, 2, 2, 2)
        );
    }

    /**
     */
    private static Stream<Arguments> generateOutputTestProvider() {
        List<Integer> genome = new ArrayList<>();
        List<Integer> outputs = new ArrayList<>();
        List<FunctionalPrimitive<Double, Double>> primitives = new ArrayList<>();

        Collections.addAll(genome,
                0, 1,  // inputs
                0, 0, 0,        // n = 2
                1, 0, 1,        // n = 3
                4, 2, 3,        // n = 4
                3, 1, 3         // n = 5
        );

        Collections.addAll(outputs, 4, 2, 5);

        Collections.addAll(primitives,
                new Addition(), new Subtraction(), new Multiplication(), new Division(), new Sin()
        );

        List<Double> samples = new LinkedList<>();
        Collections.addAll(samples, 1., 0., 5.);

        Map<Integer, List<Integer>> activeNodes = new HashMap<>();
        activeNodes.put(4, new ArrayList<>());
        activeNodes.put(2, new ArrayList<>());
        activeNodes.put(5, new ArrayList<>());
        Collections.addAll(activeNodes.get(4), 0, 2, 4);
        Collections.addAll(activeNodes.get(2), 0, 2);
        Collections.addAll(activeNodes.get(5), 0, 1, 3, 5);

        Map<Integer, Double> expected = new HashMap<>();
        expected.put(4, 0.9093); // sin(add(1, 1))
        expected.put(2, 2.); // add(1, 1)
        expected.put(5, 0.); // div(0, sub(1, 0))

        // Number of inputs, genome, outputs, primitives, samples, expected
        return Stream.of(Arguments.of(2, genome, outputs, primitives, activeNodes, samples, expected));
    }

    @Disabled
    @RepeatedTest(10000)
    @DisplayName("Brute force testing of findActiveNodes")
    void findActiveNodesBruteForceTest() {
        List<FunctionalPrimitive<Double, Double>> primitives = new ArrayList<>();
        primitives.add(new Addition());
        primitives.add(new Subtraction());
        primitives.add(new Sin());

        CartesianChromosomeFactory<Double> supplier = new CartesianChromosomeFactory<>(
                3, 10, 10, 10, 10
        );
        supplier.addFunction(primitives);

        final CartesianChromosome<Double> c = supplier.getChromosome();
        c.getActiveNodes(c.getInputs(), c.getGenome(), c.getOutputs(), primitives);
    }
}

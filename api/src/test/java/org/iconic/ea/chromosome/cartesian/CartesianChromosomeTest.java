package org.iconic.ea.chromosome.cartesian;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.operator.primitive.*;
import org.junit.jupiter.api.DisplayName;
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
}

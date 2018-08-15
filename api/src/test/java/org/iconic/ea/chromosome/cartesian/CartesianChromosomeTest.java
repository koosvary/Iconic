package org.iconic.ea.chromosome.cartesian;

import lombok.extern.log4j.Log4j2;
import org.iconic.ea.operator.primitive.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * <p>
 * A test suite for the {@link CartesianChromosome} class.
 * </p>
 *
 * <p>
 * This test suite ensures the following:
 *  - the active nodes of chromosomes are correctly evaluated
 *  - the output of chromosomes are correctly evaluated
 * </p>
 */
@Log4j2
public class CartesianChromosomeTest {
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
        log.info("{}{}", genome, outputs);
        log.info(c.findActiveNodes(inputs, genome, outputs, primitives));
    }

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
        log.info(expected);
        log.info(c.generateOutput(activeNodes, genome, inputs, outputs, primitives, samples));
    }

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

    /**
     */
    private static Stream<Arguments> generateOutputTestProvider() {
        List<Integer> genome = new ArrayList<>();
        List<Integer> outputs = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
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
        return Stream.of(Arguments.of(2, genome, outputs, primitives, expected));
    }
}

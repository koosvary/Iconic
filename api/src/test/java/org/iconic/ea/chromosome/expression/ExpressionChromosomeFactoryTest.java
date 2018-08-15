package org.iconic.ea.chromosome.expression;

import org.iconic.ea.operator.primitive.Addition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * A test suite for the {@link ExpressionChromosomeFactory} class.
 * </p>
 *
 * <p>
 * This test suite ensures the following:
 * - chromosomes are always produced with the same parameters as its parent factory
 * - factories can't be instantiated with an invalid combination of parameters
 * </p>
 */
public class ExpressionChromosomeFactoryTest {
    @ParameterizedTest
    @MethodSource("initialisationTestProvider")
    @DisplayName("Test that an expression factory can't be initialised with invalid parameters")
    void intialisationTest(final int headLength, final int numFeatures) {
        assertThrows(
                AssertionError.class,
                () -> new ExpressionChromosomeFactory<>(headLength, numFeatures)
        );
    }

    @ParameterizedTest
    @MethodSource("parameterTestProvider")
    @DisplayName("Test that an expression factory constructs an expression chromosome with the right parameters")
    void parameterTest(final int headLength, final int numFeatures) {
        ExpressionChromosomeFactory<Double> supplier = new ExpressionChromosomeFactory<>(headLength, numFeatures);
        // Needs to be mocked
        supplier.addFunction(Arrays.asList(
                new Addition()
        ));

        ExpressionChromosome<Double> c = supplier.getChromosome();

        // Chromosomes produced by the factory should always have the same parameters as the factory
        assertAll("parameters",
                () -> assertEquals(c.getHeadLength(), headLength),
                () -> assertEquals(c.getNumFeatures(), numFeatures)
        );
    }

    /**
     * <p>
     * Returns a stream of integer tuples, where each tuple contains an invalid combination of a
     * head length and number of features.
     * </p>
     *
     * @return a stream of integer tuples
     */
    private static Stream<Arguments> initialisationTestProvider() {
        return Stream.of(
                Arguments.of(0, 0),
                Arguments.of(0, 1),
                Arguments.of(1, 0),
                Arguments.of(-1, 1),
                Arguments.of(1, -1),
                Arguments.of(-1, -1)
        );
    }

    /**
     * <p>
     * Returns a stream of integer tuples, where each tuple contains a head length and number of features.
     * </p>
     *
     * @return a stream of integer tuples
     */
    private static Stream<Arguments> parameterTestProvider() {
        return Stream.of(
                Arguments.of(1, 1),
                Arguments.of(2, 1),
                Arguments.of(1, 2),
                Arguments.of(10, 10)
        );
    }
}

package org.iconic.ea.operator.primitive;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for {@link GaussianFunction}
 * @author Jack Newley
 */

public class GaussianFunctionTest {


    @DisplayName("Test GaussianFunction using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void addDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> gaussianFunction = new GaussianFunction();
        final double delta = 0.001d;
        final double actual = gaussianFunction.apply(args);

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>
     * Returns a stream of double n-tuples, where the last member of the tuple is the value of a simple Gaussian function
     * (ie exp(-x^2))
     * </p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(2.0), 0.01831),
                Arguments.of(Arrays.asList(-2.0), 0.01831),
                Arguments.of(Arrays.asList(1.0), 0.36787),
                Arguments.of(Arrays.asList(-1.0), 0.36787),
                Arguments.of(Arrays.asList(0.0), 1.0)
        );
    }
}

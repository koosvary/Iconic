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
 * Test class for {@link LogisticFunction}
 * @author Jack Newley
 */

public class LogisticFunctionTest {


    @DisplayName("Test LogisticFunction using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void addDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> logisticFunction = new LogisticFunction();
        final double delta = 0.001d;
        final double actual = logisticFunction.apply(args);

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>
     * Returns a stream of double n-tuples, performs simple logistic function
     * (ie 1/(1+Math.exp(x)))
     * </p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(Double.NEGATIVE_INFINITY), 1.0),
                Arguments.of(Arrays.asList(Double.POSITIVE_INFINITY), 0.0),
                Arguments.of(Arrays.asList(1.0), 0.26894),
                Arguments.of(Arrays.asList(-1.0), 0.73105),
                Arguments.of(Arrays.asList(0.0), 0.5)
        );
    }
}

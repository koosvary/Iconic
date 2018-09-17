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
 * Test class for {@link Exponential}
 * @author Jack Newley
 */

public class ExponentialTest {


    @DisplayName("Test Exponential using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void addDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> exponential = new Exponential();
        final double delta = 0.001d;
        final double actual = exponential.apply(args);

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>
     * Returns a stream of double n-tuples, where the last member of the tuple is the value of exponential
     * of the operand, (ie e^x)
     * </p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(-1.0), 0.36787),
                Arguments.of(Arrays.asList(1.0), 2.71828),
                Arguments.of(Arrays.asList(2.0), 7.38905)
        );
    }
}

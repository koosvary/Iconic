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
 * Test class for {@link SquareRoot}
 *
 * @author Jack Newley
 */

public class SquareRootTest {
    @DisplayName("Test SquareRoot using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void addDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> squareRoot = new SquareRoot();
        final double delta = 0.001d;
        final double actual = squareRoot.apply(args);
        assertEquals(expected, actual, delta);
    }

    /**
     * <p>
     * Returns a stream of double n-tuples, Evaluates to the squareroot of the operand
     * </p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(-2.d), Double.NaN),
                Arguments.of(Arrays.asList(0.d), 0.d),
                Arguments.of(Arrays.asList(16.d), 4.d)

        );
    }
}

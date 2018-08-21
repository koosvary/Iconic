package org.iconic.ea.operator.primitive;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Double.NEGATIVE_INFINITY;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for {@link Not}
 *
 * @author Jack Newley
 */

public class NotTest {
    @DisplayName("Test Not using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void addDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> not = new Not();
        final double delta = 0.001d;
        final double actual = not.apply(args);
        assertEquals(expected, actual, delta);
    }

    /**
     * <p>
     * Returns a stream of double n-tuples, Last member of tuple is 0 if first argument is greater than 0,
     * 1 otherwise
     * </p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Collections.singletonList(2.d), 0.d),
                Arguments.of(Collections.singletonList(-1.d), 1.d),
                Arguments.of(Collections.singletonList(0.d), 1.d)

        );
    }
}
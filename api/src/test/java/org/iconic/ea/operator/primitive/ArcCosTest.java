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
 * Test class for {@link org.iconic.ea.operator.primitive.ArcCos}
 * @author Jack Newley
 */

public class ArcCosTest {

    private static final double PI = 3.14159;

    @DisplayName("Test ArcCos using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void addDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> arcCos = new ArcCos();
        final double delta = 0.001d;
        final double actual = arcCos.apply(args);

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>
     * Returns a stream of double n-tuples, where the last member of the tuple is the value of ArcCos
     * of the operand
     * </p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(1.0), 0.0),
                Arguments.of(Arrays.asList(0.70711), PI/4),
                Arguments.of(Arrays.asList(0.0), PI/2),
                Arguments.of(Arrays.asList(-0.70711), 3*PI/4),
                Arguments.of(Arrays.asList(-1.0), PI)
        );
    }
}

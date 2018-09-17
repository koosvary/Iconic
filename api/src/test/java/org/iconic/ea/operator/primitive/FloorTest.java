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
 * Test class for {@link Floor}
 * @author Jack Newley
 */

public class FloorTest {


    @DisplayName("Test Floor using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void addDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> floor = new Floor();
        final double delta = 0.001d;
        final double actual = floor.apply(args);

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>
     * Returns a stream of double n-tuples, where the last member of the tuple is the value of Floor
     * of the operand
     * </p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(1.0), 1.0),
                Arguments.of(Arrays.asList(1.01), 1.0),
                Arguments.of(Arrays.asList(-1.2), -2.0)
        );
    }
}

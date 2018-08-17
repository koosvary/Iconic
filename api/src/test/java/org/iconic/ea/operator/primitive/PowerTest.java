package org.iconic.ea.operator.primitive;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link org.iconic.ea.operator.primitive.Power}
 * @author Scott Walker
 */
class PowerTest {
    @DisplayName("Test powers using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void powerDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> power = new Power();
        final double delta = 0.001;
        final double actual = power.apply(args);

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>Returns a stream of double n-tuples, where the last number is the first raised to the power
     * of the second</p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(1.0, 1.0), 1.0),
                Arguments.of(Arrays.asList(100.0, 0.0), 1.0),
                Arguments.of(Arrays.asList(2.0, 2.0), 4.0),
                Arguments.of(Arrays.asList(32.0, 2.0), 1024.0),
                Arguments.of(Arrays.asList(10.0, 4.0), 10000.0),
                Arguments.of(Arrays.asList(-2.0, 2.0), 4.0),
                Arguments.of(Arrays.asList(-2.0, 3.0), -8.0)
        );
    }
}
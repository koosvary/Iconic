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
 * Test class for {@link org.iconic.ea.operator.primitive.Division}
 * @author Jasbir Shah
 */
class DivisionTest {
    @DisplayName("Test division using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void divideDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> division = new Division();
        final double delta = 0.001;
        final double actual = division.apply(args);

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>Returns a stream of double n-tuples, where the last member is the result of dividing
     * the first by the second</p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(1.0, 1.0), 1.0),
                Arguments.of(Arrays.asList(1.0, -1.0), -1.0),
                Arguments.of(Arrays.asList(0.0, -1.0), 0.0),
                Arguments.of(Arrays.asList(0.0, 1.0), 0.0),
                Arguments.of(Arrays.asList(1.0, 0.0), 1.0),
                Arguments.of(Arrays.asList(1024.0, 512.0), 2.0)
        );
    }
}

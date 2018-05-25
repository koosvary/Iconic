package org.iconic.ea.operator.primitive;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtractionTest {
    @DisplayName("Test subtraction using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void addDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> subtract = new Subtraction();
        final double delta = 0.001d;
        final double actual = subtract.apply(args);

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>
     * Returns a stream of double n-tuples, where the last member of the tuple is the sum of all the preceeding
     * members
     * </p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(1.d, 1.d), 0.d),
                Arguments.of(Arrays.asList(1.d, -1.d), 2.d),
                Arguments.of(Arrays.asList(0.d, -1.d), 1.d),
                Arguments.of(Arrays.asList(0.d, 1.d), -1.d),
                Arguments.of(Arrays.asList(87.d, 27.d, 9.8d, 4.d, 28.d, 93.d, 92.d, 63.d, 55.d, 30.d, -105.3d), -209.5d)
        );
    }
}

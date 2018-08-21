package org.iconic.ea.operator.primitive;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RootTest {
    @DisplayName("Test root using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void addDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> add = new Root();
        final double delta = 0.001d;
        final double actual = add.apply(args);
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
                Arguments.of(Arrays.asList(1.d, 1.d), 1.d)
//                Arguments.of(Arrays.asList(27.d, 3.d), 3.d),
//                Arguments.of(Arrays.asList(-2.d, -2.d), 1.d),
//                Arguments.of(Arrays.asList(2.d, -2.d), 1.d),
//                Arguments.of(Arrays.asList(-2.d, 2.d), NaN),
//                Arguments.of(Arrays.asList(2.d, 2.d), 1.4142135623731)

        );
    }
}

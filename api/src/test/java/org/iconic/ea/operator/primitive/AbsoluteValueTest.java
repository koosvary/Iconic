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
 * Test class for {@link org.iconic.ea.operator.primitive.AbsoluteValue}
 * @author Jack Newle
 */

public class AbsoluteValueTest {
    @DisplayName("Test absolute value using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void addDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> absoluteValue = new AbsoluteValue();
        final double delta = 0.001d;
        final double actual = absoluteValue.apply(args);

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>
     * Returns a stream of double n-tuples, where the last member of the tuple is the absolute value of the proceeding
     * member
     * </p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(1.d), 1.d),
                Arguments.of(Arrays.asList(-1.d), 1.d),
                Arguments.of(Arrays.asList(0.d), 0.d)
        );
    }
}

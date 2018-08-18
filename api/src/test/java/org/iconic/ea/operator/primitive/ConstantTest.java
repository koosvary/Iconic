package org.iconic.ea.operator.primitive;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link org.iconic.ea.operator.primitive.Constant}
 * @author Scott Walker
 */
class ConstantTest {
    @DisplayName("Test constants using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void constantDoublesTest(final double arg, final double expected) {
        final FunctionalPrimitive<Double, Double> constant = new Constant<>(arg);
        final double delta = 0.001;
        final double actual = constant.apply(new ArrayList<>());

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>Returns a stream of two identical doubles</p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(1.0, 1.0),
                Arguments.of(0.0, 0.0),
                Arguments.of(10.0, 10.0)
        );
    }
}
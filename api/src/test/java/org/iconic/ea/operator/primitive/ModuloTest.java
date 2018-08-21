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
 * Test class for {@link Modulo}
 *
 * @author Jack Newley
 */

public class ModuloTest {


    @DisplayName("Test Modulo using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void addDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> modulo = new Modulo();
        final double delta = 0.001d;
        final double actual = modulo.apply(args);

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>
     * Returns a stream of double n-tuples, last in tuple is the modulo of first 2 arguments
     * </p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(4.0, 2.0), 0.0),
                Arguments.of(Arrays.asList(3.0, 2.0), 1.0),
                Arguments.of(Arrays.asList(1.0, -1.0), 0.0)
        );
    }
}

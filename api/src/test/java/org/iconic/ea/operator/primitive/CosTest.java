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
 * Test class for {@link org.iconic.ea.operator.primitive.Cos}
 * @author Scott Walker
 */
class CosTest {

    /** Hard-coded value of pi for testing purposes */
    private static final double PI = 3.14159;

    @DisplayName("Test cosine using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void cosDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> cos = new Cos();
        final double delta = 0.001;
        final double actual = cos.apply(args);

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>Returns a stream of double n-tuples, where the last number cos of the first</p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(0.0), 1.0),
                Arguments.of(Arrays.asList(PI/2), 0.0),
                Arguments.of(Arrays.asList(PI), -1.0),
                Arguments.of(Arrays.asList(-PI/2), 0.0),
                Arguments.of(Arrays.asList(PI/4), 0.70711),
                Arguments.of(Arrays.asList(-PI/4), 0.70711)
        );
    }
}
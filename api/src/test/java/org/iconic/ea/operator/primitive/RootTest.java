package org.iconic.ea.operator.primitive;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link org.iconic.ea.operator.primitive.Root}
 * @author Scott Walker
 */
@Disabled
class RootTest {
    @DisplayName("Test root using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void rootDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> root = new Root();
        final double delta = 0.001;
        final double actual = root.apply(args);

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>Returns a stream of double n-tuples, where the last number is the first raised to the inverse power
     * of the second (the nth root)</p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(1.0, 1.0), 1.0),
                Arguments.of(Arrays.asList(100.0, 1.0), 100.0),
                Arguments.of(Arrays.asList(4.0, 2.0), 2.0),
                Arguments.of(Arrays.asList(1024.0, 2.0), 32.0),
                Arguments.of(Arrays.asList(1024.0, 10.0), 2.0),
                Arguments.of(Arrays.asList(-8.0, 3.0), -2.0),
                Arguments.of(Arrays.asList(8.0, -3.0), 0.5)
        );
    }
}
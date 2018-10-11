/**
 * Copyright (C) 2018 Iconic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
 * Test class for {@link TwoArcTan}
 *
 * @author Jack Newley
 */

public class TwoArcTanTest {

    private static final double PI = 3.14159;

    @DisplayName("Test TWoArcTan using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void subtractDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> twoArcTan = new TwoArcTan();
        final double delta = 0.001d;
        final double actual = twoArcTan.apply(args);

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>
     * Returns a stream of double n-tuples, returns arctan2(x,y), atan but with the angle from the vector made by x,y
     * </p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(0.d, 0.d), 0.d),
                Arguments.of(Arrays.asList(0.d, 1.d), 0.d),
                Arguments.of(Arrays.asList(1.d, 1.d), PI / 4),
                Arguments.of(Arrays.asList(1.d, 0.d), PI / 2),
                Arguments.of(Arrays.asList(1.d, -1.d), 3 * PI / 4),
                Arguments.of(Arrays.asList(0.d, -1.d), PI),
                Arguments.of(Arrays.asList(-1.d, -1.d), -3 * PI / 4),
                Arguments.of(Arrays.asList(-1.d, 0.d), -PI / 2),
                Arguments.of(Arrays.asList(-1.d, 1.d), -PI / 4)
        );
    }
}

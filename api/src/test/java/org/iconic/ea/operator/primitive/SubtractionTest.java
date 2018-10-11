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
 * Test class for {@link org.iconic.ea.operator.primitive.Subtraction}
 * @author Jasbir Shah
 */
class SubtractionTest {
    @DisplayName("Test subtraction using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void subtractDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> subtract = new Subtraction();
        final double delta = 0.001;
        final double actual = subtract.apply(args);

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>Returns a stream of double n-tuples, where the last member is the subraction of
     * all numbers in the list (a - b - c - d - ...)</p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(1.0, 1.0), 0.0),
                Arguments.of(Arrays.asList(1.0, -1.0), 2.0),
                Arguments.of(Arrays.asList(0.0, -1.0), 1.0),
                Arguments.of(Arrays.asList(0.0, 1.0), -1.0),
                Arguments.of(Arrays.asList(87.0, 27.0, 9.8, 4.0, 28.0, 93.0, 92.0, 63.0, 55.0, 30.0, -105.3), -209.5d),
                Arguments.of(Arrays.asList(10.0, 1.0, 2.0, 3.0, 4.0), 0.0)
        );
    }
}

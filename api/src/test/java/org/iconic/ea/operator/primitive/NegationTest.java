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

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for {@link Negation}
 *
 * @author Jack Newley
 */

public class NegationTest {
    @DisplayName("Test Negation using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void addDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> negation = new Negation();
        final double delta = 0.001d;
        final double actual = negation.apply(args);
        assertEquals(expected, actual, delta);
    }

    /**
     * <p>
     * Returns a stream of double n-tuples, where the last member of the tuple is the natural log of the first argument
     * </p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Collections.singletonList(2.d), -2.d),
                Arguments.of(Collections.singletonList(Double.POSITIVE_INFINITY), NEGATIVE_INFINITY),
                Arguments.of(Collections.singletonList(-1.d), 1.d)

        );
    }
}

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
package org.iconic.ea.operator.objective.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link org.iconic.ea.operator.objective.error.MeanSquaredError}
 * @author Scott Walker
 */
class MeanSquaredErrorTest {
    @DisplayName("Test mean squared error")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void testMeanSquaredError(final double result, final List<Double> actual, final List<Double> expected) {
        final ErrorFunction mse = new MeanSquaredError();
        final double error = mse.apply(actual, expected);
        final double delta = 0.00001;

        assertEquals(result, error, delta);
    }

    /**
     * <p>Returns a stream of two lists of doubles actual and expected, as well as the result</p>
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(0.0, Arrays.asList(1.0), Arrays.asList(1.0)),
                Arguments.of(16.0, Arrays.asList(1.0), Arrays.asList(5.0)),
                Arguments.of(4.0, Arrays.asList(1.0), Arrays.asList(-1.0)),
                Arguments.of(2.0, Arrays.asList(1.0, 3.0), Arrays.asList(-1.0, 3.0)),
                Arguments.of(0.0, Arrays.asList(1.0, 2.0, 3.0), Arrays.asList(1.0, 2.0, 3.0)),
                Arguments.of(1.25, Arrays.asList(1.0, 2.0, 3.0, 4.0), Arrays.asList(1.0, 4.0, 4.0, 4.0)),
                Arguments.of(7.5, Arrays.asList(1.0, 2.0, 3.0, 4.0), Arrays.asList(2.0, 4.0, 6.0, 8.0))
        );
    }
}
/**
 * Copyright 2018 Iconic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
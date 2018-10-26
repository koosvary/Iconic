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
     * all numbers in the list (a - b - c - d - ...)
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

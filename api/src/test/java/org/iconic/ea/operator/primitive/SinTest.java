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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link org.iconic.ea.operator.primitive.Sin}
 * @author Scott Walker
 */
class SinTest {

    /** Hard-coded value of pi for testing purposes */
    private static final double PI = 3.14159;

    @DisplayName("Test sine using doubles")
    @MethodSource("doubleListProvider")
    @ParameterizedTest
    void sinDoublesTest(final List<Double> args, final double expected) {
        final FunctionalPrimitive<Double, Double> sin = new Sin();
        final double delta = 0.001;
        final double actual = sin.apply(args);

        assertEquals(expected, actual, delta);
    }

    /**
     * <p>Returns a stream of double n-tuples, where the last number sin of the first</p>
     *
     * @return a stream of double n-tuples
     */
    private static Stream<Arguments> doubleListProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(0.0), 0.0),
                Arguments.of(Arrays.asList(PI/2), 1.0),
                Arguments.of(Arrays.asList(PI), 0.0),
                Arguments.of(Arrays.asList(-PI/2), -1.0),
                Arguments.of(Arrays.asList(PI/4), 0.70711),
                Arguments.of(Arrays.asList(-PI/4), -0.70711)
        );
    }
}
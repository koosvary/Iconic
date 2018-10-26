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
package org.iconic.ea.data.preprocessing;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link org.iconic.ea.data.preprocessing.Offset}
 * @author Scott Walker
 */
@Disabled // Disabled temporarily until offset is improved
class OffsetTest {
    @DisplayName("Test offsets")
    @MethodSource("offsetProvider")
    @ParameterizedTest
    void testOffset(final ArrayList<Number> input, final ArrayList<Number> expected, final double offsetValue) {
        final double delta = 0.00001;
        Offset offset = new Offset(offsetValue);
        offset.apply(input);

        assertEquals(input.size(), expected.size());
        for (int i = 0; i < input.size(); i++) {
            assertEquals(input.get(i).doubleValue(), expected.get(i).doubleValue(), delta);
        }
    }

    /**
     * <p>Get the list of inputs for testOffset
     * @return Stream of arguments, two lists and the offset
     */
    private static Stream<Arguments> offsetProvider() {
        return Stream.of(
                Arguments.of(Arrays.asList(1.0), Arrays.asList(1.0), 0.0),
                Arguments.of(Arrays.asList(1.0, 5.0), Arrays.asList(1.0, 5.0), 0.0),
                Arguments.of(Arrays.asList(1.0, 10.0), Arrays.asList(4.0, 13.0), 3.0),
                Arguments.of(Arrays.asList(1.0, 10.0, 100.0), Arrays.asList(-1.5, 7.5, 97.5), -2.5)
        );
    }
}
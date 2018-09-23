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
     * <p>Get the list of inputs for testOffset</p>
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
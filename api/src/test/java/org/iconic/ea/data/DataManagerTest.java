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
package org.iconic.ea.data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DataManagerTest {

    /** Test file */
    private static final String TEST_FILE_HEADER    = "Iris-Header.txt";
    /** Same test file without the header row */
    private static final String TEST_FILE_NO_HEADER = "Iris-NoHeader.txt";

    /** No. samples in test file */
    private static final int SAMPLES = 37;
    /** No. features in test file */
    private static final int FEATURES = 5;

    /** DM for file with header */
    private static DataManager<Integer> dataManagerHeader;
    /** DM for file without header */
    private static DataManager<Integer> dataManagerNoHeader;
    /** Generic DM (Will be set randomly) */
    private static DataManager<Integer> dataManager;

    /**
     * Run once, before any of the tests start
     */
    @BeforeAll
    static void setUp() {
        dataManagerHeader   = new DataManager<>(TEST_FILE_HEADER);
        dataManagerNoHeader = new DataManager<>(TEST_FILE_NO_HEADER);
        // Set to either of the above two, shouldn't matter.
        dataManager = (new Random().nextInt(2) == 0) ? dataManagerHeader : dataManagerNoHeader;
    }

    @DisplayName("Test conversion of integers to Excel-style headers")
    @MethodSource("intToHeaderProvider")
    @ParameterizedTest
    void testIntToHeader(int num, String expected) {
        assertEquals(expected, dataManager.intToHeader(num));
    }

    @DisplayName("Test the header values are correct")
    @Test
    void testHeaderValues() {
        List<String> supplied = dataManagerHeader.getSampleHeaders();
        List<String> converted = dataManagerNoHeader.getSampleHeaders();
        assertAll("Header checks",
                () -> assertEquals("Sepal Length", supplied.get(0)),
                () -> assertEquals("Class", supplied.get(4)),
                () -> assertEquals("A", converted.get(0)),
                () -> assertEquals("E", converted.get(4))
        );
    }

    @DisplayName("Assert that all sizes match the input files")
    @Test
    void testSizes() {
        assertAll("All sizes",
                () -> assertEquals(SAMPLES, dataManagerHeader.getSampleSize()),
                () -> assertEquals(FEATURES, dataManagerHeader.getFeatureSize()),
                () -> assertEquals(SAMPLES, dataManagerNoHeader.getSampleSize()),
                () -> assertEquals(FEATURES, dataManagerNoHeader.getFeatureSize()),
                // Only need to test one of the data managers from here on
                () -> assertEquals(FEATURES, dataManager.getDataset().size()),
                () -> {
                    for (int i = 0; i < FEATURES; i++) {
                        assertEquals(SAMPLES, dataManager.getSampleColumn(i).size());
                    }
                },
                () -> {
                    for (int i = 0; i < SAMPLES; i++) {
                        assertEquals(FEATURES, dataManager.getSampleRow(i).size());
                    }
                }
        );
    }

    @DisplayName("Assert that certain variables match what was supplied")
    @Test
    void testVariables() {
        final double delta = 0.001;
        assertAll("Check certain variables match the input file",
                () ->  assertEquals(5.1, getVariable("A", 0), delta),
                () ->  assertEquals(3.0, getVariable("B", 1), delta),
                () ->  assertEquals(1.3, getVariable("C", 2), delta),
                () ->  assertEquals(0.2, getVariable("D", 3), delta),
                () ->  assertEquals(0.0, getVariable("E", 4), delta)
        );
    }

    //---------
    // Helpers
    //---------

    /**
     * Get the expected results for the method intToHeader. Current usage dictates no negative numbers are supplied.
     * @return Stream of: int, String
     */
    private static Stream<Arguments> intToHeaderProvider() {
        return Stream.of(
                Arguments.of(0, "A"),
                Arguments.of(1, "B"),
                Arguments.of(25, "Z"),
                Arguments.of(26, "AA"),
                Arguments.of(260, "JA"),
                Arguments.of(701, "ZZ"),
                Arguments.of(702, "AAA")
        );
    }

    /**
     * Helper method to retrieve the double variable of a certain row, making the test more readable
     * (For data manager without a header, so headers are A, B, C, ...)
     * @param header Feature selected
     * @param row Sample selected
     * @return Double value of this variable
     */
    private double getVariable(String header, int row) {
        return dataManagerNoHeader.getSampleVariable(header, row).doubleValue();
    }
}
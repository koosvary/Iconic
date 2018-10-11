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

import java.util.ArrayList;
import java.util.List;
import java.lang.*;

public class Smooth extends Preprocessor<Number> {
    private int N = 2; // N is the number of neighboring data points on either side of the value

    /**
     * <p>
     * Smooths the values of an Array. Alternatively known as "Moving Average Filtering".
     * </p>
     *
     * <p>
     * Given an array of values, the function will take the 'N' neighbouring values on either side of the index, take
     * the sum of all these values, then update the value of the index to the average of the sum.
     * </p>
     *
     * <p>
     * If the span window is outside of the Array bounds, the window will become the minimum reach of both sides.
     * </p>
     *
     * @param values the array that will be smoothed
     */
    public List<Number> apply(List<Number> values) {
        List<Number> newValues = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            // The index of the lowest span from the point
            int lowerBound = Math.max(0, i - N);

            // The size of the lowest span
            int lowerBoundRange = i - lowerBound;

            // The index of the highest span from the point
            int upperBound = Math.min(values.size() - 1, i + N);

            // The size of the highest span
            int upperBoundRange = upperBound - i;

            // The smallest span of reach for both sides
            int span = Math.min(lowerBoundRange, upperBoundRange);

            // Find the sum of all values in the span
            Double sum = 0.0;
            for (int j = i - span; j <= i + span; j++) {
                sum += values.get(j).doubleValue();
            }

            // Average of the span size
            newValues.add(1.0 / (2.0 * span + 1.0) * sum);
        }

        // Update the old values to the new values
        for (int i = 0; i < values.size(); i++) {
            values.set(i, newValues.get(i));
        }

        return newValues;
    }

    /**
     * <p>
     * Sets the window size of values to use when smoothing the array of data.
     * </p>
     *
     * @param neighbourSize the number of neighbours on either side of each point to be used for smoothing
     */
    public void setNeighbourSize(int neighbourSize) {
        this.N = neighbourSize;
    }
}
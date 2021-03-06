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

import java.util.ArrayList;
import java.util.List;
import java.lang.*;

public class Smooth extends Preprocessor<Number> {
    private int N = 2; // N is the number of neighboring data points on either side of the value

    /**
     * <p>
     * Smooths the values of an Array. Alternatively known as "Moving Average Filtering".
     *
     *
     * <p>
     * Given an array of values, the function will take the 'N' neighbouring values on either side of the index, take
     * the sum of all these values, then update the value of the index to the average of the sum.
     *
     *
     * <p>
     * If the span window is outside of the Array bounds, the window will become the minimum reach of both sides.
     *
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
                Number value = values.get(j);
                if (value == null) {
                    continue;
                }
                sum += value.doubleValue();
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
     *
     *
     * @param neighbourSize the number of neighbours on either side of each point to be used for smoothing
     */
    public void setNeighbourSize(int neighbourSize) {
        this.N = neighbourSize;
    }
}
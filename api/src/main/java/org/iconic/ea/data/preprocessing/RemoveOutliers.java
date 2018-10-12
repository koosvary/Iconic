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

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

public class RemoveOutliers extends Preprocessor<Number> {
    private double threshold = 2.00;

    public List<Number> apply(List<Number> values) {
        // Calculate the mean and IQR
        double mean = calculateMean(values);
        double IQR = calculateIQR(values);

        // Use iterator to safely remove outlier values
        Iterator<Number> iterator = values.iterator();

        while (iterator.hasNext()) {
            // Checks if the distance between the point and the mean is greater than the threshold multiplied by the IQR
            if (Math.abs(mean - iterator.next().doubleValue()) > threshold * IQR) {
                iterator.remove();
            }
        }

        return values;
    }

    private double calculateMean(List<Number> values) {
        double total = 0;

        for (Number value : values) {
            total += value.doubleValue();
        }

        return total / values.size();
    }

    private double calculateMedian(Double[] values) {
        if (values.length % 2 == 0) {
            return (values[values.length / 2] + values[values.length / 2] - 1) / 2;
        } else {
            return values[values.length / 2];
        }
    }

    private double calculateIQR(List<Number> values) {
        // Convert values to an array and sort
        Double[] numbers = values.toArray(new Double[values.size()]);
        Arrays.sort(numbers);

        // Calculate the size of the first/second half sets
        int splitSize = (int)Math.floor(numbers.length / 2);

        Double[] firstHalf = new Double[splitSize];
        Double[] secondHalf = new Double[splitSize];

        // Assigns each number to either the first half array or second half array
        // If the set has an odd length, the middle element is ignored
        for (int i=0; i < numbers.length; i++) {
            if (i < splitSize) {
                firstHalf[i] = numbers[i];
            } else if (numbers.length % 2 == 0 && (i == splitSize || i > splitSize)) {
                secondHalf[i - splitSize] = numbers[i];
            } else if (numbers.length %2 != 0 && i > splitSize) {
                secondHalf[i - splitSize - 1] = numbers[i];
            }
        }

        // Returns the result of Q3 - Q1
        return calculateMedian(secondHalf) - calculateMedian(firstHalf);
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
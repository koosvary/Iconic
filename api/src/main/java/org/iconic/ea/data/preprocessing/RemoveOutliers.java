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

import java.util.Arrays;
import java.util.List;
import java.util.Comparator;

public class RemoveOutliers extends Preprocessor<Number> {
    private double threshold = 2.00;

    // Replaces any number outside of the range with null
    public List<Number> apply(List<Number> values) {
        // Calculate the mean and IQR
        double mean = calculateMean(values);
        double IQR = calculateIQR(values);

        for (int i=0; i < values.size(); i++) {
            // Checks if the distance between the point and the mean is greater than the threshold multiplied by the IQR
            if (values.get(i) != null) {
                if (Math.abs(mean - values.get(i).doubleValue()) > threshold * IQR) {
                    values.set(i, null);
                }
            }
        }

        return values;
    }

    private double calculateMean(List<Number> values) {
        double total = 0;

        for (Number value : values) {
            if (value != null) {
                total += value.doubleValue();
            }
        }

        return total / values.size();
    }

    private double calculateMedian(Double[] values) {
        Double center = values[values.length / 2];

        if (values.length % 2 == 0) {
            if (center != null) {
                return (center + (center - 1) / 2);
            } else {
                return 0;
            }
        } else {
            if (center != null) {
                return center;
            } else {
                return 0;
            }
        }
    }

    private double calculateIQR(List<Number> values) {
        // Convert values to an array and sort
        Double[] numbers = values.toArray(new Double[values.size()]);
        Arrays.sort(numbers, new Comparator<Double>() {
                    @Override
                    public int compare(Double o1, Double o2) {
                        if (o1 != null && o2 != null) {
                            return o1.compareTo(o2);
                        } else {
                            return 0;
                        }
                    }
                });

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
package org.iconic.ea.data.preprocessing;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RemoveOutliers extends Preprocessor<Number> {
    private double threshold = 1.50;

    public List<Number> apply(List<Number> values) {
        // Convert values arraylist to an array for sorting
        Double[] numbers = (Double[])values.toArray();
        Arrays.sort(numbers);






        return values;
    }

    private double calculateMean(List<Number> values) {
        double total = 0;

        for (Number value : values) {
            total += value.doubleValue();
        }

        return total / values.size();
    }

    private void calculateMedian() {

    }

    private void calculateIQR() {

    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
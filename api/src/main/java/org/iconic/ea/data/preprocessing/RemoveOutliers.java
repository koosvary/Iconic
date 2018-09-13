package org.iconic.ea.data.preprocessing;

import java.util.ArrayList;

public class RemoveOutliers {
    private static Number minCutoff, maxCutoff;

    public static ArrayList<Number> apply(ArrayList<Number> values) {

        // Remove outliers functionality

        return values;
    }

    public void setMinCutoff(Number minCutoff) {
        this.minCutoff = minCutoff;
    }

    public void setMaxCutoff(Number maxCutoff) {
        this.maxCutoff = maxCutoff;
    }

    public Number getMinCutoff() {
        return minCutoff;
    }

    public Number getMaxCutoff() {
        return maxCutoff;
    }
}
package org.iconic.ea.data.preprocessing;

import java.util.List;

public class RemoveOutliers extends Preprocessor<Number> {
    private static Number minCutoff, maxCutoff;

    public List<Number> apply(List<Number> values) {

        // Remove outliers functionality

        return values;
    }

    public void setMinCutoff(Number minCutoff) {
        this.minCutoff = minCutoff;
    }

    public void setMaxCutoff(Number maxCutoff) {
        this.maxCutoff = maxCutoff;
    }
}
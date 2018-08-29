package org.iconic.ea.data.preprocessing;

import java.util.ArrayList;

public class RemoveOutliers extends Preprocessor<Number> {
    private Number minCutoff, maxCutoff;

    @Override
    public void apply(ArrayList<Number> values) {

    }

    public void setMinCutoff(Number minCutoff) { this.minCutoff = minCutoff; }
    public void setMaxCutoff(Number maxCutoff) { this.maxCutoff = maxCutoff; }
    public Number getMinCutoff() { return minCutoff; }
    public Number getMaxCutoff() { return maxCutoff; }
}
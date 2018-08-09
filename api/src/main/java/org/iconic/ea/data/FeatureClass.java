package org.iconic.ea.data;

import org.iconic.ea.data.preprocessing.Normalise;

import java.util.ArrayList;

public class FeatureClass<T extends Number> {
    private boolean expectedResultsFeature;
    private ArrayList<Number> originalSamples, modifiedSamples;
    private Normalise<T> normaliseClass;
    // TODO - Add in the pre-processing features

    public FeatureClass() {
        this.expectedResultsFeature = false;
        originalSamples = new ArrayList<>();
        modifiedSamples = new ArrayList<>();
        normaliseClass = new Normalise<>();
    }

    public void addSampleValue(T value) {
        originalSamples.add(value);
        modifiedSamples.add(value);
    }

    public Number getSampleValue(int row) {
        return modifiedSamples.get(row);
    }

    public ArrayList<Number> getSamples() {
        return modifiedSamples;
    }

    public void applyPreProcessing() {
        if (normaliseClass.isEnabled()) {
            normaliseClass.apply(modifiedSamples);
        }
    }

    public void setExpectedResultsFeature(boolean value) { this.expectedResultsFeature = value; }
}
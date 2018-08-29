package org.iconic.ea.data;

import org.iconic.ea.data.preprocessing.Preprocessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class FeatureClass<T> {
    private boolean output;
    private boolean active;

    private ArrayList<T> originalSamples, modifiedSamples;
    private Set<Preprocessor<T>> preprocessors;
    // TODO - Add in the pre-processing features

    protected FeatureClass(boolean output) {
        this.output = output;
        this.active = true;
        this.originalSamples = new ArrayList<>();
        this.modifiedSamples = new ArrayList<>();
        this.preprocessors = new HashSet<>();
    }

    public void addSampleValue(T value) {
        originalSamples.add(value);
        modifiedSamples.add(value);
    }

    public T getSampleValue(int row) {
        return modifiedSamples.get(row);
    }

    public ArrayList<T> getSamples() {
        return modifiedSamples;
    }

    public void applyPreProcessing() {
        // Ignoring order of operations
        getPreprocessors().stream()
                .filter(Preprocessor::isEnabled)
                .forEach(p -> p.apply(modifiedSamples));
    }

    public Set<Preprocessor<T>> getPreprocessors() {
        return preprocessors;
    }

    public void setPreprocessors(Set<Preprocessor<T>> preprocessors) {
        this.preprocessors = preprocessors;
    }

    // Returns true if the feature is the objective value to calculate towards
    public boolean isOutput() {
        return output;
    }

    // Sets whether the feature is the objective value
    public void setOutput(boolean value) {
        this.output = value;
    }

    // Returns true if the feature was found in the function definition
    public boolean isActive() {
        return active;
    }

    // Set to true if the feature was found in the function definition
    public void setActive(boolean value) {
        this.active = value;
    }

    public void updateModifiedSample(int index, T value) {
        modifiedSamples.set(index, value);
    }

    public void resetModifiedSample(int index) {
        modifiedSamples.set(index, originalSamples.get(index));
    }
}
package org.iconic.ea.data;

import org.iconic.ea.data.preprocessing.Preprocessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class FeatureClass<T> {
    private boolean output;
    private ArrayList<T> originalSamples, modifiedSamples;
    private Set<Preprocessor<T>> preprocessors;
    // TODO - Add in the pre-processing features

    protected FeatureClass(boolean output) {
        this.output = output;
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

    public boolean isOutput() {
        return output;
    }

    public void setOutput(boolean value) {
        this.output = value;
    }

    public void updateModifiedSamples(int index, T value) {
        modifiedSamples.set(index, value);
    }
}
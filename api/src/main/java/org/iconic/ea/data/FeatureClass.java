package org.iconic.ea.data;

import org.iconic.ea.data.preprocessing.Preprocessor;
import org.iconic.ea.data.preprocessing.TransformType;
import org.iconic.ea.data.preprocessing.Transformation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class FeatureClass<T> {
    private boolean output;
    private ArrayList<T> originalSamples, modifiedSamples;
    private Set<Preprocessor<T>> preprocessors;
    private ArrayList<Transformation> transformations;
    private boolean modified;

    protected FeatureClass(boolean output) {
        this.output = output;
        this.originalSamples = new ArrayList<>();
        this.modifiedSamples = new ArrayList<>();
        this.preprocessors = new HashSet<>();
        this.transformations = new ArrayList<>();
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

    public void updateModifiedSample(int index, T value) {
        modifiedSamples.set(index, value);
    }

    public void resetModifiedSample(int index) {
        modifiedSamples.set(index, originalSamples.get(index));
    }

    public void addTransformation(Transformation transformation) {
        transformations.add(transformation);
    }

    /**
     * Removes a specific transformation from the stored list given a TransformType.
     *
     * @param transformType Enum used to identify the type of transformation.
     */
    public void removeTransformation(TransformType transformType) {
        for (int i=0; i < transformations.size(); i++) {
            if (transformations.get(i).getTransform().equals(transformType)) {
                transformations.remove(i);
                break;
            }
        }
    }

    public ArrayList<Transformation> getTransformations() {
        return transformations;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
}
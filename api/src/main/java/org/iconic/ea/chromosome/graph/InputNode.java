package org.iconic.ea.chromosome.graph;

import java.util.List;

public class InputNode<T> extends Node<T> {
    /*
     * <p>
     * The feature index refers to the feature to use from the data, e.g. sex, age, height
     * </p>
     */
    private final int featureIndex;

    public InputNode(int featureIndex) {
        super();
        this.featureIndex = featureIndex;
    }

    public T apply(List<T> sampleRowValues) {
        return sampleRowValues.get(featureIndex);
    }

    private int getFeatureIndex() {
        return featureIndex;
    }

    @Override
    public String toString() {
        return " F" + featureIndex + " ";
    }

    @Override
    public Node<T> clone() {
        return new InputNode<>(getFeatureIndex());
    }

    public int getNumberOfChildren() {
        return 0;
    }
}

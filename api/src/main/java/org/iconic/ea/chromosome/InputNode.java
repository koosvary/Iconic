package org.iconic.ea.chromosome;

import java.util.List;

public class InputNode<T> extends Node<T> implements Cloneable {
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
        return featureIndex + "";
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public InputNode<T> clone() {
        InputNode<T> clone = new InputNode<>(getFeatureIndex());
        return clone;
    }

    public int getChildrenSize() {
        return 0;
    }
}

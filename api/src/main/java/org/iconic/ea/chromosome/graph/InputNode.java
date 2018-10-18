/**
 * Copyright 2018 Iconic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        return "F" + featureIndex + "";
    }

    @Override
    public Node<T> clone() {
        return new InputNode<>(getFeatureIndex());
    }

    public int getNumberOfChildren() {
        return 0;
    }
}

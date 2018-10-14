/**
 * Copyright (C) 2018 Iconic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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

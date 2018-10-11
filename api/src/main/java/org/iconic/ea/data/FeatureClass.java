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
package org.iconic.ea.data;

import org.iconic.ea.data.preprocessing.*;

import java.util.ArrayList;
import java.util.List;

public abstract class FeatureClass<T> {
    private boolean output;
    private boolean active;

    private List<T> originalSamples, modifiedSamples;

    // A list of preprocessors that are currently applied to the feature. This list updates dynamically, meaning when
    // a preprocessor is disabled, it is removed from this list.
    private List<Preprocessor<T>> preprocessors;

    protected FeatureClass(boolean output) {
        this.output = output;
        this.active = true;
        this.originalSamples = new ArrayList<>();
        this.modifiedSamples = new ArrayList<>();
        this.preprocessors = new ArrayList<>();
    }

    public void addSampleValue(T value) {
        originalSamples.add(value);
        modifiedSamples.add(value);
    }

    public T getSampleValue(int row) {
        return modifiedSamples.get(row);
    }

    public List<T> getSamples() {
        return modifiedSamples;
    }

    public List<Preprocessor<T>> getPreprocessors() {
        return preprocessors;
    }

    /**
     * Updates an existing preprocessor if parameters changes, or adds a new preprocessor to the active list. The
     * remaining preprocessors are then re-applied in order.
     *
     * @param preprocessor
     */
    public void addPreprocessor(Preprocessor<T> preprocessor) {
        // Checks if a preprocessor of this type is already active, and if so, replaces it.
        for (int i=0; i < preprocessors.size(); i++) {
            if (preprocessors.get(i).getTransformType() == preprocessor.getTransformType()) {
                preprocessors.remove(i);
                preprocessors.add(i, preprocessor);

                applyPreprocessors();
                return;
            }
        }

        // If a preprocessor of this type is not currently active, simply add it to the end of the list.
        preprocessors.add(preprocessor);
        applyPreprocessors();
    }

    /**
     * Given a TransformType, the corresponding preprocessor is removed from the currently active list. The remaining
     * preprocessors are then re-applied in order.
     *
     * @param transformType Identifies the preprocessor to be removed
     */
    public void removePreprocessor(TransformType transformType) {
        for (int i=0; i < preprocessors.size(); i++) {
            if (preprocessors.get(i).getTransformType() == transformType) {
                preprocessors.remove(i);
                break;
            }
        }

        applyPreprocessors();
    }

    /**
     * Reapplies all currently active preprocessors to the original samples, and then updates modifised samples
     * with these values.
     */
    private void applyPreprocessors() {
        List<T> values = new ArrayList<>(originalSamples);

        if (!preprocessors.isEmpty()) {
            // Apply the first preprocessor using the original samples
            values = preprocessors.get(0).apply(values);

            // Apply each subsequent preprocessor using the newly modified samples
            for (int i=1; i < preprocessors.size(); i++) {
                values = preprocessors.get(i).apply(values);
            }
        }

        modifiedSamples = values;
    }

    public boolean isOutput() {
        return output;
    }

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

    /**
     * Returns whether or not any preprocessors have been applied to the feature.
     *
     * @return True in the case that at least one preprocessor is active, or false if less than 1.
     */
    public boolean isModified() {
        if (preprocessors.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *  TEMPORARY DEBUG METHODS
     */
    private void printValues(List<T> values) {
        String valueString = "";

        for (int i=0; i < values.size(); i++) {
            valueString += values.get(i) + " ";
        }

        System.out.println(valueString);
    }

    private void printPreprocessors() {
        String test="";

        for (int i=0; i < preprocessors.size(); i++) {
            test += " " + preprocessors.get(i).getTransformType();
        }

        System.out.println(test);
    }
}
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
package org.iconic.ea.data;

import org.iconic.ea.data.preprocessing.*;

import java.util.ArrayList;
import java.util.List;

public abstract class FeatureClass<T> {
    private boolean output;
    private boolean active;
    private boolean missingValues;

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
        this.missingValues = false;
    }

    public void addSampleValue(T value) {
        originalSamples.add(value);
        modifiedSamples.add(value);

        if (value == null) {
            setMissingValues(true);
        }
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
     * Reapplies all currently active preprocessors to the original samples, and then updates modified samples
     * with these values.
     */
    private void applyPreprocessors() {
        List<T> values = new ArrayList<>(originalSamples);

        if (!preprocessors.isEmpty()) {
            // Apply each subsequent preprocessor
            for (int i=0; i < preprocessors.size(); i++) {
                values = preprocessors.get(i).apply(values);
            }
        }

        modifiedSamples = values;

        // Check if the new modified samples contain a missing value or not.
        if (modifiedSamples.contains(null)) {
            setMissingValues(true);
        } else {
            setMissingValues(false);
        }
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

    public void setMissingValues(boolean value) {
        missingValues = value;
    }

    public boolean isMissingValues() {
        return missingValues;
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
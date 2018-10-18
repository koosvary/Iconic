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
package org.iconic.ea.data.preprocessing;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class HandleMissingValues extends Preprocessor<Number> {
    private Mode mode;

    /**
     * The method to be used when dealing with missing values that occur within a List of values.
     */
    public enum Mode {
        /** Ignore the entire row of data from the tests. */
        IGNORE_ROW,
        /** Use the first non-empty value. */
        COPY_PREVIOUS_ROW,
        /** Use the mean value of all elements in the array. */
        MEAN,
        /** Use the median value of the array, if the size of the array is odd use the average of the two middle indexes. */
        MEDIAN,
        /** Replace it's value with the numerical value of "0". */
        ZERO,
        /** Replace it's value with the numerical value of "1". */
        ONE,
        /** Replace it's value with the numerical value set by the user. */
        NUMERICAL
    }

    /** Replacement value, if needed */
    private static double numericalValueReplacement = 0;

    public List<Number> apply(List<Number> values) {
        switch (mode) {
            case COPY_PREVIOUS_ROW:
                copyPreviousRow(values);
                break;
            case MEAN:
                mean(values);
                break;
            case MEDIAN:
                median(values);
                break;
            case ZERO:
                replaceMissingWith(values, 0);
                break;
            case ONE:
                replaceMissingWith(values, 1);
                break;
            case NUMERICAL:
                replaceMissingWith(values, numericalValueReplacement);
                break;
        }

        return values;
    }

    // TODO - Decide how this function will work with all other feature classes
    private void ignoreRow(List<Number> values) {
    }

    /**
     * <p>
     * Given an ArrayList of values, all null value elements that occur in the array will have it's value set to the
     * previous indexes value. If the previous index is also null, the value will be set to the first non-null occurring
     * value that occurs in the search. If the entire list contains null values then this algorithm will have no effect.
     * </p>
     *
     * <p>
     * For example, given an array {null, 2, null, null}. At index 0 the value is null, therefore the previous index is
     * index 3, but its value is also null. The next previous index is index 2 which is also null, and lastly index 1
     * with the value 2. Now that a non-null value has been found, all values from that point will be updated.
     * {2, 2, 2, 2} will be the final output
     * </p>
     * @param values The ArrayList to perform the function on.
     */
    private List<Number> copyPreviousRow(List<Number> values) {
        // Loop through all values in the array
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) != null) {
                continue;
            }
            // The currentIndex is equal to i - 1 (with wrapping)
            int currentIndex = i - 1;

            if (currentIndex < 0) {
                currentIndex = values.size() - 1;
            }

            // While the currentIndex value in the array is equal to null, decrement currentIndex
            while (values.get(currentIndex) == null) {
                // Decrement and check if its less than 0 in the same line.
                if (--currentIndex < 0) {
                    currentIndex = values.size() - 1;
                }

                // If currentIndex loops all the way back to i, then the entire array is null and nothing
                // can be done, therefore exit the method.
                if (i == currentIndex) {
                    return values;
                }
            }

            // Once an index has been found != null, loop forwards from that point to i updating each value
            // to the previous value
            // Index to replace is currentIndex + 1 (with wrapping)
            int index = currentIndex;
            while (index != i) {
                if (++index >= values.size()) {
                    index = 0;
                }

                // Update the current array index is currentIndex (currentIndex = index - 1)
                values.set(index, values.get(currentIndex));
            }
        }

        return values;
    }

    /**
     * <p>
     * Given an ArrayList of values, all null values that occur in this list will be changed into the mean of the list.
     * All null values are ignored when calculating the mean.
     * </p>
     * @param values The ArrayList to perform the function on.
     */
    private List<Number> mean(List<Number> values) {
        // Create a list to track all the null value indexes
        List<Integer> indexesToReplace = new ArrayList<>();
        double mean;
        double sum = 0; // Used to track the total sum value of the array
        int count = 0;  // used to track how many elements have values.

        for (int i = 0; i < values.size(); i++) {
            Number v = values.get(i);
            if (v == null) {
                indexesToReplace.add(i);
            } else {
                count++;
                sum += v.doubleValue();
            }
        }

        mean = sum / count;

        // Update the old values to the new ones
        for (Integer i : indexesToReplace) {
            values.set(i, mean);
        }

        return values;
    }

    /**
     * <p>
     * Given an ArrayList of values, change all null elements to the median value of the other elements. If the size
     * of the array is even then the median value is set to the average of the middle 2 points.
     * </p>
     *
     * <p>
     * For example, the array {5, null, 2, 4, 3} will first extract all non-null value elements to be sorted ->
     * {2, 3, 4, 5}. Because the size of the array is even, The median values are 3 and 4. The median value will be set
     * to (3 + 4) / 2 = 3.5, and thus the original array will be updated to now look like -> {5, 3.5, 2, 4, 3}
     * </p>
     * @param values The ArrayList to perform the function on.
     */
    private List<Number> median(List<Number> values) {
        List<Double> doubleValues = new ArrayList<>();
        double medianValue;

        for (Number v : values) {
            if (v != null) {
                doubleValues.add(v.doubleValue());
            }
        }

        // Sort all the values to find the median value
        Collections.sort(doubleValues);

        // If its an even number take the average between the two numbers
        if (doubleValues.size() % 2 == 0) {
            int upperIndex = doubleValues.size() / 2;
            int lowerIndex = doubleValues.size() / 2 - 1;
            medianValue = (doubleValues.get(lowerIndex) + doubleValues.get(upperIndex)) / 2;
        } else { // Otherwise just take the median value
            int index = doubleValues.size() / 2;
            medianValue = doubleValues.get(index);
        }

        // Update all null values to the median value
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == null) {
                values.set(i, medianValue);
            }
        }

        return values;
    }

    /**
     * <p>
     * Changes all null values within the ArrayList to the numerical value supplied
     * </p>
     * @param values The ArrayList to perform the function on.
     * @param replacement The replacement value to use
     */
    private List<Number> replaceMissingWith(List<Number> values, double replacement) {
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == null) {
                values.set(i, replacement);
            }
        }

        return values;
    }

    /**
     * <p>
     * Sets a custom value to be used with the numericalValue() function which replaces all null value elements within
     * an array to the given custom value.
     * </p>
     *
     * <p>
     * by default the value is set to "0"
     * </p>
     * @param value Defines the number to be used when replacing the null values of an array.
     */
    public void setNumericalValueReplacement(double value) {
        this.numericalValueReplacement = value;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * DEBUG METHOD: Can be deleted once copyPreviousRow is fixed.
     *
     * @param values
     */
    private static void printValues(List<Number> values) {
        String valueString = "";

        for (int i=0; i < values.size(); i++) {
            valueString += values.get(i) + ", ";
        }

        System.out.println(valueString);
    }
}
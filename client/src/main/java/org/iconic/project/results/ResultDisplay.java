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
package org.iconic.project.results;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Displayable for the results table
 */
public class ResultDisplay implements Comparable<ResultDisplay> {

    /** Size property */
    private final SimpleIntegerProperty size = new SimpleIntegerProperty();
    /** Error property */
    private final SimpleDoubleProperty error = new SimpleDoubleProperty();
    /** Solution property */
    private final SimpleStringProperty solution = new SimpleStringProperty();

    /**
     * Constructor
     * @param size Size
     * @param error Error
     * @param solution Solution
     */
    public ResultDisplay(int size, double error, String solution) {
        setSize(size);
        setError(Math.round(error * 1000) / 1000.0);
        setSolution(solution);
    }

    /**
     * Sort by error
     * @param o Other object
     * @return Comparison
     */
    @Override
    public int compareTo(ResultDisplay o) {
        return Double.compare(getError(), o.getError());
    }

    // -- Getters --

    public double getError() {
        return error.get();
    }

    public SimpleDoubleProperty errorProperty() {
        return error;
    }

    public void setError(double error) {
        this.error.set(error);
    }

    public String getSolution() {
        return solution.get();
    }

    public SimpleStringProperty solutionProperty() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution.set(solution);
    }

    public int getSize() {
        return size.get();
    }

    public SimpleIntegerProperty sizeProperty() {
        return size;
    }

    public void setSize(int size) {
        this.size.set(size);
    }
}
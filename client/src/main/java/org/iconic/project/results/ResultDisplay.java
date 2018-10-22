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

public class ResultDisplay {

    private final SimpleIntegerProperty size = new SimpleIntegerProperty();
    private final SimpleDoubleProperty fit = new SimpleDoubleProperty();
    private final SimpleStringProperty solution = new SimpleStringProperty();


    public ResultDisplay(int size, double fit, String solution) {
        setSize(size);
        setFit(Math.round(fit * 1000) / 1000.0);
        setSolution(solution);
    }

    public double getFit() {
        return fit.get();
    }

    public SimpleDoubleProperty fitProperty() {
        return fit;
    }

    public void setFit(double fit) {
        this.fit.set(fit);
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
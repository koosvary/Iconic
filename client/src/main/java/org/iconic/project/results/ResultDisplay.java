package org.iconic.project.results;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ResultDisplay {

    private final SimpleIntegerProperty size = new SimpleIntegerProperty();
    private final SimpleDoubleProperty fit = new SimpleDoubleProperty();
    private final SimpleStringProperty solution = new SimpleStringProperty();


    public ResultDisplay(int size, double fit, String solution) {
        setSize(size);
        setFit(fit);
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
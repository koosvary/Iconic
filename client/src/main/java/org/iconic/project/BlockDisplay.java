package org.iconic.project;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class BlockDisplay {
    private final SimpleStringProperty name = new SimpleStringProperty("");
    private final SimpleBooleanProperty enabled = new SimpleBooleanProperty(true);
    private final SimpleIntegerProperty complexity = new SimpleIntegerProperty(1);

    public BlockDisplay() {
    }

    public BlockDisplay(Boolean enabled, String name, Integer complexity) {
        setEnabled(enabled);
        setName(name);
        setComplexity(complexity);
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public SimpleBooleanProperty enabledProperty() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getComplexity() {
        return complexity.get();
    }

    public SimpleIntegerProperty complexityProperty() {
        return complexity;
    }

    public void setComplexity(int complexity) {
        this.complexity.set(complexity);
    }
}
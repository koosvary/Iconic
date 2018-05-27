package org.iconic.project;

import javafx.beans.property.SimpleStringProperty;

public class BlockDisplay {
    private final SimpleStringProperty enabled = new SimpleStringProperty("true");
    private final SimpleStringProperty name = new SimpleStringProperty("");
    private final SimpleStringProperty complexity = new SimpleStringProperty("1");

    public BlockDisplay() {
    }

    public BlockDisplay(String enabled, String name, String complexity) {
        setEnabled(enabled);
        setName(name);
        setComplexity(complexity);
    }

    public void setEnabled(String enabled) { this.enabled.set(enabled); }
    public String getEnabled() { return enabled.get(); }

    public void setName(String name) { this.name.set(name); }
    public String getName() { return name.get(); }

    public void setComplexity(String complexity) { this.complexity.set(complexity); }
    public String getComplexity() { return complexity.get(); }
}
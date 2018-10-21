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
package org.iconic.project;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;

import java.util.Map;

public class BlockDisplay {
    private final SimpleStringProperty name = new SimpleStringProperty("");
    private final SimpleBooleanProperty enabled = new SimpleBooleanProperty(true);
    private final SimpleIntegerProperty complexity = new SimpleIntegerProperty(1);
    private final SimpleStringProperty description = new SimpleStringProperty("");

    public BlockDisplay(final Map.Entry<FunctionalPrimitive<Double, Double>, Boolean> primitive) {
        this(
                primitive.getValue(),
                primitive.getKey().getSymbol(),
                primitive.getKey().getDefaultComplexity(),
                primitive.getKey().getDescription()
        );
    }

    public BlockDisplay(Boolean enabled, String name, Integer complexity, String description) {
        setEnabled(enabled);
        setName(name);
        setComplexity(complexity);
        setDescription(description);
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

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
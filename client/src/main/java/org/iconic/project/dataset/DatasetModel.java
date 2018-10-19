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
package org.iconic.project.dataset;

import javafx.beans.property.SimpleStringProperty;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.controlsfx.glyphfont.FontAwesome;
import org.iconic.ea.data.DataManager;
import org.iconic.project.Displayable;

import java.util.Optional;
import java.util.UUID;

/**
 * <p>
 * A model for datasets.
 *
 */
@Log4j2
public class DatasetModel implements Displayable {
    private final UUID id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty absolutePath;
    private final DataManager<Double> dataManager;

    /**
     * <p>Constructs a new DatasetModel with the provided name.
     *
     * @param name         The name of the dataset
     */
    public DatasetModel(@NonNull final String name) {
        this.name = new SimpleStringProperty(name);
        this.absolutePath = new SimpleStringProperty("");
        this.dataManager = new DataManager<>();
        this.id = UUID.randomUUID();
    }

    /**
     * <p>Constructs a new DatasetModel with the provided name and absolute path.
     *
     * @param name         The name of the dataset
     * @param absolutePath The absolute path to the dataset
     */
    public DatasetModel(@NonNull final String name, @NonNull final String absolutePath) {
        this.name = new SimpleStringProperty(name);
        this.absolutePath = new SimpleStringProperty(absolutePath);
        this.dataManager = new DataManager<>(absolutePath);
        this.id = UUID.randomUUID();
    }

    /**
     * Returns the name of this DatasetModel
     *
     * @return the name of the DatasetModel
     */
    public String getName() {
        return nameProperty().get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabel() {
        return getName();
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public Optional<Enum<?>> getIcon() {
        return Optional.of(FontAwesome.Glyph.DATABASE);
    }

    /**
     * <p>Returns the name property of this dataset.
     *
     * @return The name property of the dataset
     */
    public final SimpleStringProperty nameProperty() {
        return name;
    }

    /**
     * <p>Returns the absolute path of this dataset.
     *
     * @return The absolute path to the dataset
     */
    public String getAbsolutePath() {
        return absolutePath.get();
    }

    /**
     * <p>Returns the absolute path property of this dataset.
     *
     * @return The absolute path property of the dataset
     */
    public final SimpleStringProperty absolutePathProperty() {
        return absolutePath;
    }

    /**
     * <p>Sets the name of this dataset to the provided value.
     *
     * @param name The new name for this dataset
     */
    public void setName(final String name) {
        this.nameProperty().set(name);
    }

    /**
     * <p>Sets the absolute path to this dataset to the provided value.
     *
     * @param absolutePath The new absolute path for this dataset
     */
    public void setAbsolutePath(final String absolutePath) {
        this.absolutePathProperty().set(absolutePath);
    }

    /**
     * <p>Returns the UUID of this dataset.
     *
     * @return The universally unique identifier of the dataset
     */
    public UUID getId() {
        return id;
    }

    public DataManager<Double> getDataManager() {
        return dataManager;
    }
}

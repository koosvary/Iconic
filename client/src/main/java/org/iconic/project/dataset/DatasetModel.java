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
 * </p>
 */
@Log4j2
public class DatasetModel implements Displayable {
    private final UUID id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty absolutePath;
    private final DataManager<Double> dataManager;



    /**
     * <p>Constructs a new DatasetModel with the provided name.</p>
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
     * <p>Constructs a new DatasetModel with the provided name and absolute path.</p>
     *
     * @param name         The name of the dataset
     * @param absolutePath The absolute path to the dataset
     */
    public DatasetModel(@NonNull final String name, @NonNull final String absolutePath) {
        this.name = new SimpleStringProperty(name);
        this.absolutePath = new SimpleStringProperty(absolutePath);
        this.dataManager = new DataManager<Double>(absolutePath);
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
     * <p>Returns the name property of this dataset.</p>
     *
     * @return The name property of the dataset
     */
    public final SimpleStringProperty nameProperty() {
        return name;
    }

    /**
     * <p>Returns the absolute path of this dataset.</p>
     *
     * @return The absolute path to the dataset
     */
    public String getAbsolutePath() {
        return absolutePath.get();
    }

    /**
     * <p>Returns the absolute path property of this dataset.</p>
     *
     * @return The absolute path property of the dataset
     */
    public final SimpleStringProperty absolutePathProperty() {
        return absolutePath;
    }

    /**
     * <p>Sets the name of this dataset to the provided value.</p>
     *
     * @param name The new name for this dataset
     */
    public void setName(final String name) {
        this.nameProperty().set(name);
    }

    /**
     * <p>Sets the absolute path to this dataset to the provided value.</p>
     *
     * @param absolutePath The new absolute path for this dataset
     */
    public void setAbsolutePath(final String absolutePath) {
        this.absolutePathProperty().set(absolutePath);
    }

    /**
     * <p>Returns the UUID of this dataset.</p>
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

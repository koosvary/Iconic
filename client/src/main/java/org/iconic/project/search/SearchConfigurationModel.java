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
package org.iconic.project.search;

import javafx.beans.property.SimpleStringProperty;
import lombok.NonNull;
import org.controlsfx.glyphfont.FontAwesome;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;

import java.util.Optional;
import java.util.UUID;

public class SearchConfigurationModel implements Displayable {
    private final UUID id;
    private final SimpleStringProperty name;
    private DatasetModel datasetModel;
    private SearchExecutor searchExecutor;

    /**
     * <p>Constructs a new search configuration with the provided name.</p>
     *
     * @param name The name of the search configuration
     */
    public SearchConfigurationModel(@NonNull final String name) {
        this.name = new SimpleStringProperty(name);
        this.id = UUID.randomUUID();
    }

    /**
     * Returns the name of this search configuration
     *
     * @return the name of the search configuration
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
        return Optional.of(FontAwesome.Glyph.SEARCH);
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
     * <p>Sets the name of this search configuration to the provided value.</p>
     *
     * @param name The new name for this search configuration
     */
    public void setName(final String name) {
        this.nameProperty().set(name);
    }

    /**
     * <p>Returns the UUID of this search configuration.</p>
     *
     * @return The universally unique identifier of the search configuration
     */
    public UUID getId() {
        return id;
    }

    /**
     * <p>Returns the dataset model associated with this search configuration.</p>
     *
     * @return The dataset model associated with the search configuration
     */
    public Optional<DatasetModel> getDatasetModel() {
        return (datasetModel == null) ? Optional.empty() : Optional.of(datasetModel);
    }

    public void setDatasetModel(DatasetModel datasetModel) {
        this.datasetModel = datasetModel;
    }

    /**
     * <p>Returns the search executor associated with this search configuration.</p>
     *
     * @return The search executor associated with the search configuration
     */
    public Optional<SearchExecutor> getSearchExecutor() {
        return (searchExecutor == null) ? Optional.empty() : Optional.of(searchExecutor);
    }

    public void setSearchExecutor(SearchExecutor searchExecutor) {
        this.searchExecutor = searchExecutor;
    }
}

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
package org.iconic.project;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.controlsfx.glyphfont.FontAwesome;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.config.SearchConfigurationModel;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 *
 * A model for projects.
 *
 */
@Log4j2
public class ProjectModel implements Cloneable, Displayable {
    private final ObjectProperty<String> name;
    private final ObservableList<DatasetModel> datasets;
    private final ObservableList<SearchConfigurationModel> searchConfigurations;

    /**
     * Constructs a new ProjectModel with the provided name and datasets.
     *
     * @param name     The name of the project.
     * @param datasets The datasets associated with the project.
     */
    private ProjectModel(@NonNull final String name,
                         @NonNull final List<DatasetModel> datasets,
                         @NonNull final List<SearchConfigurationModel> searchConfigurations

    ) {
        // Make sure the name isn't empty
        if (name.isEmpty()) {
            log.error("Attempted to create a project with an empty name");
            throw new IllegalArgumentException("Project names cannot be empty");
        }

        this.name = new SimpleObjectProperty<>(name);

        // Create an observable list of datasets that can be stored within the project
        this.datasets = FXCollections.observableArrayList(dataset -> new Observable[]{
                dataset.absolutePathProperty(), dataset.nameProperty()
        });

        this.datasets.addAll(datasets);

        // Create an observable list of search configurations that can be stored within the project
        this.searchConfigurations = FXCollections.observableArrayList(config -> new Observable[]{
                config.nameProperty()
        });

        this.searchConfigurations.addAll(searchConfigurations);
    }

    /**
     * Returns the name property of this project.
     *
     * @return The name property of the project.
     */
    public final ObjectProperty<String> nameProperty() {
        return name;
    }

    /**
     * Returns the name of this project.
     *
     * @return the name of the project.
     */
    public String getName() {
        return nameProperty().get();
    }

    /**
     * {@inheritDoc}
     *
     * @return
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
        return Optional.of(FontAwesome.Glyph.FOLDER);
    }

    /**
     * Returns the datasets associated with this project.
     *
     * @return The datasets associated with the project.
     */
    public final ObservableList<DatasetModel> getDatasets() {
        return datasets;
    }


    /**
     * Returns the search configurations associated with this project.
     *
     * @return The search configurations associated with the project.
     */
    public final ObservableList<SearchConfigurationModel> getSearchConfigurations() {
        return searchConfigurations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public ProjectModel clone() {
        return this.toBuilder().build();
    }

    /**
     * Returns a new builder for a ProjectModel.
     *
     * @return A ProjectModel builder.
     */
    static public Builder builder() {
        return new Builder();
    }

    /**
     * Returns a ProjectModel builder based on this instance.
     *
     * @return A ProjectModel builder based on the calling ProjectModel.
     */
    public Builder toBuilder() {
        return new Builder()
                .name(getLabel())
                .datasets(getDatasets())
                .searchConfigurations(getSearchConfigurations());
    }

    /**
     * A builder for a ProjectModel.
     */
    public static class Builder {
        private String name;
        private List<DatasetModel> datasets;
        private List<SearchConfigurationModel> searchConfigurations;

        /**
         * Constructs a new builder with no name and no datasets.
         */
        private Builder() {
            this.name = "";
            this.datasets = new LinkedList<>();
            this.searchConfigurations = new LinkedList<>();
        }

        /**
         * Sets the name of the project that will be constructed to the provided value.
         *
         * @param name The name to construct the project with.
         * @return The builder instance.
         */
        public Builder name(@NonNull final String name) {
            this.name = name.trim();
            return this;
        }

        /**
         * Adds the provided dataset to the project that will be constructed.
         *
         * @param dataset The dataset to construct the project with.
         * @return The builder instance.
         */
        public Builder dataset(@NonNull final DatasetModel dataset) {
            this.datasets.add(dataset);
            return this;
        }

        /**
         * Adds the provided datasets to the project that will be constructed.
         *
         * @param datasets The datasets to construct the project with.
         * @return The builder instance.
         */
        public Builder datasets(@NonNull final Collection<DatasetModel> datasets) {
            this.datasets.addAll(datasets);
            return this;
        }

        /**
         * Adds the provided search configuration to the project that will be constructed.
         *
         * @param searchConfiguration The search configuration to construct the project with.
         * @return The builder instance.
         */
        public Builder searchConfiguration(@NonNull final SearchConfigurationModel searchConfiguration) {
            this.searchConfigurations.add(searchConfiguration);
            return this;
        }

        /**
         * Adds the provided search configurations to the project that will be constructed.
         *
         * @param searchConfigurations The search configurations to construct the project with.
         * @return The builder instance.
         */
        public Builder searchConfigurations(@NonNull final Collection<SearchConfigurationModel> searchConfigurations) {
            this.searchConfigurations.addAll(searchConfigurations);
            return this;
        }

        /**
         * Returns the project constructed by this builder instance.
         *
         * @return A new project.
         */
        public ProjectModel build() {
            return new ProjectModel(name, datasets, searchConfigurations);
        }
    }
}

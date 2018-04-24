package org.iconic.project;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.iconic.project.dataset.DatasetModel;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * A model for projects.
 * </p>
 */
@Log4j2
public class ProjectModel implements Cloneable, Displayable {
    private final ObjectProperty<String> name;
    private final ObservableList<DatasetModel> datasets;

    /**
     * <p>Constructs a new ProjectModel with the provided name and datasets.</p>
     *
     * @param name The name of the project
     * @param datasets The datasets associated with the project
     */
    private ProjectModel(@NonNull final String name, @NonNull final List<DatasetModel> datasets) {
        // Make sure the name isn't empty
        if (name.isEmpty()) {
            log.error("Attempted to create a project with an empty name");
            throw new IllegalArgumentException("Project names cannot be empty");
        }

        this.name = new SimpleObjectProperty<>(name);
        this.datasets = FXCollections.observableArrayList(dataset -> new Observable[]{
                dataset.absolutePathProperty(), dataset.nameProperty()
        });

        this.datasets.addAll(datasets);
    }

    /**
     * <p>Returns the name property of this project.</p>
     *
     * @return The name property of the project
     */
    public final ObjectProperty<String> nameProperty() {
        return name;
    }

    /**
     * Returns the name of this project
     *
     * @return the name of the project
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
    public Optional<URI> getIcon() {
        return Optional.empty();
    }

    /**
     * <p>
     * Returns the datasets associated with this project
     * </p>
     *
     * @return The datasets associated with the project
     */
    public final ObservableList<DatasetModel> getDatasets() {
        return datasets;
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
     * <p>Returns a new builder for a ProjectModel</p>
     *
     * @return A ProjectModel builder
     */
    static public Builder builder() {
        return new Builder();
    }

    /**
     * <p>Returns a ProjectModel builder based on this instance</p>
     *
     * @return A ProjectModel builder based on the calling ProjectModel
     */
    public Builder toBuilder() {
        return new Builder().name(getLabel()).datasets(getDatasets());
    }

    /**
     * <p>A builder for a ProjectModel.</p>
     */
    public static class Builder {
        private String name;
        private List<DatasetModel> datasets;

        /**
         * Constructs a new builder with no name and no datasets
         */
        private Builder() {
            this.name = "";
            this.datasets = new LinkedList<>();
        }

        /**
         * <p>Sets the name of the project that will be constructed to the provided value</p>
         *
         * @param name The name to construct the project with
         * @return The builder instance
         */
        public Builder name(@NonNull final String name) {
            this.name = name.trim();
            return this;
        }

        /**
         * <p>Adds the provided dataset to the project that will be constructed</p>
         *
         * @param dataset The dataset to construct the project with
         * @return The builder instance
         */
        public Builder dataset(@NonNull final DatasetModel dataset) {
            this.datasets.add(dataset);
            return this;
        }

        /**
         * <p>Adds the provided datasets to the project that will be constructed</p>
         *
         * @param datasets The datasets to construct the project with
         * @return The builder instance
         */
        public Builder datasets(@NonNull final Collection<DatasetModel> datasets) {
            this.datasets.addAll(datasets);
            return this;
        }

        /**
         * <p>Returns the project constructed by this builder instance</p>
         *
         * @return A new project
         */
        public ProjectModel build() {
            return new ProjectModel(name, datasets);
        }
    }
}

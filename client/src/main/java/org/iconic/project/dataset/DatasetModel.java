package org.iconic.project.dataset;

import javafx.beans.property.SimpleStringProperty;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.iconic.ea.data.DataManager;
import org.iconic.project.Displayable;

import java.net.URI;
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
     * <p>Constructs a new DatasetModel with the provided name and absolute path.</p>
     *
     * @param name         The name of the dataset
     * @param absolutePath The absolute path to the dataset
     */
    public DatasetModel(@NonNull final String name, @NonNull final String absolutePath) {
        this.name = new SimpleStringProperty(name);
        this.absolutePath = new SimpleStringProperty(absolutePath);
        this.dataManager = new DataManager<>(Double.class, absolutePath);
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
    public Optional<URI> getIcon() {
        return Optional.empty();
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

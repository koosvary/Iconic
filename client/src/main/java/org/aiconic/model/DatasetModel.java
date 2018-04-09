package org.aiconic.model;

import javafx.beans.property.SimpleStringProperty;

import java.util.UUID;

/**
 * <p>
 * A model for datasets.
 * </p>
 */
public class DatasetModel {
    private final UUID id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty absolutePath;

    /**
     * <p>Constructs a new DatasetModel with the provided name and absolute path.</p>
     *
     * @return
     *      The absolute path to the dataset
     */
    public DatasetModel(String name, String absolutePath) {
        this.name = new SimpleStringProperty(name);
        this.absolutePath = new SimpleStringProperty(absolutePath);
        this.id = UUID.randomUUID();
    }

    /**
     * <p>Returns the file name of this dataset.</p>
     *
     * @return
     *      The file name of the dataset
     */
    public String getName() {
        return name.get();
    }

    /**
     * <p>Returns the name property of this dataset.</p>
     *
     * @return
     *      The name property of the dataset
     */
    final SimpleStringProperty nameProperty() {
        return name;
    }

    /**
     * <p>Returns the absolute path of this dataset.</p>
     *
     * @return
     *      The absolute path to the dataset
     */
    public String getAbsolutePath() {
        return absolutePath.get();
    }

    /**
     * <p>Returns the absolute path property of this dataset.</p>
     *
     * @return
     *      The absolute path property of the dataset
     */
    final SimpleStringProperty absolutePathProperty() {
        return absolutePath;
    }

    /**
     * <p>Sets the name of this dataset to the provided value.</p>
     *
     * @param name
     *      The new name for this dataset
     */
    public void setName(final String name) {
        this.nameProperty().set(name);
    }

    /**
     * <p>Sets the absolute path to this dataset to the provided value.</p>
     *
     * @param absolutePath
     *      The new absolute path for this dataset
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
}

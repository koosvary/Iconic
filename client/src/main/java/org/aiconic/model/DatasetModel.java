package org.aiconic.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * <p>
 * A model for datasets.
 * </p>
 */
public class DatasetModel {
    private final SimpleStringProperty name;
    private final SimpleStringProperty absolutePath;

    /**
     * <p>Constructs a new DatasetModel with the provided name and absolute path.</p>
     * @return
     *      The absolute path to the dataset
     */
    public DatasetModel(String name, String absolutePath) {
        this.name = new SimpleStringProperty(name);
        this.absolutePath = new SimpleStringProperty(absolutePath);
    }

    /**
     * <p>Returns the file name of this dataset.</p>
     * @return
     *      The file name of the dataset
     */
    public String getName() {
        return name.get();
    }

    final SimpleStringProperty nameProperty() {
        return name;
    }

    /**
     * <p>Returns the absolute path of this dataset.</p>
     * @return
     *      The absolute absolute path to the dataset
     */
    public String getAbsolutePath() {
        return absolutePath.get();
    }

    final SimpleStringProperty absolutePathProperty() {
        return absolutePath;
    }

    public void setName(final String name) {
        this.nameProperty().set(name);
    }

    public void setAbsolutePath(final String absolutePath) {
        this.absolutePathProperty().set(absolutePath);
    }
}

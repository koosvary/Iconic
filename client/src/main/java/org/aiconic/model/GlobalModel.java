package org.aiconic.model;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * <p>
 * A model for data that needs to be shared globally across the application.
 * </p>
 * <p>
 * A GlobalModel is a singleton.
 * </p>
 */
public enum GlobalModel {
    INSTANCE;

    private final ObjectProperty<DatasetModel> activeDataset;
    private final ObservableList<DatasetModel> datasets;

    /**
     * <p>
     * A private constructor for a GlobalModel.
     * </p>
     */
    private GlobalModel() {
        this.activeDataset = new SimpleObjectProperty<>(null);
        this.datasets = FXCollections.observableArrayList(dataset -> new Observable[]{
                dataset.absolutePathProperty(), dataset.nameProperty()
        });
        this.datasets.add(new DatasetModel("Home", "Home"));
    }

    /**
     * <p>
     * Returns the current active dataset property of this GlobalModel.
     * </p>
     *
     * <p>
     * Properties may have listeners set on them.
     * </p>
     * @return
     *      The active dataset property
     */
    public final ObjectProperty<DatasetModel> activeDatasetProperty() {
        return activeDataset;
    }

    /**
     * <p>
     * Returns a list of imported datasets owned by this GlobalModel.
     * </p>
     * @return
     *      The list of imported datasets
     */
    public ObservableList<DatasetModel> getDatasets() {
        return datasets;
    }

    /**
     * <p>
     * Returns the current active dataset of this GlobalModel.
     * </p>
     * @return
     *      The active dataset
     */
    public DatasetModel getActiveDataset() {
        return activeDatasetProperty().get();
    }

    /**
     * <p>
     * Sets the current active dataset of this GlobalModel.
     * </p>
     * @param activeDataset
     *      The new active dataset
     */
    public void setActiveDataset(final DatasetModel activeDataset) {
        activeDatasetProperty().set(activeDataset);
    }
}

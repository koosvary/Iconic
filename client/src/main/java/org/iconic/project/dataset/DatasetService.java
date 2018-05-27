package org.iconic.project.dataset;

import javafx.collections.ObservableMap;

import java.util.UUID;

/**
 * <p>
 * A dataset service that maintains a dataset map.
 * </p>
 */
public interface DatasetService {
    /**
     * <p>
     * Returns the property of all datasets attached to this service.
     * </p>
     *
     * <p>
     * Properties may have listeners set on them.
     * </p>
     *
     * @return The property of datasets attached to the service
     */
    ObservableMap<UUID, DatasetModel> datasetsProperty();
}

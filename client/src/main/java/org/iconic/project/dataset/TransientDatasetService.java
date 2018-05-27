package org.iconic.project.dataset;

import com.google.inject.Singleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * {@inheritDoc}
 * <p>
 * A transient dataset service doesn't persist its datasets.
 * </p>
 */
@Log4j2
@Singleton
public class TransientDatasetService implements DatasetService {
    private final ObservableMap<UUID, DatasetModel> datasets;

    /**
     * <p>
     * A constructor for a TransientDatasetService.
     * </p>
     */
    public TransientDatasetService() {
        Map<UUID, DatasetModel> map = new HashMap<>();

        this.datasets = FXCollections.observableMap(map);
    }

    /**
     * {@inheritDoc}
     */
    public ObservableMap<UUID, DatasetModel> datasetsProperty() {
        return datasets;
    }
}

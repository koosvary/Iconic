package org.iconic.project.search;

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
 * A transient search service doesn't persist its searches.
 * </p>
 */
@Log4j2
@Singleton
public class TransientSearchService implements SearchService {
    private final ObservableMap<UUID, SearchModel> searches;

    /**
     * <p>
     * A constructor for a TransientSearchService.
     * </p>
     */
    public TransientSearchService() {
        Map<UUID, SearchModel> map = new HashMap<>();

        this.searches = FXCollections.observableMap(map);
    }

    /**
     * {@inheritDoc}
     */
    public ObservableMap<UUID, SearchModel> searchesProperty() {
        return searches;
    }
}

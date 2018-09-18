package org.iconic.project.search;

import javafx.collections.ObservableMap;
import org.iconic.project.search.io.SearchExecutor;

import java.util.UUID;

/**
 * <p>
 * A search service that maintains a search map.
 * </p>
 */
public interface SearchService {
    /**
     * <p>
     * Returns the property of all searches attached to this service.
     * </p>
     *
     * <p>
     * Properties may have listeners set on them.
     * </p>
     *
     * @return The property of searches attached to the service
     */
    ObservableMap<UUID, SearchExecutor> searchesProperty();
}

package org.iconic.views;

import org.iconic.View;

import java.util.Map;

/**
 * <p>A view service that maintains a list of views</p>
 */
public interface ViewService {
    /**
     * <p>Returns a list of views owned by this service</p>
     *
     * @return The list of views
     */
    Map<String, View> getViews();

    /**
     * <p>Adds the provided key value pair to this service's map of views</p>
     *
     * @param key
     * @param value
     */
    void put(final String key, final View value);
}

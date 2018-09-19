package org.iconic.views;

import com.google.inject.Singleton;
import org.iconic.View;

import java.util.HashMap;
import java.util.Map;

/**
 * {@inheritDoc}
 */
@Singleton
public class DefaultViewService implements ViewService {
    private final Map<String, View> views;

    public DefaultViewService() {
        this.views = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, View> getViews() {
        return views;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final String key, final View value) {
        getViews().put(key, value);
    }
}

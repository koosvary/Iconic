/**
 * Copyright 2018 Iconic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

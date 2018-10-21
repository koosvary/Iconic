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

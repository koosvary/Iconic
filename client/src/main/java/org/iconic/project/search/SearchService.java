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
package org.iconic.project.search;

import javafx.collections.ObservableMap;
import org.iconic.project.search.io.SearchExecutor;

import java.util.UUID;

/**
 * A search service that maintains a search map.
 *
 * @deprecated Searches should now be accessed through
 * {@link org.iconic.workspace.WorkspaceService the workspace}.
 */
@Deprecated
public interface SearchService {
    /**
     * Returns the property of all searches attached to this service.
     * <p>
     * Properties may have listeners set on them.
     *
     * @return The property of searches attached to the service
     */
    ObservableMap<UUID, SearchExecutor<?>> searchesProperty();
}

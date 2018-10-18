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
package org.iconic.workspace;

import javafx.beans.property.ObjectProperty;
import org.iconic.project.Displayable;

/**
 * <p>
 * A workspace service that tracks the active workspace item.
 * </p>
 */
public interface WorkspaceService {
    /**
     * <p>
     * Returns the current active workspace item property.
     * </p>
     *
     * <p>
     * Properties may have listeners set on them.
     * </p>
     *
     * @return The active workspace item property
     */
    ObjectProperty<Displayable> activeWorkspaceItemProperty();

    /**
     * <p>
     * Returns the current active workspace item.
     * </p>
     *
     * @return The active workspace item
     */
    Displayable getActiveWorkspaceItem();

    /**
     * <p>
     * Sets the current active workspace item.
     * </p>
     *
     * @param activeWorkspaceItem The new active workspace item
     */
    void setActiveWorkspaceItem(final Displayable activeWorkspaceItem);
}

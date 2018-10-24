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

import com.google.inject.Singleton;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.extern.log4j.Log4j2;
import org.iconic.project.Displayable;

/**
 * {@inheritDoc}
 */
@Singleton
@Log4j2
public class DefaultWorkspaceService implements WorkspaceService {
    private final ObjectProperty<Displayable> activeWorkspaceItem;

    /**
     * <p>
     * aConstructs a new DefaultWorkspaceService
     *
     */
    public DefaultWorkspaceService() {
        activeWorkspaceItem = new SimpleObjectProperty<>(null);
    }

    /**
     * {@inheritDoc}
     */
    public final ObjectProperty<Displayable> activeWorkspaceItemProperty() {
        return activeWorkspaceItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Displayable getActiveWorkspaceItem() {
        return activeWorkspaceItemProperty().get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActiveWorkspaceItem(final Displayable activeWorkspaceItem) {
        activeWorkspaceItemProperty().set(activeWorkspaceItem);
    }
}

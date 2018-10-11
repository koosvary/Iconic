/**
 * Copyright (C) 2018 Iconic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
     * </p>
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

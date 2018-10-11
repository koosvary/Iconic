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

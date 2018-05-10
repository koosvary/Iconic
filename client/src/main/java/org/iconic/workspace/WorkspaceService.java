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

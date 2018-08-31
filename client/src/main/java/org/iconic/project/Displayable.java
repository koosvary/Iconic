package org.iconic.project;

import java.util.Optional;

/**
 * An interface for non-rendered objects that may be displayed through a user interface.
 */
public interface Displayable {
    /**
     * <p>Returns the label for this object</p>
     *
     * @return The label of the object
     */
    String getLabel();

    /**
     * <p>Returns the icon for for this object if present</p>
     *
     * @return The icon of the object if present
     */
    Optional<Enum<?>> getIcon();
}

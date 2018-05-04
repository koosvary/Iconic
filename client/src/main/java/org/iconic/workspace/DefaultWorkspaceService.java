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

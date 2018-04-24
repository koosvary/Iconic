package org.iconic.global;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import lombok.extern.log4j.Log4j2;
import org.iconic.project.Displayable;
import org.iconic.project.ProjectModel;
import org.iconic.project.search.SearchModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * A model for data that needs to be shared globally across the application.
 * </p>
 * <p>
 * A GlobalModel is a singleton.
 * </p>
 */
@Log4j2
public enum GlobalModel {
    INSTANCE;
    private final ObservableMap<UUID, SearchModel> searches;
    private final ObjectProperty<Displayable> activeProjectItem;
    private final ObservableList<ProjectModel> projects;

    /**
     * <p>
     * A private constructor for a GlobalModel.
     * </p>
     */
    GlobalModel() {
        Map<UUID, SearchModel> map = new HashMap<>();

        this.searches = FXCollections.observableMap(map);
        this.activeProjectItem = new SimpleObjectProperty<>(null);

        projects = FXCollections.observableArrayList(project -> new Observable[]{
                project.nameProperty(), project.getDatasets()
        });
    }

    /**
     * <p>Returns a GlobalModel instance</p>
     *
     * @return A GlobalModel instance
     */
    public static GlobalModel getInstance() {
        return INSTANCE;
    }

    /**
     * <p>
     * Returns the current active project property of this global model.
     * </p>
     *
     * <p>
     * Properties may have listeners set on them.
     * </p>
     *
     * @return The active project property
     */
    public final ObjectProperty<Displayable> activeProjectItemProperty() {
        return activeProjectItem;
    }

    /**
     * <p>
     * Returns a list of projects owned by this global model.
     * </p>
     *
     * @return The list of projects
     */
    public ObservableList<ProjectModel> getProjects() {
        return projects;
    }

    /**
     * <p>
     * Returns the current active project of this global model.
     * </p>
     *
     * @return The active project
     */
    public Displayable getActiveProjectItem() {
        return activeProjectItemProperty().get();
    }

    /**
     * <p>
     * Sets the current active project item of this global model.
     * </p>
     *
     * @param activeWorkspaceItem The new active project item
     */
    public void setActiveProjectItem(final Displayable activeWorkspaceItem) {
        activeProjectItemProperty().set(activeWorkspaceItem);
    }

    /**
     * <p>
     * Returns the property of all searches attached to this global model.
     * </p>
     *
     * <p>
     * Properties may have listeners set on them.
     * </p>
     *
     * @return The property of searches attached to the global model
     */
    public ObservableMap<UUID, SearchModel> searchesProperty() {
        return searches;
    }
}

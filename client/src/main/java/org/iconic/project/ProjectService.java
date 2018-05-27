package org.iconic.project;

import javafx.collections.ObservableList;

/**
 * <p>
 * A project service that maintains a list of projects.
 * </p>
 */
public interface ProjectService {
    /**
     * <p>
     * Returns a list of projects owned by this service.
     * </p>
     *
     * @return The list of projects
     */
     ObservableList<ProjectModel> getProjects();
}

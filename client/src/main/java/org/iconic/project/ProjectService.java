package org.iconic.project;

import javafx.collections.ObservableList;

import java.util.Optional;

/**
 * <p>
 * A project service that maintains a list of projects.
 * </p>
 */
public interface ProjectService {
    /**
     * <p>Returns a list of projects owned by this service</p>
     *
     * @return The list of projects
     */
    ObservableList<ProjectModel> getProjects();

    /**
     * <p>Returns the parent project of the provided item if one exists</p>
     *
     * @param item the child to search for
     * @return The parent of the provided item if available
     */
    Optional<ProjectModel> findParentProject(final Displayable item);
}

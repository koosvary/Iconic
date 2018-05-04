package org.iconic.project;

import com.google.inject.Singleton;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.log4j.Log4j2;

/**
 * {@inheritDoc}
 * <p>
 * A transient project service doesn't persist its projects.
 * </p>
 */
@Singleton
@Log4j2
public class TransientProjectService implements ProjectService {
    private final ObservableList<ProjectModel> projects;

    /**
     * <p>
     * Constructs a new TransientProjectService
     * </p>
     */
    public TransientProjectService() {
        this.projects = FXCollections.observableArrayList(project -> new Observable[]{
                project.nameProperty(), project.getDatasets()
        });
    }

    /**
     * {@inheritDoc}
     */
    public ObservableList<ProjectModel> getProjects() {
        return projects;
    }

}

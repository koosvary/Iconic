package org.iconic.project;

import com.google.inject.Singleton;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.extern.log4j.Log4j2;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.config.SearchConfigurationModel;

import java.util.Optional;

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
    @Override
    public ObservableList<ProjectModel> getProjects() {
        return projects;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ProjectModel> findParentProject(final Displayable item) {
        FilteredList<ProjectModel> parents = null;

        if (item instanceof SearchConfigurationModel) {
            parents = getProjects().filtered(
                    p -> p.getSearchConfigurations().contains(item)
            );
        } else if (item instanceof DatasetModel){
            parents = getProjects().filtered(
                    p -> p.getDatasets().contains(item)
            );
        }

        return (parents == null || parents.size() < 1)
                ? Optional.empty()
                : Optional.of(parents.get(0));
    }
}

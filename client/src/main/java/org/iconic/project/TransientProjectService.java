/**
 * Copyright 2018 Iconic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 *
 */
@Singleton
@Log4j2
public class TransientProjectService implements ProjectService {
    private final ObservableList<ProjectModel> projects;

    /**
     * <p>
     * Constructs a new TransientProjectService
     *
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

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

import javafx.collections.ObservableList;

import java.util.Optional;

/**
 * <p>
 * A project service that maintains a list of projects.
 *
 */
public interface ProjectService {
    /**
     * <p>Returns a list of projects owned by this service
     *
     * @return The list of projects
     */
    ObservableList<ProjectModel> getProjects();

    /**
     * <p>Returns the parent project of the provided item if one exists
     *
     * @param item the child to search for
     * @return The parent of the provided item if available
     */
    Optional<ProjectModel> findParentProject(final Displayable item);
}

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
package org.iconic.project.dataset;

import com.google.inject.Singleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * {@inheritDoc}
 * <p>
 * A transient dataset service doesn't persist its datasets.
 *
 */
@Log4j2
@Singleton
public class TransientDatasetService implements DatasetService {
    private final ObservableMap<UUID, DatasetModel> datasets;

    /**
     * <p>
     * A constructor for a TransientDatasetService.
     *
     */
    public TransientDatasetService() {
        Map<UUID, DatasetModel> map = new HashMap<>();

        this.datasets = FXCollections.observableMap(map);
    }

    /**
     * {@inheritDoc}
     */
    public ObservableMap<UUID, DatasetModel> datasetsProperty() {
        return datasets;
    }
}

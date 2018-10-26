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
package org.iconic.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.iconic.project.dataset.DatasetModel;

public class DatasetComboBox extends ComboBox<DatasetModel> {
    public DatasetComboBox() {
        this(FXCollections.emptyObservableList());
    }

    public DatasetComboBox(ObservableList<DatasetModel> items) {
        super(items);

        Callback<ListView<DatasetModel>, ListCell<DatasetModel>> cellFactory =
                new Callback<ListView<DatasetModel>, ListCell<DatasetModel>>() {
                    @Override
                    public ListCell<DatasetModel> call(ListView<DatasetModel> param) {
                        return new ListCell<DatasetModel>() {
                            @Override
                            protected void updateItem(DatasetModel item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item == null || empty) {
                                    setGraphic(null);
                                } else {
                                    setText(item.getLabel());
                                }
                            }
                        };
                    }
                };

        this.setButtonCell(cellFactory.call(null));
        this.setCellFactory(cellFactory);
    }
}

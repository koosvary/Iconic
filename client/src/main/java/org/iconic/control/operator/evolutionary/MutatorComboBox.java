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
package org.iconic.control.operator.evolutionary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.evolutionary.mutation.Mutator;
import org.iconic.project.dataset.DatasetModel;

public class MutatorComboBox<T extends Chromosome<Double>> extends ComboBox<Mutator<T, Double>> {
    public MutatorComboBox() {
        this(FXCollections.emptyObservableList());
    }

    public MutatorComboBox(ObservableList<Mutator<T, Double>> items) {
        super(items);

        Callback<ListView<Mutator<T, Double>>, ListCell<Mutator<T, Double>>> cellFactory =
                new Callback<ListView<Mutator<T, Double>>, ListCell<Mutator<T, Double>>>() {
                    @Override
                    public ListCell<Mutator<T, Double>> call(ListView<Mutator<T, Double>> param) {
                        return new ListCell<Mutator<T, Double>>() {
                            @Override
                            protected void updateItem(Mutator<T, Double> item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item == null || empty) {
                                    setGraphic(null);
                                } else {
                                    setText(item.toString());
                                }
                            }
                        };
                    }
                };

        this.setButtonCell(cellFactory.call(null));
        this.setCellFactory(cellFactory);
        this.setMinWidth(250);
        this.setMaxWidth(250);
    }
}

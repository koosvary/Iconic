/**
 * Copyright (C) 2018 Iconic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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

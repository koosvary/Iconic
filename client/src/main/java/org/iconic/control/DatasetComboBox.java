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

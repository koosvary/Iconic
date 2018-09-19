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

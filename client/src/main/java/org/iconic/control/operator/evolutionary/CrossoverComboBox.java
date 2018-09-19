package org.iconic.control.operator.evolutionary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.operator.evolutionary.crossover.Crossover;
import org.iconic.ea.operator.evolutionary.mutation.Mutator;

public class CrossoverComboBox<T extends Chromosome<Double>> extends ComboBox<Crossover<T, Double>> {
    public CrossoverComboBox() {
        this(FXCollections.emptyObservableList());
    }

    public CrossoverComboBox(ObservableList<Crossover<T, Double>> items) {
        super(items);

        Callback<ListView<Crossover<T, Double>>, ListCell<Crossover<T, Double>>> cellFactory =
                new Callback<ListView<Crossover<T, Double>>, ListCell<Crossover<T, Double>>>() {
                    @Override
                    public ListCell<Crossover<T, Double>> call(ListView<Crossover<T, Double>> param) {
                        return new ListCell<Crossover<T, Double>>() {
                            @Override
                            protected void updateItem(Crossover<T, Double> item, boolean empty) {
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

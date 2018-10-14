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
package org.iconic.project.search.config;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;
import org.iconic.control.LabelledSlider;
import org.iconic.control.operator.evolutionary.CrossoverComboBox;
import org.iconic.control.operator.evolutionary.MutatorComboBox;
import org.iconic.ea.chromosome.cartesian.CartesianChromosome;
import org.iconic.ea.operator.evolutionary.crossover.Crossover;
import org.iconic.ea.operator.evolutionary.mutation.Mutator;
import org.iconic.ea.operator.evolutionary.mutation.cgp.CartesianSingleActiveMutator;
import org.iconic.project.Displayable;
import org.iconic.workspace.WorkspaceService;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class CgpConfigurationController implements Initializable {
    private final WorkspaceService workspaceService;

    private CgpConfigurationModel previousModel;

    @FXML
    private TextField tfPopulationSize;
    @FXML
    private TextField tfNumGenerations;
    @FXML
    private TextField tfNumOutputs;
    @FXML
    private TextField tfNumColumns;
    @FXML
    private TextField tfNumRows;
    @FXML
    private TextField tfNumLevelsBack;
    @FXML
    private LabelledSlider sldrMutationRate;
    @FXML
    private LabelledSlider sldrCrossoverRate;
    @FXML
    MutatorComboBox<CartesianChromosome<Double>> cbMutators;
    @FXML
    CrossoverComboBox<CartesianChromosome<Double>> cbCrossovers;

    @Inject
    public CgpConfigurationController(final WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
        InvalidationListener selectionChangedListener = observable -> updateView();
        getWorkspaceService().activeWorkspaceItemProperty().addListener(selectionChangedListener);
    }

    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        updateView();
    }

    private void updateView() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        if (previousModel != null) {
            Bindings.unbindBidirectional(tfPopulationSize.textProperty(), previousModel.populationSizeProperty());
            Bindings.unbindBidirectional(tfNumGenerations.textProperty(), previousModel.numGenerationsProperty());
            Bindings.unbindBidirectional(tfNumOutputs.textProperty(), previousModel.numOutputsProperty());
            Bindings.unbindBidirectional(tfNumColumns.textProperty(), previousModel.numColumnsProperty());
            Bindings.unbindBidirectional(tfNumRows.textProperty(), previousModel.numRowsProperty());
            Bindings.unbindBidirectional(tfNumLevelsBack.textProperty(), previousModel.numLevelsBackProperty());
        }

        if (!(item instanceof CgpConfigurationModel)) {
            return;
        }

        CgpConfigurationModel configModel = (CgpConfigurationModel) item;
        previousModel = configModel;

        @SuppressWarnings("unchecked")
        ObservableList<Mutator<CartesianChromosome<Double>, Double>> mutators =
                FXCollections.observableArrayList(
                        new CartesianSingleActiveMutator<>()
                );

        @SuppressWarnings("unchecked")
        ObservableList<Crossover<CartesianChromosome<Double>, Double>> crossovers =
                FXCollections.emptyObservableList();

        cbMutators.setItems(mutators);
        cbCrossovers.setItems(crossovers);

        bindTextProperty(configModel.populationSizeProperty(), tfPopulationSize.textProperty());
        bindTextProperty(configModel.numGenerationsProperty(), tfNumGenerations.textProperty());
        bindTextProperty(configModel.numOutputsProperty(), tfNumOutputs.textProperty());
        bindTextProperty(configModel.numColumnsProperty(), tfNumColumns.textProperty());
        bindTextProperty(configModel.numRowsProperty(), tfNumRows.textProperty());
        bindTextProperty(configModel.numLevelsBackProperty(), tfNumLevelsBack.textProperty());

        sldrCrossoverRate.getSlider().valueProperty().unbind();
        sldrMutationRate.getSlider().valueProperty().unbind();
        sldrCrossoverRate.getSlider().valueProperty().bindBidirectional(configModel.crossoverRateProperty());
        sldrMutationRate.getSlider().valueProperty().bindBidirectional(configModel.mutationRateProperty());

        disableControlIfEmpty(cbMutators, mutators);
        disableControlIfEmpty(cbCrossovers, crossovers);
    }

    private void bindTextProperty(
            final Property<Number> property,
            final StringProperty field
    ) {
        Bindings.bindBidirectional(
                field,
                property,
                new NumberStringConverter()
        );
    }

    private void disableControlIfEmpty(final Control control, Collection<?> options) {
        if (options.size() < 1) {
            control.setDisable(true);
        } else {
            control.setDisable(false);
        }
    }

    /**
     * <p>Returns the workspace service of this controller</p>
     *
     * @return the workspace service of the controller
     */
    private WorkspaceService getWorkspaceService() {
        return workspaceService;
    }
}
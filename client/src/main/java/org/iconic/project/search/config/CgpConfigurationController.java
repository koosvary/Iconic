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
        cbMutators.getSelectionModel().selectFirst();

        cbCrossovers.setItems(crossovers);
        cbCrossovers.getSelectionModel().selectFirst();

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
     * <p>Returns the workspace service of this controller
     *
     * @return the workspace service of the controller
     */
    private WorkspaceService getWorkspaceService() {
        return workspaceService;
    }
}

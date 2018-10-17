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
import org.iconic.ea.chromosome.expression.ExpressionChromosome;
import org.iconic.ea.operator.evolutionary.crossover.Crossover;
import org.iconic.ea.operator.evolutionary.crossover.gep.SimpleExpressionCrossover;
import org.iconic.ea.operator.evolutionary.mutation.Mutator;
import org.iconic.ea.operator.evolutionary.mutation.gep.ExpressionMutator;
import org.iconic.project.Displayable;
import org.iconic.workspace.WorkspaceService;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class GepConfigurationController implements Initializable {
    private final WorkspaceService workspaceService;
    private GepConfigurationModel previousModel;

    @FXML
    private TextField tfPopulationSize;
    @FXML
    private TextField tfNumGenerations;
    @FXML
    private TextField tfHeadLength;
    @FXML
    private LabelledSlider sldrMutationRate;
    @FXML
    private LabelledSlider sldrCrossoverRate;
    @FXML
    private MutatorComboBox<ExpressionChromosome<Double>> cbMutators;
    @FXML
    private CrossoverComboBox<ExpressionChromosome<Double>> cbCrossovers;

    @Inject
    public GepConfigurationController(final WorkspaceService workspaceService) {
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
            Bindings.unbindBidirectional(tfHeadLength.textProperty(), previousModel.headLengthProperty());
        }

        if (!(item instanceof GepConfigurationModel)) {
            return;
        }

        GepConfigurationModel configModel = (GepConfigurationModel) item;
        previousModel = configModel;

                @SuppressWarnings("unchecked")
        ObservableList<Mutator<ExpressionChromosome<Double>, Double>> mutators =
                FXCollections.observableArrayList(
                        new ExpressionMutator<>()
                );

        @SuppressWarnings("unchecked")
        ObservableList<Crossover<ExpressionChromosome<Double>, Double>> crossovers =
                FXCollections.observableArrayList(
                        new SimpleExpressionCrossover<>()
                );

        cbMutators.setItems(mutators);
        cbCrossovers.setItems(crossovers);

        bindTextProperty(configModel.populationSizeProperty(), tfPopulationSize.textProperty());
        bindTextProperty(configModel.numGenerationsProperty(), tfNumGenerations.textProperty());
        bindTextProperty(configModel.headLengthProperty(), tfHeadLength.textProperty());

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

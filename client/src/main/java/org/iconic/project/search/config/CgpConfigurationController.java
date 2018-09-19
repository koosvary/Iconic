package org.iconic.project.search.config;

import com.google.inject.Inject;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
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

        if (!(item instanceof CgpConfigurationModel)) {
            return;
        }

        CgpConfigurationModel configModel = (CgpConfigurationModel) item;

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

        tfNumOutputs.setText(String.valueOf(configModel.getNumOutputs()));
        tfNumOutputs.textProperty().addListener((obs, oldValue, newValue) -> {
            int i;
            try {
                i = Integer.parseInt(newValue);
                configModel.setNumOutputs(i);
            }
            catch (NumberFormatException ex) {
                // Do nothing
            }
        });

        tfNumColumns.setText(String.valueOf(configModel.getNumColumns()));
        tfNumColumns.textProperty().addListener((obs, oldValue, newValue) -> {
            int i;
            try {
                i = Integer.parseInt(newValue);
                configModel.setNumColumns(i);
            }
            catch (NumberFormatException ex) {
                // Do nothing
            }
        });

        tfNumRows.setText(String.valueOf(configModel.getNumRows()));
        tfNumRows.textProperty().addListener((obs, oldValue, newValue) -> {
            int i;
            try {
                i = Integer.parseInt(newValue);
                configModel.setNumRows(i);
            }
            catch (NumberFormatException ex) {
                // Do nothing
            }
        });

        tfNumLevelsBack.setText(String.valueOf(configModel.getNumLevelsBack()));
        tfNumLevelsBack.textProperty().addListener((obs, oldValue, newValue) -> {
            int i;
            try {
                i = Integer.parseInt(newValue);
                configModel.setNumLevelsBack(i);
            }
            catch (NumberFormatException ex) {
                // Do nothing
            }
        });

        sldrCrossoverRate.getSlider().valueProperty().unbind();
        sldrMutationRate.getSlider().valueProperty().unbind();
        sldrCrossoverRate.getSlider().valueProperty().bindBidirectional(configModel.crossoverRateProperty());
        sldrMutationRate.getSlider().valueProperty().bindBidirectional(configModel.mutationRateProperty());

        disableControlIfEmpty(cbMutators, mutators);
        disableControlIfEmpty(cbCrossovers, crossovers);
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

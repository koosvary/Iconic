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

        if (!(item instanceof GepConfigurationModel)) {
            return;
        }

        GepConfigurationModel configModel = (GepConfigurationModel) item;

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

        // TODO: switch to a TextFormatter
        tfHeadLength.setText(String.valueOf(configModel.getHeadLength()));
        tfHeadLength.textProperty().addListener((obs, oldValue, newValue) -> {
            int i;
            try {
                i = Integer.parseInt(newValue);
                configModel.setHeadLength(i);
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

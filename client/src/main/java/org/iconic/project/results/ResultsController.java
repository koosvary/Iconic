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
package org.iconic.project.results;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.log4j.Log4j2;
import org.iconic.control.WorkspaceTab;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.chromosome.expression.ExpressionChromosome;
import org.iconic.ea.operator.primitive.FunctionalPrimitive;
import org.iconic.project.Displayable;
import org.iconic.project.dataset.DatasetModel;
import org.iconic.project.search.io.SearchExecutor;
import org.iconic.project.search.SearchService;
import org.iconic.project.search.SolutionStorage;
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.workspace.WorkspaceService;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A controller for the Results view
 */
@Log4j2
public class ResultsController implements Initializable {

    private final WorkspaceService workspaceService;

    private SolutionStorage<Chromosome<?>> storage;
    private SearchConfigurationModel lastSearch;
    private InvalidationListener resultAddedListener;

    @FXML
    private WorkspaceTab resultsTab;
    @FXML
    private TableView<ResultDisplay> solutionsTableView;

    /**
     * Constructs a new ResultsController that attaches an invalidation listener onto the workspace service.
     */
    @Inject
    public ResultsController(final WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;

        // Update the workspace whenever the active dataset changes
        resultAddedListener = observable -> updateWorkspace();
        getWorkspaceService().activeWorkspaceItemProperty().addListener(resultAddedListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL arg1, ResourceBundle arg2) {
        updateWorkspace();

        resultsTab.setOnSelectionChanged(event -> updateWorkspace());
    }

    /**
     * Calls the main thread to update the workspace when it can.
     */
    private synchronized void updateWorkspace() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // If no dataset, stop what you're doing.
        if (!(item instanceof SearchConfigurationModel)) {
            // TODO clear the UI?
            return;
        }

        SearchConfigurationModel search = (SearchConfigurationModel) item;
        search.getSearchExecutor().ifPresent(executor -> {
            if (search != lastSearch) {
                // If a search is running, use that current one for results. Else use the last search
                //noinspection unchecked
                storage = (SolutionStorage<Chromosome<?>>) executor.getSolutionStorage();
                storage.getSolutions().addListener(resultAddedListener);
                lastSearch = search;
            }

        });
        if (storage == null) {
            // No storage? No worries
            return;
        }

        Platform.runLater(this::updateWorkspaceMainThread);
    }

    /**
     * Updates the workspace to match the current active dataset.
     */
    private synchronized void updateWorkspaceMainThread() {
        List<ResultDisplay> resultDisplays = new ArrayList<>();
        for (Map.Entry<Integer, List<Chromosome<?>>> entry : storage.getSolutions().entrySet()) {
            Chromosome<?> result = entry.getValue().get(0);
            resultDisplays.add(new ResultDisplay(result.getSize(), result.getFitness(), result.toString()));
            resultDisplays.add(new ResultDisplay(result.getSize(), result.getFitness(), result.simplifyExpression(
                    result.getExpression(result.toString(), new ArrayList<>(lastSearch.getEnabledPrimitives()), true)
            )));
        }

        // Add all the results as FX observables
        solutionsTableView.setItems(FXCollections.observableArrayList(resultDisplays));

        TableColumn<ResultDisplay, Integer> sizeCol = new TableColumn<>("Size");
        TableColumn<ResultDisplay, Double> fitnessCol = new TableColumn<>("Fitness");
        TableColumn<ResultDisplay, String> solutionCol = new TableColumn<>("Solution");

        // Set conversion factories for data types into string
        sizeCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        fitnessCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        // Set where the values come from
        sizeCol.setCellValueFactory(cellData -> cellData.getValue().sizeProperty().asObject());
        fitnessCol.setCellValueFactory(cellData -> cellData.getValue().fitProperty().asObject());
        solutionCol.setCellValueFactory(cellData -> cellData.getValue().solutionProperty());

        // Set the columns to be these ones
        solutionsTableView.getColumns().setAll(sizeCol, fitnessCol, solutionCol);
    }

    /**
     * Get the workspace service
     * @return Workspace service
     */
    private WorkspaceService getWorkspaceService() {
        return workspaceService;
    }
}

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
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCombination;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.log4j.Log4j2;
import org.iconic.control.WorkspaceTab;
import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.data.FeatureClass;
import org.iconic.project.Displayable;
import org.iconic.project.search.SolutionStorage;
import org.iconic.project.search.config.SearchConfigurationModel;
import org.iconic.project.search.io.SearchExecutor;
import org.iconic.workspace.WorkspaceService;

import java.net.URL;
import java.util.*;

/**
 * A controller for the Results view
 */
@Log4j2
public class ResultsController implements Initializable {

    private final WorkspaceService workspaceService;

    private SolutionStorage<Chromosome<?>> storage;
    private SearchConfigurationModel model;
    private SearchExecutor<?> lastSearch;
    private InvalidationListener resultAddedListener;

    @FXML
    private WorkspaceTab resultsTab;
    @FXML
    private TableView<ResultDisplay> solutionsTableView;
    @FXML
    private LineChart<Number,Number> solutionsPlot;


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
        resultsTab.setOnSelectionChanged(event -> updateWorkspace());

        // Listener for the solutions being clicked in the table
        solutionsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Platform.runLater(this::graphExpectedValues);
            }
        });

        setupContextMenu();
        updateWorkspace();
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

        model = (SearchConfigurationModel) item;
        if (model.getSearchExecutor().isPresent()) {
            SearchExecutor<?> search = model.getSearchExecutor().get();
            if (search.isRunning() && search != lastSearch) {
                // If a search is running, use that current one for results. Else use the last search
                //noinspection unchecked
                storage = (SolutionStorage<Chromosome<?>>) search.getSolutionStorage();
                storage.getSolutions().addListener(resultAddedListener);
                lastSearch = search;
            }
        }
        if (storage == null) {
            // No storage? No worries
            return;
        }

        Platform.runLater(this::updateResultsTable);
    }

    private void graphExpectedValues() {
        Displayable item = getWorkspaceService().getActiveWorkspaceItem();

        // If no dataset, stop what you're doing.
        if (!(item instanceof SearchConfigurationModel)) {
            return;
        }

        SearchConfigurationModel search = (SearchConfigurationModel) item;

        // Dataset model is not present
        if (!search.getDatasetModel().isPresent()) {
            return;
        }

        // Get the dataset
        DataManager dataManager = search.getDatasetModel().get().getDataManager();
        HashMap<String, FeatureClass<Number>> dataset = dataManager.getDataset();

        // If the dataset doesnt exist for some reason
        if (dataset == null) {
            log.info("There is no dataset so the expected values cannot be checked");
            return;
        }

        // Get the first output Feature we find
        FeatureClass<Number> outputFeatureClass = null;
        for (FeatureClass<Number> featureClass : dataset.values()) {
            if (featureClass.isOutput()) {
                outputFeatureClass = featureClass;
                break;
            }
        }

        // No output feature class found.
        if (outputFeatureClass == null) {
            log.info("No output feature class was found in the dataset to be used to graph expected results vs actual results");
            return;
        }

        // Create the two new lines, with names in the legend
        XYChart.Series<Number, Number> seriesExpected = new XYChart.Series<>();
        XYChart.Series<Number, Number> seriesActual = new XYChart.Series<>();
        seriesExpected.setName("Expected Values");
        seriesActual.setName("Actual Values");

        // Get all the expected values
        List<Number> samples = outputFeatureClass.getSamples();
        for (int i = 0; i < samples.size(); i++) {
            seriesExpected.getData().add(new XYChart.Data<>(i, samples.get(i)));
        }

        /*
            ***** Actual Values calculated from the solution ****
         */

        // These are all the solutions we have stored and that are currently displayed in the solutions table
        ObservableMap<Integer, List<Chromosome<?>>> solutions = storage.getSolutions();

        // This is the row the user selected in that solutions table
        ResultDisplay row = solutionsTableView.getSelectionModel().getSelectedItem();

        // This will be the referenced chromosome that was selected if it finds a match
        Chromosome<?> selectedChromosome = null;

        for (Integer size : solutions.keySet()) {
            List<Chromosome<?>> chromosomeList = solutions.get(size);

            // This is all the chromosomes with the same size of 'x'
            for (Chromosome<?> chromosome : chromosomeList) {
                // Chromosome simplified expression
                String simplifiedChromosome = chromosome.simplifyExpression(chromosome.getExpression(chromosome.toString(), new ArrayList<>(model.getEnabledPrimitives()), true));

                // If the chromosome equals the selected chromosome
                if (simplifiedChromosome.equals(row.getSolution())) {
                    selectedChromosome = chromosome;
                    break;
                }
            }

            // If the chromosome has been found exit the loop
            if (selectedChromosome != null) {
                break;
            }
        }

        // If no chromosome was found (aka the block is missing and the strings dont match)
        if (selectedChromosome == null) {
            log.info("There was no chromosome found in the storage");
            solutionsPlot.getData().clear();
            return;
        }

        // Take the selected chromosome and run the evaluate function on it
        List<Map<Integer, Number>> results = selectedChromosome.evaluate(dataManager);
        if (results.isEmpty()) {
            log.debug("actualValues size is empty");
            return;
        }

        // Go through all the results, There are multiple outputs
        for (int i = 0; i < results.size(); i++) {
            // This is the list of results for all the different outputs
            Map<Integer, Number> rowOfResults = results.get(i);

            // Keys (They are random numbers, i think it might be the node id's of the output nodes in cgp
            Integer[] keys = rowOfResults.keySet().toArray(new Integer[0]);

            // My shit attempt to only display the "first" element in the list
            seriesActual.getData().add(new XYChart.Data<>(i, rowOfResults.get(keys[0])));
        }

        // Update the graph
        solutionsPlot.setAnimated(false);
        solutionsPlot.setCreateSymbols(true);
        solutionsPlot.getData().clear();
        //noinspection unchecked
        solutionsPlot.getData().addAll(seriesActual, seriesExpected);
    }

    /**
     * Updates the workspace to match the current active dataset.
     */
    private synchronized void updateResultsTable() {
        List<ResultDisplay> resultDisplays = new ArrayList<>();
        for (Map.Entry<Integer, List<Chromosome<?>>> entry : storage.getSolutions().entrySet()) {
            Chromosome<?> result = entry.getValue().get(0);
            resultDisplays.add(new ResultDisplay(result.getSize(), result.getFitness(), result.simplifyExpression(
                    result.getExpression(result.toString(), new ArrayList<>(model.getEnabledPrimitives()), true)
            )));
        }

        // Sort by error
        Collections.sort(resultDisplays);

        // Add all the results as FX observables
        solutionsTableView.setItems(FXCollections.observableArrayList(resultDisplays));

        TableColumn<ResultDisplay, Integer> sizeCol = new TableColumn<>("Size");
        TableColumn<ResultDisplay, Double> errorColumn = new TableColumn<>("Error");
        TableColumn<ResultDisplay, String> solutionCol = new TableColumn<>("Solution");

        // Set conversion factories for data types into string
        sizeCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        errorColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        // Set where the values come from
        sizeCol.setCellValueFactory(cellData -> cellData.getValue().sizeProperty().asObject());
        errorColumn.setCellValueFactory(cellData -> cellData.getValue().errorProperty().asObject());
        solutionCol.setCellValueFactory(cellData -> cellData.getValue().solutionProperty());

        // Set the columns to be these ones
        solutionsTableView.getColumns().set(0, sizeCol);
        solutionsTableView.getColumns().set(1, errorColumn);
        solutionsTableView.getColumns().set(2, solutionCol);
    }

    /**
     * Sets up the right click menu for copying and pasting
     */
    private void setupContextMenu(){
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copy = new MenuItem("Copy");
        copy.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
        copy.setOnAction(actionEvent -> copySelectionToClipboard());
        contextMenu.getItems().add(copy);
        solutionsTableView.setContextMenu(contextMenu);
    }

    /**
     * Reads the selected cells and places them in the clipboard formatted as a spreadsheet
     */
    private void copySelectionToClipboard() {
        ResultDisplay row = solutionsTableView.getSelectionModel().getSelectedItem();
        if (row == null) {
            return;
        }

        // Set clipboard content
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(row.getSolution());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    /**
     * Get the workspace service
     * @return Workspace service
     */
    private WorkspaceService getWorkspaceService() {
        return workspaceService;
    }
}
